package com.vfi.android.payment.presentation.view.contracts;

import com.vfi.android.payment.presentation.view.contracts.base.UI;

public interface InputAmountUI extends UI {
    void setMaxAmountDigit(int digit);
    void clearAmountText();
}
