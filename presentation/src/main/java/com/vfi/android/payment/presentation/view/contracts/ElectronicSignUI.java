package com.vfi.android.payment.presentation.view.contracts;

import com.vfi.android.payment.presentation.view.contracts.base.UI;

public interface ElectronicSignUI extends UI {
    void showTransInfo(String acount, String amount);
    void clearESignBoard();
}
