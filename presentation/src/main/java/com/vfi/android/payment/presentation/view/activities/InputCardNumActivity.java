package com.vfi.android.payment.presentation.view.activities;

import android.app.Dialog;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.InputCardNumPresenter;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.KeyboardUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.contracts.InputCardNumUI;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InputCardNumActivity extends BaseTransFlowActivity<InputCardNumUI> implements InputCardNumUI {
    @BindView(R.id.keyboardview_digital)
    KeyboardView keyboardview_digital;
    @BindView(R.id.btn_keyboard_confirm)
    Button btn_keyboard_confirm;
    @BindView(R.id.tv_show_card_num)
    TextView tv_show_card_num;

    private Keyboard keyboard;

    @Inject
    InputCardNumPresenter inputCardNumPresenter;
    private Dialog selectDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_card_num);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setBackGround(R.drawable.background_keyboard);
        keyboard = new Keyboard(this, R.xml.keyboard1);
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_digital, keyboard, tv_show_card_num, KeyboardUtil.TYPE_CARD_NUM);
        btn_keyboard_confirm.setOnClickListener(v -> {
            inputCardNumPresenter.submitCardNum(tv_show_card_num.getText().toString());
        });
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(inputCardNumPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
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
    public void clearCardNum() {
        tv_show_card_num.setText("");
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview_digital, keyboard, tv_show_card_num, KeyboardUtil.TYPE_CARD_NUM);
    }

}
