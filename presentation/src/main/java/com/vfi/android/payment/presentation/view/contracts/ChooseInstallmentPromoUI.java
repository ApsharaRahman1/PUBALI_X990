package com.vfi.android.payment.presentation.view.contracts;

import com.vfi.android.payment.presentation.view.contracts.base.UI;

import java.util.List;

public interface ChooseInstallmentPromoUI extends UI {
    void showSelectPromos(List<String> promoList);
}
