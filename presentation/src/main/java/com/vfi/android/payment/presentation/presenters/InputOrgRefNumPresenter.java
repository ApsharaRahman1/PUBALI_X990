package com.vfi.android.payment.presentation.presenters;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.InputOrgAuthCodeUI;
import com.vfi.android.payment.presentation.view.contracts.InputOrgRefNumUI;

import javax.inject.Inject;

public class InputOrgRefNumPresenter extends BasePresenter<InputOrgRefNumUI> {
    private final CurrentTranData currentTranData;

    @Inject
    public InputOrgRefNumPresenter(UseCaseGetCurTranData useCaseGetCurTranData) {
        this.currentTranData = useCaseGetCurTranData.execute(null);
    }

    @Override
    protected void onFirstUIAttachment() {
        doUICmd_showTitle(currentTranData.getTitle(), "");
    }

    public void submitRefNumCode(String orgRefNum) {
        LogUtil.d("TAG", "orgRefNum=" + orgRefNum);
        if (!checkIsValidAuthCode(orgRefNum)) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_enter_correct_org_ref_num));
            doUICmd_clearInputText();
        } else {
            currentTranData.getRecordInfo().setOrgRefNo(orgRefNum);
            doUICmd_navigatorToNext();
        }
    }

    private boolean checkIsValidAuthCode(String refNum) {

        if (refNum.length() != 12) {
            return false;
        }
        return true;
    }

    private void doUICmd_clearInputText() {
        execute(ui -> ui.clearInputText());
    }
}
