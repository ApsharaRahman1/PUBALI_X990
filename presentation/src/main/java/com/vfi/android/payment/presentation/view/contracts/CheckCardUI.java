package com.vfi.android.payment.presentation.view.contracts;


import com.vfi.android.payment.presentation.view.contracts.base.UI;

import java.util.List;

public interface CheckCardUI extends UI {
    void showAmount(String amount);
    void hideAmount();
    void displayCheckCardIcon(boolean isSupportTap, boolean isSupportInsert, boolean isSupportSwipe);
    void showTransNotAllowDialog();
    void showCardNotSupportDialog();
    void showFallbackRemoveCardDialog(String msg);
    void dismissFallbackRemoveCardDialog();
    void enableManualInputBtn(boolean isEnable);
    void showInputLast4CardNumDialog();
}
