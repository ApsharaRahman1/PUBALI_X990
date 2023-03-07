package com.vfi.android.payment.presentation.view.contracts.base;

import android.app.Dialog;
import android.support.annotation.StringRes;

import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.view.widget.CountdownDialog;

/**
 * Base interface.
 * Presenter can call UI interface methods to control UI display.
 */
public interface UI {
    void showToastMessage(String msg);
    void showToastMessage(@StringRes int id);
    void showTitle(String title, float titleTextSize, String subTitle, float subTitleTextSize);
    void navigatorToNextStep();
    void setLoadingDialogStatus(boolean isShow);
    void setLoadingDialogStatus(boolean isShow, String loadingHint);
    void setProcessingDialogStatus(boolean isShow, String processingHint);

    Dialog showHintDialog(String hintMsg, CountdownDialog.DialogType type, int timeoutSecond, DialogUtil.TimeoutListener listener);
    Dialog showAskDialog(String hintMsg, DialogUtil.AskDialogListener listener);
    Dialog showWarnDialog(String hintMsg, DialogUtil.WarnDialogListener listener);
    Dialog showCountDownAskDialog(String hintMsg, int timeoutSeconds, DialogUtil.AskDialogListener listener);
    Dialog showCheckPasswordDialog(int operatorType, DialogUtil.PasswdDialogListener listener);
    void dismissCurrentDialog();

    void showFragment(Class fragmentClass, int resContainerId, boolean isAddToBackStack);
    void replaceFragment(Class fragmentClass, int resContainerId, boolean isAddToBackStack);
    void removeFragment(Class fragmentClass);
    void popBackStack(String name, int flags);
//
//    void showPasswdDialog(int passwdType, InputPasswdDialog.OnCheckPasswdListener listener); // passwdType please refer to InputPasswdDialog
//    void dismissPasswdDialog();
}
