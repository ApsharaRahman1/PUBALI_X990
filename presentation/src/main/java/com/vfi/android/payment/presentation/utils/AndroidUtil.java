package com.vfi.android.payment.presentation.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.AndroidApplication;


public class AndroidUtil {
    private static final String TAG = TAGS.UTILS;

    public static void startActivity(Context context, Class activityClass) {
        LogUtil.d(TAG, "Start activity[" + activityClass.getSimpleName() + "]");
        Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }
    }

    public static void startActivity(Context context, Intent intent) {
        LogUtil.d(TAG, "Start intent activity");
        context.startActivity(intent);
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }
    }

    public static void startActivityWithBundle(Context context, Class activityClass, Bundle bundle) {
        LogUtil.d(TAG, "Start activity[" + activityClass.getSimpleName() + "]");
        Intent intent = new Intent(context, activityClass);
        intent.putExtras(bundle);
        context.startActivity(intent);
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }
    }

    public static void finishActivity(Context activityContext) {
        if (activityContext != null && activityContext instanceof AppCompatActivity) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) activityContext;
            appCompatActivity.finish();
        }
    }

    public static void sendBroadcast(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        AndroidApplication.getInstance().getApplicationContext().sendBroadcast(intent);
    }

    public static void setEditTextPassWordShowStyle(EditText et) {
        et.setTransformationMethod(new PasswordCharSequenceStyle());
    }

    public static void wakeupScreen() {
        try {
            PowerManager pm = (PowerManager) AndroidApplication.getInstance()
                    .getSystemService(Context.POWER_SERVICE);
            boolean screenOn = pm.isInteractive();
            if (!screenOn) {
                String tag = "dfafsda";
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP| PowerManager.FULL_WAKE_LOCK, tag);
                wl.acquire(5000); // 点亮屏幕
                wl.release(); // 释放
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class PasswordCharSequenceStyle extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;

            public PasswordCharSequence(CharSequence source) {
                mSource = source;
            }

            public char charAt(int index) {
                return '*';
            }

            public int length() {
                return mSource.length();
            }

            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end);
            }
        }
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        boolean isInstalled = false;

        if (context == null || packageName == null || packageName.length() == 0) {
            return false;
        }

        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            isInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("TAG", "Wechat/Alipay Apk is not found.");
            e.printStackTrace();
            isInstalled = false;
        }

        return isInstalled;
    }

    public static void replaceFragment(Context context, Class fragmentClass, int resContainerId, boolean isAddToBackStack) {
        if (fragmentClass == null || !(context instanceof FragmentActivity)) {
            return;
        }

        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        final String fragmentTag = fragmentClass.getName();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment == null) {
            fragment = getFragmentInstance(fragmentClass);
            if (fragment == null) {
                LogUtil.d(TAG, "new Fragment instance failed." + fragmentClass.getSimpleName());
                return;
            }
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (isAddToBackStack) {
            transaction = transaction.addToBackStack(fragmentTag);
        }
//
//        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_left)
//                .replace(resContainerId, fragment)
//                .commit();
    }

    public static void popBackStackToFragment(Context context, Class fragmentClass) {
        if (fragmentClass == null || !(context instanceof FragmentActivity)) {
            return;
        }

        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        final String fragmentTag = fragmentClass.getName();
        fragmentManager.popBackStack(fragmentTag, 0);
    }

    public static void popBackStackToPrevious(Context context) {
        if (!(context instanceof FragmentActivity)) {
            return;
        }

        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    public static boolean isFragmentInBackStack(Context context, Class fragmentClass) {
        if (fragmentClass == null || !(context instanceof FragmentActivity)) {
            return false;
        }

        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        final String fragmentTag = fragmentClass.getName();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment == null) {
            return false;
        } else {
            return true;
        }
    }

    private static Fragment getFragmentInstance(Class fragmentClass) {
        Fragment fragment = null;
        try {
            Object object = fragmentClass.newInstance();
            if (object instanceof Fragment) {
                fragment = (Fragment) object;
            } else {
                return null;
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return fragment;
    }

    public static void finishAllActivitys() {
        AndroidApplication.finishAllActivity();
    }

    public static void reCreateAllActivities() {
        AndroidApplication.reCreateAllActivities();
    }
}
