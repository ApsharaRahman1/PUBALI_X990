package com.vfi.android.payment.presentation.view.contracts;

import com.vfi.android.payment.presentation.view.contracts.base.UI;

public interface InputTipAmountUI extends UI {
    void showRecommendedTipAmount(String tipAmount);
    void clearInputText();
    void showCurrencyHint(String currency);
    void showOldTipAmount(String oldTipAmount);
}
