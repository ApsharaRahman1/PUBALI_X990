package com.vfi.android.payment.presentation.view.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.ElectronicSignPresenter;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.contracts.ElectronicSignUI;
import com.vfi.android.payment.presentation.view.widget.ESignBoradSurfaceView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ElectronicSignActivity extends BaseTransFlowActivity<ElectronicSignUI> implements ElectronicSignUI {
    @BindView(R.id.signArea)
    FrameLayout signArea;
    @BindView(R.id.btn_next)
    Button btn_next;
    @BindView(R.id.tv_show_acount)
    TextView tv_show_acount;
    @BindView(R.id.tv_amount)
    TextView tv_amount;
    @BindView(R.id.btn_print_slip)
    Button btn_print_slip;
    @BindView(R.id.lv_card)
    LinearLayout lv_card;
    @BindView(R.id.imageview_resign)
    ImageView imageview_resign;

    @Inject
    ElectronicSignPresenter electronicSignPresenter;

    private ESignBoradSurfaceView surfaceView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electronic_sign);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        surfaceView = new ESignBoradSurfaceView(this, "", 660, 660, 300);
        signArea.addView(surfaceView);

        btn_next.setOnClickListener(v -> {
            electronicSignPresenter.saveESginData(surfaceView.getCompressedESignData(), false);
        });

        btn_print_slip.setOnClickListener(v -> {
            electronicSignPresenter.saveESginData(surfaceView.getCompressedESignData(), true);
        });

        imageview_resign.setOnClickListener(v -> {
            surfaceView.resetCanvas();
        });
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(electronicSignPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void showTransInfo(String acount, String amount) {
        lv_card.setVisibility(View.VISIBLE);
        tv_show_acount.setText(acount);
        tv_amount.setText(amount);
    }


    @Override
    public void clearESignBoard() {
        surfaceView.resetCanvas();
    }

    @Override
    public void onBackPressed() {
        // Disable return key
    }
}
