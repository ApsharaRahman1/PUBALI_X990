package com.vfi.android.payment.presentation.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;

import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.view.widget.ConfirmCardInfoDialog;
import com.vfi.android.payment.presentation.view.widget.CountdownDialog;
import com.vfi.android.payment.presentation.view.widget.CustomDialog;
import com.vfi.android.payment.presentation.view.widget.EditParamDialog;
import com.vfi.android.payment.presentation.view.widget.InputPasswdDialog;
import com.vfi.android.payment.presentation.view.widget.InputTextDialog;
import com.vfi.android.payment.presentation.view.widget.SelectItemDialog;

/**
 * Created by CuncheW1 on 2017/4/20.
 */

public class DialogUtil {
    private static final String TAG = "DialogUtil";

    public static synchronized Dialog showAskDialog(Context context, String message, final AskDialogListener listener) {
        CustomDialog dialog = new CustomDialog(context, message);
        dialog.show();
        dialog.setDialogListener(new CustomDialog.DialogListener() {
            @Override
            public void onClick(boolean isLeftBtnClicked) {
                if (listener != null) {
                    boolean isSure = true;
                    if (isLeftBtnClicked) {
                        isSure = false;
                    }
                    listener.onClick(isSure);
                }
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static synchronized Dialog showAskDialog(Context context, String message, String negativeButtonText, String positiveButtonText, final AskDialogListener listener) {
        CustomDialog dialog = new CustomDialog(context, CustomDialog.DIALOG_TYPE_ASK, message, negativeButtonText, positiveButtonText);
        dialog.show();
        dialog.setDialogListener(new CustomDialog.DialogListener() {
            @Override
            public void onClick(boolean isLeftBtnClicked) {
                if (listener != null) {
                    boolean isSure = true;
                    if (isLeftBtnClicked) {
                        isSure = false;
                    }
                    listener.onClick(isSure);
                }
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static synchronized Dialog showCountDownAskDialog(Context context, String message, int timeoutSeconds, final AskDialogListener listener) {
        CountdownDialog dialog = new CountdownDialog(context, message, timeoutSeconds, listener);
        dialog.show();
        return dialog;
    }

    public static synchronized Dialog showWarnDialog(Context context, String message, final WarnDialogListener listener) {
        CustomDialog dialog = new CustomDialog(context, CustomDialog.DIALOG_TYPE_WARN, message);
        dialog.show();
        dialog.setDialogListener(new CustomDialog.DialogListener() {
            @Override
            public void onClick(boolean isLeftBtnClicked) {
                if (listener != null) {
                    boolean isSure = true;
                    if (isLeftBtnClicked) {
                        isSure = false;
                    }
                    listener.onClick();
                }
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static synchronized Dialog showRemoveCardDialog(Context context, final WarnDialogListener listener) {
        String message = ResUtil.getString(R.string.tv_hint_remove_card);
        CustomDialog dialog = new CustomDialog(context, CustomDialog.DIALOG_TYPE_REMOVE_CARD, message);
        dialog.show();
        dialog.setDialogListener(isLeftBtnClicked -> {
            if (listener != null) {
                boolean isSure = true;
                if (isLeftBtnClicked) {
                    isSure = false;
                }
                listener.onClick();
            }
            dialog.dismiss();
        });

        return dialog;
    }

    public static synchronized Dialog showWarnDialog(Context context, String message, String buttonText, final WarnDialogListener listener) {
        CustomDialog dialog = new CustomDialog(context, message, buttonText);
        dialog.show();
        dialog.setDialogListener(new CustomDialog.DialogListener() {
            @Override
            public void onClick(boolean isLeftBtnClicked) {
                if (listener != null) {
                    boolean isSure = true;
                    if (isLeftBtnClicked) {
                        isSure = false;
                    }
                    listener.onClick();
                }
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static synchronized Dialog showHintDialog(Context context, String hintMsg, int timeoutSecond, TimeoutListener listener) {
        return showCountDownDialog(context, CountdownDialog.DialogType.TEXT_ONLY, hintMsg, timeoutSecond, listener);
    }

    public static synchronized Dialog showSuccessDialog(Context context, String hintMsg, int timeoutSecond, TimeoutListener listener) {
        return showCountDownDialog(context, CountdownDialog.DialogType.SUCCESS, hintMsg, timeoutSecond, listener);
    }

    public static synchronized Dialog showFailedDialog(Context context, String hintMsg, int timeoutSecond, TimeoutListener listener) {
        return showCountDownDialog(context, CountdownDialog.DialogType.FAIL, hintMsg, timeoutSecond, listener);
    }

    private static synchronized Dialog showCountDownDialog(Context context, CountdownDialog.DialogType dialogType, String hintMsg, int timeoutSecond, TimeoutListener listener) {
        CountdownDialog dialog = new CountdownDialog(context, timeoutSecond, dialogType);
        dialog.setMessage(hintMsg);
        dialog.setOnCountFinishedListener(() -> {
            if (listener != null) {
                try {
                    listener.onFinished();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        showDialog(dialog);
        return dialog;
    }

    public static synchronized Dialog showSelectItemDialog(Context context, String title, String[] items, final SelectDialogListener listener) {
        SelectItemDialog dialog = new SelectItemDialog(context, title, items);
        dialog.setOnSelectListener(new SelectItemDialog.OnSelectListener() {
            @Override
            public void onSelected(int itemIdx) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onSelect(itemIdx);
                }
            }
        });
        showDialog(dialog);
        return dialog;
    }

    public static synchronized Dialog showInputDialog(Context context, String title, final InputDialogListener listener) {
        InputTextDialog dialog = new InputTextDialog(context, title);
        dialog.setCancelable(false);
        dialog.setOnClickListener((inputText, isConfirm) -> {
            if (listener != null) {
                listener.onInput(inputText, isConfirm);
            }
        });
        dialog.show();

        return dialog;
    }

    public static synchronized Dialog showInputNumberDialog(Context context, String title, final InputDialogListener listener) {
        InputTextDialog dialog = new InputTextDialog(context, title, true);
        dialog.setCancelable(false);
        dialog.setOnClickListener((inputText, isConfirm) -> {
            if (listener != null) {
                listener.onInput(inputText, isConfirm);
            }
        });
        dialog.show();

        return dialog;
    }

    /**
     * require user to input a password, and check it.
     * @param context
     * @param operatorType {@link com.vfi.android.domain.entities.databeans.OperatorInfo}
     * @param listener
     * @return
     */
    public static Dialog showPasswordDialog(Context context, int operatorType, PasswdDialogListener listener) {
        InputPasswdDialog dialog = new InputPasswdDialog(context, operatorType);
        dialog.setCancelable(false);
        dialog.setOnClickListener(isCorrect -> {
            if (listener != null) {
                listener.checkPasswdResult(isCorrect);
                if (!isCorrect) {
                    showPasswordDialog(context, operatorType, listener);
                }
            }
        });
        dialog.show();

        return dialog;
    }

    public static Dialog showEditParameterDialog(Context context, String paramName, String oldParamValue, int paramFormat, EditParamListener listener) {
        EditParamDialog dialog = new EditParamDialog(context, paramName, oldParamValue, paramFormat);
        dialog.setCancelable(false);
        dialog.setOnClickListener(newParameter -> {
            if (listener != null) {
                listener.onChanged(newParameter);
            }
        });
        dialog.show();

        return dialog;
    }

    public static Dialog showCardConfirmDialog(Context context, String pan, String expireDate, AskDialogListener listener) {
        ConfirmCardInfoDialog dialog = new ConfirmCardInfoDialog(context, pan, expireDate);
        dialog.setCancelable(false);
        dialog.setDialogListener( isConfirm -> {
            if (listener != null) {
                listener.onClick(isConfirm);
            }
            dialog.dismiss();
        });
        dialog.show();

        return dialog;
    }

    private static void showDialog(final AlertDialog.Builder builder) {
        final AlertDialog dialog = builder.create();
        showDialog(dialog);
    }

    private static void showDialog(Dialog dialog) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            dialog.show();
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
        }
    }

    public interface InputDialogListener {
        void onInput(String inputStr, boolean isConfirm);
    }

    public interface AskDialogListener {
        void onClick(boolean isSure);
    }

    public interface WarnDialogListener {
        void onClick();
    }

    public interface SelectDialogListener {
        void onSelect(int index);
    }

    public interface ConfirmClickListener {
        void onClick();
    }

    public interface TimeoutListener {
        void onFinished();
    }

    public interface PasswdDialogListener {
        void checkPasswdResult(boolean isCorrectPassword);
    }

    public interface EditParamListener {
        void onChanged(String newParam);
    }
}

