package com.vfi.android.payment.presentation.view.activities;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.ChooseInstallmentTermPresenter;
import com.vfi.android.payment.presentation.view.activities.base.BaseTransFlowActivity;
import com.vfi.android.payment.presentation.view.adapters.SelectItemAdapter;
import com.vfi.android.payment.presentation.view.contracts.ChooseInstallmentTermUI;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseInstallmentTermActivity extends BaseTransFlowActivity<ChooseInstallmentTermUI> implements ChooseInstallmentTermUI {

    @BindView(R.id.recyclerview_select)
    RecyclerView recyclerview_select;
    @BindView(R.id.tv_select_hint_msg)
    TextView tv_select_hint_msg;

    @Inject
    ChooseInstallmentTermPresenter chooseInstallmentTermPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_installment_term);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setBackGround(R.drawable.background1);
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(chooseInstallmentTermPresenter, this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void showSelectTermItems(List<String> termList) {
        String[] terms = termList.toArray(new String[termList.size()]);

        tv_select_hint_msg.setText(R.string.tv_hint_select_term);

        SelectItemAdapter selectItemAdapter = new SelectItemAdapter(terms);
        selectItemAdapter.setOnItemClickListener((view, position) -> {
            LogUtil.d(LogUtil.TAG, "Select installment term : " + termList.get(position));
            chooseInstallmentTermPresenter.onTermSelected(position);
        });

        if (termList.size() >= 6) {
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
