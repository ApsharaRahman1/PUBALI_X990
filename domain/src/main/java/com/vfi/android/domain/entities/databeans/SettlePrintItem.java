package com.vfi.android.domain.entities.databeans;

public class SettlePrintItem {
    // help
    private int recordType;
    public static final int SALE                = 1; // Trans types: credit sale/offline sale
    public static final int VOID_SALE           = 2; // Trans types: void credit sale/ void offline
    public static final int PREAUTH_COMP        = 3; // .....
    public static final int VOID_PREAUTH_COMP   = 4; // .....

    private int cardType;
    private long totalAmountLong; // tip + currencyAmountText
    private int cardEntryMode;

    // print info
    private String cardTypeAbbr;// VS
    private String pan;         // **** **** **** 0232
    private String expireDate;  // xx/xx
    private String invoiceNum;  // 000001
    private String transaction; // SALE/VOID SALE ....
    private String currencyAmountText;      // PHP 11.11  Format(tip + currencyAmountText)
    private String approvalCode;// 123456
    private String dateTime;    // 29/08 09:31:08

    public int getRecordType() {
        return recordType;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public long getTotalAmountLong() {
        return totalAmountLong;
    }

    public void setTotalAmountLong(long totalAmountLong) {
        this.totalAmountLong = totalAmountLong;
    }

    public String getCardTypeAbbr() {
        return cardTypeAbbr;
    }

    public void setCardTypeAbbr(String cardTypeAbbr) {
        this.cardTypeAbbr = cardTypeAbbr;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getCurrencyAmountText() {
        return currencyAmountText;
    }

    public void setCurrencyAmountText(String currencyAmountText) {
        this.currencyAmountText = currencyAmountText;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getCardEntryMode() {
        return cardEntryMode;
    }

    public void setCardEntryMode(int cardEntryMode) {
        this.cardEntryMode = cardEntryMode;
    }
}
