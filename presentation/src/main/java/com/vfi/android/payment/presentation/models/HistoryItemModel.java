package com.vfi.android.payment.presentation.models;

public class HistoryItemModel {
    private String totalAmountText;
    private String currencyText;
    private String transTypeText;
    private String transDateTimeText;
    private String invoiceNum;
    private String traceNum;

    // VOID Reversal set to red color.
    private boolean isNeedRedColor;

    public HistoryItemModel(String totalAmountText, String currencyText, String transTypeText, String transDateTimeText, String invoiceNum, String traceNum) {
        this.totalAmountText = totalAmountText;
        this.currencyText = currencyText;
        this.transTypeText = transTypeText;
        this.transDateTimeText = transDateTimeText;
        this.invoiceNum = invoiceNum;
        this.traceNum = traceNum;
    }

    public String getTotalAmountText() {
        return totalAmountText;
    }

    public void setTotalAmountText(String totalAmountText) {
        this.totalAmountText = totalAmountText;
    }

    public String getCurrencyText() {
        return currencyText;
    }

    public void setCurrencyText(String currencyText) {
        this.currencyText = currencyText;
    }

    public String getTransDateTimeText() {
        return transDateTimeText;
    }

    public void setTransDateTimeText(String transDateTimeText) {
        this.transDateTimeText = transDateTimeText;
    }

    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public String getTraceNum() {
        return traceNum;
    }

    public void setTraceNum(String traceNum) {
        this.traceNum = traceNum;
    }

    public String getTransTypeText() {
        return transTypeText;
    }

    public void setTransTypeText(String transTypeText) {
        this.transTypeText = transTypeText;
    }

    public boolean isNeedRedColor() {
        return isNeedRedColor;
    }

    public void setNeedRedColor(boolean needRedColor) {
        isNeedRedColor = needRedColor;
    }
}
