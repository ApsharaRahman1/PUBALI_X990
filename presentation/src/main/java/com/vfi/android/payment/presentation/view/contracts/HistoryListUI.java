package com.vfi.android.payment.presentation.view.contracts;

import com.vfi.android.payment.presentation.models.HistoryItemModel;
import com.vfi.android.payment.presentation.view.contracts.base.UI;

import java.util.List;

public interface HistoryListUI extends UI {
    void resetRecordInfoList(List<HistoryItemModel> historyItemModels);
    void addRecordInfoList(List<HistoryItemModel> historyItemModels);
    void refreshHistoryByType(int type);
    void refreshHistoryByInvoiceNum(String invoiceNo);
    void refreshHistoryByDate(String date);
    void showPrintFailedDialog(String errMsg, boolean isSummaryReport);
}
