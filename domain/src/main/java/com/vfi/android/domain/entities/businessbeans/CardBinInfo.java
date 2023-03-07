package com.vfi.android.domain.entities.businessbeans;

/**
 * Card Data Table
 *
 * @author Verifone
 */
public class CardBinInfo {

    /**
     * index
     */
    private int cardIndex;

    /**
     * Link to hosts, if multi host config like this:  1,2,4
     */
    private String hostIndexs;

    /**
     * {@link com.vfi.android.domain.entities.consts.CardType}
     */
    private int cardType;

    /**
     * {@link com.vfi.android.domain.entities.consts.CVV2ControlType}
     * 0: no need
     * 1: force
     * 2: can bypass
     */
    private int cvv2Control;

    /**
     * Minimum card  range
     */
    private String panLow;

    /**
     * Maximum card range
     */
    private String panHigh;

    /**
     * Card label <br>
     * Can be modified by customer
     */
    private String cardLabel;

    /**
     *Card name
     */
    private String cardName;

    /**
     *ISSUE ID
     */
    private String issueId;

    /**
     *Allow/Not allow installment
     */
    private boolean isAllowInstallment;

    /**
     *Allow/Not allow offline sale
     */
    private boolean isAllowOfflineSale;

    /**
     *Allow/Not allow refund
     */
    private boolean isAllowRefund;

    /**
     *Enable/Disable check card number with luhn
     */
    private boolean isEnableCheckLuhn;

    /**
     *Enable/Disable multi currency
     */
    private boolean isEnableMultiCurrency;

    /**
     * Min size of CVV2
     */
    private int cvv2Min;

    /**
     * Max size of CVV2
     */
    private int cvv2Max;

    /**
     * used to set the threshold amount of transactions that do not require signature.
     * Default set to 200000 (equivalent to PHP2,000.00)
     */
    private long signLimitAmount;

    /**
     * used to set the threshold amount of transactions that do not require print.
     * Default set to 0
     * Value 100 equivalent to PHP 1.00
     */
    private long printLimitAmount;

    /**
     * Tip %nnn – this is used to set the percentage tip limit.
     * valid value is between 100 - 10000, It just help show advice tip amount.
     * For example: If enter amount 100.00 and set tipPercent to 1000,
     * When in tip interface, it will show tip amount 10.00, but you can change it.
     */
    private int tipPercent;

    /**
     * used to control card pin bypass function. default false.
     */
    private boolean isAllowPinBypass;

    public int getCardIndex() {
        return cardIndex;
    }

    public void setCardIndex(int cardIndex) {
        this.cardIndex = cardIndex;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public int getCvv2Control() {
        return cvv2Control;
    }

    public void setCvv2Control(int cvv2Control) {
        this.cvv2Control = cvv2Control;
    }

    public String getPanLow() {
        return panLow;
    }

    public void setPanLow(String panLow) {
        this.panLow = panLow;
    }

    public String getPanHigh() {
        return panHigh;
    }

    public void setPanHigh(String panHigh) {
        this.panHigh = panHigh;
    }

    public String getCardLabel() {
        return cardLabel;
    }

    public void setCardLabel(String cardLabel) {
        this.cardLabel = cardLabel;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public boolean isAllowInstallment() {
        return isAllowInstallment;
    }

    public void setAllowInstallment(boolean allowInstallment) {
        isAllowInstallment = allowInstallment;
    }

    public boolean isAllowOfflineSale() {
        return isAllowOfflineSale;
    }

    public void setAllowOfflineSale(boolean allowOfflineSale) {
        isAllowOfflineSale = allowOfflineSale;
    }

    public boolean isAllowRefund() {
        return isAllowRefund;
    }

    public void setAllowRefund(boolean allowRefund) {
        isAllowRefund = allowRefund;
    }

    public boolean isEnableCheckLuhn() {
        return isEnableCheckLuhn;
    }

    public void setEnableCheckLuhn(boolean enableCheckLuhn) {
        isEnableCheckLuhn = enableCheckLuhn;
    }

    public boolean isEnableMultiCurrency() {
        return isEnableMultiCurrency;
    }

    public void setEnableMultiCurrency(boolean enableMultiCurrency) {
        isEnableMultiCurrency = enableMultiCurrency;
    }

    public String getHostIndexs() {
        return hostIndexs;
    }

    public void setHostIndexs(String hostIndexs) {
        this.hostIndexs = hostIndexs;
    }

    @Override
    public String toString() {
        return "CardBinInfo{" +
                "cardIndex=" + cardIndex +
                ", hostIndexs='" + hostIndexs + '\'' +
                ", cardType=" + cardType +
                ", cvv2Control=" + cvv2Control +
                ", panLow='" + panLow + '\'' +
                ", panHigh='" + panHigh + '\'' +
                ", cardLabel='" + cardLabel + '\'' +
                ", cardName='" + cardName + '\'' +
                ", issueId='" + issueId + '\'' +
                ", isAllowInstallment=" + isAllowInstallment +
                ", isAllowOfflineSale=" + isAllowOfflineSale +
                ", isAllowRefund=" + isAllowRefund +
                ", isEnableCheckLuhn=" + isEnableCheckLuhn +
                ", isEnableMultiCurrency=" + isEnableMultiCurrency +
                '}';
    }

    public int getCvv2Min() {
        return cvv2Min;
    }

    public void setCvv2Min(int cvv2Min) {
        this.cvv2Min = cvv2Min;
    }

    public int getCvv2Max() {
        return cvv2Max;
    }

    public void setCvv2Max(int cvv2Max) {
        this.cvv2Max = cvv2Max;
    }

    public long getSignLimitAmount() {
        return signLimitAmount;
    }

    public void setSignLimitAmount(long signLimitAmount) {
        this.signLimitAmount = signLimitAmount;
    }

    public int getTipPercent() {
        return tipPercent;
    }

    public void setTipPercent(int tipPercent) {
        this.tipPercent = tipPercent;
    }

    public long getPrintLimitAmount() {
        return printLimitAmount;
    }

    public void setPrintLimitAmount(long printLimitAmount) {
        this.printLimitAmount = printLimitAmount;
    }

    public boolean isAllowPinBypass() {
        return isAllowPinBypass;
    }

    public void setAllowPinBypass(boolean allowPinBypass) {
        isAllowPinBypass = allowPinBypass;
    }
}
