package com.vfi.android.payment.presentation.view.contracts;


import com.vfi.android.payment.presentation.view.contracts.base.UI;

public interface InputInvoiceNumUI extends UI {
    void showNotFoundWarnDialog(String msg);
    void clearInputText();
}
