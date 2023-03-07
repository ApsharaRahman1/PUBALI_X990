package com.vfi.android.domain.interactor.deviceservice;

import android.text.TextUtils;

import com.vfi.android.domain.entities.businessbeans.CardInformation;
import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.EMVCallback;
import com.vfi.android.domain.entities.businessbeans.EmvInformation;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.consts.CTLSCvmResult;
import com.vfi.android.domain.entities.consts.CTLSLedStatus;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.entities.consts.EMVResult;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.databeans.EMVParamIn;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalCfg;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.domain.utils.Tlv2Map;

import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by yunlongg1 on 12/10/2017
 * Modify by yunlongg1 @2018-01-20
 */

public class UseCaseEmvStart extends UseCase<EMVCallback, Void> {
    private static final String TAG = TAGS.EmvFlow;
    private final IPosService posService;
    private final IRepository iRepository;
    private final CurrentTranData currentTranData;
    private final UseCaseSetCTLSLedStatus useCaseSetCTLSLedStatus;
    private final UseCaseStartBeep useCaseStartBeep;
    private final UseCaseGetTerminalCfg useCaseGetTerminalCfg;
    private final UseCaseSaveEmvDebugInfo useCaseSaveEmvDebugInfo;
    private final TerminalCfg terminalCfg;

