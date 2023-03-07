package com.vfi.android.payment.presentation.presenters;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.InputOrgAuthCodeUI;

import javax.inject.Inject;

public class InputOrgAuthCodePresenter extends BasePresenter<InputOrgAuthCodeUI> {
    private final CurrentTranData currentTranData;

    @Inject
    public InputOrgAuthCodePresenter(UseCaseGetCurTranData useCaseGetCurTranData) {
        this.currentTranData = useCaseGetCurTranData.execute(null);
    }

    @Override
    protected void onFirstUIAttachment() {
        doUICmd_showTitle(currentTranData.getTitle(), "");
    }

    public void submitOrgAuthCode(String orgAuthCode) {
        LogUtil.d("TAG", "orgAuthCode=" + orgAuthCode);
        if (!checkIsValidAuthCode(orgAuthCode)) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_enter_correct_org_auth_code));
            doUICmd_clearInputText();
        } else {
            currentTranData.getRecordInfo().setOrgAuthCode(orgAuthCode);
            doUICmd_navigatorToNext();
        }
    }

    private boolean checkIsValidAuthCode(String authCode) {

        if (authCode.length() < 2) {
            return false;
        }
        return true;
    }

    private void doUICmd_clearInputText() {
        execute(ui -> ui.clearInputText());
    }
}
