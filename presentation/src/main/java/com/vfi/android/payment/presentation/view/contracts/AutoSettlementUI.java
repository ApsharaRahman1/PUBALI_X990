package com.vfi.android.payment.presentation.view.contracts;

import com.vfi.android.payment.presentation.models.SettlementResultModel;
import com.vfi.android.payment.presentation.view.contracts.base.UI;

import java.util.List;

public interface AutoSettlementUI extends UI {
    void setProcessHint(String hintMsg);
    void showReversalFailedDialog(String msg);
    void showAmount(String amount, String currencySymbol);
    void showPrintFailedDialog(String errMsg, boolean isNeedBacktoMainMenu);
    void showSettlementResultList(List<SettlementResultModel> settlementResultModelList);
    void backToMainMenu();
    void showTransResult(boolean isAllSettleSuccess);
}
