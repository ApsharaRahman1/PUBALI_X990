package com.vfi.android.domain.entities.businessbeans;

import com.vfi.android.domain.entities.annotations.Param;

public class MerchantInfo {

    /**
     * index of MerchantInfo
     */
    private int merchantIndex;

    /**
     * Merchant Name
     */
    private String merchantName;

    /**
     * mid
     */
    private String merchantId;

    /**
     * tid
     */
    private String terminalId;

    /**
     * batch number, range 1 - 999999
     */
    private String batchNum;

    /**
     * trace number, range 1 - 999999
     */
    private String traceNum;

    /**
     * index of currency table
     */
    private int currencyIndex;

    /**
     * Enable/Disable multi currency
     */
    private boolean isEnableMultiCurrency;

    /**
     * Index of print param table
     */
    private int printParamIndex;

    /**
     * used also to control the maximum amount accepted by the application. Currently set to 8, to avoid amount that are in millions
     */
    @Param.Default(Integer = 8)
    private int amountDigits;

    /**
     * Minimum Amt, Any value that is out of range is considered an invalid amount.
     * Default 100 means amount 1.00
     */
    @Param.Default(Long = 100)
    private long minAmount;

    /**
     * Maximum Amt, Any value that is out of range is considered an invalid amount.
     * Default 50000000 means amount 500000.00
     */
    @Param.Default(Long = 50000000)
    private long maxAmount;

    /**
     * enable/disable terminal line encryption(or NMX).
     */
    private boolean isEnableTle;

    /**
     * terminal line encryption(or NMX) configuration table index.
     */
    private int tleIndex;

    public int getMerchantIndex() {
        return merchantIndex;
    }

    public void setMerchantIndex(int merchantIndex) {
        this.merchantIndex = merchantIndex;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public String getTraceNum() {
        return traceNum;
    }

    public void setTraceNum(String traceNum) {
        this.traceNum = traceNum;
    }

    public int getCurrencyIndex() {
        return currencyIndex;
    }

    public void setCurrencyIndex(int currencyIndex) {
        this.currencyIndex = currencyIndex;
    }

    public boolean isEnableMultiCurrency() {
        return isEnableMultiCurrency;
    }

    public void setEnableMultiCurrency(boolean enableMultiCurrency) {
        isEnableMultiCurrency = enableMultiCurrency;
    }

    public int getPrintParamIndex() {
        return printParamIndex;
    }

    public void setPrintParamIndex(int printParamIndex) {
        this.printParamIndex = printParamIndex;
    }

    public int getAmountDigits() {
        return amountDigits;
    }

    public void setAmountDigits(int amountDigits) {
        this.amountDigits = amountDigits;
    }

    public long getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(long minAmount) {
        this.minAmount = minAmount;
    }

    public long getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(long maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public int getTleIndex() {
        return tleIndex;
    }

    public void setTleIndex(int tleIndex) {
        this.tleIndex = tleIndex;
    }

    public boolean isEnableTle() {
        return isEnableTle;
    }

    public void setEnableTle(boolean enableTle) {
        this.isEnableTle = enableTle;
    }
}
