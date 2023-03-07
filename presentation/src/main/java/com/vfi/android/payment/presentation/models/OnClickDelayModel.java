package com.vfi.android.payment.presentation.models;

/**
 * Created by fusheng.z on 2018/2/7.
 * fusheng.z 为解决极短时间多次onclick问题
 */

public class OnClickDelayModel {
    /**
     * 与上一次点周的最小间隔时间 500ms
     */
    private static final long MIN_CLICK_DELAY_TIME = 800;

    /**
     * 最后一次点击事事件的时间
     */
    private static long lastClickTime;

    public static boolean Control() {
        long curClickTime = System.currentTimeMillis();
        if (Math.abs(curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime;
            return true;
        } else {
            return false;
        }
    }


}
