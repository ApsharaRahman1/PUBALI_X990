package com.vfi.android.payment.presentation.view.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.payment.BuildConfig;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.AndroidApplication;
import com.vfi.android.payment.presentation.internal.di.components.CommonComponent;
import com.vfi.android.payment.presentation.internal.di.components.DaggerCommonComponent;
import com.vfi.android.payment.presentation.utils.DensityUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InputTextDialog extends AlertDialog {
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.et_input_text)
    EditText et_input_text;
    @BindView(R.id.btn_positive)
    Button btnPositive;
    @BindView(R.id.btn_negative)
    Button btnNegative;

    private final String TAG = TAGS.Dialog;

    private String message;
    private String btnPositiveText;
    private String btnNegativeText;

    private OnClickListener onClickListener;
    private String hintMsg;
    private boolean isDigitalKeyboard;

    public InputTextDialog(@NonNull Context context, String hintMsg) {
        super(context, R.style.PasswordDialog);
        CommonComponent commonComponent;
        commonComponent = DaggerCommonComponent.builder()
                .applicationComponent(AndroidApplication.getInstance().getApplicationComponent())
                .build();
        commonComponent.inject(this);

        this.hintMsg = hintMsg;
        isDigitalKeyboard = false;
    }

    public InputTextDialog(@NonNull Context context, String hintMsg, boolean isDigitalKeyboard) {
        super(context, R.style.PasswordDialog);
        CommonComponent commonComponent;
        commonComponent = DaggerCommonComponent.builder()
                .applicationComponent(AndroidApplication.getInstance().getApplicationComponent())
                .build();
        commonComponent.inject(this);

        this.hintMsg = hintMsg;
        this.isDigitalKeyboard = isDigitalKeyboard;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_text);
        if (!BuildConfig.isAllowScreenShot) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = (int)(DensityUtil.getScreenWidth(getContext()) * 0.8);
        this.getWindow().setAttributes(lp);
        Window window = getWindow();
        if (window != null)
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ButterKnife.bind(this);
        initView();
    }

    public void initView() {
        btnNegativeText = ResUtil.getString(R.string.btn_hint_cancel);
        btnPositiveText = ResUtil.getString(R.string.btn_hint_confirm);
        message = hintMsg;

        btnPositive.setText(btnPositiveText);
        btnNegative.setText(btnNegativeText);
        tvMessage.setText(message);
        if (isDigitalKeyboard) {
            et_input_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        et_input_text.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doOnclickProcess(true);
            }

            return false;
        });
    }

    @OnClick({R.id.btn_positive, R.id.btn_negative})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_negative:
                doOnclickProcess(false);
                break;
            case R.id.btn_positive:
                doOnclickProcess(true);
                break;
        }
    }

    private void doOnclickProcess(boolean isConfirm) {
        if (onClickListener != null) {
            onClickListener.onClick(et_input_text.getText().toString(), isConfirm);
        }

        dismiss();
    }

    public void setMessage(@StringRes int messageRes) {
        setMessage(getContext().getString(messageRes));
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPositiveButton(@StringRes int textRes) {
        setPositiveButton(getContext().getString(textRes));
    }

    public void setPositiveButton(String text) {
        this.btnPositiveText = text;
    }

    public void setNegativeButton(@StringRes int textRes) {
        setNegativeButton(getContext().getString(textRes));
    }

    public void setNegativeButton(String text) {
        this.btnNegativeText = text;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(String input, boolean isConfirm);
    }
}
