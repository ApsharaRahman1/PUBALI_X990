package com.vfi.android.payment.presentation.view.activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.NetworkProcessPresenter;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.contracts.NetworkProcessUI;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NetworkProcessActivity extends BaseTransFlowActivity<NetworkProcessUI> implements NetworkProcessUI {

    @BindView(R.id.tv_progress_msg)
    TextView tv_progress_msg;
    @BindView(R.id.imageview_hourglass)
    ImageView imageview_hourglass;

    ObjectAnimator hourglassRotation;

    @Inject
    NetworkProcessPresenter networkProcessPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_process);
        ButterKnife.bind(this);
        setBackGround(R.drawable.background_dark);
    }

    @Override
    protected void onResume() {
        super.onResume();

        hourglassRotation = ObjectAnimator.ofFloat(imageview_hourglass, "rotation", 0, 360).setDuration(1200);
        hourglassRotation.setInterpolator(input -> input > 2f / 3 ? 1 : 1.5f * input);
        hourglassRotation.setRepeatCount(ValueAnimator.INFINITE);
        hourglassRotation.setRepeatMode(ValueAnimator.RESTART);
        hourglassRotation.start();
    }

    @Override
    protected void onStop() {
        hourglassRotation.cancel();
        super.onStop();
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(networkProcessPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void onBackPressed() {
        // Disable return key
    }

    @Override
    public void setProcessHint(String hintMsg) {
        tv_progress_msg.setText("" + hintMsg);
    }

    @Override
    public void showReversalFailedDialog(String msg) {
        DialogUtil.showAskDialog(this, msg, new DialogUtil.AskDialogListener() {
            @Override
            public void onClick(boolean isNeedDoReversalAgain) {
                networkProcessPresenter.doReversalAgain(isNeedDoReversalAgain);
            }
        });
    }
}
