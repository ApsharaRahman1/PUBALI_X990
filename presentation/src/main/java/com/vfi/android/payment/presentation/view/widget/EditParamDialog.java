package com.vfi.android.payment.presentation.view.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.BuildConfig;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.AndroidApplication;
import com.vfi.android.payment.presentation.internal.di.components.CommonComponent;
import com.vfi.android.payment.presentation.internal.di.components.DaggerCommonComponent;
import com.vfi.android.payment.presentation.utils.DensityUtil;
import com.vfi.android.payment.presentation.utils.ParamFormatUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditParamDialog extends AlertDialog {
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.tv_old_param)
    TextView tv_old_param;
    @BindView(R.id.et_new_param)
    EditText etNewParam;
    @BindView(R.id.btn_positive)
    Button btnPositive;
    @BindView(R.id.btn_negative)
    Button btnNegative;
    @BindView(R.id.tv_new_param_display)
    TextView tv_new_param_display;
    @BindView(R.id.fl_input_area)
    FrameLayout fl_input_area;

    private final String TAG = TAGS.Encryption;

    private String message;
    private String btnPositiveText;
    private String btnNegativeText;

    private OnClickListener onClickListener;
    private String paramName;
    private String oldParamValue;
    private int paramFormat;
    ParamFormatUtil paramFormatUtil;

    public EditParamDialog(@NonNull Context context, String paramName, String oldParamValue, int paramFormat) {
        super(context, R.style.PasswordDialog);
        CommonComponent commonComponent;
        commonComponent = DaggerCommonComponent.builder()
                .applicationComponent(AndroidApplication.getInstance().getApplicationComponent())
                .build();
        commonComponent.inject(this);

        this.paramName = paramName;
        this.oldParamValue = oldParamValue;
        this.paramFormat = paramFormat;
        paramFormatUtil = new ParamFormatUtil(paramFormat);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_param);
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

    private boolean isPasswordInputType() {
        int inputType = paramFormatUtil.getKeyboardInputType();
        if ((inputType & InputType.TYPE_NUMBER_VARIATION_PASSWORD) > 0
                || (inputType & InputType.TYPE_TEXT_VARIATION_PASSWORD) > 0
                || (inputType & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) > 0
                || (inputType & InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD) > 0) {
            return true;
        }

        return false;
    }

    private void initView() {
        // set input type
        etNewParam.setInputType(paramFormatUtil.getKeyboardInputType());

        btnNegativeText = ResUtil.getString(R.string.btn_hint_cancel);
        btnPositiveText = ResUtil.getString(R.string.btn_hint_confirm);
        message = paramName;

        if (isPasswordInputType()) {
            tv_old_param.setText("******");
        } else {
            tv_old_param.setText(oldParamValue);
        }
        tv_new_param_display.setText(paramFormatUtil.getFormatHint());

        fl_input_area.setOnClickListener(v -> {
            etNewParam.setFocusable(true);
            etNewParam.setFocusableInTouchMode(true);
            etNewParam.requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        });

        btnPositive.setText(btnPositiveText);
        btnNegative.setText(btnNegativeText);
        tvMessage.setText(message);
        etNewParam.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveNewParameter();
            }

            return false;
        });

        etNewParam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                LogUtil.d(TAG, "beforeTextChanged s=[" + s + "] start=" + start + " count=" + count + " after" + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtil.d(TAG, "onTextChanged s=[" + s + "] start=" + start + " before=" + before + " count" + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                LogUtil.d(TAG, "afterTextChanged s=[" + s + "]");
                tv_new_param_display.setText(paramFormatUtil.formatParameter(s.toString()));
            }
        });
    }

    @OnClick({R.id.btn_positive, R.id.btn_negative})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_negative:
                dismiss();
                break;
            case R.id.btn_positive:
                saveNewParameter();
                break;
        }
    }

    private void saveNewParameter() {
        String newParam = tv_new_param_display.getText().toString();
        if (!paramFormatUtil.isValidParamter(newParam)) {
            if (paramFormatUtil.getInvalidParamHint() != null) {
                ToastUtil.showToastLong(paramFormatUtil.getInvalidParamHint());
            } else {
                ToastUtil.showToastLong(ResUtil.getString(R.string.setting_toast_invalid_value));
            }
            tv_new_param_display.setText("");
            etNewParam.setText("");
            return;
        }

        if (onClickListener != null) {
            newParam = paramFormatUtil.getFinalSaveValue(newParam);
            LogUtil.d(TAG, "new Param=" + newParam);
            onClickListener.onClick(newParam);
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
        void onClick(String newParameter);
    }
}
