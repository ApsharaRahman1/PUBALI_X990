package com.vfi.android.payment.presentation.view.contracts;

public interface ReprintUI extends SettingItemUI {
    void showPrintFailedDialog(String errMsg);
    void showHistory();
}
