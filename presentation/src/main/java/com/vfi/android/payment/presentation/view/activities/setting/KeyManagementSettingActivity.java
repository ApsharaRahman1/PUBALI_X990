package com.vfi.android.payment.presentation.view.activities.setting;

import android.app.Dialog;

import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.setting.KeyManagementPresenter;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseSettingItemAcitivity;
import com.vfi.android.payment.presentation.view.contracts.KeyManagementUI;

import javax.inject.Inject;

public class KeyManagementSettingActivity extends BaseSettingItemAcitivity<KeyManagementUI> implements KeyManagementUI {
    @Inject
    KeyManagementPresenter keyManagementPresenter;

    private Dialog inputPinDialog;

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(keyManagementPresenter,this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void showInputPinDialog(DialogUtil.InputDialogListener listener) {
        inputPinDialog = DialogUtil.showInputDialog(this, "Enter PIN", (inputStr, isConfirm) -> {
            listener.onInput(inputStr, isConfirm);
        });
    }

    @Override
    protected void onDestroy() {
        if (inputPinDialog != null && inputPinDialog.isShowing()) {
            inputPinDialog.dismiss();
        }
        super.onDestroy();
    }
}
