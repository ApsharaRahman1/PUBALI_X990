package com.vfi.android.payment.presentation.view.activities;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.InputCardExpiryDatePresenter;
import com.vfi.android.payment.presentation.utils.KeyboardUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.contracts.InputCardExpiryDateUI;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InputCardExpiryDateActivity extends BaseTransFlowActivity<InputCardExpiryDateUI> implements InputCardExpiryDateUI {
    @BindView(R.id.keyboardview_digital)
    KeyboardView keyboardview_digital;
    @BindView(R.id.btn_keyboard_confirm)
    Button btn_keyboard_confirm;
    @BindView(R.id.tv_show_card_expiry_date)
    TextView tv_show_card_expiry_date;

    @Inject
    InputCardExpiryDatePresenter inputCardExpiryDatePresenter;

    private Keyboard keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_card_expiry_date);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        keyboard = new Keyboard(this, R.xml.keyboard1);
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_digital, keyboard, tv_show_card_expiry_date, KeyboardUtil.TYPE_CARD_EXPIRY_DATE);
        btn_keyboard_confirm.setOnClickListener(v -> {
            inputCardExpiryDatePresenter.submitCardExpiryDate(tv_show_card_expiry_date.getText().toString());
        });
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(inputCardExpiryDatePresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void clearInputText() {
        tv_show_card_expiry_date.setText("00/00");
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_digital, keyboard, tv_show_card_expiry_date, KeyboardUtil.TYPE_CARD_EXPIRY_DATE);
    }
}
