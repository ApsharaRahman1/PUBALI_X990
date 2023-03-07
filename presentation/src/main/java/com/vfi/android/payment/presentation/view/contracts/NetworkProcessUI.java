package com.vfi.android.payment.presentation.view.contracts;


import com.vfi.android.payment.presentation.view.contracts.base.UI;

public interface NetworkProcessUI extends UI {
    void setProcessHint(String hintMsg);
    void showReversalFailedDialog(String msg);
}
