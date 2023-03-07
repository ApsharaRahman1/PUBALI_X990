package com.vfi.android.domain.entities.databeans;

import javax.inject.Inject;

public class HistoryConstraint {
    public final static int CONSTRAINT_ALL          = 0;
    public final static int CONSTRAINT_DATE         = 1;
    public final static int CONSTRAINT_INVOICE_NUM  = 2;
    public final static int CONSTRAINT_PAY_TYPE     = 3;

    private int constraintType = CONSTRAINT_ALL;
    private String date;
    private String invoiceNum;
    private int payType;
    public static final int PAYTYPE_CARD        = 0;
    public static final int PAYTYPE_CARD_VISA   = 1;
    public static final int PAYTYPE_CARD_MASTER = 2;
    public static final int PAYTYPE_CARD_JCB    = 3;
    public static final int PAYTYPE_CARD_CUP    = 4;
    public static final int PAYTYPE_QR          = 5;
    public static final int PAYTYPE_DCC         = 6;
    public static final int PAYTYPE_INSTALLMENT = 7;
    public static final int PAYTYPE_DEBIT       = 8;

    @Inject
    public HistoryConstraint() {
    }

    public void init() {
        constraintType = CONSTRAINT_ALL;
    }

    public void initByPayType(int payType) {
        constraintType = CONSTRAINT_PAY_TYPE;
        this.payType = payType;
    }

    public void initByInvoiceNum(String invoiceNum) {
        constraintType = CONSTRAINT_INVOICE_NUM;
        this.invoiceNum = invoiceNum;
    }

    public void initByDate(String date) {
        constraintType = CONSTRAINT_DATE;
        this.date = date;
    }

    public int getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(int constraintType) {
        this.constraintType = constraintType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    @Override
    public String toString() {
        return "HistoryConstraint{" +
                ", constraintType=" + constraintType +
                ", date='" + date + '\'' +
                ", invoiceNum='" + invoiceNum + '\'' +
                '}';
    }
}
