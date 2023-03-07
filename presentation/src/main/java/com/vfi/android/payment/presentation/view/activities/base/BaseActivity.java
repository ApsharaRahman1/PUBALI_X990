package com.vfi.android.payment.presentation.view.activities.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.LayoutRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Constraints;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.leakcanary.RefWatcher;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.BuildConfig;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.AndroidApplication;
import com.vfi.android.payment.presentation.internal.di.components.ApplicationComponent;
import com.vfi.android.payment.presentation.internal.di.components.CommonComponent;
import com.vfi.android.payment.presentation.internal.di.components.DaggerCommonComponent;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.StatusBarUtil;
import com.vfi.android.payment.presentation.utils.ToastUtil;
import com.vfi.android.payment.presentation.view.contracts.base.UI;
import com.vfi.android.payment.presentation.view.widget.CountdownDialog;
import com.vfi.android.payment.presentation.view.widget.LoadingDialog;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

public class BaseActivity extends AppCompatActivity implements UI {
    private final String TAG = this.getClass().getSimpleName();
    private PowerManager.WakeLock mWakeLock;
    private Toolbar toolbar;
    private TextView tvTitle;
    private ConstraintLayout llBackgroud;

    private OnBackPressedListener onBackPressedListener = null;
    private LoadingDialog loadingDialog = null;
//    private InputPasswdDialog inputPasswdDialog = null;
//    private String teriminalSN = "";

    private Dialog hintDialog;
    private Dialog askDialog;
    private Dialog warnDialog;
    private Dialog countDownAskDialog;
    private Dialog passwordDialog;

    @Inject
    UINavigator uiNavigator;
//    @Inject
//    UseCaseControlHomeKeyStatusBar useCaseControlHomeKeyStatusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.isAllowScreenShot) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        StatusBarUtil.translucentStatusBar(this, true);
        CommonComponent commonComponent;
        commonComponent = DaggerCommonComponent.builder()
                .applicationComponent(getApplicationComponent())
                .build();

        commonComponent.inject(this);
        AndroidApplication.addActivity(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        setContentView(layoutResID, true);
    }

