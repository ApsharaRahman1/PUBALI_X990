package com.vfi.android.payment.presentation.view.activities;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.InputOrgTransDatePresenter;
import com.vfi.android.payment.presentation.utils.KeyboardUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.contracts.InputOrgTransDateUI;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InputOrgTransDateActivity extends BaseTransFlowActivity<InputOrgTransDateUI> implements InputOrgTransDateUI {
    @BindView(R.id.keyboardview_digital)
    KeyboardView keyboardview_digital;
    @BindView(R.id.btn_keyboard_confirm)
    Button btn_keyboard_confirm;
    @BindView(R.id.tv_show_original_trans_date)
    TextView tv_show_original_trans_date;
    @BindView(R.id.tv_hint)
    TextView tv_hint;
    @BindView(R.id.view_line)
    View line;

    @Inject
    InputOrgTransDatePresenter inputOrgTransDatePresenter;

    private Keyboard keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_org_trans_date);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        keyboard = new Keyboard(this, R.xml.keyboard1);
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_digital, keyboard, tv_show_original_trans_date, KeyboardUtil.TYPE_ORG_TRANS_DATE);
        btn_keyboard_confirm.setOnClickListener(v -> {
            inputOrgTransDatePresenter.submitOrgTransDate(tv_show_original_trans_date.getText().toString());
        });
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(inputOrgTransDatePresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void clearInputText() {
        tv_show_original_trans_date.setText("00/00");
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_digital, keyboard, tv_show_original_trans_date, KeyboardUtil.TYPE_ORG_TRANS_DATE);
    }
}
