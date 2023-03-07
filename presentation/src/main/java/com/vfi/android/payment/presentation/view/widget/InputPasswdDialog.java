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
import com.vfi.android.domain.entities.databeans.OperatorInfo;
import com.vfi.android.domain.interactor.repository.UseCaseCheckOperatorPasswd;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalSN;
import com.vfi.android.domain.utils.EncryptionUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.domain.utils.logOutPut.LogOutPutUtil;
import com.vfi.android.payment.BuildConfig;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.AndroidApplication;
import com.vfi.android.payment.presentation.internal.di.components.CommonComponent;
import com.vfi.android.payment.presentation.internal.di.components.DaggerCommonComponent;
import com.vfi.android.payment.presentation.utils.DensityUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.ToastUtil;

import java.util.Calendar;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InputPasswdDialog extends AlertDialog {
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_positive)
    Button btnPositive;
    @BindView(R.id.btn_negative)
    Button btnNegative;

    private final String TAG = TAGS.Encryption;

    private String message;
    private String btnPositiveText;
    private String btnNegativeText;

    @Inject
    UseCaseCheckOperatorPasswd useCaseCheckOperatorPasswd;
    @Inject
    UseCaseGetTerminalSN useCaseGetTerminalSN;

    private OnClickListener onClickListener;
    private int operatorType;
    private String a;
    private String d;

    /**
     * @param context
     * @param operatorType {@link OperatorInfo}
     */
    public InputPasswdDialog(@NonNull Context context, int operatorType) {
        super(context, R.style.PasswordDialog);
        CommonComponent commonComponent;
        commonComponent = DaggerCommonComponent.builder()
                .applicationComponent(AndroidApplication.getInstance().getApplicationComponent())
                .build();
        commonComponent.inject(this);

        LogUtil.d(TAG, "InputPasswdDialog operatorType=" + operatorType);
        this.operatorType = operatorType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_pwd);
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
        message = ResUtil.getString(R.string.tv_hint_please_enter_password);
        if (operatorType == OperatorInfo.TYPE_SUPER_MANAGER) {
            message = ResUtil.getString(R.string.tv_hint_enter_super_manager_password);
        } else if (operatorType == OperatorInfo.TYPE_EXPORT_LOG) {
            a = getRandomHexString(6);
            message = ResUtil.getString(R.string.tv_hint_enter_export_log_password) + "\n\n" + a;
            etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        btnPositive.setText(btnPositiveText);
        btnNegative.setText(btnNegativeText);
        tvMessage.setText(message);
        etPwd.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkPassword();
            }

            return false;
        });
    }

    @OnClick({R.id.btn_positive, R.id.btn_negative})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_negative:
                dismiss();
                break;
            case R.id.btn_positive:
                checkPassword();
                break;
        }
    }

    private void checkPassword() {
        boolean isPasswordCorrect = false;
        if (operatorType == OperatorInfo.TYPE_EXPORT_LOG) {
            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH) + 1;//注意月份
            int day = now.get(Calendar.DAY_OF_MONTH);
            String c = "" + year + month + day;
            d = useCaseGetTerminalSN.execute(null);
            String data = a + c + d;
            String b = EncryptionUtil.getMd5HexString(data.getBytes());
            if (b.substring(2, 8).equals(etPwd.getText().toString())) {
                LogOutPutUtil.setE(b.substring(10, 16));
                isPasswordCorrect = true;
            } else {
                isPasswordCorrect = false;
            }
        } else {
            String passwdMd5 = EncryptionUtil.getMd5HexString(etPwd.getText().toString().getBytes());
            OperatorInfo operatorInfo = new OperatorInfo(operatorType, passwdMd5);
            isPasswordCorrect = useCaseCheckOperatorPasswd.execute(operatorInfo);
        }

        if (onClickListener != null) {
            onClickListener.onClick(isPasswordCorrect);
        }

        dismiss();
        LogUtil.d(TAG, "isPasswordCorrect=" + isPasswordCorrect);
        if (!isPasswordCorrect) {
            ToastUtil.showToastLong(ResUtil.getString(R.string.toast_hint_invalid_password));
            etPwd.setText("");
        }
    }

    private String getRandomHexString(int len) {
        Random random = new Random(System.currentTimeMillis());
        byte randomBytes[] = new byte[len];
        random.nextBytes(randomBytes);
        return StringUtil.byte2HexStr(randomBytes);
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
        void onClick(boolean isCorrect);
    }
}
