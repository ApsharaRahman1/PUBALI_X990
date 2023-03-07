package com.vfi.android.payment.presentation;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.vfi.android.data.database.DBFlowDatabase;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.logOutPut.LogOutPutUtil;
import com.vfi.android.payment.BuildConfig;
import com.vfi.android.payment.presentation.internal.di.components.ApplicationComponent;
import com.vfi.android.payment.presentation.internal.di.components.DaggerApplicationComponent;
import com.vfi.android.payment.presentation.internal.di.modules.ApplicationModule;
import com.vfi.android.payment.presentation.view.activities.base.BaseSettingActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.plugins.RxJavaPlugins;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class AndroidApplication extends Application {
    ApplicationComponent applicationComponent;
    private static AndroidApplication application;
    private static RefWatcher refWatcher;
    private static List<Activity> mActivityList;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        mActivityList = new ArrayList<>();

        /**
         * Initialize database
         */
        DBFlowDatabase.dbInit(this);
        if (BuildConfig.DEBUG) {
            DBFlowDatabase.setLogLevel();
        }

        LogUtil.setLogEntry(new AndroidLogEntry());

        if (BuildConfig.isCheckMemoryLeak) {
            LogUtil.d("Application", "Debug start leakCanary checking");
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            refWatcher = LeakCanary.install(this);
        }

        //DB flow
        //FlowManager.init(this);
        initializeInjector();

        /**
         * catch unhandled rx error.
         */
        RxJavaPlugins.setErrorHandler(e -> {
        });

        CrashHandler crashHandler = new CrashHandler();
        crashHandler.init(getApplicationContext());

//        Start SQLiteStudio service.
        SQLiteStudioService.instance().setPort(20005);
        SQLiteStudioService.instance().start(this);
    }

    public static AndroidApplication getInstance(){
        return application;
    }

    public static RefWatcher getRefWatcher() {
        return refWatcher;
    }

    public void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public static void addActivity(Activity activity) {
        if (!mActivityList.contains(activity)) {
            mActivityList.add(activity);
        }
    }

    public static void removeActivity(Activity activity) {
        if (mActivityList.contains(activity)) {
            mActivityList.remove(activity);
        }
    }

    public static void finishAllActivity() {
        for (Activity activity : mActivityList) {
            if (activity != null){
                activity.finish();
            }
        }
    }

    public void cancelAllSettingTimer() {
        for (Activity activity : mActivityList) {
            if (activity != null && activity instanceof BaseSettingActivity){
                BaseSettingActivity baseSettingActivity = (BaseSettingActivity) activity;
                baseSettingActivity.cancelTimer();
            }
        }
    }

    public Context getCurrentActivityContext() {
        return mActivityList.get(mActivityList.size() - 1);
    }

    public static void reCreateAllActivities() {
        for (Activity activity : mActivityList) {
            if (activity != null) {
                activity.recreate();
            }
        }
    }

    public class CrashHandler implements Thread.UncaughtExceptionHandler {
        private Context mContext;

        void init(Context context) {
            mContext = context;
            Thread.setDefaultUncaughtExceptionHandler(this);
        }

        @Override
        public void uncaughtException(Thread p1, Throwable p2) {
            LogUtil.e("Application", "uncaughtException found");
            LogUtil.e("Application", "=====================================================");
            LogUtil.e("Application", "=====================================================");
            LogUtil.e("Application", p2.getMessage());
            p2.printStackTrace();
            LogUtil.e("Application", "=====================================================");
            LogUtil.e("Application", "=====================================================");
            PackageManager packageManager = getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                PendingIntent restartIntent = PendingIntent.getActivity(
                        mContext.getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
                AndroidApplication.finishAllActivity();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }

    private class AndroidLogEntry implements LogUtil.ILogEntry {
        StringBuffer stringBuffer = new StringBuffer();

        @Override
        public void v(String TAG, String logInfo) {
            Log.v(TAG, printIn(logInfo));
        }

        @Override
        public void d(String TAG, String logInfo) {
            Log.d(TAG, printIn(logInfo));
        }

        @Override
        public void i(String TAG, String logInfo) {
            Log.i(TAG, printIn(logInfo));
        }

        @Override
        public void w(String TAG, String logInfo) {
            Log.w(TAG, printIn(logInfo));
        }

        @Override
        public void e(String TAG, String logInfo) {
            Log.e(TAG, printIn(logInfo));
        }

        private String printIn(String msg) {
            if (msg == null || msg.length() == 0) {
                return "";
            }

            stringBuffer.setLength(0);

            int segmentSize = 3 * 1024;
            long length = msg.length();
            if (length <= segmentSize) {// 长度小于等于限制直接打印
                stringBuffer.append(msg);
            } else {
                while (msg.length() > segmentSize) {// 循环分段打印日志
                    String logContent = msg.substring(0, segmentSize);
                    msg = msg.replace(logContent, "");
                    stringBuffer.append(logContent + "\n");
                }
                stringBuffer.append(msg);// 打印剩余日志
            }
            if (LogOutPutUtil.isIsOutPutLog()) {
                LogOutPutUtil.outPutLog(msg);
            }

            return stringBuffer.toString();
        }
    }
}
