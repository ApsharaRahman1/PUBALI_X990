package com.vfi.android.payment.presentation.view.activities.setting;

import android.app.AlertDialog;

import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.setting.CommunicationPresenter;
import com.vfi.android.payment.presentation.view.activities.base.BaseSettingItemAcitivity;
import com.vfi.android.payment.presentation.view.contracts.CommunicationUI;
import com.vfi.android.payment.presentation.models.SettingItemViewModel;

import javax.inject.Inject;

public class CommunicationSettingActivity extends BaseSettingItemAcitivity<CommunicationUI> implements CommunicationUI {
    @Inject
    CommunicationPresenter communicationPresenter;
    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(communicationPresenter,this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void showHostOperationItemList(String hostName, SettingItemViewModel itemViewModel) {

        int itemID = R.array.host_manage_dialog_items;
        new AlertDialog.Builder(this)
                .setTitle(hostName)
                .setItems(itemID, (dialog, which) -> {
                    switch (which){
                        case 0:
                            communicationPresenter.showHostDetail(itemViewModel);
                            break;
                        case 1:
                            communicationPresenter.testHost();
                            break;
                        case 2:
                            communicationPresenter.loadTMK();
                            break;
                        case 3:
                            communicationPresenter.loadTWK();
                            break;
                    }
                }).show();
    }
}
