package com.vfi.android.payment.presentation.view.contracts;


import com.vfi.android.payment.presentation.view.contracts.base.UI;

public interface InputPinUI extends UI {
    void showTransInfo(String acount, String amount);
    void showPassword(int len);
    void showPinHint(String pinHint);
}
