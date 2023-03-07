package com.vfi.android.payment.presentation.utils;

import com.vfi.android.domain.entities.businessbeans.SwitchParameter;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalCfg;
import com.vfi.android.domain.interactor.repository.UseCaseGetTransSwitchParameter;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.presentation.navigation.UINavigator;

public class TransUtil {
    private static final String TAG = TAGS.Transaction;

    public static void doUINavigatorParamInit(UINavigator uiNavigator,
                                              int transType,
                                              UseCaseGetTerminalCfg useCaseGetTerminalCfg,
                                              UseCaseGetTransSwitchParameter useCaseGetTransSwitchParameter) {

        try {
            uiNavigator.resetUIFlowControlData();

            uiNavigator.getUiFlowControlData().setTransType(transType);
            LogUtil.d(TAG, "transType=" + transType);

            TerminalCfg terminalCfg = useCaseGetTerminalCfg.execute(null);
            uiNavigator.getUiFlowControlData().setSysParamOperatorTimeout(terminalCfg.getOperationTimeout());

            SwitchParameter switchParameter = useCaseGetTransSwitchParameter.execute(transType);
            boolean isTransNeedCheckPasswd = switchParameter.isEnableInputManagerPwd();
            LogUtil.d(TAG, "isEnableInputManagerPwd=" + isTransNeedCheckPasswd);
            if (isTransNeedCheckPasswd) {
                uiNavigator.getUiFlowControlData().setSysParamIsNeedCheckPasswd(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
