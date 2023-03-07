package com.vfi.android.payment.presentation.view.activities;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.models.SettlementResultModel;
import com.vfi.android.payment.presentation.presenters.TransSuccessPresenter;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.adapters.SettlementResultAdapter;
import com.vfi.android.payment.presentation.view.contracts.TransSuccessUI;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransSuccessActivity extends BaseTransFlowActivity<TransSuccessUI> implements TransSuccessUI {

    @BindView(R.id.iv_trans_result_icon)
    ImageView iv_trans_result_icon;
    @BindView(R.id.btn_success)
    Button btn_success;
    @BindView(R.id.tv_show_amount)
    TextView tv_show_amount;
    @BindView(R.id.tv_show_currency_symbol)
    TextView tv_show_currency_symbol;
    @BindView(R.id.ll_settlement_result_detail)
    LinearLayout ll_settlement_result_detail;
    @BindView(R.id.recyclerview_settlement_result_list)
    RecyclerView recyclerview_settlement_result_list;
    @BindView(R.id.lv_normal_trans_success)
    LinearLayout lvNormalTransSuccess;
    @BindView(R.id.tv_result_hint)
    TextView tv_result_hint;

    @Inject
    TransSuccessPresenter transSuccessPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_success);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(transSuccessPresenter, this);
    }

    private void initView() {
        setBackGround(R.drawable.background_dark);
        btn_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transSuccessPresenter.doClickBtnSuccessProcess();
            }
        });
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
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
                    transSuccessPresenter.startPrintSlip(true);
                } else {
                    transSuccessPresenter.clearPrintBuffer();
                    if (isNeedBackToMainMenu) {
                        navigatorToNextStep();
                    } else {
                        transSuccessPresenter.doPostTransProcessing();
                    }
                }
            }
        });
    }

    @Override
    public void showButtonText(String buttonText) {
        btn_success.setText(buttonText);
    }

    @Override
    public void showSettlementResultList(List<SettlementResultModel> settlementResultModelList) {
        ll_settlement_result_detail.setVisibility(View.VISIBLE);
        SettlementResultAdapter settlementResultAdapter = new SettlementResultAdapter(settlementResultModelList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        recyclerview_settlement_result_list.setLayoutManager(gridLayoutManager);
        recyclerview_settlement_result_list.setAdapter(settlementResultAdapter);
    }

    @Override
    public void showIPPView(String interest, String paymentTerm) {
    }

    @Override
    public void showRedeemInfo(boolean isShowAmountLayout, boolean isShowPointTitle, String amount, String point) {
    }

    @Override
    public void hideAmount() {
        lvNormalTransSuccess.setVisibility(View.GONE);
    }

    @Override
    public void showSettlementResultIcon(boolean isAllSettleSuccess) {
        if (isAllSettleSuccess) {
            iv_trans_result_icon.setImageResource(R.drawable.icon_success);
            tv_result_hint.setText(ResUtil.getString(R.string.tv_hint_success));
        } else {
            iv_trans_result_icon.setImageResource(R.drawable.icon_failed);
            tv_result_hint.setText(ResUtil.getString(R.string.tv_hint_failed));
        }
    }
}
