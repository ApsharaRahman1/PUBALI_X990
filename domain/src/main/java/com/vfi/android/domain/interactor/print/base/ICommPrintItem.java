package com.vfi.android.domain.interactor.print.base;

public interface ICommPrintItem {
    void addLogoImage();
    void addHeader();
    void addDuplicateLabel();
    void addDateTime();
    void addTerminalIdMerchantId();
    void addBatchInvoice();
    void addRRN();
    void addTransTypeLine();
    void addCardNum();
    void addCardType();
    void addApprovalCode();
    void addAID();
    void addApplicationLabel();
    void addTC();
    void addAmountLine();
    void addTotalAvailablePTS();
    void addCVMResultLine();
    void addCardHolderNameLine();
    void addDisclaimer();
    void addSlipCopy();
    void addFooter();
}
