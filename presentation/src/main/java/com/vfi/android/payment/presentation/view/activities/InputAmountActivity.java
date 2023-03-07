package com.vfi.android.payment.presentation.view.activities;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.Button;
import android.widget.TextView;

import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.InputAmountPresenter;
import com.vfi.android.payment.presentation.utils.KeyboardUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.contracts.InputAmountUI;
import com.vfi.android.payment.presentation.view.widget.DigitalKeyboardView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InputAmountActivity extends BaseTransFlowActivity<InputAmountUI> implements InputAmountUI {
    @BindView(R.id.tv_show_amount)
    TextView tv_show_amount;

    @BindView(R.id.keyboardview_digital)
    DigitalKeyboardView keyboardview_inputamount;

    @BindView(R.id.btn_keyboard_confirm)
    Button btn_confirm_amount;

    @Inject
    InputAmountPresenter inputAmountPresenter;

    private Keyboard keyboard;
    private int maxAmountDigits = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_amount);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setBackGround(R.drawable.background_keyboard);
        keyboard = new Keyboard(this, R.xml.keyboard1);
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_inputamount, keyboard, tv_show_amount, KeyboardUtil.TYPE_PRICE, maxAmountDigits);

        btn_confirm_amount.setOnClickListener(v -> inputAmountPresenter.saveAmount(tv_show_amount.getText().toString().trim()));
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(inputAmountPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void setMaxAmountDigit(int digit) {
        this.maxAmountDigits = digit;
        tv_show_amount.setText("0.00");
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_inputamount, keyboard, tv_show_amount, KeyboardUtil.TYPE_PRICE, maxAmountDigits);
    }

    @Override
    public void clearAmountText() {
        tv_show_amount.setText("0.00");
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_inputamount, keyboard, tv_show_amount, KeyboardUtil.TYPE_PRICE, maxAmountDigits);
    }
}
