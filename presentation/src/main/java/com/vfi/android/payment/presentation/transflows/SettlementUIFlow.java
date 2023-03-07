package com.vfi.android.payment.presentation.transflows;

import android.content.Context;

import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.presentation.navigation.TransUiFlow;
import com.vfi.android.payment.presentation.navigation.UIFlowControlData;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.view.activities.CheckPasswdActivity;
import com.vfi.android.payment.presentation.view.activities.MainMenuActivity;
import com.vfi.android.payment.presentation.view.activities.NetworkProcessActivity;
import com.vfi.android.payment.presentation.view.activities.SettlementActivity;
import com.vfi.android.payment.presentation.view.activities.TransFailedActivity;
import com.vfi.android.payment.presentation.view.activities.TransSuccessActivity;

import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_CHECK_PASSWD;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_SHOW_STETTLEMENT_DETAIL;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_TRANS_END;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_TRANS_ENTRY;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_TRANS_FAILED;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_TRANS_NETWORK_PROCESS;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_TRANS_SUCCESS;

public class SettlementUIFlow extends BaseTransFlow implements TransUiFlow {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void navigatorToNext(Context context, UINavigator uiNavigator) {
        UIFlowControlData controlData = uiNavigator.getUiFlowControlData();
        if (context == null || controlData == null) {
            return;
        }

        Class entryActivityClass = MainMenuActivity.class;
        if (!isCorrectUIEntry(context, controlData, entryActivityClass)) {
            return;
        }

        int[] supportUiBackStateList = {UI_STATE_TRANS_ENTRY, UI_STATE_CHECK_PASSWD};
        if (isNeedUIBack(context, controlData, supportUiBackStateList)) {
            controlData.setManualInput(false);
            return;
        }

        if (controlData.isTransFailed()
                && controlData.getStackTopUIState() != UI_STATE_TRANS_FAILED) {
            controlData.setTransFailed(false);
            jumpToState(context,UI_STATE_TRANS_FAILED,  TransFailedActivity.class);
            return;
        }

        switch (controlData.getStackTopUIState()) {
            case UI_STATE_TRANS_ENTRY:
                ui_state_trans_entry(context, controlData);
                break;
            case UI_STATE_CHECK_PASSWD:
                ui_state_check_passwd(context, controlData);
                break;
            case UI_STATE_SHOW_STETTLEMENT_DETAIL:
                ui_state_show_settlement_detail(context, controlData);
                break;
            case UI_STATE_TRANS_NETWORK_PROCESS:
                ui_state_network_process(context, controlData);
                break;
            case UI_STATE_TRANS_SUCCESS:
            case UI_STATE_TRANS_FAILED:
                doBackToEntry(context, uiNavigator, entryActivityClass, false);
                break;
            case UI_STATE_TRANS_END:
                LogUtil.d("TAG", "UI_STATE_TRANS_END");
                doBackToEntry(context, uiNavigator, entryActivityClass, true);
                break;
        }
    }

    private void ui_state_trans_entry(Context context, UIFlowControlData controlData) {
        if (controlData.isSysParamIsNeedCheckPasswd()) {
            jumpToState(context,UI_STATE_CHECK_PASSWD,  CheckPasswdActivity.class);
        } else {
            jumpToState(context,UI_STATE_SHOW_STETTLEMENT_DETAIL,  SettlementActivity.class);
        }
    }

    private void ui_state_check_passwd(Context context, UIFlowControlData controlData) {
        if (controlData.isCheckPasswdSuccess()) {
            jumpToState(context,UI_STATE_SHOW_STETTLEMENT_DETAIL,  SettlementActivity.class);
        }
    }

    private void ui_state_show_settlement_detail(Context context, UIFlowControlData controlData) {
        jumpToState(context,UI_STATE_TRANS_NETWORK_PROCESS,  NetworkProcessActivity.class);
    }

    private void ui_state_network_process(Context context, UIFlowControlData controlData) {
        boolean isTransFailed = controlData.isTransFailed();
        if (isTransFailed) {
            jumpToState(context,UI_STATE_TRANS_FAILED,  TransFailedActivity.class);
        } else {
            jumpToState(context,UI_STATE_TRANS_SUCCESS,  TransSuccessActivity.class);
        }
    }
}
