package com.vfi.android.domain.interactor.print.base;

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.databeans.PrintInfo;

public class BaseTransPrintTask extends AbstractTransPrintTask {
    public BaseTransPrintTask(RecordInfo recordInfo, PrintInfo printInfo) {
        super(recordInfo, printInfo);
    }

    @Override
    protected void addPrintContent() {
        addLogoImage();
        addOneBlackLine();
        addHeader();
        if (getPrintInfo().isDuplicateSlip()) {
            addDuplicateLabel();
        }
        addOneBlackLine();
        addDateTime();
        addTerminalIdMerchantId();
        addBatchInvoice();

        addTransTypeLine();

        addCardNum();
        addCardType();
        addAID();
        addApplicationLabel();
        addApprovalCode();
        addRRN();
        addTC();
        addOneBlackLine();
        addAmountLine();
        addTotalAvailablePTS();
        addOneBlackLine();
        addCVMResultLine();
        addCardHolderNameLine();
        addOneBlackLine();
        addDisclaimer();
        addSlipCopy();
        addFooter();
        addOneBlackLine();
        addOneBlackLine();
        addOneBlackLine();
    }
}
//apshara