package com.vfi.android.payment.presentation.view.activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.models.SettlementResultModel;
import com.vfi.android.payment.presentation.presenters.AutoSettlementPresenter;
import com.vfi.android.payment.presentation.utils.AndroidUtil;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseMvpActivity;
import com.vfi.android.payment.presentation.view.adapters.SettlementResultAdapter;
import com.vfi.android.payment.presentation.view.contracts.AutoSettlementUI;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AutoSettlementActivity extends BaseMvpActivity<AutoSettlementUI> implements AutoSettlementUI {
    @BindView(R.id.iv_trans_result_icon)
    ImageView iv_trans_result_icon;
    @BindView(R.id.tv_progress_msg)
    TextView tv_progress_msg;
    @BindView(R.id.imageview_hourglass)
    ImageView imageview_hourglass;
    @BindView(R.id.ll_settle_network)
    LinearLayout ll_settle_network;

    @BindView(R.id.btn_success)
    Button btn_success;
    @BindView(R.id.tv_show_amount)
    TextView tv_show_amount;
    @BindView(R.id.tv_show_currency_symbol)
    TextView tv_show_currency_symbol;
    @BindView(R.id.ll_settle_result)
    LinearLayout ll_settle_result;
    @BindView(R.id.recyclerview_settlement_result_list)
    RecyclerView recyclerview_settlement_result_list;
    @BindView(R.id.tv_result_hint)
    TextView tv_result_hint;

    ObjectAnimator hourglassRotation;

    @Inject
    AutoSettlementPresenter autoSettlementPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_auto_settlement);
        ButterKnife.bind(this);
        setBackGround(R.drawable.background_dark);
        btn_success.setOnClickListener(v -> backToMainMenu());
    }

    @Override
    protected void onResume() {
        super.onResume();

        hourglassRotation = ObjectAnimator.ofFloat(imageview_hourglass, "rotation", 0, 360).setDuration(1200);
        hourglassRotation.setInterpolator(input -> input > 2f / 3 ? 1 : 1.5f * input);
        hourglassRotation.setRepeatCount(ValueAnimator.INFINITE);
        hourglassRotation.setRepeatMode(ValueAnimator.RESTART);
        hourglassRotation.start();
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(autoSettlementPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void onBackPressed() {
        // Disable return key
    }

    @Override
    public void setProcessHint(String hintMsg) {
        tv_progress_msg.setText("" + hintMsg);
    }

    @Override
    public void showReversalFailedDialog(String msg) {
        DialogUtil.showAskDialog(this, msg, new DialogUtil.AskDialogListener() {
            @Override
            public void onClick(boolean isNeedDoReversalAgain) {
                autoSettlementPresenter.doReversalAgain(isNeedDoReversalAgain);
            }
        });
    }

    @Override
    public void showAmount(String amount, String currencySymbol) {
        tv_show_amount.setText(amount);
        tv_show_currency_symbol.setText(currencySymbol);
    }

    @Override
    public void showPrintFailedDialog(String errMsg, boolean isNeedBackToMainMenu) {
        String negativeButtonText = ResUtil.getString(R.string.btn_hint_cancel);
        String positiveButtonText = ResUtil.getString(R.string.btn_hint_reprint);
        DialogUtil.showAskDialog(this, errMsg, negativeButtonText, positiveButtonText, new DialogUtil.AskDialogListener() {
            @Override
            public void onClick(boolean isSure) {
                if (isSure) {
                    autoSettlementPresenter.startPrintSettlementSlip(true);
                } else {
                    autoSettlementPresenter.clearPrintBuffer();
                    autoSettlementPresenter.startBackToMainMenuTimer();
                }
            }
        });
    }

    @Override
    public void showSettlementResultList(List<SettlementResultModel> settlementResultModelList) {
        SettlementResultAdapter settlementResultAdapter = new SettlementResultAdapter(settlementResultModelList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        recyclerview_settlement_result_list.setLayoutManager(gridLayoutManager);
        recyclerview_settlement_result_list.setAdapter(settlementResultAdapter);
    }

    @Override
    public void backToMainMenu() {
        finish();
        AndroidUtil.startActivity(this, MainMenuActivity.class);
    }

    @Override
    public void showTransResult(boolean isAllSettleSuccess) {
        if (isAllSettleSuccess) {
            iv_trans_result_icon.setImageResource(R.drawable.icon_success);
            tv_result_hint.setText(ResUtil.getString(R.string.tv_hint_success));
        } else {
            iv_trans_result_icon.setImageResource(R.drawable.icon_failed);
            tv_result_hint.setText(ResUtil.getString(R.string.tv_hint_failed));
        }
        hourglassRotation.cancel();
        ll_settle_network.setVisibility(View.GONE);
        ll_settle_result.setVisibility(View.VISIBLE);
    }
}
