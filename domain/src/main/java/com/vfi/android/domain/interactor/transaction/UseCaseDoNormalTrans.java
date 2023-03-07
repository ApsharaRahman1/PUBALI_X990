package com.vfi.android.domain.interactor.transaction;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.entities.businessbeans.CardInformation;
import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.EmvInformation;
import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.businessbeans.PinInformation;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.businessbeans.TransAttribute;
import com.vfi.android.domain.entities.consts.CVMResult;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.entities.consts.CardType;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransErrorCode;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interactor.deviceservice.UseCaseSaveEmvDebugInfo;
import com.vfi.android.domain.interactor.transaction.base.ITransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.InstallmentSaleTransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.OfflineSaleTransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.OfflineVoidTransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.PreAuthCompTransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.PreAuthTransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.SaleTransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.SimulateNormalTransacion;
import com.vfi.android.domain.interactor.transaction.tle.trans.TipAdjustTransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.OnlineVoidTransaction;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class UseCaseDoNormalTrans extends UseCase<Integer, Void> {
    private final String TAG = TAGS.Transaction;

    private final IRepository iRepository;
    private final IPosService posService;
    private final UseCaseStartTransCommunication useCaseStartTransCommunication;
    private final UseCaseSaveEmvDebugInfo useCaseSaveEmvDebugInfo;
    private final CurrentTranData currentTranData;

    @Inject
    public UseCaseDoNormalTrans(ThreadExecutor threadExecutor,
                                PostExecutionThread postExecutionThread,
                                IRepository iRepository,
                                IPosService posService,
                                UseCaseSaveEmvDebugInfo useCaseSaveEmvDebugInfo,
                                UseCaseStartTransCommunication useCaseStartTransCommunication) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
        this.posService = posService;
        this.useCaseStartTransCommunication = useCaseStartTransCommunication;
        this.useCaseSaveEmvDebugInfo = useCaseSaveEmvDebugInfo;
        this.currentTranData = iRepository.getCurrentTranData();
    }

    @Override
    public Observable<Integer> buildUseCaseObservable(Void aVoid) {
        return Observable.create(emitter -> {
            int transType = currentTranData.getTransType();
            LogUtil.d(TAG, "transType=" + transType);
            ITransaction iTransaction = null;

            switch (transType) {
                case TransType.SALE:
                    collectRecordInfo();
                    iTransaction = new SaleTransaction(iRepository, posService);
                    break;
                case TransType.VOID:
                    RecordInfo recordInfo = currentTranData.getRecordInfo();
                    if ((recordInfo.getTipAdjustTimes() > 0 || recordInfo.getVoidOrgTransType() == TransType.OFFLINE)
                            && !recordInfo.isOfflineTransUploaded()) {
                        iTransaction = new OfflineVoidTransaction(iRepository, posService);
                    } else {
                        iTransaction = new OnlineVoidTransaction(iRepository, posService);
                    }
                    break;
                case TransType.PREAUTH:
                    collectRecordInfo();
                    iTransaction = new PreAuthTransaction(iRepository, posService);
                    break;
                case TransType.PREAUTH_COMP:
                    collectRecordInfo();
                    iTransaction = new PreAuthCompTransaction(iRepository, posService);
                    break;
                case TransType.OFFLINE:
                    collectRecordInfo();
                    iTransaction = new OfflineSaleTransaction(iRepository, posService);
                    break;
                case TransType.TIP_ADJUST:
                    iTransaction = new TipAdjustTransaction(iRepository, posService);
                    break;
                case TransType.INSTALLMENT:
                    collectRecordInfo();
                    iTransaction = new InstallmentSaleTransaction(iRepository, posService);
                    break;
            }

            // check if training mode is turn on
            TerminalCfg terminalCfg = iRepository.getTerminalCfg();
            LogUtil.d(TAG, "Training mode enable status=[" + terminalCfg.isEnableTrainingMode() + "]");
            if (terminalCfg.isEnableTrainingMode()) {
                iTransaction = new SimulateNormalTransacion(iRepository, posService, transType);
            }

            if (iTransaction != null) {
                Disposable disposable = useCaseStartTransCommunication.asyncExecute(iTransaction).doOnNext(commStatus -> {
                    // deliver communication status to UI
                    LogUtil.d(TAG, "commStatus=" + commStatus);
                    emitter.onNext(commStatus);
                }).doOnComplete(() -> {
                    emitter.onComplete();
                }).doOnError(throwable -> {
                    LogUtil.d(TAG, "start trans communication error.");
                    if (throwable instanceof CommonException) {
                        emitter.onError(throwable);
                    } else {
                        throwable.printStackTrace();
                        emitter.onError(new CommonException(ExceptionType.TRANS_FAILED, TransErrorCode.TRANS_REJECT));
                    }
                }).subscribe();
            } else {
                LogUtil.d(TAG, "Transaction entry not found.");
                emitter.onError(new CommonException(ExceptionType.TRANS_FAILED, TransErrorCode.TRANS_NOT_FOUND));
            }

            if (currentTranData.getCardInfo().getCardEntryMode() == CardEntryMode.IC
                    && TransAttribute.findTypeByType(transType).isNeedInputOnlineResult()) {
                useCaseSaveEmvDebugInfo.asyncExecuteWithoutResult(null);
            }
        });
    }

    private void collectRecordInfo() {
        RecordInfo recordInfo = currentTranData.getRecordInfo();
        if (recordInfo != null) {
            HostInfo hostInfo = currentTranData.getHostInfo();
            MerchantInfo merchantInfo = currentTranData.getMerchantInfo();
            CardBinInfo cardBinInfo = currentTranData.getCardBinInfo();
            CardInformation cardInfo = currentTranData.getCardInfo();
            PinInformation pinInfo = currentTranData.getPinInfo();
            EmvInformation emvInfo = currentTranData.getEmvInfo();

            int cardEntryMode = 0;
            boolean isFallback = false;
            boolean isMSDCard = false;
            if (cardInfo != null) {
                cardEntryMode = cardInfo.getCardEntryMode();
                isMSDCard = cardInfo.isMsdCard();
                recordInfo.setPan(cardInfo.getPan());
                recordInfo.setCardExpiryDate(cardInfo.getExpiredDate());
                recordInfo.setCardSequenceNum(cardInfo.getCardSequenceNum());
                recordInfo.setTrack1(cardInfo.getTrack1());
                recordInfo.setTrack2(cardInfo.getTrack2());
                recordInfo.setTrack3(cardInfo.getTrack3());
                recordInfo.setCardHolderName(cardInfo.getCardHolderName());
                recordInfo.setAID(cardInfo.getAid());
                recordInfo.setAppLabel(cardInfo.getApplicationLabel());
            }

            TransAttribute transAttribute = TransAttribute.findTypeByType(currentTranData.getTransType());
            if (transAttribute != null) {
                // rcbc cup card sale process code is 000000
                if (cardBinInfo.getCardType() == CardType.CUP
                        && transAttribute.getTransType() == TransType.SALE) {
                    recordInfo.setProcessCode("000000");
                } else {
                    recordInfo.setProcessCode(transAttribute.getProcessCode());
                }
            }

            if (merchantInfo != null) {
                recordInfo.setTraceNum(merchantInfo.getTraceNum());
                recordInfo.setMerchantIndex(merchantInfo.getMerchantIndex());
                recordInfo.setTerminalId(merchantInfo.getTerminalId());
                recordInfo.setMerchantId(merchantInfo.getMerchantId());
                recordInfo.setBatchNo(merchantInfo.getBatchNum());
            }

            if (hostInfo != null) {
                recordInfo.setHostType(hostInfo.getHostType());
                recordInfo.setNii(hostInfo.getNII());
            }

            if (cardBinInfo != null) {
                recordInfo.setCardBinIndex(cardBinInfo.getCardIndex());
                recordInfo.setCardType(cardBinInfo.getCardType());
            }

            if (pinInfo != null) {
                if (pinInfo.isInputPinRequested()) {
                    if (pinInfo.isPinBypassed()) {
                        recordInfo.setCvmResult(CVMResult.PIN_BYPASS);
                    } else {
                        recordInfo.setCvmResult(CVMResult.INPUT_PIN);
                    }
                }
                if (pinInfo.getEncryptedPinblock() != null) {
                    recordInfo.setPinBlock(StringUtil.byte2HexStr(pinInfo.getEncryptedPinblock()));
                }
            }

            if (emvInfo != null) {
                isFallback = emvInfo.isDoFallback();
                recordInfo.setField55(emvInfo.getAuthReqData());
                recordInfo.setCurrencyCode(emvInfo.getCurrencyCode());
                if (recordInfo.getCurrencyCode() == null
                        || recordInfo.getCurrencyCode().length() == 0) {
                    TerminalCfg terminalCfg = iRepository.getTerminalCfg();
                    LogUtil.d(TAG, "Use default currency code=" + terminalCfg.getDefaultCurrencyCode());
                    recordInfo.setCurrencyCode(terminalCfg.getDefaultCurrencyCode());
                }
                recordInfo.setTc(emvInfo.getTc());
            }

            // posEntryMode for rcbc is fixed value of pin byte.
            String posEntryMode = "";
            if (isFallback) {
                posEntryMode = "801";
            } else if (isMSDCard) {
                posEntryMode = "911";
            } else {
                switch (cardEntryMode) {
                    case CardEntryMode.IC:
                        posEntryMode = "051";
                        break;
                    case CardEntryMode.RF:
                        posEntryMode = "071";
                        break;
                    case CardEntryMode.MAG:
                        posEntryMode = "022";
                        break;
                    case CardEntryMode.MANUAL:
                        posEntryMode = "012";
                        break;
                    default:
                        posEntryMode = "000";
                        break;
                }
            }

            LogUtil.d(TAG, "PosEntryMode=" + posEntryMode);
            recordInfo.setPosEntryMode(posEntryMode);

            if (currentTranData.getCurrentInstallmentPromo() != null) {
                recordInfo.setPromoCode(currentTranData.getCurrentInstallmentPromo().getPromoCode());
                recordInfo.setPromoLabel(currentTranData.getCurrentInstallmentPromo().getPromoLabel());
                recordInfo.setInstallmentTerm(currentTranData.getInstallmentTerm());
            }
        }
    }
}
