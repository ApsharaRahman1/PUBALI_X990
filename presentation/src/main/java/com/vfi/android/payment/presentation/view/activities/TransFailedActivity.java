package com.vfi.android.payment.presentation.view.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.TransFailedPresenter;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.contracts.TransFailedUI;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransFailedActivity extends BaseTransFlowActivity<TransFailedUI> implements TransFailedUI {

    @BindView(R.id.btn_back_to_main_menu)
    Button btn_back_to_main_menu;
    @BindView(R.id.tv_show_error_msg)
    TextView tv_show_error_msg;
    @BindView(R.id.tv_error)
    TextView tv_error;

    @Inject
    TransFailedPresenter transFailedPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_failed);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(transFailedPresenter, this);
    }

    private void initView() {
        setBackGround(R.drawable.background_dark);
        btn_back_to_main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigatorToNextStep();
            }
        });
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void showErrorMessage(String errorMsg) {
        tv_show_error_msg.setText(errorMsg);
    }
}
