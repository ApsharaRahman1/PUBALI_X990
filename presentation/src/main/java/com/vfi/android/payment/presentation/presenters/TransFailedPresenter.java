package com.vfi.android.payment.presentation.presenters;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.interactor.transaction.UseCaseDoPostTrans;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.view.contracts.TransFailedUI;

import javax.inject.Inject;

public class TransFailedPresenter extends BasePresenter<TransFailedUI> {
    private final String TAG = TAGS.UILevel;
    private final CurrentTranData currentTranData;
    private final UseCaseDoPostTrans useCaseDoPostTrans;

    @Inject
    public TransFailedPresenter(
            UseCaseDoPostTrans useCaseDoPostTrans,
            UseCaseGetCurTranData useCaseGetCurTranData) {
        this.currentTranData = useCaseGetCurTranData.execute(null);
        this.useCaseDoPostTrans = useCaseDoPostTrans;
    }

    @Override
    protected void onFirstUIAttachment() {
        resetUnlockStatus();
        LogUtil.d(TAG, "error msg=[" + currentTranData.getErrorMsg() + "]");
        doUICmd_showErrorMessage(currentTranData.getErrorMsg());
        useCaseDoPostTrans.asyncExecuteWithoutResult(null);
    }

    private void resetUnlockStatus() {

    }

    private void doUICmd_showErrorMessage(String errMsg) {
        execute(ui -> ui.showErrorMessage(errMsg));
    }
}
