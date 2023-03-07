package com.vfi.android.payment.presentation.view.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.consts.CurrencyCode;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.mappers.CurrencySelectorMapper;
import com.vfi.android.payment.presentation.models.OnClickDelayModel;
import com.vfi.android.payment.presentation.presenters.ShowTransInfoPresenter;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.ToastUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.contracts.ShowTransInfoUI;
import com.vfi.android.payment.presentation.view.itemdecorations.MyItemDecoration;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowTransInfoActivity extends BaseTransFlowActivity<ShowTransInfoUI> implements ShowTransInfoUI {

    @BindView(R.id.tv_show_amount)
    TextView tv_show_amount;
    @BindView(R.id.tv_show_currency_symbol)
    TextView tv_show_currency_symbol;
    @BindView(R.id.tv_show_acount)
    TextView tv_show_acount;
    @BindView(R.id.tv_card_holder)
    TextView tv_card_holder;
    @BindView(R.id.btn_cancel)
    Button btn_cancel;
    @BindView(R.id.btn_next)
    Button btn_next;
    @BindView(R.id.liearLayout_common)
    LinearLayout liearLayout_common;
    @BindView(R.id.liearLayout_tip_adjust)
    LinearLayout liearLayout_tip_adjust;
    @BindView(R.id.tv_show_base_amount)
    TextView tv_show_base_amount;
    @BindView(R.id.tv_show_tip_ajd)
    TextView tv_show_tip_ajd;
    @BindView(R.id.tv_show_total_amount)
    TextView tv_show_total_amount;
    @BindView(R.id.lv_ali_we)
    LinearLayout lv_ali_we;
    @BindView(R.id.lv_card)
    LinearLayout lv_card;
    @BindView(R.id.tv_show_trans)
    TextView tv_show_trans;
    @BindView(R.id.tv_payment_id)
    TextView tv_payment_id;
    @BindView(R.id.tv_pay)
    TextView tv_pay;
    @BindView(R.id.lv_thai_qr)
    LinearLayout lv_thai_qr;
    @BindView(R.id.tv_ref1)
    TextView tv_ref1;
    @BindView(R.id.tv_ref2)
    TextView tv_ref2;
    @BindView(R.id.tv_ref3)
    TextView tv_ref3;
    @BindView(R.id.tv_ali_title)
    TextView tv_ali_title;

    @BindView(R.id.tv_p_total_amount)
    TextView tv_p_total_amount;
    @BindView(R.id.tv_p_ajd)
    TextView tv_p_ajd;
    @BindView(R.id.tv_p_base_amount)
    TextView tv_p_base_amount;
    @BindView(R.id.tv_p_amount)
    TextView tv_p_amount;
    @BindView(R.id.tv_p_card_holder)
    TextView tv_p_card_holder;
    @BindView(R.id.tv_p_card)
    TextView tv_p_card;

    @Inject
    ShowTransInfoPresenter showTransInfoPresenter;

    private boolean isCheckPointTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_trans_info);
        ButterKnife.bind(this);
        initView();
    }


    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(showTransInfoPresenter, this);
    }

    private void initView() {
        btn_cancel.setOnClickListener(v -> {
            showTransInfoPresenter.cancelTrans();
        });
        btn_next.setOnClickListener(v -> {
            showTransInfoPresenter.confirmTrans();
        });
    }

    @Override
    public void onBackPressed() {
        // disable back press key function.
        // if trans is check point enable back key ---- modify by huan.lu
        if (isCheckPointTrans) {
         super.onBackPressed();
        }
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void showAmount(String amount, String currencySymbol) {
        liearLayout_common.setVisibility(View.VISIBLE);
        liearLayout_tip_adjust.setVisibility(View.GONE);
        tv_show_amount.setText(amount);
        tv_show_currency_symbol.setText(currencySymbol);
    }


    @Override
    public void showTipAdjustAmount(String baseAmount, String tipAJD, String totalAmount, RecordInfo recordInfo) {
        LogUtil.d("showTipAdjustAmount excuted.");
        liearLayout_common.setVisibility(View.GONE);
        lv_thai_qr.setVisibility(View.GONE);
        liearLayout_tip_adjust.setVisibility(View.VISIBLE);

        String amountUnit;
//        if (recordInfo.getCurrencyCodeDCC() != null && recordInfo.getAcquirerHostType() == HostType.DCC) {
//            amountUnit = CurrencySelectorMapper.view2ShowInMultilingual(recordInfo.getCurrencyCodeDCC());
//        } else {
//            amountUnit = CurrencySelectorMapper.view2ShowInMultilingual(CurrencySelectorMapper.THB);
//        }
        amountUnit = CurrencySelectorMapper.view2ShowInMultilingual(CurrencyCode.PHP);
        tv_show_base_amount.setText(baseAmount + " " + amountUnit);
        tv_show_tip_ajd.setText(tipAJD + " " + amountUnit);
        tv_show_total_amount.setText(totalAmount + " " + amountUnit);
    }

    @Override
    public void showCardInfo(String acount, String cardHolder) {
        lv_ali_we.setVisibility(View.GONE);
        lv_thai_qr.setVisibility(View.GONE);
        lv_card.setVisibility(View.VISIBLE);
        tv_show_acount.setText(acount);
        tv_card_holder.setText(cardHolder);
    }

    @Override
    public void showThaqiQrInfo(String ref1, String ref2, String ref3) {
        lv_card.setVisibility(View.GONE);
        lv_ali_we.setVisibility(View.GONE);
        lv_thai_qr.setVisibility(View.VISIBLE);
        tv_ref1.setText(ref1);
        tv_ref2.setText(ref2);
        tv_ref3.setText(ref3);
    }

    @Override
    public void showQrcsInfo() {
        lv_card.setVisibility(View.GONE);
        lv_ali_we.setVisibility(View.GONE);
        lv_thai_qr.setVisibility(View.GONE);
        btn_next.setVisibility(View.GONE);
        ToastUtil.showToastShort(getString(R.string.not_support_void));
    }

    @Override
    public void showAlipayWechatInfo(String partnerTransactionId, String paymentId, String title) {
        lv_card.setVisibility(View.GONE);
        lv_ali_we.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(partnerTransactionId)){
            tv_show_trans.setText(partnerTransactionId);
        }
        if (!TextUtils.isEmpty(paymentId)){
            tv_payment_id.setText(paymentId);
        }else {
            tv_pay.setVisibility(View.GONE);
        }
        tv_ali_title.setText(title);
    }



    @Override
    public void showPrintFailedDialog(String errMsg, boolean isNeedBackToMainMenu) {
        String negativeButtonText = ResUtil.getString(R.string.btn_hint_cancel);
        String positiveButtonText = ResUtil.getString(R.string.btn_hint_reprint);
        DialogUtil.showAskDialog(this, errMsg, negativeButtonText, positiveButtonText, new DialogUtil.AskDialogListener() {
            @Override
            public void onClick(boolean isSure) {
                if (isSure) {
                  //  showTransInfoPresenter.printBalancePointSlip(isNeedBackToMainMenu);
                } else {
                    if (isNeedBackToMainMenu) {
                        navigatorToNextStep();
                    }
                }
            }
        });
    }
    @Override
    public void showChangeAmountdDialog(String errMsg, boolean isNeedBackToMainMenu) {
        DialogUtil.showAskDialog(this, errMsg, new DialogUtil.AskDialogListener() {
            @Override
            public void onClick(boolean isSure) {
          //      getUiNavigator().getUiFlowControlData().setPreNeedToChangeAmount(isSure);
                navigatorToNextStep();
            }
        });
    }
    @Override
    public void enableBackKey(boolean isEnabled) {
        isCheckPointTrans = isEnabled;
    }




}
