package com.vfi.android.payment.presentation.view.activities;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.InputCVV2Presenter;
import com.vfi.android.payment.presentation.utils.KeyboardUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.contracts.InputCVV2UI;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InputCVV2Activity extends BaseTransFlowActivity<InputCVV2UI> implements InputCVV2UI {
    @BindView(R.id.keyboardview_digital)
    KeyboardView keyboardview_digital;
    @BindView(R.id.btn_keyboard_confirm)
    Button btn_keyboard_confirm;
    @BindView(R.id.tv_show_cvv2)
    TextView tv_show_cvv2;

    @Inject
    InputCVV2Presenter inputCVV2Presenter;
    private Keyboard keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_cvv2);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(inputCVV2Presenter, this);

    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }


    private void initView() {
        keyboard = new Keyboard(this, R.xml.keyboard1);
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_digital, keyboard, tv_show_cvv2, KeyboardUtil.TYPE_CVV2);
        btn_keyboard_confirm.setOnClickListener(v -> {
            inputCVV2Presenter.submitCVV2(tv_show_cvv2.getText().toString());
        });
    }

    @Override
    public void clearInputText() {
        tv_show_cvv2.setText("");
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_digital, keyboard, tv_show_cvv2, KeyboardUtil.TYPE_CVV2);
    }
}
