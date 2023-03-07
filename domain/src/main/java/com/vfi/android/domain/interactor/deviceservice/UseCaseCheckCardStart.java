package com.vfi.android.domain.interactor.deviceservice;

import com.vfi.android.domain.entities.businessbeans.CardInformation;
import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.SwitchParameter;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.consts.CTLSLedStatus;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.entities.consts.CheckCardErrorConst;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.CheckCardParamIn;
import com.vfi.android.domain.entities.databeans.CheckCardResult;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.vfi.android.domain.entities.consts.ExceptionType.GET_CARD_BIN_FAILED;

/**
 * Created by yunlongg1 on 20/10/2017.
 */

public class UseCaseCheckCardStart extends UseCase<CheckCardResult, Void> {
    private static final String TAG = TAGS.CHECK_CARD;
    private IPosService posService;
    private IRepository iRepository;
    private CurrentTranData currentTranData;
    private TerminalCfg terminalCfg;
    private CheckCardResult checkCardResult;
    private CheckCardParamIn checkCardParamIn;
    private final UseCaseSetCTLSLedStatus useCaseSetCTLSLedStatus;
    private final UseCaseStartBeep useCaseStartBeep;

    @Inject
    UseCaseCheckCardStart(
            ThreadExecutor threadExecutor,
            PostExecutionThread postExecutionThread,
            IPosService posService,
            IRepository iRespository,
            UseCaseStartBeep useCaseStartBeep,
            UseCaseSetCTLSLedStatus useCaseSetCTLSLedStatus) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
        this.iRepository = iRespository;
        this.useCaseSetCTLSLedStatus = useCaseSetCTLSLedStatus;
        this.useCaseStartBeep = useCaseStartBeep;
        terminalCfg = iRespository.getTerminalCfg();
    }

    Observable<CheckCardResult> dealWithException(Throwable throwable) {
        useCaseSetCTLSLedStatus.asyncExecuteWithoutResult(CTLSLedStatus.CLEAR_ALL_LEDS);

        useCaseStartBeep.asyncExecuteWithoutResult(2);

        Observable<CheckCardResult> observable;
        if (throwable instanceof CommonException) {
            CommonException commonException = (CommonException) throwable;
            int exceptionType = commonException.getExceptionType();
            int subErrorType = commonException.getSubErrType();

            LogUtil.i(TAG, "IC read card failed: exceptionType=" + exceptionType);
            if (exceptionType == ExceptionType.CHECK_CARD_FAILED) {
                switch (subErrorType) {
                    case CheckCardErrorConst.INSERT_CARD_FAILED:
                    case CheckCardErrorConst.READ_CHIP_FAILED:
                        currentTranData.getCardInfo().setCardEntryMode(CardEntryMode.IC);
                        break;
                    case CheckCardErrorConst.TAP_CARD_FAILED:
                    case CheckCardErrorConst.ACTIVATE_CONTACTLESS_CARD_FAILED:
                    case CheckCardErrorConst.CARD_CONFLICT:
                        currentTranData.getCardInfo().setCardEntryMode(CardEntryMode.RF);
                        break;
                    default:
                        currentTranData.getCardInfo().setCardEntryMode(CardEntryMode.NONE);
                        break;
                }
            }

            int cardEntryMode = currentTranData.getCardInfo().getCardEntryMode();
            if (exceptionType == ExceptionType.CHECK_CARD_FAILED
                    && (cardEntryMode == CardEntryMode.IC
                    || cardEntryMode == CardEntryMode.RF)) {
                checkCardResult.setNeedRetry(false);
                if (terminalCfg.isAllowFallback()) {
                    observable = Observable.error(new CommonException(ExceptionType.FALL_BACK, subErrorType));
                } else {
                    observable = Observable.error(new CommonException(exceptionType, subErrorType));
                }
            } else if (exceptionType == ExceptionType.CHECK_CARD_TIMEOUT) {
//                    || exceptionType == ExceptionType.TRACK_KEY_NOT_FOUND) {
//                LogUtil.i(TAG, "track key not found.");
                observable = Observable.error(throwable);
            } else if (exceptionType == GET_CARD_BIN_FAILED) {
                observable = Observable.error(throwable);
            } else {
                LogUtil.i(TAG, "read card info failed, try again");
                checkCardResult.setNeedRetry(true);
                checkCardResult.setFallBack(false);
                observable = Observable.just(getCheckCardResult());
            }
        } else {
            LogUtil.i(TAG, "read card info failed, throwable " + throwable.getMessage());
            observable = Observable.error(throwable);
        }

        return posService.stopCheckCard().flatMap(unused -> observable);
    }

    void dealWithCardInfo() throws Exception {
        CardInformation cardInformation = currentTranData.getCardInfo();

        if (cardInformation.getCardEntryMode() == CardEntryMode.RF) {
            useCaseSetCTLSLedStatus.asyncExecuteWithoutResult(CTLSLedStatus.DETECT_CTLS_CARD);
        } else {
            useCaseSetCTLSLedStatus.asyncExecuteWithoutResult(CTLSLedStatus.CLEAR_ALL_LEDS);
        }

        if (cardInformation.getCardEntryMode() == CardEntryMode.MAG) {
            if (!cardInformation.isValid()) {
                throw new CommonException(ExceptionType.CHECK_CARD_FAILED, CheckCardErrorConst.CARD_INFO_INVALID);
            }

            // IC card do swipe card should not be allowed (Except fallback)
            if (cardInformation.isICCard()
                    && (checkCardParamIn.isSupportICCard() || checkCardParamIn.isSupportRFCard())) {
                checkCardParamIn.setSupportMagCard(false);
                checkCardResult.setNeedRetry(true);
                throw new CommonException(ExceptionType.CHECK_CARD_FAILED, CheckCardErrorConst.IC_CARD_NOT_ALLOW_SWIPE_FIRST);
            }

            if (cardInformation.getTrack2() != null && cardInformation.getTrack2().endsWith("F")) {
                LogUtil.i(TAG, "check if Track2 end with F character");
                int length = cardInformation.getTrack2().length();
                String track2 = currentTranData.getCardInfo().getTrack2();
                track2.substring(0, length - 1);
                currentTranData.getCardInfo().setTrack2(track2);

            //apshara
//            LogUtil.i(TAG, "Apshara");
//            LogUtil.i(TAG, cardInformation.getTrack2());
//            try{
//            if (cardInformation.getTrack2() != null) {
//                LogUtil.i(TAG, "check if Track2 end with F character");
//                int length = cardInformation.getTrack2().length();
//                String track2 = currentTranData.getCardInfo().getTrack2();
//
//                if(length %2 != 0){
//                    track2 += "F";
//                }
//            }}catch(Exception e){
//                LogUtil.i(TAG, e.toString());
            }

            currentTranData.getCardInfo().setPan(cardInformation.getPan());
            currentTranData.getCardInfo().setTrack2(cardInformation.getTrack2());
            currentTranData.getCardInfo().setTrack3(cardInformation.getTrack3());
            currentTranData.getCardInfo().setExpiredDate(cardInformation.getExpiredDate());
            currentTranData.getCardInfo().setCardSequenceNum(cardInformation.getServiceCode());

            LogUtil.i(TAG, "getTrack1  " + cardInformation.getTrack1());
            LogUtil.i(TAG, "getTrack2  " + cardInformation.getTrack2());
            LogUtil.i(TAG, "getTrack3  " + cardInformation.getTrack3());
            LogUtil.i(TAG, "getPan  " + cardInformation.getPan());
            LogUtil.i(TAG, "getExpiredDate  " + cardInformation.getExpiredDate());

            saveCardHolder(currentTranData);
        }


        //校验都成功，保存当前输入方式 //The verification is successful, save the current input method
        checkCardResult.setInputMode(cardInformation.getCardEntryMode());
        //增加所有交易都进行当前卡片类型结果的传递 //Added all transactions to pass through the result of the current card type
        checkCardResult.setSupportICCard(checkCardParamIn.isSupportICCard());
        checkCardResult.setSupportMagCard(checkCardParamIn.isSupportMagCard());
        checkCardResult.setSupportRFCard(checkCardParamIn.isSupportRFCard());
    }

