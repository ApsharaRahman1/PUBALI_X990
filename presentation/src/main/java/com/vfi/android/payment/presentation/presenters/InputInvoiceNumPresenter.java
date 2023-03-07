package com.vfi.android.payment.presentation.presenters;


import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.interactor.repository.UseCaseIsExistAdjustTransRecord;
import com.vfi.android.domain.interactor.repository.UseCaseIsExistVoidTransRecord;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.ExceptionErrMsgMapper;
import com.vfi.android.payment.presentation.mappers.NotAllowErrorMapper;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.InputInvoiceNumUI;

import javax.inject.Inject;

public class InputInvoiceNumPresenter extends BasePresenter<InputInvoiceNumUI> {
    private final String TAG = TAGS.UILevel;
    private final UseCaseIsExistVoidTransRecord useCaseIsExistVoidTransRecord;
    private final UseCaseIsExistAdjustTransRecord useCaseIsExistAdjustTransRecord;
    private final CurrentTranData currentTranData;
    private final UINavigator uiNavigator;

    @Inject
    public InputInvoiceNumPresenter(
            UseCaseIsExistVoidTransRecord useCaseIsExistVoidTransRecord,
            UseCaseIsExistAdjustTransRecord useCaseIsExistAdjustTransRecord,
            UseCaseGetCurTranData useCaseGetCurTranData,
            UINavigator uiNavigator
    ) {
        this.useCaseIsExistVoidTransRecord = useCaseIsExistVoidTransRecord;
        this.useCaseIsExistAdjustTransRecord = useCaseIsExistAdjustTransRecord;
        this.uiNavigator = uiNavigator;
        this.currentTranData = useCaseGetCurTranData.execute(null);
    }

    @Override
    protected void onFirstUIAttachment() {
        doUICmd_showTitle(currentTranData.getTitle(), "");
    }

    public void checkTransRecordByInvoiceNum(String invoiceNum) {
        if (invoiceNum == null || invoiceNum.length() == 0) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.tv_hint_please_input_invoice_number));
        } else {
            final int MAX_TRACE_NUM_LEN = 6;
            if (invoiceNum.length() < MAX_TRACE_NUM_LEN) {
                invoiceNum = "000000".substring(invoiceNum.length()) + invoiceNum;
            }
            LogUtil.d("traceNum=" + invoiceNum);

            int transType = currentTranData.getTransType();
            LogUtil.d(TAG, "transType=" + transType);

            if (transType == TransType.TIP_ADJUST) {
                doTipAdjustOrgTransCheck(invoiceNum);
            } else if (transType == TransType.VOID){
                doVoidOrgTransCheck(invoiceNum);
            } else {
                throw new RuntimeException("Unknown transaction.");
            }
        }
    }

    private void doVoidOrgTransCheck(String invoiceNum) {
        useCaseIsExistVoidTransRecord.asyncExecute(invoiceNum).doOnError(throwable -> {
            doErrorProcess(throwable);
        }).doOnNext(isExistOriginTrans -> {
            if (isExistOriginTrans) {
                uiNavigator.getUiFlowControlData().setTransRecordFound(true);
                doUICmd_navigatorToNext();
            } else {
                doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_not_found_org_trans));
                doUICmd_clearInputText();
            }
        }).subscribe();
    }

    private void doTipAdjustOrgTransCheck(String invoiceNum) {
        useCaseIsExistAdjustTransRecord.asyncExecute(invoiceNum).doOnError(throwable -> {
            doErrorProcess(throwable);
        }).doOnNext(isExistOriginTrans -> {
            if (isExistOriginTrans) {
                uiNavigator.getUiFlowControlData().setTransRecordFound(true);
                doUICmd_navigatorToNext();
            } else {
                doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_not_found_org_trans));
                doUICmd_clearInputText();
            }
        }).subscribe();
    }

    private void doErrorProcess(Throwable throwable) {
        if (throwable instanceof CommonException) {
            CommonException commonException = (CommonException) throwable;
            int exceptionType = commonException.getExceptionType();
            int subErrorCode = commonException.getSubErrType();
            LogUtil.e(TAG, "exceptionType=" + ExceptionType.toDebugString(exceptionType));
            LogUtil.e(TAG, "subErrorCode=" + subErrorCode);
            if (commonException.getExceptionType() == ExceptionType.TRANS_NOT_ALLOW) {
                doUICmd_showToastMessage(NotAllowErrorMapper.toErrorString(subErrorCode));
            } else {
                doUICmd_showToastMessage(ExceptionErrMsgMapper.toErrorMsg(exceptionType));
            }
        } else {
            doUICmd_showToastMessage(throwable.getMessage());
        }

        doUICmd_clearInputText();
    }

    private void doUICmd_showNotFoundWarnDialog(String msg) {
        execute(ui -> ui.showNotFoundWarnDialog(msg));
    }

    private void doUICmd_clearInputText() {
        execute(ui -> ui.clearInputText());
    }
}
