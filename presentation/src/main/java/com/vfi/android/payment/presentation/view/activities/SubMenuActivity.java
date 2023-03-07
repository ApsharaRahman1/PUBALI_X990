package com.vfi.android.payment.presentation.view.activities;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.models.MenuViewModel;
import com.vfi.android.payment.presentation.presenters.SubMenuPresenter;
import com.vfi.android.payment.presentation.view.activities.base.BaseMvpActivity;
import com.vfi.android.payment.presentation.view.adapters.SubMenuAdapter;
import com.vfi.android.payment.presentation.view.contracts.SubMenuUI;
import com.vfi.android.payment.presentation.view.itemdecorations.MyItemDecoration;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubMenuActivity extends BaseMvpActivity<SubMenuUI> implements SubMenuUI {

    @BindView(R.id.recyclerview_auth_type_list)
    RecyclerView recyclervier_sub_menu;

    @Inject
    SubMenuPresenter subMenuPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_menu);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        MyItemDecoration itemDecoration = new MyItemDecoration(this);
        recyclervier_sub_menu.addItemDecoration(itemDecoration);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        recyclervier_sub_menu.setLayoutManager(gridLayoutManager);
    }

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(subMenuPresenter,this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void showSubMenu(ArrayList<MenuViewModel> menuViewModelList) {

        SubMenuAdapter menuListAdapter = new SubMenuAdapter(menuViewModelList);
        recyclervier_sub_menu.setAdapter(menuListAdapter);

        menuListAdapter.setOnItemClickListener(new SubMenuAdapter.OnitemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                subMenuPresenter.startTradeFlow(menuViewModelList.get(position));
            }
        });
    }
}
