package com.vfi.android.payment.presentation.view.contracts;

import com.vfi.android.payment.presentation.models.HistoryItemModel;
import com.vfi.android.payment.presentation.view.contracts.base.UI;

import java.util.List;

public interface HistoryUI extends UI {
    void showRecordInfoList(int opType, List<HistoryItemModel> historyItemModels);
    void showPrintFailedDialog(String errMsg, boolean isSummaryReport);
}