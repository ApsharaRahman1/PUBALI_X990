package com.vfi.android.payment.presentation.presenters;


import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.interactor.repository.UseCaseSaveApprovalCode;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.InputApprovalCodeUI;

import javax.inject.Inject;

public class InputApprovalCodePresenter extends BasePresenter<InputApprovalCodeUI> {
    private final static String TAG = "InputApprovalCodePresenter";
    private final UseCaseSaveApprovalCode useCaseSaveApprovalCode;
    private final CurrentTranData currentTranData;

    @Inject
    public InputApprovalCodePresenter(
            UseCaseSaveApprovalCode useCaseSaveApprovalCode,
            UseCaseGetCurTranData useCaseGetCurTranData) {
        this.useCaseSaveApprovalCode = useCaseSaveApprovalCode;
        this.currentTranData = useCaseGetCurTranData.execute(null);
    }

    @Override
    protected void onFirstUIAttachment() {
        doUICmd_showTitle(currentTranData.getTitle(), "");
    }

    public void saveApprovalCode(String approvalCode) {
        int APPR_CODE_LEN = 6;
        if (approvalCode == null || approvalCode.length() != APPR_CODE_LEN) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_invalid_entry));
            return;
        }

        LogUtil.d(TAG, "Save approvalCode=" + approvalCode);
        useCaseSaveApprovalCode.execute(approvalCode);
        doUICmd_navigatorToNext();
    }
}
