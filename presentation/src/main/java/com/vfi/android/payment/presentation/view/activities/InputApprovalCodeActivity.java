package com.vfi.android.payment.presentation.view.activities;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.InputApprovalCodePresenter;
import com.vfi.android.payment.presentation.utils.KeyboardUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.contracts.InputApprovalCodeUI;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InputApprovalCodeActivity extends BaseTransFlowActivity<InputApprovalCodeUI> implements InputApprovalCodeUI {
    @BindView(R.id.keyboardview_digital)
    KeyboardView keyboardview_full;
    @BindView(R.id.btn_keyboard_confirm)
    Button btn_keyboard_confirm;
    @BindView(R.id.tv_show_approval_code)
    TextView tv_show_approval_code;
    @BindView(R.id.tv_show)
    TextView tv_show;
    @BindView(R.id.view_line)
    View view_line;
    @Inject
    InputApprovalCodePresenter inputApprovalCodePresenter;
    private Keyboard keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_approval_code);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        keyboard = new Keyboard(this, R.xml.keyboard1);
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_full, keyboard, tv_show_approval_code, KeyboardUtil.TYPE_APPROVAL_CODE);
        btn_keyboard_confirm.setOnClickListener(v -> {
            inputApprovalCodePresenter.saveApprovalCode(tv_show_approval_code.getText().toString());
        });
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(inputApprovalCodePresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void clearInputText() {
        tv_show_approval_code.setText("");
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_full, keyboard, tv_show_approval_code, KeyboardUtil.TYPE_APPROVAL_CODE);
    }
}
