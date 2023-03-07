package com.vfi.android.payment.presentation.view.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.BuildConfig;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.DensityUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;

public class CustomDialog extends AlertDialog {
    private final String TAG = TAGS.UILevel;

    public static final int DIALOG_TYPE_ASK = 1;
    public static final int DIALOG_TYPE_WARN = 2;
    public static final int DIALOG_TYPE_WARM_WITH_BTN = 3;
    public static final int DIALOG_TYPE_COUNT_DOWN = 4;
    public static final int DIALOG_TYPE_REMOVE_CARD = 5;

    private DialogListener dialogListener;

    private int dialogType;
    private String leftBtnText;
    private String rightBtnText;
    private String hintMsg;

    public CustomDialog(Context context, String msg) {
        super(context, R.style.loadingDialog);
        String leftBtnText = ResUtil.getString(R.string.button_cancel);
        String rightBtnText = ResUtil.getString(R.string.button_confirm);
        this.dialogType = DIALOG_TYPE_ASK;
        this.hintMsg = msg;
        this.leftBtnText = leftBtnText;
        this.rightBtnText = rightBtnText;
    }

    public CustomDialog(Context context, int dialogType, String msg) {
        super(context, R.style.loadingDialog);
        String leftBtnText = ResUtil.getString(R.string.button_cancel);
        String rightBtnText = ResUtil.getString(R.string.button_confirm);
        this.dialogType = dialogType;
        this.hintMsg = msg;
        this.leftBtnText = leftBtnText;
        this.rightBtnText = rightBtnText;
    }

    public CustomDialog(Context context, String msg, int dialogType) {
        super(context, R.style.loadingDialog);
        this.dialogType = dialogType;
        this.hintMsg = msg;
        this.leftBtnText = "";
        this.rightBtnText = "";
    }

    public CustomDialog(Context context, String msg, String btnText) {
        super(context, R.style.loadingDialog);
        this.dialogType = DIALOG_TYPE_WARN;
        this.hintMsg = msg;
        this.leftBtnText = btnText;
        this.rightBtnText = "";
    }

    public CustomDialog(Context context, int dialogType, String msg, String leftBtnText, String rightBtnText) {
        super(context, R.style.loadingDialog);
        this.dialogType = dialogType;
        this.hintMsg = msg;
        this.leftBtnText = leftBtnText;
        this.rightBtnText = rightBtnText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "Dialog type=" + dialogType);
        if (!BuildConfig.isAllowScreenShot) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        initDialog(getContext(), dialogType, hintMsg, leftBtnText, rightBtnText);
    }

    public void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    private void initDialog(Context context, int dialogType, String msg, String leftBtnHint, String rightBtnHint) {
        setContentView(R.layout.dialog_custom);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int)(DensityUtil.getScreenWidth(context) * 0.9);
        lp.height = (int) (lp.width * 0.7);
        getWindow().setAttributes(lp);

        ImageView imageview_dialog_logo = findViewById(R.id.imageview_dialog_logo);
        TextView tv_dialog_msg = findViewById(R.id.tv_dialog_msg);
        View view_line = findViewById(R.id.view_line);
        Button btn_dialog_left = findViewById(R.id.btn_dialog_left);
        Button btn_dialog_right = findViewById(R.id.btn_dialog_right);
        LinearLayout dialog_btn_layout = findViewById(R.id.dialog_btn_layout);

        tv_dialog_msg.setText(msg);

        if (dialogType == DIALOG_TYPE_ASK) {
            imageview_dialog_logo.setImageResource(R.drawable.icon_ask);
        } else if (dialogType == DIALOG_TYPE_WARN) {
            btn_dialog_left.setVisibility(View.GONE);
            view_line.setVisibility(View.GONE);
            imageview_dialog_logo.setImageResource(R.drawable.icon_warn);
        } else if (dialogType == DIALOG_TYPE_WARM_WITH_BTN) {
            imageview_dialog_logo.setImageResource(R.drawable.icon_warn);
        } else if (dialogType == DIALOG_TYPE_REMOVE_CARD) {
            // Remove card no need left right button.
            imageview_dialog_logo.setImageResource(R.drawable.icon_warn);
            dialog_btn_layout.setVisibility(View.GONE);
            return;
        }

        btn_dialog_left.setText(leftBtnHint);
        btn_dialog_left.setOnClickListener(v -> {
            if (dialogListener != null) {
                dialogListener.onClick(true);
            }
        });

        btn_dialog_right.setText(rightBtnHint);
        btn_dialog_right.setOnClickListener(v -> {
            if (dialogListener != null) {
                dialogListener.onClick(false);
            }
        });
    }

    public interface DialogListener {
        public void onClick(boolean isLeftBtnClicked);
    }
}
