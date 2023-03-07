package com.vfi.android.payment.presentation.view.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;


import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.InputPinPresenter;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.adapters.PasswdListAdapter;
import com.vfi.android.payment.presentation.view.contracts.InputPinUI;
import com.vfi.android.payment.presentation.view.widget.PinKeyBoardView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InputPinActivity extends BaseTransFlowActivity<InputPinUI> implements InputPinUI {
    private final String TAG = this.getClass().getSimpleName();

    @BindView(R.id.tv_pin_hint)
    TextView tv_pin_hint;
    @BindView(R.id.tv_show_acount)
    TextView tv_show_acount;
    @BindView(R.id.tv_amount)
    TextView tv_amount;
    @BindView(R.id.recyclerview_passwd_list)
    RecyclerView recyclerview_passwd_list;
    @BindView(R.id.keyboardview_inputpin)
    PinKeyBoardView keyboardview_inputpin;

    @Inject
    InputPinPresenter inputPinPresenter;

    private PasswdListAdapter passwdListAdapter;
    private boolean onPreDrawFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        onPreDrawFirst = false;
        setContentView(R.layout.activity_input_pin);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setBackGround(R.drawable.background_keyboard);
        keyboardview_inputpin.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                LogUtil.i(TAG, "onPreDraw been called.");
                if (!onPreDrawFirst)
                {
                    onPreDrawFirst = true;
                    try {
                        LogUtil.i(TAG, "getRandomKeyBoradNumber.");
                        int[] numPositionList = inputPinPresenter.initkeyboardAndPinpad(keyboardview_inputpin.getCoordinateList());
                        keyboardview_inputpin.setRandomNumber(numPositionList);
                        inputPinPresenter.startPinInput();
                    } catch (Exception e) {
                        LogUtil.e(TAG, "FindView() exception happened.");
                        e.printStackTrace();
                    }
                }
                return onPreDrawFirst;
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        passwdListAdapter = new PasswdListAdapter(6);
        recyclerview_passwd_list.setLayoutManager(linearLayoutManager);
        recyclerview_passwd_list.setAdapter(passwdListAdapter);
    }

    @Override
    protected void onStart() {
        inputPinPresenter.setPowerKeyStatus(false);
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        inputPinPresenter.setPowerKeyStatus(true);
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(inputPinPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void showTransInfo(String acount, String amount) {
        tv_show_acount.setText(acount);
        tv_amount.setText(amount);
    }

    @Override
    public void showPassword(int len) {
        passwdListAdapter.showPasswd(len);
    }

    @Override
    public void showPinHint(String pinHint) {
        tv_pin_hint.setText("" + pinHint);
    }
}
