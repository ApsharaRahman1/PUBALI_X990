package com.vfi.android.payment.presentation.view.activities;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.InputOrgAuthCodePresenter;
import com.vfi.android.payment.presentation.utils.KeyboardUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.contracts.InputOrgAuthCodeUI;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InputOrgAuthCodeActivity extends BaseTransFlowActivity<InputOrgAuthCodeUI> implements InputOrgAuthCodeUI {
    @BindView(R.id.keyboardview_digital)
    KeyboardView keyboardview_digital;
    @BindView(R.id.btn_keyboard_confirm)
    Button btn_keyboard_confirm;
    @BindView(R.id.tv_show_original_auth_code)
    TextView tv_show_original_auth_code;
    @BindView(R.id.tv_hint)
    TextView tv_hint;
    @BindView(R.id.view_line)
    View line;
    @Inject
    InputOrgAuthCodePresenter inputOrgAuthCodePresenter;

    private Keyboard keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_org_auth_code);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        keyboard = new Keyboard(this, R.xml.keyboard1);
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_digital, keyboard, tv_show_original_auth_code, KeyboardUtil.TYPE_APPROVAL_CODE);
        btn_keyboard_confirm.setOnClickListener(v -> {
            inputOrgAuthCodePresenter.submitOrgAuthCode(tv_show_original_auth_code.getText().toString());
        });
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(inputOrgAuthCodePresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void clearInputText() {
        tv_show_original_auth_code.setText("");
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_digital, keyboard, tv_show_original_auth_code, KeyboardUtil.TYPE_APPROVAL_CODE);
    }
}
