package com.vfi.android.payment.presentation.view.activities.option;

import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.option.ReprintPresenter;
import com.vfi.android.payment.presentation.utils.AndroidUtil;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseSettingItemAcitivity;
import com.vfi.android.payment.presentation.view.activities.history.HistoryActivity;
import com.vfi.android.payment.presentation.view.contracts.ReprintUI;

import javax.inject.Inject;

public class ReprintActivity extends BaseSettingItemAcitivity<ReprintUI> implements ReprintUI {
    @Inject
    ReprintPresenter reprintPresenter;

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(reprintPresenter,this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void showPrintFailedDialog(String errMsg) {
        String negativeButtonText = ResUtil.getString(R.string.btn_hint_cancel);
        String positiveButtonText = ResUtil.getString(R.string.btn_hint_reprint);
        DialogUtil.showAskDialog(this, errMsg, negativeButtonText, positiveButtonText, new DialogUtil.AskDialogListener() {
            @Override
            public void onClick(boolean isSure) {
                if (isSure) {
                    reprintPresenter.startPrintSlip(true);
                } else {
                    reprintPresenter.clearPrintBuffer();
                }
            }
        });
    }

    @Override
    public void showHistory() {
        finish();
        AndroidUtil.startActivity(this, HistoryActivity.class);
    }
}
