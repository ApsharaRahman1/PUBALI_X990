package com.vfi.android.payment.presentation.view.fragments.base;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


import com.vfi.android.payment.presentation.AndroidApplication;
import com.vfi.android.payment.presentation.internal.di.components.CommonComponent;
import com.vfi.android.payment.presentation.internal.di.components.DaggerCommonComponent;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ToastUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseActivity;
import com.vfi.android.payment.presentation.view.contracts.base.UI;
import com.vfi.android.payment.presentation.view.widget.CountdownDialog;

import javax.inject.Inject;

public class BaseFragment extends Fragment implements UI {

    @Inject
    UINavigator uiNavigator;


    private Dialog hintDialog;
    private Dialog askDialog;
    private Dialog warnDialog;
    private Dialog countDownAskDialog;
    private Dialog passwordDialog;

    public BaseFragment() {
        CommonComponent commonComponent;
        commonComponent = DaggerCommonComponent.builder()
                .applicationComponent(AndroidApplication.getInstance().getApplicationComponent())
                .build();

        commonComponent.inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void initAgodaView() {

    }

    @Override
    public void showToastMessage(String msg) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.showToastMessage(msg);
        }
    }

    @Override
    public void showToastMessage(int id) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.showToastMessage(id);
        }
    }

    @Override
    public void showTitle(String title, float titleTextSize, String subTitle, float subTitleTextSize) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.showTitle(title, titleTextSize, subTitle, subTitleTextSize);
        }
    }


    @Override
    public void navigatorToNextStep() {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.navigatorToNextStep();
        }
    }

    @Override
    public void setLoadingDialogStatus(boolean isShow) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.setLoadingDialogStatus(isShow);
        }
    }

    @Override
    public void setLoadingDialogStatus(boolean isShow, String processingHint) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.setLoadingDialogStatus(isShow, processingHint);
        }
    }

    @Override
    public void setProcessingDialogStatus(boolean isShow, String processingHint) {

    }

    @Override
    public Dialog showHintDialog(String hintMsg, CountdownDialog.DialogType type, int timeoutSecond, DialogUtil.TimeoutListener listener) {
        if (hintDialog != null && hintDialog.isShowing()) {
            hintDialog.dismiss();
        }

        switch (type) {
            case TEXT_ONLY:
                hintDialog = DialogUtil.showHintDialog(getActivity(), hintMsg, timeoutSecond, listener);
                break;
            case FAIL:
                hintDialog = DialogUtil.showFailedDialog(getActivity(), hintMsg, timeoutSecond, listener);
                break;
            case SUCCESS:
                hintDialog = DialogUtil.showSuccessDialog(getActivity(), hintMsg, timeoutSecond, listener);
                break;
        }

        return hintDialog;
    }

    @Override
    public Dialog showAskDialog(String hintMsg, DialogUtil.AskDialogListener listener) {
        if (askDialog != null && askDialog.isShowing()) {
            askDialog.dismiss();
        }

        askDialog = DialogUtil.showAskDialog(getActivity(), hintMsg, listener);

        return askDialog;
    }

    @Override
    public Dialog showWarnDialog(String hintMsg, DialogUtil.WarnDialogListener listener) {
        if (warnDialog != null && warnDialog.isShowing()) {
            warnDialog.dismiss();
        }

        warnDialog = DialogUtil.showWarnDialog(getActivity(), hintMsg, listener);

        return warnDialog;
    }

    @Override
    public Dialog showCountDownAskDialog(String hintMsg, int timeoutSeconds, DialogUtil.AskDialogListener listener) {
        if (countDownAskDialog != null && countDownAskDialog.isShowing()) {
            countDownAskDialog.dismiss();
        }

        countDownAskDialog = DialogUtil.showCountDownAskDialog(getActivity(), hintMsg, timeoutSeconds, listener);
        return countDownAskDialog;
    }

    @Override
    public Dialog showCheckPasswordDialog(int operatorType, DialogUtil.PasswdDialogListener listener) {
        if (passwordDialog != null && passwordDialog.isShowing()) {
            passwordDialog.dismiss();
        }

        passwordDialog = DialogUtil.showPasswordDialog(getActivity(), operatorType, listener);
        return passwordDialog;
    }

    @Override
    public void dismissCurrentDialog() {
        if (getActivity() != null && getActivity() instanceof UI) {
            UI ui = (UI) getActivity();
            ui.dismissCurrentDialog();
        }
    }

    @Override
    public void showFragment(Class fragmentClass, int resContainerId, boolean isAddToBackStack) {
        if (getActivity() != null && getActivity() instanceof UI) {
            UI ui = (UI) getActivity();
            ui.showFragment(fragmentClass, resContainerId, isAddToBackStack);
        }
    }

    @Override
    public void replaceFragment(Class fragmentClass, int resContainerId, boolean isAddToBackStack) {
        if (getActivity() != null && getActivity() instanceof UI) {
            UI ui = (UI) getActivity();
            ui.replaceFragment(fragmentClass, resContainerId, isAddToBackStack);
        }
    }

    @Override
    public void removeFragment(Class fragmentClass) {
        if (getActivity() != null && getActivity() instanceof UI) {
            UI ui = (UI) getActivity();
            ui.removeFragment(fragmentClass);
        }
    }

    @Override
    public void popBackStack(String name, int flags) {
        if (getActivity() != null && getActivity() instanceof UI) {
            UI ui = (UI) getActivity();
            ui.popBackStack(name, flags);
        }
    }


    public void hideIputKeyboard() {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager mInputKeyBoard = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (activity.getCurrentFocus() != null) {
                        mInputKeyBoard.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    }
                }
            });
        }
    }
}
