package com.vfi.android.payment.presentation.view.contracts;

import com.vfi.android.payment.presentation.view.contracts.base.UI;
import com.vfi.android.payment.presentation.models.SettingItemViewModel;

import java.util.List;

public interface SettingItemUI extends UI {
    void showSettingItems(List<SettingItemViewModel> viewModels);

    void showSettingTitle(String title);

    void finishUI();

    void cancelSettingUITimer();

    void startSettingUITimer();
}
