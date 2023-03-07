package com.vfi.android.payment.presentation.view.contracts;


import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.view.contracts.base.UI;

import java.util.List;

public interface EmvProcessUI extends UI {
    void showSelectAppItems(List<String> appItems);
    void showTransNotAllowDialog();
    void showFallbackRemoveCardDialog(String msg);
    void dismissFallbackRemoveCardDialog();
    void showCardNotSupportDialog();
    void showCardConfirmDialog(String pan, String expireDate, DialogUtil.AskDialogListener listener);
    void showAskBackToCheckCardDialog(String msg);
}
