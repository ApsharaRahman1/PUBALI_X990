package com.vfi.android.payment.presentation.view.contracts;


import com.vfi.android.payment.presentation.view.contracts.base.UI;

public interface CheckPasswdUI extends UI {
    void showPasswdBoxView(int maxPasswdLen);
    void resetPasswdBoxView();
}
