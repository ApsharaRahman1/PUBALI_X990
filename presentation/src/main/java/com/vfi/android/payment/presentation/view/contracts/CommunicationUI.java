package com.vfi.android.payment.presentation.view.contracts;

import com.vfi.android.payment.presentation.models.SettingItemViewModel;

public interface CommunicationUI extends SettingItemUI{
    void showHostOperationItemList(String hostName, SettingItemViewModel itemViewModel);
}
