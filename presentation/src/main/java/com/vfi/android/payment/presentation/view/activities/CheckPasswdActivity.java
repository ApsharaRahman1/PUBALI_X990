package com.vfi.android.payment.presentation.view.activities;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.CheckPasswdPresenter;
import com.vfi.android.payment.presentation.utils.KeyboardUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.adapters.PasswdListAdapter;
import com.vfi.android.payment.presentation.view.contracts.CheckPasswdUI;
import com.vfi.android.payment.presentation.view.widget.DigitalKeyboardView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckPasswdActivity extends BaseTransFlowActivity<CheckPasswdUI> implements CheckPasswdUI {

    @BindView(R.id.recyclerview_passwd_list)
    RecyclerView recyclerview_passwd_list;

    @BindView(R.id.keyboardview_digital)
    DigitalKeyboardView keyboardview;

    @BindView(R.id.btn_keyboard_confirm)
    Button btn_keyboard_confirm;
    @BindView(R.id.view_line)
    View view_line;
    @BindView(R.id.tv_check)
    TextView tv_check;
    @Inject
    CheckPasswdPresenter checkPasswdPresenter;

    private PasswdListAdapter passwdListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_passwd);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        setBackGround(R.drawable.background_keyboard);
        btn_keyboard_confirm.setOnClickListener(v -> {
            checkPasswdPresenter.checkPasswd(passwdListAdapter.getPasswd());
        });
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(checkPasswdPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void showPasswdBoxView(int maxPasswdLen) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        passwdListAdapter = new PasswdListAdapter(maxPasswdLen);
        recyclerview_passwd_list.setLayoutManager(linearLayoutManager);
        recyclerview_passwd_list.setAdapter(passwdListAdapter);
        Keyboard keyboard = new Keyboard(this, R.xml.keyboard1);
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview, keyboard, passwdListAdapter);
    }

    @Override
    public void resetPasswdBoxView() {
        passwdListAdapter.clearPasswd();
    }
}
