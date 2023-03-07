package com.vfi.android.payment.presentation.view.activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.ImageView;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.EmvProcessPresenter;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TvShowUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.contracts.EmvProcessUI;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmvProcessActivity extends BaseTransFlowActivity<EmvProcessUI> implements EmvProcessUI {
    private final static String TAG = TAGS.EmvFlow;
    @BindView(R.id.imageview_hourglass)
    ImageView imageview_hourglass;
    ObjectAnimator hourglassRotation;

    @Inject
    EmvProcessPresenter emvProcessPresenter;

    private Dialog customDialog = null;
    private Dialog selectDialog = null;
    private Dialog confirmCardDialog = null;

    private boolean isNeedResumeSelectDialog;
    private List<String> tmpAppItems;
    private List<String> tmpHostItems;
    private List<String> tmpMerchantItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emv_process);
        ButterKnife.bind(this);
        setBackGround(R.drawable.background_dark);
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(emvProcessPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        hourglassRotation = ObjectAnimator.ofFloat(imageview_hourglass, "rotation", 0, 360).setDuration(1200);
        hourglassRotation.setInterpolator(input -> input > 2f / 3 ? 1 : 1.5f * input);
        hourglassRotation.setRepeatCount(ValueAnimator.INFINITE);
        hourglassRotation.setRepeatMode(ValueAnimator.RESTART);
        hourglassRotation.start();

        if (isNeedResumeSelectDialog) {
            showSelectAppItems(tmpAppItems);
        }
    }

    @Override
    public void onStop() {
        hourglassRotation.cancel();

        if (selectDialog != null && selectDialog.isShowing()) {
            selectDialog.dismiss();
        }

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        // Disable return key
    }

    @Override
    public void showSelectAppItems(List<String> appItems) {
        String[] showItems = TvShowUtil.toStringArray(appItems);
        isNeedResumeSelectDialog = true;
        tmpAppItems = appItems;
        selectDialog = DialogUtil.showSelectItemDialog(this, getString(R.string.select_card_app), showItems, new DialogUtil.SelectDialogListener() {
            @Override
            public void onSelect(int index) {
                isNeedResumeSelectDialog = false;
                tmpAppItems = null;
                emvProcessPresenter.selectAppIdex(index);
            }
        });
    }

    @Override
    public void showTransNotAllowDialog() {
        DialogUtil.showWarnDialog(this, ResUtil.getString(R.string.tv_hint_trans_not_allowed), new DialogUtil.WarnDialogListener() {
            @Override
            public void onClick() {
                getUiNavigator().getUiFlowControlData().setGoBackToMainMenu(true);
                getUiNavigator().getUiFlowControlData().setNotNeedDialogConfirmUIBack(true);
                navigatorToNextStep();
            }
        });
    }

    @Override
    public void showFallbackRemoveCardDialog(String msg) {
        customDialog = DialogUtil.showWarnDialog(this, msg, new DialogUtil.WarnDialogListener() {
            @Override
            public void onClick() {
                emvProcessPresenter.backToCheckCard();
            }
        });
    }

    @Override
    public void dismissFallbackRemoveCardDialog() {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
    }

    @Override
    public void showCardNotSupportDialog() {

    }

    @Override
    public void showCardConfirmDialog(String pan, String expireDate, DialogUtil.AskDialogListener listener) {
        confirmCardDialog = DialogUtil.showCardConfirmDialog(this, pan, expireDate, isSure -> {
            if (listener != null) {
                listener.onClick(isSure);
            }
        });
    }

    @Override
    public void showAskBackToCheckCardDialog(String msg) {
        DialogUtil.showAskDialog(this,msg, isSure -> {
            LogUtil.i(TAG,"OnClick : " + isSure);
            if (isSure) {
                emvProcessPresenter.backToCheckCard();
            } else {
                emvProcessPresenter.abortTrans();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (selectDialog != null && selectDialog.isShowing()) {
            selectDialog.dismiss();
        }
        if (confirmCardDialog != null && confirmCardDialog.isShowing()) {
            confirmCardDialog.dismiss();
        }
        dismissFallbackRemoveCardDialog();
        super.onDestroy();
    }
}
