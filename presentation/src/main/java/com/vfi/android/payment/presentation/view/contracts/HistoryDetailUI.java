package com.vfi.android.payment.presentation.view.contracts;


import com.vfi.android.payment.presentation.view.contracts.base.UI;

public interface HistoryDetailUI extends UI {
    void setViewVisibility(int resId, int visibility);
    void setViewText(int resId, String text);
    void showPrintException(String msg);
    void navigatorToHistoryActivity();
}