//    Observable<CurrentTranData> dealWithTrackEncrypted() throws Exception {
//
//    }

    private void saveCardHolder(CurrentTranData currentTranData) {
        String track1 = currentTranData.getCardInfo().getTrack1();
        currentTranData.getCardInfo().setCardHolderName("");
        if (track1 != null && track1.length() > 0) {
            int cardHolderFirstMarkIdx = track1.indexOf("^");
            LogUtil.d(TAG, "cardHolderFirstMarkIdx=" + cardHolderFirstMarkIdx);
            if (cardHolderFirstMarkIdx == -1) {
                return;
            }

            String cardHolder = track1.substring(cardHolderFirstMarkIdx + 1);

            int cardHolderLastMarkIdx = cardHolder.indexOf("^");
            LogUtil.d(TAG, "cardHolderLastMarkIdx=" + cardHolderLastMarkIdx);
            if (cardHolderLastMarkIdx == -1) {
                return;
            }

            cardHolder = cardHolder.substring(0, cardHolderLastMarkIdx);
            LogUtil.d(TAG, "cardHolder=" + cardHolder);

            currentTranData.getCardInfo().setCardHolderName(cardHolder.trim());
        }
    }

    public CheckCardResult getCheckCardResult() {
        if (currentTranData.getCardInfo().getCardEntryMode() != CardEntryMode.RF) {
            useCaseStartBeep.asyncExecuteWithoutResult(1);
        }

        checkCardResult.setSupportRFCard(checkCardParamIn.isSupportRFCard());
        checkCardResult.setSupportMagCard(checkCardParamIn.isSupportMagCard());
        checkCardResult.setSupportICCard(checkCardParamIn.isSupportICCard());

        return checkCardResult;
    }

    public Observable<CardInformation> saveCardInformation(CardInformation cardInfo) {
        CardInformation cardInformation = currentTranData.getCardInfo();
        cardInformation.setTrack1(cardInfo.getTrack1());
        cardInformation.setTrack2(cardInfo.getTrack2());
        cardInformation.setTrack3(cardInfo.getTrack3());
        cardInformation.setServiceCode(cardInfo.getServiceCode());
        cardInformation.setExpiredDate(cardInfo.getExpiredDate());
        cardInformation.setPan(cardInfo.getPan());
        cardInformation.setCardEntryMode(cardInfo.getCardEntryMode());
        return Observable.just(cardInformation);
    }

    @Override
    public Observable<CheckCardResult> buildUseCaseObservable(Void unused) {
        iRepository.lockCheckCardLock();
        try {
            checkCardResult = new CheckCardResult();
            checkCardResult.setNeedRetry(false);
            TerminalCfg terminalCfg = iRepository.getTerminalCfg();
            currentTranData = iRepository.getCurrentTranData();
            SwitchParameter switchParameter = currentTranData.getSwitchParameter();
            if (currentTranData.getCurrCheckCardParam() == null) {
                checkCardParamIn = new CheckCardParamIn()
                        .setSupportICCard(switchParameter.isEnableInsertCard())
                        .setSupportMagCard(switchParameter.isEnableSwipeCard())
                        .setSupportRFCard(switchParameter.isEnableTapCard())
                        .setTimeout(terminalCfg.getOperationTimeout());
                currentTranData.setCurrCheckCardParam(checkCardParamIn);
            } else {
                checkCardParamIn = currentTranData.getCurrCheckCardParam();
            }

            if (currentTranData.isEMVNeedFallback()) {
                LogUtil.d(TAG, "isEMVNeedFallback");
                currentTranData.setEMVNeedFallback(false);
                if (currentTranData.getIcCardfallbackRemainTimes() > 0) {
                    int remainTimes = currentTranData.getIcCardfallbackRemainTimes() - 1;
                    LogUtil.d(TAG, "remainTimes=" + remainTimes);
                    currentTranData.setIcCardfallbackRemainTimes(remainTimes);
                } else {
                    currentTranData.getEmvInfo().setDoFallback(true);
                    LogUtil.i(TAG, "Begin fallback");
                    checkCardParamIn.setSupportICCard(false);
                    checkCardParamIn.setSupportRFCard(false);
                    checkCardParamIn.setSupportMagCard(true);
                    checkCardResult.setSupportICCard(false);
                    checkCardResult.setSupportRFCard(false);
                    checkCardResult.setSupportMagCard(true);
                    checkCardResult.setFallBack(true);
                }
            } else if (currentTranData.isEMVNeedTapToInsert()) {
                LogUtil.d(TAG, "isEmvTabToInsert");
                currentTranData.setEMVNeedTapToInsert(false);
                if (currentTranData.getRfCardFallbackRemainTimes() > 0) {
                    int remainTimes = currentTranData.getRfCardFallbackRemainTimes() - 1;
                    LogUtil.d(TAG, "remainTimes=" + remainTimes);
                    currentTranData.setRfCardFallbackRemainTimes(remainTimes);
                } else {
                    checkCardParamIn.setSupportRFCard(false);
                    checkCardParamIn.setSupportICCard(true);
                    checkCardParamIn.setSupportMagCard(true);
                }
            }

            if (checkCardParamIn.isSupportRFCard()) {
                useCaseSetCTLSLedStatus.asyncExecuteWithoutResult(CTLSLedStatus.START_CHECK_CARD);
            }

            return posService.startCheckCard(checkCardParamIn)
                    .doOnNext(this::saveCardInformation)
                    .flatMap(cardInformation -> posService.stopCheckCard())
                    .doOnNext(unused1 -> dealWithCardInfo())
//                    .flatMap(o -> dealWithTrackEncrypted())
                    .flatMap(unused2 -> Observable.just(getCheckCardResult()))
                    .onErrorResumeNext(this::dealWithException)
                    .observeOn(Schedulers.from(threadExecutor))
                    .repeatUntil(() -> {
                        boolean isNeedRetry = checkCardResult.isNeedRetry();
                        if (isNeedRetry) {
                            checkCardResult = new CheckCardResult();
                            checkCardResult.setNeedRetry(false);
                            if (checkCardParamIn.isSupportRFCard()) {
                                useCaseSetCTLSLedStatus.asyncExecuteWithoutResult(CTLSLedStatus.START_CHECK_CARD);
                            }
                        }
                        return !isNeedRetry;
                    });
        } finally {
            iRepository.unlockCheckCardLock();
        }
    }
}
