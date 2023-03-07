package com.vfi.android.payment.presentation.view.contracts;


import com.vfi.android.payment.presentation.view.contracts.base.UI;

public interface TransFailedUI extends UI {
    void showErrorMessage(String errorMsg);
}
