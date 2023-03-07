package com.vfi.android.domain.utils;


import com.vfi.android.domain.entities.consts.TAGS;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    private static final String TAG = TAGS.UTILS;

    public static String getCurrentDateYYMM() {
        /**
         * 获取当前时间
         */
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String YYMM = simpleDateFormat.format(date).substring(2, 6);

        LogUtil.d(TAG, "getCurrentDateYYMM=" + YYMM);
        return YYMM;
    }

    public static String formatDate(long time) {
        /**
         * 获取当前时间
         */
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// yyyyMMdd
        return simpleDateFormat.format(date);
    }

    public static String formatTime(long time) {
        /**
         * 获取当前时间
         */
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");// HH:mm:ss
        return simpleDateFormat.format(date);
    }
}
