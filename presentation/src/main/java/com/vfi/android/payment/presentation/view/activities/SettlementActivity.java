package com.vfi.android.payment.presentation.view.activities;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.models.SettlementItemModel;
import com.vfi.android.payment.presentation.presenters.SettlementPresenter;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.adapters.SettlementAdapter;
import com.vfi.android.payment.presentation.view.contracts.SettlementUI;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettlementActivity extends BaseTransFlowActivity<SettlementUI> implements SettlementUI {
    @BindView(R.id.recyclerview_settlement_list)
    RecyclerView recyclerview_settlement_list;
    @BindView(R.id.tv_show_total_amount)
    TextView tv_show_total_amount;
    @BindView(R.id.btn_next)
    Button btn_next;
    @BindView(R.id.checkbox_select_all_host)
    CheckBox checkbox_select_all_host;
    @BindView(R.id.tv_top_title)
    TextView tv_top_title;

    @Inject
    SettlementPresenter settlementPresenter;

    private SettlementAdapter settlementAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(settlementPresenter, this);
    }

    private void initView() {
        setBackGround(R.color.color_top_background);
        btn_next.setOnClickListener(v -> {
            settlementPresenter.doSettlementProcess(settlementAdapter.getSettlementItemModelList());
        });

        checkbox_select_all_host.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (settlementAdapter != null && buttonView.isPressed()) {
                    settlementAdapter.markAllItemCheckedStatus(isChecked);
                }
            }
        });
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void showTitle(String title, float titleTextSize, String subTitle, float subTitleTextSize) {
        tv_top_title.setText(title);
    }

    @Override
    public void showSettlementDetail(List<SettlementItemModel> settlementItemModels) {
        settlementAdapter = new SettlementAdapter(settlementItemModels);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerview_settlement_list.setLayoutManager(linearLayoutManager);
        recyclerview_settlement_list.setAdapter(settlementAdapter);

        settlementAdapter.setOnItemSelectChangedListener(new SettlementAdapter.OnItemSelectChangedListener() {
            @Override
            public void onItemSelected(View view, List<SettlementItemModel> settlementItemModels, int position, boolean isChecked) {
                LogUtil.d("Position=" + position + " isChecked=" + isChecked);
                settlementPresenter.calcSelectedSettlementTotalAmount(settlementItemModels);
                if (checkIsAllItemSelected(settlementItemModels)) {
                    checkbox_select_all_host.setChecked(true);
                } else {
                    checkbox_select_all_host.setChecked(false);
                }
            }
        });
    }

    private boolean checkIsAllItemSelected(List<SettlementItemModel> settlementItemModels) {
        boolean isAllItemSelected = true;
        for (SettlementItemModel settlementItemModel : settlementItemModels) {
            if (settlementItemModel.isSelected() == false) {
                isAllItemSelected = false;
                break;
            }
        }

        LogUtil.d("TAG", "isAllItemSelected=" + isAllItemSelected);
        return isAllItemSelected;
    }

    @Override
    public void showTotalAmount(String totalAmount) {
        tv_show_total_amount.setText(totalAmount);
    }
}
