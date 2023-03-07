package com.vfi.android.payment.presentation.view.contracts;


import com.vfi.android.payment.presentation.models.SettlementResultModel;
import com.vfi.android.payment.presentation.view.contracts.base.UI;

import java.util.List;

public interface TransSuccessUI extends UI {
    void showAmount(String amount, String currencySymbol);
    void showPrintFailedDialog(String errMsg, boolean isNeedBacktoMainMenu);
    void showButtonText(String buttonText);
    void showSettlementResultList(List<SettlementResultModel> settlementResultModelList);
    void showIPPView(String interest, String paymentTerm);
    void showRedeemInfo(boolean isShowAmountLayout, boolean isShowPointTitle, String amount, String point);
    void hideAmount();
    void showSettlementResultIcon(boolean isAllSettleSuccess);
}
