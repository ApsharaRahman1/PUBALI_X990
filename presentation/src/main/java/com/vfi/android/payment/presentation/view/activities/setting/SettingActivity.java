package com.vfi.android.payment.presentation.view.activities.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.vfi.android.domain.entities.databeans.OperatorInfo;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.models.SettingMenuItemViewModel;
import com.vfi.android.payment.presentation.utils.AndroidUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.activities.base.BaseSettingActivity;
import com.vfi.android.payment.presentation.view.adapters.SettingMenuAdapter;
import com.vfi.android.payment.presentation.view.contracts.SettingUI;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends BaseSettingActivity<SettingUI> implements SettingUI {

    @BindView(R.id.tv_top_title)
    TextView tv_top_title;
    @BindView(R.id.recyclerview_setting_menu)
    RecyclerView recyclerview_setting_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setBackGround(R.color.color_top_background);
        tv_top_title.setText(R.string.setting_title);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        recyclerview_setting_menu.setLayoutManager(gridLayoutManager);

        ArrayList<SettingMenuItemViewModel> menuModels = new ArrayList<>();
        final int MENU_ID_COMMUNICATION = 0;
        final int MENU_ID_CONFIGURATION = 1;
        final int MENU_ID_KEY_MANAGEMENT = 2;
        final int MENU_ID_WIFI = 3;
        final int MENU_ID_INITLIZATION = 4;
        final int MENU_ID_EXIT_APP = 5;
        final int MENU_ID_VHQ = 6;

        menuModels.add(new SettingMenuItemViewModel(ResUtil.getString(R.string.setting_communication), R.drawable.icon_communication_setup, MENU_ID_COMMUNICATION));
        menuModels.add(new SettingMenuItemViewModel(ResUtil.getString(R.string.setting_configuration), R.drawable.icon_configuration_setup, MENU_ID_CONFIGURATION));
        menuModels.add(new SettingMenuItemViewModel(ResUtil.getString(R.string.setting_key_management), R.drawable.icon_key_management, MENU_ID_KEY_MANAGEMENT));
        menuModels.add(new SettingMenuItemViewModel(ResUtil.getString(R.string.setting_wlan), R.drawable.icon_wlan, MENU_ID_WIFI));
        menuModels.add(new SettingMenuItemViewModel(ResUtil.getString(R.string.setting_initialization), R.drawable.icon_initialization, MENU_ID_INITLIZATION));
        menuModels.add(new SettingMenuItemViewModel(ResUtil.getString(R.string.setting_exit_app), R.drawable.icon_exit_app, MENU_ID_EXIT_APP));
        menuModels.add(new SettingMenuItemViewModel(ResUtil.getString(R.string.setting_vhq), R.drawable.icon_vhq, MENU_ID_VHQ));
        SettingMenuAdapter menuListAdapter = new SettingMenuAdapter(menuModels);
        recyclerview_setting_menu.setAdapter(menuListAdapter);

        menuListAdapter.setOnItemClickListener((view, position) -> {
            Context context = SettingActivity.this;
            switch (position) {
                case MENU_ID_COMMUNICATION:
                    AndroidUtil.startActivity(context, CommunicationSettingActivity.class);
                    break;
                case MENU_ID_CONFIGURATION:
                    AndroidUtil.startActivity(context, ConfigurationSettingAcitivity.class);
                    break;
                case MENU_ID_KEY_MANAGEMENT:
                    AndroidUtil.startActivity(context, KeyManagementSettingActivity.class);
                    break;
                case MENU_ID_WIFI:
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    break;
                case MENU_ID_INITLIZATION:
                    break;
                case MENU_ID_EXIT_APP:
                    showCheckPasswordDialog(OperatorInfo.TYPE_SUPER_MANAGER, isCorrectPassword -> {
                        if (isCorrectPassword) {
                            AndroidUtil.finishAllActivitys();
                            controlHomeKeyAndStatusBar(true);
                        }
                    });
                    break;
                case MENU_ID_VHQ:
                    break;
            }
        });
    }

    @Override
    protected void callSuperSetupPresenter() {
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
    }
}