    @Inject
    UseCaseEmvStart(ThreadExecutor threadExecutor,
                    PostExecutionThread postExecutionThread,
                    IRepository iRepository,
                    IPosService posService,
                    UseCaseGetTerminalCfg useCaseGetTerminalCfg,
                    UseCaseSetCTLSLedStatus useCaseSetCTLSLedStatus,
                    UseCaseSaveEmvDebugInfo useCaseSaveEmvDebugInfo,
                    UseCaseStartBeep useCaseStartBeep) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
        this.posService = posService;
        currentTranData = iRepository.getCurrentTranData();
        this.useCaseSetCTLSLedStatus = useCaseSetCTLSLedStatus;
        this.useCaseStartBeep = useCaseStartBeep;
        this.useCaseGetTerminalCfg = useCaseGetTerminalCfg;
        this.useCaseSaveEmvDebugInfo = useCaseSaveEmvDebugInfo;
        terminalCfg = useCaseGetTerminalCfg.execute(null);
    }

    private int getEmvProcessType() {
        switch (currentTranData.getTransType()) {
            case TransType.OFFLINE:
                LogUtil.d(TAG, "Use simple emv flow");
                currentTranData.getEmvInfo().setFullEmvFlow(false);
                return EMVParamIn.PROCESS_TYPE_SIMPLE_FLOW;
        }

        LogUtil.d(TAG, "Use emv flow");
        currentTranData.getEmvInfo().setFullEmvFlow(true);
        return EMVParamIn.PROCESS_TYPE_EMV_FLOW;
    }

    private Observable<EMVParamIn> getConfig() {
        return Observable.create(emitter -> {

            EMVParamIn emvParamIn = new EMVParamIn()
                    .setAuthAmount(((currentTranData.getTransAmount() != null) ? Long.parseLong(currentTranData.getTransAmount()) : 0L))
                    .setMerchantId("000000000000")
                    .setTerminalId("00000000")
                    .setMerchantName("Test")
                    .setForceOnline(currentTranData.getSwitchParameter().isEMVForceOnline())
                    .setSlotType(currentTranData.getCardInfo().getCardEntryMode())
                    .setSupportSM(false)
                    .setTimeout(terminalCfg.getOperationTimeout())
                    .setEmvProcesstype(getEmvProcessType());

            checkAndSetCtlsLed(CTLSLedStatus.START_CTLS_EMV_FLOW);

            emitter.onNext(emvParamIn);
            emitter.onComplete();
        });
    }

    private Observable<CurrentTranData> encryptTrackData(CurrentTranData currentTranData) throws Exception {
        return Observable.just(currentTranData);
    }

    private void saveRequireTagInfo(CurrentTranData currentTranData) {
        String[] tagList = {"5F20", "84", "9F7E", "56", "50", "9F40", "9F12", "9F11", "5F2A"};
        String tlvTags = posService.getEMVTagList(tagList);
        if (tlvTags != null && tlvTags.length() > 0) {
            LogUtil.d(TAG, "emv tag list=" + tlvTags);
            Map<String, String> valueMap = Tlv2Map.tlv2Map(tlvTags);
            String cardHolderName = valueMap.get("5F20");
            LogUtil.d(TAG, "IC Card holder bcd name=" + cardHolderName);
            cardHolderName = StringUtil.hexStr2Str(cardHolderName);
            LogUtil.d(TAG, "IC Card holder asc name=" + cardHolderName);
            currentTranData.getCardInfo().setCardHolderName(cardHolderName.trim());
            String AID = valueMap.get("84").trim();
            LogUtil.d(TAG, "IC card AID=" + AID);
            currentTranData.getCardInfo().setAid(AID);

            String applicationLabel;
            if (valueMap.containsKey("9F11") && valueMap.containsKey("9F40")) {
                String tag9F11 = valueMap.get("9F11");
                String tag9F40 = valueMap.get("9F40");
                LogUtil.d(TAG, "9F40=" + valueMap.get("9F40"));
                LogUtil.d(TAG, "9F11=" + valueMap.get("9F11"));

                if (tag9F11 != null && tag9F11.length() == 2
                        && tag9F40 != null && tag9F40.length() == 10) {
                    if (tag9F11.equals(tag9F40.substring(8, 10))) {
                        applicationLabel = valueMap.get("9F12");
                        LogUtil.d(TAG, "9F12=" + valueMap.get("9F12"));
                    } else {
                        applicationLabel = valueMap.get("50");
                    }
                } else {
                    applicationLabel = valueMap.get("50");
                }
            } else {
                applicationLabel = valueMap.get("50");
            }

            LogUtil.d(TAG, "applicationLabel=" + applicationLabel);
            if (applicationLabel != null && applicationLabel.length() > 0) {
                applicationLabel = new String(StringUtil.hexStr2Bytes(applicationLabel));
                LogUtil.d(TAG, "applicationLabel=" + applicationLabel);
                currentTranData.getCardInfo().setApplicationLabel(applicationLabel);
                currentTranData.getEmvInfo().setAppLabel(applicationLabel);
            } else { // get application label from table 1
                applicationLabel = "";
                LogUtil.d(TAG, "applicationLabel=" + applicationLabel);
                currentTranData.getCardInfo().setApplicationLabel(applicationLabel);
                currentTranData.getEmvInfo().setAppLabel(applicationLabel);
            }

            if (valueMap.containsKey("5F2A")) {
                LogUtil.d(TAG, "currencyCode=" + valueMap.get("5F2A"));
                currentTranData.getEmvInfo().setCurrencyCode(valueMap.get("5F2A"));
            }

            CardInformation cardInformation = currentTranData.getCardInfo();
            if (cardInformation.isMsdCard() || cardInformation.isPhoneMsdCard()) {
                if (valueMap.containsKey("9F7E")) {
                    String tag9F7E = valueMap.get("9F7E");
                    LogUtil.d(TAG, "MobileAcceptanceIndicator[9F7E]=" + tag9F7E);
                    cardInformation.setMobileAcceptanceIndicator(tag9F7E);
                }

                if (valueMap.containsKey("56")) {
                    String track1 = valueMap.get("56");
                    String track1Org = new String(StringUtil.hexStr2Bytes(track1));
                    if ((track1Org.length() % 2 == 0) && track1Org.endsWith("F")) {
                        track1Org = track1Org.substring(0, track1Org.length() - 1);
                    }
                    LogUtil.d(TAG, "track1=" + track1Org);
                    cardInformation.setTrack1(track1Org);
                    int cardHolderNameStartIndex = -1;
                    int cardHolderNameEndIdx = -1;
                    cardHolderNameStartIndex = track1Org.indexOf("^");
                    if (cardHolderNameStartIndex > 0 && cardHolderNameStartIndex < track1Org.length()) {
                        cardHolderNameEndIdx = track1Org.indexOf("^", cardHolderNameStartIndex + 1);
                        if (cardHolderNameEndIdx > 0) {
                            cardHolderName = track1Org.substring(cardHolderNameStartIndex + 1, cardHolderNameEndIdx);
                            LogUtil.d(TAG, "cardHolderName=" + cardHolderName);
                            cardInformation.setCardHolderName(cardHolderName);
                        }
                    }
                }
            }
        } else {
            LogUtil.d(TAG, "Card holder name not found");
            currentTranData.getCardInfo().setCardHolderName("");
        }
    }

    private Observable<EMVCallback> saveData(EMVCallback emvCallback) throws Exception {
        LogUtil.i(TAG, "saveData executed.");

        switch (emvCallback.getCallbackType()) {
            case ON_SELECT_APP:
                LogUtil.i(TAG, "ON_SELECT_APP");
                LogUtil.i(TAG, "getAppList  " + emvCallback.getAppList());
                break;
            case ON_CONFIRM_CARDINFO:
                LogUtil.i(TAG, "ON_CONFIRM_CARDINFO");
                currentTranData.getEmvInfo().setEmvConfirmCard(true);

                CardInformation cardInformation = currentTranData.getCardInfo();
                if (cardInformation.getCardEntryMode() == CardEntryMode.RF) {
                    useCaseStartBeep.asyncExecuteWithoutResult(1);
                    checkAndSetCtlsLed(CTLSLedStatus.CTLS_EMV_FINISHED);
                    checkAndSetCtlsLed(CTLSLedStatus.CLEAR_ALL_LEDS);
                }

                CardInformation result = emvCallback.getCardInformation();
                cardInformation.setPan(result.getPan());
                cardInformation.setCardSequenceNum(result.getCardSequenceNum());
                cardInformation.setExpiredDate(result.getExpiredDate());
                String track2 = result.getTrack2();
                if (track2 == null) {
                    track2 = "";
                }
                if (result.isMsdCard()) {
                    if (track2.contains("D")) {
                        track2 = track2.replace("D", "=");
                    }
                    if (track2.length() % 2 == 0 && track2.endsWith("F")) {
                        track2 = track2.substring(0, track2.length() - 1);
                    }
                } else if (track2.length() % 2 != 0) {
                    track2 += "F";
                }
                LogUtil.d(TAG, "final track2=[" + track2 + "]");
                cardInformation.setTrack2(track2);

                if (terminalCfg.isNeedRemoveTrackTailF() && track2.endsWith("F")) {
                    cardInformation.setTrack2(track2.substring(0, track2.length() - 1));
                }

                // save cardHolder RID cvmResult MobilePhoneVSDIndicator
                cardInformation.setMsdCard(result.isMsdCard());
                saveRequireTagInfo(currentTranData);

                if (result.isMsdCard()) {
                    boolean isPhoneMsd = isPhoneMsdCard();
                    cardInformation.setPhoneMsdCard(isPhoneMsd);
                    LogUtil.d(TAG, "isPhoneMsdCard:" + isPhoneMsd);
                }


                currentTranData.getCardInfo().setPan(cardInformation.getPan());
                currentTranData.getCardInfo().setCardSequenceNum(cardInformation.getCardSequenceNum());
                currentTranData.getCardInfo().setExpiredDate(cardInformation.getExpiredDate());
                currentTranData.getCardInfo().setTrack2(cardInformation.getTrack2());
                currentTranData.getCardInfo().setTrack3(cardInformation.getTrack3());

                LogUtil.i(TAG, "getPan  " + cardInformation.getPan());
                LogUtil.i(TAG, "getCardSequenceNum  " + cardInformation.getCardSequenceNum());
                LogUtil.i(TAG, "getExpiredDate  " + cardInformation.getExpiredDate());
                LogUtil.i(TAG, "getTrack2  " + cardInformation.getTrack2());
                LogUtil.i(TAG, "getTrack3  " + cardInformation.getTrack3());
                LogUtil.i(TAG, "isMsdCard  " + cardInformation.isMsdCard());
                LogUtil.i(TAG, "isPhoneMsdCard  " + cardInformation.isPhoneMsdCard());

                return encryptTrackData(currentTranData).flatMap(unused -> {
                    return Observable.just(emvCallback);
                });
            case ON_REQUEST_ONLINE:
                LogUtil.i(TAG, "ON_REQUEST_ONLINE");
                cardInformation = currentTranData.getCardInfo();

                String[] tagList = {"50", "9F12", "9B"};
                String arqc = posService.getEMVTagList(tagList);
                EmvInformation emvInformation = currentTranData.getEmvInfo();
                emvInformation.setArqc(arqc);
                emvInformation.setRequestOnline(true);

                currentTranData.getEmvInfo().setCtlsCvmResult(emvCallback.getContactlessCvmResult());
                if (currentTranData.getCardInfo().getCardEntryMode() == CardEntryMode.IC) {
                    emvInformation.setRequestSignature(emvCallback.isNeedSignature());
                } else {
                    if (emvCallback.getContactlessCvmResult() == CTLSCvmResult.CMV_SIGN) {
                        emvInformation.setRequestSignature(true);
                    }
                }

                saveAuthRequestData();
                break;
            case ON_REQUEST_INPUTPIN:
                currentTranData.setEmvRequireOnlinePin(true);
                LogUtil.i(TAG, "getOnlinePIN:  " + emvCallback.getOnlinePIN());
                break;
            case ON_REQUEST_INPUTOFFLINEPIN:
                currentTranData.setEmvRequireOnlinePin(false);
                LogUtil.i(TAG, "getOfflinePIN:  " + emvCallback.getOnlinePIN());
                break;
            case ON_TRADING_RES:
                LogUtil.d(TAG, "trading result");
                useCaseSaveEmvDebugInfo.asyncExecuteWithoutResult(null);
                EmvInformation emvInfo = currentTranData.getEmvInfo();
                if (currentTranData.getCardInfo().getCardEntryMode() == CardEntryMode.IC) {
                    emvInfo.setRequestSignature(emvCallback.isNeedSignature());
                } else {
                    if (emvCallback.getContactlessCvmResult() == CTLSCvmResult.CMV_SIGN) {
                        emvInfo.setRequestSignature(true);
                    }
                }
                emvInfo.setCtlsCvmResult(emvCallback.getContactlessCvmResult());
                int emvResult = emvCallback.getOnlineResult();
                if (emvResult == EMVResult.EMV_COMPLETE.getId()) {
                    saveAuthRequestData();
                } else if (emvResult == EMVResult.CTLS_ARQC.getId()) {
                    saveAuthRequestData();
                } else if (emvResult == EMVResult.AARESULT_TC.getId()
                        || emvResult == EMVResult.CTLS_TC.getId()) {
                    saveAuthRequestData();
                    String tcData = emvCallback.getTcData();
                    LogUtil.i(TAG, "AARESULT_TC Field55ForTC: " + tcData);
                    if (tcData != null && tcData.length() > 0) {
                        Map<String, String> valueMap = Tlv2Map.tlv2Map(emvCallback.getTcData());
                        String tc = valueMap.get("9F26");
                        LogUtil.d(TAG, "9F26 tc=" + tc);
                        if (tc != null && tc.length() > 0) {
                            currentTranData.getEmvInfo().setTc(tcData);
                        }
                    }
                    saveOfflineApprovalRecord();
                }
                break;
            default:
                break;
        }

        return Observable.just(emvCallback);
    }

    private void saveOfflineApprovalRecord() {
        RecordInfo recordInfo = currentTranData.getRecordInfo();
        recordInfo.setEmvOfflineApproval(true);
        // todo
        throw new RuntimeException("Please reference Vx then add logical here");
    }

    private void checkAndSaveTAGInfo() {
        CardInformation cardInformation = currentTranData.getCardInfo();
        LogUtil.d(TAG, "checkAndSaveTAGInfo Aid=" + cardInformation.getAid());

        saveAuthRequestData();
    }

    private void saveAuthRequestData() {
        String authRequestData;
        CurrentTranData currentTranData = iRepository.getCurrentTranData();
        
        String[] field55Tags = { "82", "84", "95", "9A", "9C", "5F2A", "5F34", "9F02",
                "9F03", "9F06", "9F07", "9F09", "9F10", "9F1A", "9F1E", "9F21", "9F26", "9F27",
                "9F33", "9F34", "9F35", "9F36", "9F37", "9F41", "9F53", "9F5B", "9F63",
                "9F74"};   //apshara


        authRequestData = posService.getEMVTagList(field55Tags);

        EmvInformation emvInformation = currentTranData.getEmvInfo();
        LogUtil.d(TAG, "authRequestData=[" + authRequestData + "]");
        emvInformation.setAuthReqData(authRequestData);
    }

    private boolean isPhoneMsdCard() {
        String tag82 = posService.getEMVTagList(new String[]{"82"});
        LogUtil.d(TAG, "tag82=" + tag82);
        if (tag82 == null || tag82.length() != 8) {
            return false;
        }
        byte[] bytes = StringUtil.hexStr2Bytes(tag82.substring(4));
        if (bytes == null || bytes.length < 2)
            return false;

        String aid = currentTranData.getCardInfo().getAid();
        if (TextUtils.isEmpty(aid) || aid.length() < 10)
            return false;
        String rid = aid.substring(0, 10);

        if ("A000000003".equals(rid)) {
            //VISA
            return 0x40 == (bytes[1] & 0x40);
        }

        if ("A000000004".equals(rid)) {
            //MASTER
            return 0x20 == (bytes[0] & 0x20);
        }

        if ("A000000025".equals(rid)) {
            //AMEX
            return 0x40 == (bytes[1] & 0x40);
        }
        return false;
    }

    private void checkAndSetCtlsLed(int ctlsLedstatus) {
        CardInformation cardInformation = currentTranData.getCardInfo();
        if (cardInformation.getCardEntryMode() == CardEntryMode.RF) {
            useCaseSetCTLSLedStatus.execute(ctlsLedstatus);
        }
    }

    @Override
    public Observable<EMVCallback> buildUseCaseObservable(Void unused) {
        return getConfig()
                .flatMap(posService::startEmvFlow)
                .concatMap(this::saveData)
                .onErrorResumeNext(throwable -> {
                    checkAndSaveTAGInfo();
//                    useCaseUpdateSystemParamTags.execute(null);
                    LogUtil.i(TAG, "onErrorResumeNext: " + throwable.getMessage());

                    useCaseSaveEmvDebugInfo.asyncExecuteWithoutResult(null);
                    checkAndSetCtlsLed(CTLSLedStatus.CLEAR_ALL_LEDS);

                    return Observable.zip(posService.stopEmvFlow(),
                            posService.beep(2),
                            (unused1, unused2) -> currentTranData)
                            .flatMap(currentTranData -> {
                                return Observable.error(throwable);
                            });
                });
    }
}
