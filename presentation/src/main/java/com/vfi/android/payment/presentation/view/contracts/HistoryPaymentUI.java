package com.vfi.android.payment.presentation.view.contracts;


import com.vfi.android.payment.presentation.view.contracts.base.UI;

public interface HistoryPaymentUI extends UI {
    void showLastTransStatus(String rspCode, String transInfo, String rspIsoDescription, String rspTerminalDisplayText);
}
