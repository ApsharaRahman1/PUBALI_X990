package com.vfi.android.payment.presentation.view.activities;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;


import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.InputInvoiceNumPresenter;
import com.vfi.android.payment.presentation.utils.KeyboardUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.contracts.InputInvoiceNumUI;
import com.vfi.android.payment.presentation.view.widget.DigitalKeyboardView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InputInvoiceNumActivity extends BaseTransFlowActivity<InputInvoiceNumUI> implements InputInvoiceNumUI {
    @BindView(R.id.keyboardview_digital)
    DigitalKeyboardView keyboardview;

    @BindView(R.id.btn_keyboard_confirm)
    Button btn_keyboard_confirm;

    @BindView(R.id.tv_show_invoice_no)
    TextView tv_show_invoice_no;

    @Inject
    InputInvoiceNumPresenter inputInvoiceNumPresenter;

    private Keyboard keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_invoice_num);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        keyboard = new Keyboard(this, R.xml.keyboard1);
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview, keyboard, tv_show_invoice_no, KeyboardUtil.TYPE_TRACE_NUM);

        btn_keyboard_confirm.setOnClickListener(v -> {
            inputInvoiceNumPresenter.checkTransRecordByInvoiceNum(tv_show_invoice_no.getText().toString());
        });
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(inputInvoiceNumPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.menu_item_camera:
//                uiNavigator.getUiFlowControlData().setScan(true);
//                navigatorToNextStep();
//                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showNotFoundWarnDialog(String msg) {
        showWarnDialog(msg, null);
    }

    @Override
    public void clearInputText() {
        tv_show_invoice_no.setText("");
        KeyboardUtil keyboardUtil = new KeyboardUtil(keyboardview, keyboard, tv_show_invoice_no, KeyboardUtil.TYPE_TRACE_NUM);
    }
}
