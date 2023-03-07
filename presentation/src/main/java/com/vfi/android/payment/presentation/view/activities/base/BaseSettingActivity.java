package com.vfi.android.payment.presentation.view.activities.base;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.presentation.view.contracts.base.UI;

public abstract class BaseSettingActivity<T extends UI> extends BaseMvpActivity<T> {
    private final String TAG = TAGS.Setting;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        createTimer();
    }

    @Override
    protected void onStart() {
        countDownTimer.start();
        super.onStart();
    }

    @Override
    protected void onStop() {
        countDownTimer.cancel();
        super.onStop();
    }

    public void cancelTimer() {
        if (countDownTimer != null) {
            try {
                countDownTimer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createTimer() {
        countDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                LogUtil.d(TAG, "Setting UI timeout");
                finish();
            }
        };
    }

    public void restartTimer() {
        cancelTimer();
        createTimer();
        countDownTimer.start();
    }
}
