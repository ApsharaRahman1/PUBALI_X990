package com.vfi.android.domain.entities.databeans;


/**
 * Created by yunlongg1 on 16/10/2017.
 */

public class EMVParamIn {

    private int emvProcesstype;
    public static final int PROCESS_TYPE_EMV_FLOW = 1;
    public static final int PROCESS_TYPE_SIMPLE_FLOW = 2;

    private int timeout;

    private int inputMode;

    private long authAmount;

    private boolean isSupportQpboc;

    private boolean isSupportSM;

    private boolean isForceOnline;

    private String merchantName;

    private String merchantId;

    private String terminalId;

    public int getEmvProcesstype() {
        return emvProcesstype;
    }

    public EMVParamIn setEmvProcesstype(int emvProcesstype) {
        this.emvProcesstype = emvProcesstype;
        return this;
    }

    public int getSlotType() {
        return inputMode;
    }

    public EMVParamIn setSlotType(int inputMode) {
        this.inputMode = inputMode;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public EMVParamIn setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public long getAuthAmount() {
        return authAmount;
    }

    public EMVParamIn setAuthAmount(long authAmount) {
        this.authAmount = authAmount;
        return this;
    }

    public boolean isSupportQpboc() {
        return isSupportQpboc;
    }

    public EMVParamIn setSupportQpboc(boolean supportQpboc) {
        isSupportQpboc = supportQpboc;
        return this;
    }

    public boolean isSupportSM() {
        return isSupportSM;
    }

    public EMVParamIn setSupportSM(boolean supportSM) {
        isSupportSM = supportSM;
        return this;
    }

    public boolean isForceOnline() {
        return isForceOnline;
    }

    public EMVParamIn setForceOnline(boolean forceOnline) {
        isForceOnline = forceOnline;
        return this;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public EMVParamIn setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public EMVParamIn setMerchantId(String merchantId) {
        this.merchantId = merchantId;
        return this;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public EMVParamIn setTerminalId(String terminalId) {
        this.terminalId = terminalId;
        return this;
    }
}
