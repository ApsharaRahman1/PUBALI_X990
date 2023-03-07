package com.vfi.android.payment.presentation.internal.di.components;

import android.app.Activity;

import com.vfi.android.payment.presentation.internal.di.PerActivity;
import com.vfi.android.payment.presentation.internal.di.modules.ActivityModule;
import com.vfi.android.payment.presentation.view.activities.AutoSettlementActivity;
import com.vfi.android.payment.presentation.view.activities.CheckCardActivity;
import com.vfi.android.payment.presentation.view.activities.CheckPasswdActivity;
import com.vfi.android.payment.presentation.view.activities.ChooseInstallmentPromoPromoActivity;
import com.vfi.android.payment.presentation.view.activities.ChooseInstallmentTermActivity;
import com.vfi.android.payment.presentation.view.activities.ElectronicSignActivity;
import com.vfi.android.payment.presentation.view.activities.EmvProcessActivity;
import com.vfi.android.payment.presentation.view.activities.InputAmountActivity;
import com.vfi.android.payment.presentation.view.activities.InputApprovalCodeActivity;
import com.vfi.android.payment.presentation.view.activities.InputCVV2Activity;
import com.vfi.android.payment.presentation.view.activities.InputCardExpiryDateActivity;
import com.vfi.android.payment.presentation.view.activities.InputCardNumActivity;
import com.vfi.android.payment.presentation.view.activities.InputInvoiceNumActivity;
import com.vfi.android.payment.presentation.view.activities.InputOrgAuthCodeActivity;
import com.vfi.android.payment.presentation.view.activities.InputOrgRefNumActivity;
import com.vfi.android.payment.presentation.view.activities.InputOrgTransDateActivity;
import com.vfi.android.payment.presentation.view.activities.InputPinActivity;
import com.vfi.android.payment.presentation.view.activities.InputTipAmountActivity;
import com.vfi.android.payment.presentation.view.activities.MainMenuActivity;
import com.vfi.android.payment.presentation.view.activities.NetworkProcessActivity;
import com.vfi.android.payment.presentation.view.activities.SelectHostMerchantActivity;
import com.vfi.android.payment.presentation.view.activities.option.OptionActivity;
import com.vfi.android.payment.presentation.view.activities.SettlementActivity;
import com.vfi.android.payment.presentation.view.activities.ShowTransInfoActivity;
import com.vfi.android.payment.presentation.view.activities.SplashActivity;
import com.vfi.android.payment.presentation.view.activities.SubMenuActivity;
import com.vfi.android.payment.presentation.view.activities.TransFailedActivity;
import com.vfi.android.payment.presentation.view.activities.TransSuccessActivity;
import com.vfi.android.payment.presentation.view.activities.history.HistoryActivity;
import com.vfi.android.payment.presentation.view.activities.history.HistoryDetailActivity;
import com.vfi.android.payment.presentation.view.activities.option.ReprintActivity;
import com.vfi.android.payment.presentation.view.activities.setting.CommunicationSettingActivity;
import com.vfi.android.payment.presentation.view.activities.setting.ConfigurationSettingAcitivity;
import com.vfi.android.payment.presentation.view.activities.setting.KeyManagementSettingActivity;

import dagger.Component;

/**
 * A base component upon which fragment's components may depend.
 * Activity-level components should extend this component.
 * <p>
 * Subtypes of ActivityComponent should be decorated with annotation:
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    //Exposed to sub-graphs.
    Activity activity();

    void inject(SplashActivity activity);

    void inject(MainMenuActivity activity);

    void inject(SubMenuActivity activity);

    void inject(CheckCardActivity activity);

    void inject(InputAmountActivity activity);

    void inject(InputTipAmountActivity activity);

    void inject(EmvProcessActivity activity);

    void inject(InputPinActivity activity);

    void inject(NetworkProcessActivity activity);

    void inject(CheckPasswdActivity activity);

    void inject(InputInvoiceNumActivity activity);

    void inject(ShowTransInfoActivity activity);

    void inject(TransSuccessActivity activity);

    void inject(TransFailedActivity activity);

    void inject(ElectronicSignActivity activity);

    void inject(OptionActivity activity);

    void inject(InputCardNumActivity activity);

    void inject(InputCardExpiryDateActivity activity);

    void inject(InputCVV2Activity activity);

    void inject(SettlementActivity activity);

    void inject(HistoryActivity activity);

    void inject(HistoryDetailActivity activity);

    void inject(SelectHostMerchantActivity activity);

    void inject(InputOrgTransDateActivity activity);

    void inject(InputOrgAuthCodeActivity activity);

    void inject(InputOrgRefNumActivity activity);

    void inject(InputApprovalCodeActivity activity);

    void inject(ConfigurationSettingAcitivity acitivity);

    void inject(CommunicationSettingActivity activity);

    void inject(AutoSettlementActivity activity);

    void inject(ReprintActivity activity);

    void inject(ChooseInstallmentPromoPromoActivity activity);

    void inject(ChooseInstallmentTermActivity activity);

    void inject(KeyManagementSettingActivity activity);
}
