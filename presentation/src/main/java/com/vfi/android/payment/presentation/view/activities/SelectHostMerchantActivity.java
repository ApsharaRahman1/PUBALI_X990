package com.vfi.android.payment.presentation.view.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.SelectHostMerchantPresenter;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.adapters.SelectItemAdapter;
import com.vfi.android.payment.presentation.view.contracts.SelectHostMerchantUI;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by RuihaoS on 2019/9/4.
 */
public class SelectHostMerchantActivity extends BaseTransFlowActivity<SelectHostMerchantUI> implements SelectHostMerchantUI {
    @Inject
    SelectHostMerchantPresenter selectHostMerchantPresenter;
    @BindView(R.id.recyclerview_select_host_merchant)
    RecyclerView recyclerview_select_host_merchant;
    @BindView(R.id.tv_select_hint_msg)
    TextView tv_select_hint_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setAlpha(0);
        setContentView(R.layout.activity_select_host_merchant);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setBackGround(R.drawable.background1);
    }


    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(selectHostMerchantPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void showSelectHosts(List<String> hostNameList) {
        this.getWindow().getDecorView().setAlpha(1);
        String[] hosts = hostNameList.toArray(new String[hostNameList.size()]);

        tv_select_hint_msg.setText(R.string.select_multi_host);

        SelectItemAdapter selectItemAdapter = new SelectItemAdapter(hosts);
        selectItemAdapter.setOnItemClickListener((view, position) -> {
            LogUtil.d(LogUtil.TAG, "Select host : " + hostNameList.get(position));
            selectHostMerchantPresenter.selectHostNameIndex(position);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerview_select_host_merchant.setLayoutManager(linearLayoutManager);
        recyclerview_select_host_merchant.setAdapter(selectItemAdapter);
    }

    @Override
    public void showSelectMerchants(List<String> merchantNameList) {
        this.getWindow().getDecorView().setAlpha(1);
        String[] merchants = merchantNameList.toArray(new String[merchantNameList.size()]);

        tv_select_hint_msg.setText(R.string.select_multi_merchant);

        SelectItemAdapter selectItemAdapter = new SelectItemAdapter(merchants);
        selectItemAdapter.setOnItemClickListener((view, position) -> {
            LogUtil.d(LogUtil.TAG, "Select merchant : " + merchantNameList.get(position));
            selectHostMerchantPresenter.selectMerchantNameIndex(position);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerview_select_host_merchant.setLayoutManager(linearLayoutManager);
        recyclerview_select_host_merchant.setAdapter(selectItemAdapter);
    }
}
