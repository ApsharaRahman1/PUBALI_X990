package com.vfi.android.payment.presentation.view.activities.setting;

import android.content.Intent;
import android.provider.Settings;

import com.vfi.android.payment.presentation.internal.di.components.ActivityComponent;
import com.vfi.android.payment.presentation.presenters.setting.ConfigurationSettingItemPresenter;
import com.vfi.android.payment.presentation.view.activities.base.BaseSettingItemAcitivity;
import com.vfi.android.payment.presentation.view.contracts.ConfigurationUI;

import javax.inject.Inject;

public class ConfigurationSettingAcitivity extends BaseSettingItemAcitivity<ConfigurationUI> implements ConfigurationUI {

    @Inject
    ConfigurationSettingItemPresenter configurationPresenter;

    @Override
    protected void callSuperSetupPresenter() {
        setupPresenter(configurationPresenter,this);
    }

    @Override
    protected void injectComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void showSystemLanguageInterface() {
        startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
    }

    @Override
    public void showSystemTimeInterface() {
        startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS));
    }
}