    public void setContentView(int layoutResID, boolean attachRoot) {
        if (attachRoot) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View viewRoot = layoutInflater.inflate(R.layout.activity_base, null);
            toolbar = viewRoot.findViewById(R.id.toolbar);
            tvTitle = viewRoot.findViewById(R.id.tv_title);
            llBackgroud = viewRoot.findViewById(R.id.ll_background);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            FrameLayout frameLayout = viewRoot.findViewById(R.id.content);
            View contentView = layoutInflater.inflate(layoutResID, null);
            frameLayout.addView(contentView);
            super.setContentView(viewRoot);
        } else
            super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View viewRoot = layoutInflater.inflate(R.layout.activity_base, null);
        toolbar = viewRoot.findViewById(R.id.toolbar);
        tvTitle = viewRoot.findViewById(R.id.tv_title);
        llBackgroud = viewRoot.findViewById(R.id.ll_background);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        FrameLayout frameLayout = view.findViewById(R.id.content);
        frameLayout.addView(view);
        super.setContentView(viewRoot);
    }

    @Override
    protected void onDestroy() {
        AndroidApplication.removeActivity(this);
        if (hintDialog != null && hintDialog.isShowing()) {
            hintDialog.dismiss();
        }
        if (askDialog != null && askDialog.isShowing()) {
            askDialog.dismiss();
        }
        if (warnDialog != null && warnDialog.isShowing()) {
            warnDialog.dismiss();
        }
        super.onDestroy();

        if (BuildConfig.isCheckMemoryLeak) {
            RefWatcher refWatcher = AndroidApplication.getRefWatcher();
            if (refWatcher != null) {
                refWatcher.watch(this);
            }
        }
    }

    @Override
    public void onBackPressed() {
        LogUtil.d(TAG, "onBackPressed");
        if (onBackPressedListener != null) {
            LogUtil.d(TAG, "onBackPressedListener is not null");
            onBackPressedListener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        Context context = ContextWrapper.wrap(newBase, ResUtil.getLocale());
//        super.attachBaseContext(context);
//    }

    public UINavigator getUiNavigator() {
        return uiNavigator;
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    public void initToolbar(String title, float titleTextSize, String subTitle, float subTitleTextSize) {
        if (titleTextSize > 0) {
            tvTitle.setTextSize(titleTextSize);
        }

        if (subTitleTextSize > 0) {
        }
        initToolbar(title, subTitle);
    }

    public void initToolbar(String title, String subtitle) {
        findViewById(R.id.toolbar).setVisibility(View.VISIBLE);

        tvTitle.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }

        if (!TextUtils.isEmpty(subtitle)) {
        }
    }

    public void setBackGround(int resId) {
        llBackgroud.setBackgroundResource(resId);
    }

    @Override
    public void showToastMessage(String msg) {
        ToastUtil.showToastShort(msg);
    }

    @Override
    public void showToastMessage(int id) {
        ToastUtil.showToastShort(getString(id));
    }

    @Override
    public void showTitle(String title, float titleTextSize, String subTitle, float subTitleTextSize) {
        initToolbar(title, titleTextSize, subTitle, subTitleTextSize);
    }

    @Override
    public void navigatorToNextStep() {
        uiNavigator.navigatorToNextUI(this);
    }

    @Override
    public void setLoadingDialogStatus(boolean isShow) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            doSetLoadingDialogStatus(isShow, "");
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    doSetLoadingDialogStatus(isShow, "");
                }
            });
        }
    }

    @Override
    public void setLoadingDialogStatus(boolean isShow, String processingHint) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            doSetLoadingDialogStatus(isShow, processingHint);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    doSetLoadingDialogStatus(isShow, processingHint);
                }
            });
        }
    }

    @Override
    public void setProcessingDialogStatus(boolean isShow, String processingHint) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            doSetProcessingDialogStatus(isShow, processingHint);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    doSetProcessingDialogStatus(isShow, processingHint);
                }
            });
        }
    }

    @Override
    public Dialog showHintDialog(String hintMsg, CountdownDialog.DialogType type, int timeoutSecond, DialogUtil.TimeoutListener listener) {
        if (hintDialog != null && hintDialog.isShowing()) {
            hintDialog.dismiss();
        }

        switch (type) {
            case TEXT_ONLY:
                hintDialog = DialogUtil.showHintDialog(this, hintMsg, timeoutSecond, listener);
                break;
            case FAIL:
                hintDialog = DialogUtil.showFailedDialog(this, hintMsg, timeoutSecond, listener);
                break;
            case SUCCESS:
                hintDialog = DialogUtil.showSuccessDialog(this, hintMsg, timeoutSecond, listener);
                break;
        }

        return hintDialog;
    }

    @Override
    public Dialog showAskDialog(String hintMsg, DialogUtil.AskDialogListener listener) {
        if (askDialog != null && askDialog.isShowing()) {
            askDialog.dismiss();
        }

        askDialog = DialogUtil.showAskDialog(this, hintMsg, listener);

        return askDialog;
    }

    @Override
    public Dialog showWarnDialog(String hintMsg, DialogUtil.WarnDialogListener listener) {
        if (warnDialog != null && warnDialog.isShowing()) {
            warnDialog.dismiss();
        }

        warnDialog = DialogUtil.showWarnDialog(this, hintMsg, listener);

        return warnDialog;
    }

    @Override
    public Dialog showCountDownAskDialog(String hintMsg, int timeoutSeconds, DialogUtil.AskDialogListener listener) {
        if (countDownAskDialog != null && countDownAskDialog.isShowing()) {
            countDownAskDialog.dismiss();
        }

        countDownAskDialog = DialogUtil.showCountDownAskDialog(this, hintMsg, timeoutSeconds, listener);
        return countDownAskDialog;
    }

    @Override
    public Dialog showCheckPasswordDialog(int operatorType, DialogUtil.PasswdDialogListener listener) {
        LogUtil.d(TAG, "showCheckPasswordDialog");
        if (passwordDialog != null && passwordDialog.isShowing()) {
            passwordDialog.dismiss();
        }

        passwordDialog = DialogUtil.showPasswordDialog(this, operatorType, listener);
        return passwordDialog;
    }

    @Override
    public void dismissCurrentDialog() {
        if (hintDialog != null && hintDialog.isShowing()) {
            hintDialog.dismiss();
        }
        if (askDialog != null && askDialog.isShowing()) {
            askDialog.dismiss();
        }
        if (warnDialog != null && warnDialog.isShowing()) {
            warnDialog.dismiss();
        }
        if (countDownAskDialog != null && countDownAskDialog.isShowing()) {
            countDownAskDialog.dismiss();
        }
        if (passwordDialog != null && passwordDialog.isShowing()) {
            passwordDialog.dismiss();
        }
    }

    private void doSetLoadingDialogStatus(boolean isShow, String processingHint) {
        LogUtil.d(TAG, "setLoadingDialogStatus is " + isShow);
        if (loadingDialog == null) {
            LogUtil.d(TAG, "loadingDialog is null");
            loadingDialog = new LoadingDialog(this);
        }
        LogUtil.d(TAG, "loadingDialog isShowing " + loadingDialog.isShowing());

        if (isShow) {
            loadingDialog.show();
            loadingDialog.setProcessingHint("" + processingHint);
        } else {
            loadingDialog.dismiss();
        }
    }

    private void doSetProcessingDialogStatus(boolean isShow, String processingHint) {

    }

    protected ApplicationComponent getApplicationComponent() {
        return ((AndroidApplication) getApplication()).getApplicationComponent();
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }

    /**
     * CUP休眠锁, 保持 CPU 运转防止设备休眠
     */
    protected void acquireWakeLock() {
        String tag = TAG;
        Log.i(TAG, "acquireWakeLock: " + tag);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag);
        mWakeLock.acquire();

    }

    /**
     * 释放CUP休眠锁
     */
    protected void releaseWakeLock() {
        Log.i(TAG, "releaseWakeLock: ");
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    private void hideFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        List<Fragment> fragments = fragmentManager.getFragments();
        Iterator<Fragment> iterator = fragments.iterator();
        while (iterator.hasNext()) {
            Fragment fragment = iterator.next();
            if (fragment.isVisible()) {
                transaction = transaction.hide(fragment);
            }
        }

        transaction.commit();
    }

    private Fragment getFragmentInstance(Class fragmentClass) {
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

    @Override
    public void showFragment(Class fragmentClass, int resContainerId, boolean isAddToBackStack) {
        if (fragmentClass == null) {
            return;
        }

        final String nextFragmentTag = fragmentClass.getName();

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment nextFragment = fragmentManager.findFragmentByTag(nextFragmentTag);
        if (nextFragment == null) {
            nextFragment = getFragmentInstance(fragmentClass);
            if (nextFragment == null) {
                LogUtil.d(TAG, "new Fragment instance failed." + fragmentClass.getSimpleName());
                return;
            }
        }

        hideFragments();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (nextFragment.isAdded()) {
            transaction = transaction.show(nextFragment);
        } else {
            transaction = transaction.add(resContainerId, nextFragment, nextFragmentTag);
        }

        if (isAddToBackStack) {
            transaction = transaction.addToBackStack(nextFragmentTag);
        }

        transaction.commit();
    }

    @Override
    public void replaceFragment(Class fragmentClass, int resContainerId, boolean isAddToBackStack) {
        if (fragmentClass == null) {
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        final String fragmentTag = fragmentClass.getName();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment == null) {
            fragment = getFragmentInstance(fragmentClass);
            if (fragment == null) {
                LogUtil.d(TAG, "new Fragment instance failed." + fragmentClass.getSimpleName());
                return;
            }
        }
//        Fragment fragment = getFragmentInstance(fragmentClass);
//        if (fragment == null) {
//            LogUtil.d(TAG, "new Fragment instance failed." + fragmentClass.getSimpleName());
//            return;
//        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (isAddToBackStack) {
            transaction = transaction.addToBackStack(fragmentTag);
        }

        transaction.replace(resContainerId, fragment)
                .commit();
    }

    @Override
    public void removeFragment(Class fragmentClass) {
        if (fragmentClass == null) {
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        final String fragmentTag = fragmentClass.getName();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(fragment)
                    .commit();
        }
        fragmentManager.popBackStack();
    }

    @Override
    public void popBackStack(String name, int flags) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(name, flags);
    }

    public void controlHomeKeyAndStatusBar(boolean isEnable) {
//        useCaseControlHomeKeyStatusBar.execute(isEnable);
    }
}
