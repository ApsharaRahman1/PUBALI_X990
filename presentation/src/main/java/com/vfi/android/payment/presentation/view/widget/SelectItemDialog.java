package com.vfi.android.payment.presentation.view.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.vfi.android.payment.BuildConfig;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.view.adapters.SelectItemAdapter;


public class SelectItemDialog extends AlertDialog {
    private RecyclerView recyclerview_select_items;
    private String[] selectItems;
    private String title;
    private OnSelectListener onSelectListener;
    private SelectItemAdapter selectItemAdapter;
    private TextView tv_title;
    private LinearLayout ll_title_area;
    private Space space;

    public SelectItemDialog(@NonNull Context context, String title, String[] items) {
        super(context, R.style.loadingDialog);
        this.selectItems = items;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false); // 不让点击返回键

        if (!BuildConfig.isAllowScreenShot) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawableResource(R.drawable.background);
        getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.dialog_select_item);
        initView();
    }

    private void initView() {
        selectItemAdapter = new SelectItemAdapter(selectItems);
        selectItemAdapter.setOnItemClickListener(new SelectItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (onSelectListener != null) {
                    onSelectListener.onSelected(position);
                }
            }
        });
        recyclerview_select_items = findViewById(R.id.recyclerview_select_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerview_select_items.setLayoutManager(linearLayoutManager);
        recyclerview_select_items.setAdapter(selectItemAdapter);
    }

    public OnSelectListener getOnSelectListener() {
        return onSelectListener;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public interface OnSelectListener {
        void onSelected(int itemIdx);
    }
}
