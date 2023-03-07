package com.vfi.android.payment.presentation.utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.vfi.android.payment.presentation.AndroidApplication;

import me.drakeet.support.toast.ToastCompat;


/**
 * Created by CuncheW1 on 2017/3/16.
 */

public class ToastUtil {
    public static void showToastLong(final String msg) {
        show(msg, Toast.LENGTH_LONG);
    }

    public static void showToastShort(final String msg) {
        show(msg, Toast.LENGTH_SHORT);
    }

    public static void show(final String msg, final int duration) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ToastCompat.makeText(AndroidApplication.getInstance().getApplicationContext(), msg, duration).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
