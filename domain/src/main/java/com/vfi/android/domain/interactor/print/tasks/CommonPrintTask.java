package com.vfi.android.domain.interactor.print.tasks;

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.databeans.PrintInfo;
import com.vfi.android.domain.interactor.print.base.BaseTransPrintTask;

public class CommonPrintTask extends BaseTransPrintTask {
    public CommonPrintTask(RecordInfo recordInfo, PrintInfo printInfo) {
        super(recordInfo, printInfo);
    }
}
