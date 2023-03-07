package com.vfi.android.payment.presentation.presenters.base;

import android.support.annotation.StringRes;

import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.view.contracts.base.UI;
import com.vfi.android.payment.presentation.view.widget.CountdownDialog;

import java.util.LinkedList;
import java.util.Queue;

public class BasePresenter<T extends UI> implements Presenter<T> {
    private final String TAG = "BasePresenter";
    private T ui;

    private Queue<UICommand<T>> commandQueue = new LinkedList<>();
    private UICommand<T> commandToRedeliver = null;
    private boolean uiAttachedBefore = false;

    @Override
    public void attachUI(T ui) {
        LogUtil.d(TAG, "============attachUI");
        if (ui != null) {
            LogUtil.d(TAG, "UI=" + ui.getClass().getSimpleName());
        }

        this.ui = ui;

        if (!this.uiAttachedBefore) {
            this.uiAttachedBefore = true;
            onFirstUIAttachment();
        }

        executeCommandQueue(true);
    }

    protected void onFirstUIAttachment() {
    }

    @Override
    public void detachUI() {
        LogUtil.d(TAG, "============detachUI");
        if (ui != null) {
            LogUtil.d(TAG, "UI=" + ui.getClass().getSimpleName());
        }

        this.ui = null;
    }

    protected void execute(UICommand<T> command) {
        commandQueue.add(command);
        executeCommandQueue(false);
    }

    protected void execute(UICommand<T> command, boolean redeliverOnAttachment) {
        commandQueue.add(command);
        executeCommandQueue(false);
        if (redeliverOnAttachment) {
            commandToRedeliver = command;
        }
    }

    private void executeCommandQueue(boolean attachment) {
        if (this.ui != null) {
            boolean commandToRedeliverExecuted = false;
            UICommand<T> command;
            while ((command = commandQueue.poll()) != null) {
                command.execute(this.ui);
                if (command == commandToRedeliver) {
                    commandToRedeliverExecuted = true;
                }
            }
            if (attachment && !commandToRedeliverExecuted && commandToRedeliver != null) {
                commandToRedeliver.execute(ui);
            }
        }

        if (!uiAttachedBefore) {
            LogUtil.e("Error", "Activity need call setupPresenter method first.");
            throw new RuntimeException("Activity/Fragment not call setupPresenter first.");
        }
    }

    public interface UICommand<T> {
        void execute(T ui);
    }

    public void doUICmd_showToastMessage(String msg) {
        execute(new UICommand<T>() {
            @Override
            public void execute(T ui) {
                ui.showToastMessage(msg);
            }
        });
    }

    public void doUICmd_showToastMessage(@StringRes int id) {
        execute(ui -> ui.showToastMessage(id));
    }

    public void doUICmd_showTitle(String title, String subTitle) {
        doUICmd_showTitle(title, 0, subTitle, 0);
    }

    public void doUICmd_showTitle(String title, float titleTextSize, String subTitle, float subTitleTextSize) {
        execute(ui -> ui.showTitle(title, titleTextSize, subTitle, subTitleTextSize));
    }

    public void doUICmd_showHintCountDownDialog(String hintMsg, int timoutSecond, DialogUtil.TimeoutListener listener) {
        execute(ui -> ui.showHintDialog(hintMsg, CountdownDialog.DialogType.TEXT_ONLY, timoutSecond, listener));
    }

    public void doUICmd_showSuccessCountDownDialog(String hintMsg, int timoutSecond, DialogUtil.TimeoutListener listener) {
        execute(ui -> ui.showHintDialog(hintMsg, CountdownDialog.DialogType.SUCCESS, timoutSecond, listener));
    }

    public void doUICmd_showFailedCountDownDialog(String hintMsg, int timoutSecond, DialogUtil.TimeoutListener listener) {
        execute(ui -> ui.showHintDialog(hintMsg, CountdownDialog.DialogType.FAIL, timoutSecond, listener));
    }

    public void doUICmd_showAskDialog(String hintMsg, DialogUtil.AskDialogListener listener) {
        execute(ui -> ui.showAskDialog(hintMsg, listener));
    }

    public void doUICmd_showCheckPasswordDialog(int operatorType, DialogUtil.PasswdDialogListener listener) {
        execute(ui -> ui.showCheckPasswordDialog(operatorType, listener));
    }

    public void doUICmd_showCountDownAskDialog(String hintMsg, int timeoutSeconds, DialogUtil.AskDialogListener listener) {
        execute(ui -> ui.showCountDownAskDialog(hintMsg, timeoutSeconds, listener));
    }

    public void doUICmd_dismissCurrentDialog() {
        execute(ui -> ui.dismissCurrentDialog());
    }

    public void doUICmd_setLoadingDialogStatus(boolean isShow) {
        execute(ui -> ui.setLoadingDialogStatus(isShow));
    }

    public void doUICmd_setLoadingDialogStatus(boolean isShow, String showText) {
        execute(ui -> ui.setLoadingDialogStatus(isShow, showText));
    }

    public void doUICmd_navigatorToNext() {
        execute(ui -> ui.navigatorToNextStep());
    }

    public void doUICmd_showFragment(Class fragmentClass, int resContainerId, boolean isAddToBackStack) {
        execute(ui -> ui.showFragment(fragmentClass, resContainerId, isAddToBackStack));
    }

    public void doUICmd_replaceFragment(Class fragmentClass, int resContainerId, boolean isAddToBackStack) {
        execute(ui -> ui.replaceFragment(fragmentClass, resContainerId, isAddToBackStack));
    }

    public void doUICmd_removeFragment(Class fragmentClass) {
        execute(ui -> ui.removeFragment(fragmentClass));
    }

    public void doUICmd_popBackStack(String name, int flags) {
        execute(ui -> ui.popBackStack(name, flags));
    }
}
