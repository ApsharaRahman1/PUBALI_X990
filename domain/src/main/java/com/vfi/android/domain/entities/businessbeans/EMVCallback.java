package com.vfi.android.domain.entities.businessbeans;

import java.util.List;

/**
 * Created by yunlongg1 on 19/10/2017.
 */

public class EMVCallback {
    private int binRouteStatusCallBack;
    public static final int BIN_ROUTE_CALL_BACK_NORMAL = 0;
    public static final int BIN_ROUTE_CALL_BACK_DCC = 1;
    public static final int BIN_ROUTE_CALL_BACK_IPP = 2;

    private CallbackType callbackType;
    private CardInformation cardInformation;
    private int onlineResult;
    private String arqcData;
    private String reversalData;
    private String tcData;
    private List<String> appList;
    private Boolean isOnlinePIN;
    private int retryTimes;

    private boolean isNeedSignature;
    private int contactlessCvmResult;

    public enum CallbackType {
        ON_REQUEST_AMOUNT,
        ON_SELECT_APP,
        ON_CONFIRM_CARDINFO,
        ON_REQUEST_INPUTPIN,
        ON_REQUEST_INPUTOFFLINEPIN,
        ON_CONFIRM_CERTINFO,
        ON_REQUEST_ONLINE,
        ON_TRADING_RES,
        ON_ERROR_MSG,
        ON_CALLBACK_FINISHED
    }

    public String getTcData() {
        return tcData;
    }

    public EMVCallback setTcData(String tcData) {
        this.tcData = tcData;
        return this;
    }

    public int getOnlineResult() {
        return onlineResult;
    }

    public EMVCallback setOnlineResult(int onlineResult) {
        this.onlineResult = onlineResult;
        return this;
    }

    public String getArqcData() {
        return arqcData;
    }

    public EMVCallback setArqcData(String arqcData) {
        this.arqcData = arqcData;
        return this;
    }

    public String getReversalData() {
        return reversalData;
    }

    public EMVCallback setReversalData(String reversalData) {
        this.reversalData = reversalData;
        return this;
    }

    public CardInformation getCardInformation() {
        return cardInformation;
    }

    public EMVCallback setCardInformation(CardInformation cardInformation) {
        this.cardInformation = cardInformation;
        return this;
    }

    public CallbackType getCallbackType() {
        return callbackType;
    }

    public EMVCallback setCallbackType(CallbackType callbackType) {
        this.callbackType = callbackType;
        return this;
    }

    public Boolean getOnlinePIN() {
        return isOnlinePIN;
    }

    public EMVCallback setOnlinePIN(Boolean onlinePIN) {
        this.isOnlinePIN = onlinePIN;
        return this;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public EMVCallback setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public static EMVCallback newInstance(CallbackType callbackType) {
        return new EMVCallback().setCallbackType(callbackType);
    }

    public static EMVCallback newInstance(CallbackType callbackType, int retryTimes) {
        return new EMVCallback().setCallbackType(callbackType).setRetryTimes(retryTimes);
    }

    public List<String> getAppList() {
        return appList;
    }

    public void setAppList(List<String> appList) {
        this.appList = appList;
    }

    public int getBinRouteStatusCallBack() {
        return binRouteStatusCallBack;
    }

    public void setBinRouteStatusCallBack(int binRouteStatusCallBack) {
        this.binRouteStatusCallBack = binRouteStatusCallBack;
    }

    public boolean isNeedSignature() {
        return isNeedSignature;
    }

    public void setNeedSignature(boolean needSignature) {
        isNeedSignature = needSignature;
    }

    public int getContactlessCvmResult() {
        return contactlessCvmResult;
    }

    public void setContactlessCvmResult(int contactlessCvmResult) {
        this.contactlessCvmResult = contactlessCvmResult;
    }
}
