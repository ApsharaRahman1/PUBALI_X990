package com.vfi.android.payment.presentation.view.contracts;


import com.vfi.android.payment.presentation.models.SettlementItemModel;
import com.vfi.android.payment.presentation.view.contracts.base.UI;

import java.util.List;

public interface SettlementUI extends UI {
    void showSettlementDetail(List<SettlementItemModel> settlementItemModels);
    void showTotalAmount(String totalAmount);
}
