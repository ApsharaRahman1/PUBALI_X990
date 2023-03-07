package com.vfi.android.payment.presentation.presenters;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.InstallmentPromo;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.ChooseInstallmentTermUI;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ChooseInstallmentTermPresenter extends BasePresenter<ChooseInstallmentTermUI> {
    private final String TAG = TAGS.INSTALLMENT;
    private final CurrentTranData currentTranData;
    private UINavigator uiNavigator;

    private List<Integer> installmentTermList;

    @Inject
    public ChooseInstallmentTermPresenter(UINavigator uiNavigator,
                                          UseCaseGetCurTranData useCaseGetCurTranData) {
        currentTranData = useCaseGetCurTranData.execute(null);
        this.uiNavigator = uiNavigator;
    }

    @Override
    protected void onFirstUIAttachment() {
        doUICmd_showTitle(currentTranData.getTitle(), "");
        showInstallmentTerms();
    }

    private void showInstallmentTerms() {
        List<String> termList = new ArrayList<>();
        InstallmentPromo installmentPromo = currentTranData.getCurrentInstallmentPromo();
        if (installmentPromo == null || installmentPromo.getTermList() == null) {
            currentTranData.setErrorMsg(ResUtil.getString(R.string.tv_hint_not_installment_term_found));
            uiNavigator.getUiFlowControlData().setTransFailed(true);
            doUICmd_navigatorToNext();
        }

        installmentTermList = new ArrayList<>();
        String[] terms = installmentPromo.getTermList().split(",");
        for (int i = 0; i < terms.length; i++) {
            int termValue = StringUtil.parseInt(terms[i], -1);
            if (termValue != -1) {
                termList.add("" + termValue + " " + ResUtil.getString(R.string.tv_hint_months));
                installmentTermList.add(termValue);
            } else {
                LogUtil.d(TAG, "Wrong installment term[" + terms[i] + "]");
            }
        }

        if (termList.size() == 0) {
            currentTranData.setErrorMsg(ResUtil.getString(R.string.tv_hint_not_installment_term_found));
            uiNavigator.getUiFlowControlData().setTransFailed(true);
            doUICmd_navigatorToNext();
        }

        doUICmd_showSelectInstallmentTerms(termList);
    }

    public void onTermSelected(int index) {
        currentTranData.setInstallmentTerm(installmentTermList.get(index));
        doUICmd_navigatorToNext();
    }

    private void doUICmd_showSelectInstallmentTerms(List<String> termList) {
        execute(ui -> ui.showSelectTermItems(termList));
    }
}
