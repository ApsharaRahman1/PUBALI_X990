package com.vfi.android.payment.presentation.view.contracts;

import com.vfi.android.payment.presentation.view.contracts.base.UI;

import java.util.List;

public interface ChooseInstallmentTermUI extends UI {
    void showSelectTermItems(List<String> termList);
}
