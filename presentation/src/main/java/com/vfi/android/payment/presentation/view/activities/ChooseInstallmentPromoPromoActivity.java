package com.vfi.android.payment.presentation.view.activities;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.ChooseInstallmentPromoPresenter;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.adapters.SelectItemAdapter;
import com.vfi.android.payment.presentation.view.contracts.ChooseInstallmentPromoUI;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseInstallmentPromoPromoActivity extends BaseTransFlowActivity<ChooseInstallmentPromoUI> implements ChooseInstallmentPromoUI {

    @BindView(R.id.recyclerview_select)
    RecyclerView recyclerview_select;
    @BindView(R.id.tv_select_hint_msg)
    TextView tv_select_hint_msg;

    @Inject
    ChooseInstallmentPromoPresenter chooseInstallmentPromoPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_installment_promo);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setBackGround(R.drawable.background1);
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(chooseInstallmentPromoPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void showSelectPromos(List<String> promoList) {
        String[] promos = promoList.toArray(new String[promoList.size()]);

        tv_select_hint_msg.setText(R.string.tv_hint_select_promo);

        SelectItemAdapter selectItemAdapter = new SelectItemAdapter(promos);
        selectItemAdapter.setOnItemClickListener((view, position) -> {
            LogUtil.d(LogUtil.TAG, "Select installment promo: " + promoList.get(position));
            chooseInstallmentPromoPresenter.onInstallmentSelected(position);
        });

        if (promoList.size() >= 6) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
            selectItemAdapter.setTwoLineShow(true);
            recyclerview_select.setLayoutManager(gridLayoutManager);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerview_select.setLayoutManager(linearLayoutManager);
        }
        recyclerview_select.setAdapter(selectItemAdapter);
    }
}
