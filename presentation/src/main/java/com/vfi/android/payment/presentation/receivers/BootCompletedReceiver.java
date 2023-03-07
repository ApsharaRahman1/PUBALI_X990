package com.vfi.android.payment.presentation.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.vfi.android.domain.memory.GlobalMemoryCache;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.presentation.AndroidApplication;
import com.vfi.android.payment.presentation.internal.di.components.CommonComponent;
import com.vfi.android.payment.presentation.internal.di.components.DaggerCommonComponent;

import javax.inject.Inject;


public class BootCompletedReceiver extends BroadcastReceiver {

    private final String BOOT_ACTION = Intent.ACTION_BOOT_COMPLETED;
    private final String TAG = this.getClass().getSimpleName();
    @Inject
    GlobalMemoryCache globalMemoryCache;
    @Inject
    Context context;

    public BootCompletedReceiver() {
        CommonComponent commonComponent;
        commonComponent = DaggerCommonComponent.builder()
                .applicationComponent(AndroidApplication.getInstance().getApplicationComponent())
                .build();

        commonComponent.inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BOOT_ACTION.equals(intent.getAction())) {
            checkPowerStatus(context);
        }
    }

    private void checkPowerStatus(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        // check is it charging.
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        LogUtil.d(TAG, "status=" + status);
        boolean isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL);
        globalMemoryCache.setPowerConnected(isCharging);

        // check is lower power
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float)scale;
        LogUtil.d(TAG, "batteryPct=" + batteryPct);
        boolean isLowPower = (batteryPct < 0.15f ? true : false);
        globalMemoryCache.setLowPowerStatus(isLowPower);
    }
}
