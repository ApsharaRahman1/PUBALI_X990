package com.vfi.android.payment.presentation.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.WindowManager;
import android.widget.TextView;

import com.vfi.android.payment.R;


public class LoadingDialog extends Dialog {
    TextView processHintTextView;

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.loadingDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setCancelable(false); // 不让点击返回键
        setContentView(R.layout.dialog_loading_indicator);
        processHintTextView = findViewById(R.id.tv_processHint);
    }

    public void setProcessingHint(String processingHint) {
        if (processHintTextView != null) {
            processHintTextView.setText(processingHint);
        }
    }
}
