package com.vfi.android.domain.entities.businessbeans;

import com.vfi.android.domain.utils.StringUtil;

public class EmvInformation {
    private String arqc;
    private String tc;
    private String authReqData; // field55
    private String secondGACResponse;
    private String declineOnlineDefaultTAC;
    private String appLabel;
    private String currencyCode; // TAG 5F2A
    private boolean isRequestInputPin;
    private boolean isRequestOfflinePin;
    private boolean isPaymentForceSignature;
    private boolean isRequestSignature;
    private boolean isRequestOnline;
    private boolean isEMVNeedFallback; //emv result indicate need fallback or Select app failed.
    private boolean isEMVNeedTapToInsert; //emv result is tap to insert
    private boolean isEmvFlowCancelled;
    private boolean isEmvConfirmCard;
    private boolean isDoFallback;

    // emv full flow or part flow, part flow only get card info.
    private boolean isFullEmvFlow;

    private int fallbackRemainTimes = 1;

    private boolean isInEmvFlow;

    private int emvResult;
    private int ctlsCvmResult;
    /*EMV online approved，offline approved，declined*/
    public final static int EMV_NOT_FINISHED = 0;
    public final static int EMV_ONLINE_APPROVED = 1;
    public final static int EMV_OFFLINE_APPROVED = 2;
    public final static int EMV_DECLINED = 3;

    public void setDefaultValue() {
        arqc = "";
        authReqData = "";
        tc = "";
        appLabel = "";
        isRequestInputPin = false;
        isRequestOfflinePin = false;
    }

    public String getArqc() {
        return getNonNullString(arqc);
    }

    public void setArqc(String arqc) {
        this.arqc = arqc;
    }

    public boolean isRequestInputPin() {
        return isRequestInputPin;
    }

    public void setRequestInputPin(boolean requestInputPin) {
        isRequestInputPin = requestInputPin;
    }

    public boolean isRequestOfflinePin() {
        return isRequestOfflinePin;
    }

    public void setRequestOfflinePin(boolean requestOfflinePin) {
        isRequestOfflinePin = requestOfflinePin;
    }

    public String getTc() {
        return getNonNullString(tc);
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public String getAuthReqData() {
        return getNonNullString(authReqData);
    }

    public void setAuthReqData(String authReqData) {
        this.authReqData = authReqData;
    }

    public int getEmvResult() {
        return emvResult;
    }

    public void setEmvResult(int emvResult) {
        this.emvResult = emvResult;
    }

    public String getSecondGACResponse() {
        return getNonNullString(secondGACResponse);
    }

    public void setSecondGACResponse(String secondGACResponse) {
        this.secondGACResponse = secondGACResponse;
    }

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }

    private String getNonNullString(String value) {
        return StringUtil.getNonNullString(value);
    }

    public boolean isRequestSignature() {
        return isRequestSignature;
    }

    public void setRequestSignature(boolean requestSignature) {
        isRequestSignature = requestSignature;
    }

    public boolean isEMVNeedFallback() {
        return isEMVNeedFallback;
    }

    public void setEMVNeedFallback(boolean EMVNeedFallback) {
        isEMVNeedFallback = EMVNeedFallback;
    }

    public boolean isEMVNeedTapToInsert() {
        return isEMVNeedTapToInsert;
    }

    public void setEMVNeedTapToInsert(boolean EMVNeedTapToInsert) {
        isEMVNeedTapToInsert = EMVNeedTapToInsert;
    }

    public String getDeclineOnlineDefaultTAC() {
        return declineOnlineDefaultTAC;
    }

    public void setDeclineOnlineDefaultTAC(String declineOnlineDefaultTAC) {
        this.declineOnlineDefaultTAC = declineOnlineDefaultTAC;
    }

    public boolean isPaymentForceSignature() {
        return isPaymentForceSignature;
    }

    public void setPaymentForceSignature(boolean paymentForceSignature) {
        isPaymentForceSignature = paymentForceSignature;
    }

    public boolean isInEmvFlow() {
        return isInEmvFlow;
    }

    public void setInEmvFlow(boolean inEmvFlow) {
        isInEmvFlow = inEmvFlow;
    }

    public boolean isRequestOnline() {
        return isRequestOnline;
    }

    public void setRequestOnline(boolean requestOnline) {
        isRequestOnline = requestOnline;
    }

    public int getCtlsCvmResult() {
        return ctlsCvmResult;
    }

    public void setCtlsCvmResult(int ctlsCvmResult) {
        this.ctlsCvmResult = ctlsCvmResult;
    }

    public boolean isEmvFlowCancelled() {
        return isEmvFlowCancelled;
    }

    public void setEmvFlowCancelled(boolean emvFlowCancelled) {
        isEmvFlowCancelled = emvFlowCancelled;
    }

    public int getFallbackRemainTimes() {
        return fallbackRemainTimes;
    }

    public void setFallbackRemainTimes(int fallbackRemainTimes) {
        this.fallbackRemainTimes = fallbackRemainTimes;
    }

    public boolean isEmvConfirmCard() {
        return isEmvConfirmCard;
    }

    public void setEmvConfirmCard(boolean emvConfirmCard) {
        isEmvConfirmCard = emvConfirmCard;
    }

    public boolean isDoFallback() {
        return isDoFallback;
    }

    public void setDoFallback(boolean doFallback) {
        isDoFallback = doFallback;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public boolean isFullEmvFlow() {
        return isFullEmvFlow;
    }

    public void setFullEmvFlow(boolean fullEmvFlow) {
        isFullEmvFlow = fullEmvFlow;
    }
}
