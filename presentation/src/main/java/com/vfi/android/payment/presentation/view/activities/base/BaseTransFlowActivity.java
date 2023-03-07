package com.vfi.android.payment.presentation.view.activities.base;

import android.os.Bundle;
import android.view.WindowManager;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.presentation.view.contracts.base.UI;


public abstract class BaseTransFlowActivity<T extends UI> extends BaseMvpActivity<T> {
    private final String TAG = TAGS.Navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setOnBackPressedListener(new OnBackPressedListener() {
            @Override
            public void onBackPressed() {
                LogUtil.d(TAG, "User press screen back btn.");
                if (uiNavigator != null) {
                    uiNavigator.getUiFlowControlData().setNeedUIBack(true);
                    navigatorToNextStep();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        acquireWakeLock();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseWakeLock();
    }
}
