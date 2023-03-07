package com.vfi.android.payment.presentation.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;


import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.presentation.AndroidApplication;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class ResUtil {

    public static String getString(@android.support.annotation.StringRes int resId) {
        return AndroidApplication.getInstance().getString(resId);
    }

    public static String getString(@android.support.annotation.StringRes int resId, Object... formatArgs) {
        return AndroidApplication.getInstance().getString(resId, formatArgs);
    }

    public static byte[] getByteFromDrawable(@DrawableRes int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(AndroidApplication.getInstance().getResources(), resId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static byte[] getByteFromDrawable(@DrawableRes int resId, float width) {
        Bitmap bitmap = BitmapFactory.decodeResource(AndroidApplication.getInstance().getResources(), resId);
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();

        float scale = (float)width / (float)bitmapWidth;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        bitmap = Bitmap.createBitmap(bitmap,0,0, bitmapWidth, bitmapHeight, matrix,true);
        LogUtil.d("TAG", "=====new width=" + bitmap.getWidth() + " height=" + bitmap.getHeight());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static int getColor(@ColorRes int colorId) {
        return ContextCompat.getColor(AndroidApplication.getInstance(), colorId);
    }

//    public static void resetLanguage() {
//        AndroidApplication app = AndroidApplication.getInstance();
//        Resources res = app.getResources();
//        Configuration configuration = res.getConfiguration();
//        Locale locale = getLocale();
//        Locale.setDefault(locale);
//        configuration.setLocale(locale);
//        app.createConfigurationContext(configuration);
//
//    }
//
//    public static boolean setLanguage(int language) {
//        SharedPreferences.Editor editor = AndroidApplication.getInstance().getSharedPreferences("language", Context.MODE_PRIVATE).edit();
//        editor.putInt("language", language);
//        return editor.commit();
//    }

//    public static int getLanguage() {
//        SharedPreferences sp = AndroidApplication.getInstance().getSharedPreferences("language", Context.MODE_PRIVATE);
//        return sp.getInt("language", Language.SPANISH);
//    }
//
//    public static Locale getLocale() {
//        int language = getLanguage();
//        Locale locale;
//        switch (language) {
//            case Language.SPANISH:
//                locale = new Locale("es");
//                break;
//            case Language.ENGLISH:
//                locale = new Locale("en");
//                break;
//            case Language.BASQUE:
//                locale = new Locale("eu");
//                break;
//            case Language.GALICIAN:
//                locale = new Locale("gl");
//                break;
//            case Language.CATALAN:
//                locale = new Locale("ca");
//                break;
//            case Language.FRENCH:
//                locale = new Locale("fr");
//                break;
//            case Language.GERMAN:
//                locale = new Locale("de");
//                break;
//            case Language.ITALIAN:
//                locale = new Locale("it");
//                break;
//            case Language.PORTUGUESE:
//                locale = new Locale("pt");
//                break;
//            default:
//                locale = new Locale("es");
//                break;
//        }
//        return locale;
//    }
}