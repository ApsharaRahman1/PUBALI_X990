package com.vfi.android.payment.presentation.view.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.BuildConfig;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.DensityUtil;
import com.vfi.android.payment.presentation.utils.DialogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountdownDialog extends AlertDialog implements LifecycleObserver {
    private final String TAG = TAGS.Dialog;
    @BindView(R.id.tv_countdown)
    TextView tvCountdown;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.ll_btn_area)
    LinearLayout ll_btn_area;
    @BindView(R.id.btn_left)
    Button btn_left;
    @BindView(R.id.btn_right)
    Button btn_right;

    private int seconds;
    private String message;
    private DialogType dialogType;
    private OnCountFinishedListener onCountFinishedListener;
    private boolean isExistBtnArea;
    private DialogUtil.AskDialogListener askDialogListener;
    private boolean isAlreadyConfirmed;
    private ValueAnimator valueAnimator;

    public CountdownDialog(@NonNull Context context, int seconds, DialogType dialogType) {
        super(context, R.style.loadingDialog);
        this.seconds = seconds;
        this.dialogType = dialogType;
        isExistBtnArea = false;
    }

    public CountdownDialog(@NonNull Context context, String message, int seconds, DialogUtil.AskDialogListener listener) {
        super(context, R.style.loadingDialog);
        this.seconds = seconds;
        this.dialogType = DialogType.ASK;
        isExistBtnArea = true;
        this.askDialogListener = listener;
        this.message = message;
        onCountFinishedListener = null;
        isAlreadyConfirmed = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.isAllowScreenShot) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.dialog_countdown);
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = (int) (DensityUtil.getScreenWidth(getContext()) * 0.85);
        this.getWindow().setAttributes(lp);
        ButterKnife.bind(this);
        initView();
    }

    public void initView() {
        setCancelable(false);

        tvMessage.setText(message);
        switch (dialogType) {
            case SUCCESS:
                ivIcon.setImageResource(R.drawable.icon_success);
                break;
            case FAIL:
                ivIcon.setImageResource(R.drawable.ic_fail_ani);
                break;
            case TEXT_ONLY:
                ivIcon.setVisibility(View.INVISIBLE);
                break;
            case ASK:
                ivIcon.setImageResource(R.drawable.icon_ask);
                break;
        }

        if (isExistBtnArea) {
            ll_btn_area.setVisibility(View.VISIBLE);
            btn_left.setOnClickListener(v -> {
                if (CountdownDialog.this.isShowing()) {
                    CountdownDialog.this.dismiss();
                }

                if (askDialogListener != null && !isAlreadyConfirmed) {
                    isAlreadyConfirmed = true;
                    valueAnimator.cancel();
                    askDialogListener.onClick(true);
                }
            });

            btn_right.setOnClickListener(v -> {
                if (CountdownDialog.this.isShowing()) {
                    CountdownDialog.this.dismiss();
                }

                if (askDialogListener != null && !isAlreadyConfirmed) {
                    isAlreadyConfirmed = true;
                    valueAnimator.cancel();
                    askDialogListener.onClick(false);
                }
            });
        } else {
            ll_btn_area.setVisibility(View.INVISIBLE);
        }

        //倒计时
        valueAnimator = ValueAnimator.ofInt(seconds, 0).setDuration(seconds * 1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                LogUtil.d(TAG, "onAnimationEnd");

                if (CountdownDialog.this.isShowing()) {
                    CountdownDialog.this.dismiss();
                }

                if (onCountFinishedListener != null) {
                    onCountFinishedListener.onFinished();
                    onCountFinishedListener = null;
                }

                if (askDialogListener != null && !isAlreadyConfirmed) {
                    isAlreadyConfirmed = true;
                    askDialogListener.onClick(true);
                }
            }
        });

        valueAnimator.addUpdateListener(animation -> {
            int count = (int) animation.getAnimatedValue();
//            LogUtil.d(TAG, "onUpdate count=" + count);
            if (CountdownDialog.this.isShowing()) {
                tvCountdown.setText(String.valueOf(count));
            }
        });
        valueAnimator.start();
    }

    public void setMessage(@StringRes int messageRes) {
        setMessage(getContext().getString(messageRes));
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void dismiss() {
        onCountFinishedListener = null;
        askDialogListener = null;
        super.dismiss();
    }

    public enum DialogType {
        SUCCESS, FAIL, TEXT_ONLY, ASK,
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        this.dismiss();
    }

    public interface OnCountFinishedListener {
        void onFinished();
    }

    public void setOnCountFinishedListener(OnCountFinishedListener onCountFinishedListener) {
        this.onCountFinishedListener = onCountFinishedListener;
    }
}
