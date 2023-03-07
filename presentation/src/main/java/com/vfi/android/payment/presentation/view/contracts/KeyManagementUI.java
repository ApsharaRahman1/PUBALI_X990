package com.vfi.android.payment.presentation.view.contracts;


import com.vfi.android.payment.presentation.utils.DialogUtil;

public interface KeyManagementUI extends SettingItemUI {
    void showInputPinDialog(DialogUtil.InputDialogListener listener);
}
