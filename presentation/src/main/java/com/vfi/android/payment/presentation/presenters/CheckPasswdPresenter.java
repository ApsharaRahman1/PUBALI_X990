package com.vfi.android.payment.presentation.presenters;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.databeans.OperatorInfo;
import com.vfi.android.domain.interactor.repository.UseCaseCheckOperatorPasswd;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.utils.EncryptionUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.CheckPasswdUI;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

public class CheckPasswdPresenter extends BasePresenter<CheckPasswdUI> {
    private final String TAG = this.getClass().getSimpleName();
    private final UseCaseCheckOperatorPasswd useCaseCheckOperatorPasswd;
    private final UINavigator uiNavigator;
    private CurrentTranData currentTranData;

    @Inject
    public CheckPasswdPresenter(
            UseCaseGetCurTranData useCaseGetCurTranData,
            UINavigator uiNavigator,
            UseCaseCheckOperatorPasswd useCaseCheckOperatorPasswd
    ) {
        this.useCaseCheckOperatorPasswd = useCaseCheckOperatorPasswd;
        this.uiNavigator = uiNavigator;
        currentTranData = useCaseGetCurTranData.execute(null);
    }

    @Override
    protected void onFirstUIAttachment() {
        showTitle();
        loadPasswdKeyboard();
    }

    private void showTitle() {
        String subTitle = "";
        doUICmd_showTitle(currentTranData.getTitle(), subTitle);
    }

    private void loadPasswdKeyboard() {
        doUICmd_showPasswdBoxView(OperatorInfo.MAX_PASSWD_LEN);
    }

    public void checkPasswd(String passwd) {
        LogUtil.d("passwd=" + passwd);

        if (passwd == null || passwd.length() == 0) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_please_input_pwd));
        } else {
            String passwdMd5Value = EncryptionUtil.getMd5HexString(passwd.getBytes());
            OperatorInfo operatorInfo = new OperatorInfo(OperatorInfo.TYPE_MANAGER, passwdMd5Value);

            Disposable subscribe = useCaseCheckOperatorPasswd.asyncExecute(operatorInfo).subscribe(aBoolean -> {
                if (aBoolean) {
                    uiNavigator.getUiFlowControlData().setCheckPasswdSuccess(true);
                    doUICmd_navigatorToNext();
                } else {
                    doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_wrong_passwd));
                    doUICmd_resetPasswdBox();
                }
            }, throwable -> {
                doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_user_name_not_exist));
            });
        }
    }

    private void doUICmd_resetPasswdBox() {
        execute(ui -> ui.resetPasswdBoxView());
    }

    private void doUICmd_showPasswdBoxView(int maxPasswdLen) {
        execute(ui -> ui.showPasswdBoxView(maxPasswdLen));
    }
}
