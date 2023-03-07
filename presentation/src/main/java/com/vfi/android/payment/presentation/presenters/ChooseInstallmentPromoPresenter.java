package com.vfi.android.payment.presentation.presenters;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.InstallmentPromo;
import com.vfi.android.domain.interactor.repository.UseCaseGetAllInstallmentPromos;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.ChooseInstallmentPromoUI;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ChooseInstallmentPromoPresenter extends BasePresenter<ChooseInstallmentPromoUI> {
    private List<InstallmentPromo> installmentPromos;
    private final CurrentTranData currentTranData;
    private UINavigator uiNavigator;

    @Inject
    public ChooseInstallmentPromoPresenter(UseCaseGetAllInstallmentPromos useCaseGetAllInstallmentPromos,
                                           UINavigator uiNavigator,
                                           UseCaseGetCurTranData useCaseGetCurTranData) {
        installmentPromos = useCaseGetAllInstallmentPromos.execute(true);
        currentTranData = useCaseGetCurTranData.execute(null);
        this.uiNavigator = uiNavigator;
    }

    @Override
    protected void onFirstUIAttachment() {
        if (installmentPromos.size() == 0) {
            currentTranData.setErrorMsg(ResUtil.getString(R.string.tv_hint_no_installment_promo_enabled));
            uiNavigator.getUiFlowControlData().setTransFailed(true);
            doUICmd_navigatorToNext();
        } else {
            doUICmd_showTitle(currentTranData.getTitle(), "");
            showInstallmentPromos();
        }
    }

    private void showInstallmentPromos() {
        List<String> promoNameList = new ArrayList<>();
        for (InstallmentPromo installmentPromo : installmentPromos) {
            promoNameList.add(installmentPromo.getPromoLabel());
        }
        doUICmd_showSelectInstallmentItems(promoNameList);
    }

    public void onInstallmentSelected(int index) {
        currentTranData.setCurrentInstallmentPromo(installmentPromos.get(index));
        doUICmd_navigatorToNext();
    }

    private void doUICmd_showSelectInstallmentItems(List<String> installmentTypeNameList) {
        execute(ui -> ui.showSelectPromos(installmentTypeNameList));
    }
}
