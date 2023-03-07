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

public class ConfirmCardInfoDialog extends AlertDialog {
    private final String TAG = TAGS.UILevel;

    private DialogListener dialogListener;

    private String leftBtnText;
    private String rightBtnText;
    private String pan;
    private String expireDate;

    public ConfirmCardInfoDialog(Context context, String pan, String expireDate) {
        super(context, R.style.loadingDialog);
        String leftBtnText = ResUtil.getString(R.string.button_cancel);
        String rightBtnText = ResUtil.getString(R.string.btn_hint_confirm);
        this.leftBtnText = leftBtnText;
        this.rightBtnText = rightBtnText;
        this.pan = pan;
        this.expireDate = expireDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.isAllowScreenShot) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        initDialog(getContext(), leftBtnText, rightBtnText);
    }

    public void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    private void initDialog(Context context, String leftBtnHint, String rightBtnHint) {
        setContentView(R.layout.dialog_confirm_card_info );
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        TextView tv_show_card_num = findViewById(R.id.tv_show_card_num);
        TextView tv_show_card_expiry_date = findViewById(R.id.tv_show_card_expiry_date);

        tv_show_card_num.setText(pan);
        tv_show_card_expiry_date.setText(expireDate + "(YYMM)");

        Button btn_dialog_left = findViewById(R.id.btn_dialog_left);
        Button btn_dialog_right = findViewById(R.id.btn_dialog_right);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int)(DensityUtil.getScreenWidth(context) * 0.9);
        lp.height = (int) (lp.width * 0.7);
        getWindow().setAttributes(lp);

        btn_dialog_left.setText(leftBtnHint);
        btn_dialog_left.setOnClickListener(v -> {
            if (dialogListener != null) {
                dialogListener.onClick(false);
            }
        });

        btn_dialog_right.setText(rightBtnHint);
        btn_dialog_right.setOnClickListener(v -> {
            if (dialogListener != null) {
                dialogListener.onClick(true);
            }
        });
    }

    public interface DialogListener {
        public void onClick(boolean isConfirm);
    }
}
