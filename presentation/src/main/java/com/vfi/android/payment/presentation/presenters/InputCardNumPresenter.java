package com.vfi.android.payment.presentation.presenters;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalCfg;
import com.vfi.android.domain.interactor.repository.UseCaseSaveManualCardNum;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.ExceptionErrMsgMapper;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TvShowUtil;
import com.vfi.android.payment.presentation.view.contracts.InputCardNumUI;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

public class InputCardNumPresenter extends BasePresenter<InputCardNumUI> {

    private static final String TAG = "InputCardNumPresenter";
    private final CurrentTranData currentTranData;
    private final UseCaseGetCurTranData useCaseGetCurTranData;
    private final UseCaseSaveManualCardNum useCaseSaveManualCardNum;
    private final UseCaseGetTerminalCfg useCaseGetTerminalCfg;
    private final UINavigator uiNavigator;
    private final TerminalCfg terminalCfg;

    @Inject
    public InputCardNumPresenter(UseCaseGetCurTranData useCaseGetCurTranData,
                                 UINavigator uiNavigator,
                                 UseCaseGetTerminalCfg useCaseGetTerminalCfg,
                                 UseCaseSaveManualCardNum useCaseSaveManualCardNum) {
        this.currentTranData = useCaseGetCurTranData.execute(null);
        this.useCaseSaveManualCardNum = useCaseSaveManualCardNum;
        this.useCaseGetCurTranData = useCaseGetCurTranData;
        this.uiNavigator = uiNavigator;
        this.useCaseGetTerminalCfg = useCaseGetTerminalCfg;
        this.terminalCfg = useCaseGetTerminalCfg.execute(null);
    }

    @Override
    protected void onFirstUIAttachment() {
        doUICmd_showTitle(currentTranData.getTitle(), "");
    }

    public void submitCardNum(String cardNum) {
        final int MIN_CARD_NUM_LEN = 13;
        if (cardNum == null || cardNum.length() < MIN_CARD_NUM_LEN) {
            doUICmd_clearCardNum();
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_bad_card_length));
        } else if (terminalCfg.isEnableCheckLuhn() && !TvShowUtil.checkCardNumLuhnResult(cardNum)) {
            doUICmd_clearCardNum();
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_bad_account));
        } else {
           saveCardNumber(cardNum);
        }
    }


    private void saveCardNumber(String cardNum) {
        Disposable disposable = useCaseSaveManualCardNum.asyncExecute(cardNum).subscribe(unused -> {
            LogUtil.d(TAG, "save card number success");
            uiNavigator.getUiFlowControlData().setNeedSelectHostMerchant(true);
            doUICmd_navigatorToNext();
        }, throwable -> {
            doErrorProcess(throwable);
        });
    }

    private void doErrorProcess(Throwable exception) {
        if (exception instanceof CommonException) {
            CommonException commonException = (CommonException) exception;
            int exceptionType = commonException.getExceptionType();
            currentTranData.setErrorMsg(ExceptionErrMsgMapper.toErrorMsg(exceptionType));
            uiNavigator.getUiFlowControlData().setTransFailed(true);
            doUICmd_navigatorToNext();
        } else {
            // other exception
            exception.printStackTrace();
            String errmsg = exception.getMessage();
            if (errmsg != null && errmsg.length() > 30) {
                errmsg = errmsg.substring(0, 30);
            }

            currentTranData.setErrorMsg("" + errmsg);
            uiNavigator.getUiFlowControlData().setTransFailed(true);
            doUICmd_navigatorToNext();
        }
    }

    private void doUICmd_showTransNotAllowDialog() {
        execute(ui -> ui.showTransNotAllowDialog());
    }

    private void doUICmd_clearCardNum() {
        execute(ui -> ui.clearCardNum());
    }


}
