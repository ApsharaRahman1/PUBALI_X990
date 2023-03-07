package com.vfi.android.data.database.mdt;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.vfi.android.data.database.DBFlowDatabase;

/**
 * Created by RuihaoS on 2019/8/13.
 */
@Table(database = DBFlowDatabase.class)
public class DBModelMerchantData extends BaseModel {
    /**
     * Index of merchant
     */
    @PrimaryKey
    @Column
    public int merchantIndex;

    /**
     * Merchant Name assigned by acquirer
     */
    @Column
    public String merchantName;

    /**
     * Terminal id assigned by acquirer
     */
    @Column
    public String terminalId;

    /**
     * Merchant id assigned by acquiror
     */
    @Column
    public String merchantId;

    /**
     * batch number, range 1 - 999999
     */
    @Column
    private String batchNum;

    /**
     * trace number, range 1 - 999999
     */
    @Column
    private String traceNum;

    /**
     * Index of CST
     */
    @Column
    public int currencyIndex;

    /**
     * Enable/Disable multi currency
     */
    @Column
    public boolean isEnableMultiCurrency;

    /**
     *
     */
    @Column
    public int printParamIndex;

    /**
     * used also to control the maximum amount accepted by the application. Currently set to 8, to avoid amount that are in millions
     */
    @Column
    private int amountDigits;

    /**
     * Minimum Amt, Any value that is out of range is considered an invalid amount.
     * Default 100 means amount 1.00
     */
    @Column
    private long minAmount;

    /**
     * Maximum Amt, Any value that is out of range is considered an invalid amount.
     * Default 50000000 means amount 500000.00
     */
    @Column
    private long maxAmount;

    /**
     * Enable/Disable DBModelTLE
     */
    @Column
    public boolean isEnableTle;

    /**
     * Index of DBModelTLE
     */
    @Column
    public int tleIndex;


    public int getMerchantIndex() {
        return merchantIndex;
    }

    public void setMerchantIndex(int merchantIndex) {
        this.merchantIndex = merchantIndex;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
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

    public boolean isEnableTle() {
        return isEnableTle;
    }

    public void setEnableTle(boolean enableTle) {
        isEnableTle = enableTle;
    }

    public int getTleIndex() {
        return tleIndex;
    }

    public void setTleIndex(int tleIndex) {
        this.tleIndex = tleIndex;
    }
}
