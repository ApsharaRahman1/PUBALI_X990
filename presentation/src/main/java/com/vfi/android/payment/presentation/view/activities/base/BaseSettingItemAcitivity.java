package com.vfi.android.payment.presentation.view.activities.base;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.AndroidApplication;
import com.vfi.android.payment.presentation.presenters.base.BaseSettingItemPresenter;
import com.vfi.android.payment.presentation.view.adapters.SettingItemAdapter;
import com.vfi.android.payment.presentation.view.contracts.SettingItemUI;
import com.vfi.android.payment.presentation.view.itemdecorations.MyItemDecoration;
import com.vfi.android.payment.presentation.models.SettingItemViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseSettingItemAcitivity<T extends SettingItemUI> extends BaseSettingActivity<T> implements SettingItemUI{

    @BindView(R.id.setting_item_title)
    TextView settingTitle;
    @BindView(R.id.setting_item_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn_save)
    Button btSave;

    private MyItemDecoration itemDecorationSetting;
    private SettingItemAdapter settingItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_base_layout);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        setBackGround(R.color.color_top_background);
        recyclerView.setAdapter(settingItemAdapter);
        itemDecorationSetting = new MyItemDecoration(this);
        recyclerView.addItemDecoration(itemDecorationSetting);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseSettingItemPresenter presenter = (BaseSettingItemPresenter) getPresenter();
                if(presenter!=null){
                    presenter.saveConfigs(settingItemAdapter.getUnSavedCItemModels());
                }
            }
        });
    }

    private void initData() {
        settingItemAdapter = new SettingItemAdapter(this);
        settingItemAdapter.setOnItemClickListener(itemViewModel -> {
            BaseSettingItemPresenter presenter = (BaseSettingItemPresenter) getPresenter();
            if(presenter!=null){
                presenter.onSelectItem(itemViewModel);
            }
        });

        settingItemAdapter.setOnItemValueChangedListener(itemViewModel -> {
            BaseSettingItemPresenter presenter = (BaseSettingItemPresenter) getPresenter();
            if(presenter!=null){
                presenter.onItemValueChanged(itemViewModel);
            }
        });
    }

    @Override
    public void showSettingItems(List<SettingItemViewModel> viewModels) {
        boolean isNeedVisableSaveButton = false;
        for (int i = 0; i < viewModels.size(); i ++) {
            if (viewModels.get(i).isNeedSaveItem()) {
                isNeedVisableSaveButton = true;
                break;
            }
        }

        if (isNeedVisableSaveButton) {
            btSave.setVisibility(View.VISIBLE);
        } else {
            btSave.setVisibility(View.GONE);
        }

        settingItemAdapter.setItemModels(viewModels);
        recyclerView.invalidateItemDecorations();
    }

    @Override
    public void showSettingTitle(String title) {
        settingTitle.setText(title);
    }

    @Override
    public void finishUI() {
        finish();
    }


    @Override
    public void onBackPressed() {
        if (getPresenter() != null
                && getPresenter() instanceof BaseSettingItemPresenter) {
            BaseSettingItemPresenter presenter = (BaseSettingItemPresenter) getPresenter();
            presenter.onBackPressEvent();
        }
    }

    @Override
    protected void onDestroy() {
        settingItemAdapter.disMissDialog();
        super.onDestroy();
    }

    public void cancelSettingUITimer() {
        AndroidApplication.getInstance().cancelAllSettingTimer();
    }

    @Override
    public void startSettingUITimer() {
        restartTimer();
    }
}
