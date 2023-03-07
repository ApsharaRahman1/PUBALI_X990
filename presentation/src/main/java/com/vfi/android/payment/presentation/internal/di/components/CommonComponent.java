package com.vfi.android.payment.presentation.internal.di.components;


import com.vfi.android.payment.presentation.internal.di.PerActivity;
import com.vfi.android.payment.presentation.transflows.BaseTransFlow;
import com.vfi.android.payment.presentation.view.activities.base.BaseActivity;
import com.vfi.android.payment.presentation.view.fragments.base.BaseFragment;
import com.vfi.android.payment.presentation.receivers.AutoSettlementReceiver;
import com.vfi.android.payment.presentation.receivers.BootCompletedReceiver;
import com.vfi.android.payment.presentation.receivers.ForceSettlementReceiver;
import com.vfi.android.payment.presentation.view.widget.EditParamDialog;
import com.vfi.android.payment.presentation.view.widget.InputPasswdDialog;
import com.vfi.android.payment.presentation.view.widget.InputTextDialog;

import dagger.Component;

/**
 * A base component upon which fragment's components may depend.
 * Activity-level components should extend this component.
 * <p>
 * Subtypes of ActivityComponent should be decorated with annotation:
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class)
public interface CommonComponent {
    void inject(BaseActivity activity);

    void inject(BaseTransFlow baseTransFlow);

    void inject(BaseFragment fragment);
//
//    void inject(PowerConnectionReceiver receiver);
//
//    void inject(BatteryLevelReceiver receiver);
//
    void inject(ForceSettlementReceiver receiver);
    void inject(BootCompletedReceiver receiver);
    void inject(AutoSettlementReceiver receiver);
    void inject(InputPasswdDialog dialog);
    void inject(EditParamDialog dialog);
    void inject(InputTextDialog dialog);
//
//    void inject(QRPaySaveRecordReceiver receiver);
//
//    void inject(TmsUpdateIntentService intentService);
//
//    void inject(BaseTransFlow baseTransFlow);
//
//    void inject(InputPasswdDialog dialog);
//
//    void inject(ShutdownReceiver receiver);
//
//    void inject(ReinstallReceiver receiver);
}
