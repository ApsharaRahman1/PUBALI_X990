package com.vfi.android.payment.presentation.view.activities;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.InputTipAmountPresenter;
import com.vfi.android.payment.presentation.utils.KeyboardUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.contracts.InputTipAmountUI;
import com.vfi.android.payment.presentation.view.widget.DigitalKeyboardView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InputTipAmountActivity extends BaseTransFlowActivity<InputTipAmountUI> implements InputTipAmountUI {

    @BindView(R.id.tv_show_tip_amount)
    TextView tv_show_tip_amount;

    @BindView(R.id.tv_old_tip_amount)
    TextView tv_show_old_tip_amount;

    @BindView(R.id.tv_hint_msg_input_amount)
    TextView tv_show_msg_input_amount;

    @BindView(R.id.keyboardview_digital)
    DigitalKeyboardView keyboardview_inputamount;

    @BindView(R.id.btn_keyboard_confirm)
    Button btn_confirm_amount;

    @Inject
    InputTipAmountPresenter inputTipAmountPresenter;
    private Keyboard keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_tip_amount);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(inputTipAmountPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    private void initView() {
        setBackGround(R.drawable.background_keyboard);
        keyboard = new Keyboard(this, R.xml.keyboard1);
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_inputamount, keyboard, tv_show_tip_amount, KeyboardUtil.TYPE_PRICE);

        btn_confirm_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputTipAmountPresenter.saveTipAmount(tv_show_tip_amount.getText().toString().trim());
            }
        });
    }

    @Override
    public void showRecommendedTipAmount(String tipAmount) {
        tv_show_tip_amount.setText(tipAmount);
    }

    @Override
    public void clearInputText() {
        tv_show_tip_amount.setText("0.00");
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_inputamount, keyboard, tv_show_tip_amount, KeyboardUtil.TYPE_PRICE);
    }

    @Override
    public void showCurrencyHint(String currency) {
//        tv_show_msg_input_amount.setText(currency);
    }

    @Override
    public void showOldTipAmount(String oldTipAmount) {
        tv_show_old_tip_amount.setVisibility(View.VISIBLE);
        tv_show_old_tip_amount.setText(oldTipAmount);
    }
}
