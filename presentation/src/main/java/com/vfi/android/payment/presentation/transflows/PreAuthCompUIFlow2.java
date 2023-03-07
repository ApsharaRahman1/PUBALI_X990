package com.vfi.android.payment.presentation.transflows;

import android.content.Context;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.presentation.navigation.TransUiFlow;
import com.vfi.android.payment.presentation.navigation.UIFlowControlData;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.utils.AndroidUtil;
import com.vfi.android.payment.presentation.view.activities.CheckCardActivity;
import com.vfi.android.payment.presentation.view.activities.CheckPasswdActivity;
import com.vfi.android.payment.presentation.view.activities.ElectronicSignActivity;
import com.vfi.android.payment.presentation.view.activities.EmvProcessActivity;
import com.vfi.android.payment.presentation.view.activities.InputAmountActivity;
import com.vfi.android.payment.presentation.view.activities.InputCVV2Activity;
import com.vfi.android.payment.presentation.view.activities.InputCardExpiryDateActivity;
import com.vfi.android.payment.presentation.view.activities.InputCardNumActivity;
import com.vfi.android.payment.presentation.view.activities.InputOrgAuthCodeActivity;
import com.vfi.android.payment.presentation.view.activities.InputOrgRefNumActivity;
import com.vfi.android.payment.presentation.view.activities.InputOrgTransDateActivity;
import com.vfi.android.payment.presentation.view.activities.InputPinActivity;
import com.vfi.android.payment.presentation.view.activities.InputTipAmountActivity;
import com.vfi.android.payment.presentation.view.activities.MainMenuActivity;
import com.vfi.android.payment.presentation.view.activities.NetworkProcessActivity;
import com.vfi.android.payment.presentation.view.activities.SelectHostMerchantActivity;
import com.vfi.android.payment.presentation.view.activities.TransFailedActivity;
import com.vfi.android.payment.presentation.view.activities.TransSuccessActivity;

import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_CHECK_CARD;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_CHECK_PASSWD;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_CHOICE_PAYMENT;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_DCC_REQUEST;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_EMV;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_ESIGN;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_INPUT_AMOUNT;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_INPUT_CARD_EXPIRY_DATE;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_INPUT_CARD_NUM;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_INPUT_CVV2;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_INPUT_ORG_AUTH_CODE;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_INPUT_ORG_REF_NUM;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_INPUT_ORG_TRANS_DATE;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_INPUT_PIN;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_INPUT_TIP_AMOUNT;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_SCAN;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_SELECT_HOST_MERCHANT;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_TRANS_END;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_TRANS_ENTRY;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_TRANS_FAILED;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_TRANS_NETWORK_PROCESS;
import static com.vfi.android.payment.presentation.navigation.UIState.UI_STATE_TRANS_SUCCESS;


public class PreAuthCompUIFlow2 extends BaseTransFlow implements TransUiFlow {
    private final String TAG = TAGS.Navigator;
    private UINavigator uiNavigator;

    @Override
    public void navigatorToNext(Context context, UINavigator uiNavigator) {
        this.uiNavigator = uiNavigator;
        UIFlowControlData controlData = uiNavigator.getUiFlowControlData();
        if (context == null || controlData == null) {
            return;
        }

        Class entryActivityClass = MainMenuActivity.class;
        if (!isCorrectUIEntry(context, controlData, entryActivityClass)) {
            return;
        }

        int[] supportUiBackStateList = {UI_STATE_TRANS_ENTRY, UI_STATE_CHECK_PASSWD,
                UI_STATE_CHOICE_PAYMENT, UI_STATE_INPUT_TIP_AMOUNT, UI_STATE_SCAN};
        if (isNeedUIBack(context, controlData, supportUiBackStateList)) {
            controlData.setScan(false);
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
            case UI_STATE_CHECK_CARD:
                ui_state_check_card(context, controlData);
                break;
            case UI_STATE_INPUT_AMOUNT:
                ui_state_input_amount(context, controlData);
                break;
            case UI_STATE_INPUT_TIP_AMOUNT:
                ui_state_input_tip_amount(context, controlData);
                break;
            case UI_STATE_INPUT_CARD_NUM:
                ui_state_input_cardnum(context, controlData);
                break;
            case UI_STATE_INPUT_CARD_EXPIRY_DATE:
                ui_state_input_card_expiry_date(context, controlData);
                break;
            case UI_STATE_SELECT_HOST_MERCHANT:
                ui_state_select_host_merchant(context, controlData);
                break;
            case UI_STATE_INPUT_CVV2:
                ui_state_input_cvv2(context, controlData);
                break;
            case UI_STATE_INPUT_ORG_TRANS_DATE:
                ui_state_input_org_trans_date(context, controlData);
                break;
            case UI_STATE_INPUT_ORG_AUTH_CODE:
                ui_state_input_org_auth_code(context, controlData);
                break;
            case UI_STATE_INPUT_ORG_REF_NUM:
                ui_state_input_org_ref_num(context, controlData);
                break;
            case UI_STATE_EMV:
                ui_state_start_emv(context, controlData);
                break;
            case UI_STATE_DCC_REQUEST:
                ui_state_DCC_request(context, controlData);
                break;
            case UI_STATE_TRANS_NETWORK_PROCESS:
                ui_state_network_process(context, controlData);
                break;
            case UI_STATE_ESIGN:
                ui_state_esign(context, controlData);
                break;
            case UI_STATE_TRANS_FAILED:
            case UI_STATE_TRANS_SUCCESS:
                doBackToEntry(context, uiNavigator, entryActivityClass, false);
                break;
            case UI_STATE_INPUT_PIN:
                ui_state_input_pin(context, controlData);
                break;
            case UI_STATE_TRANS_END:
                LogUtil.d(TAG, "UI_STATE_TRANS_END");
                doBackToEntry(context, uiNavigator, entryActivityClass, true);
                break;
        }
    }

    private void ui_state_trans_entry(Context context, UIFlowControlData controlData) {
        if (controlData.isSysParamIsNeedCheckPasswd()) {
            jumpToState(context,UI_STATE_CHECK_PASSWD,  CheckPasswdActivity.class);
        } else {
            jumpToState(context,UI_STATE_INPUT_AMOUNT,  InputAmountActivity.class);
        }
    }

    private void ui_state_check_passwd(Context context, UIFlowControlData controlData) {
        if (controlData.isCheckPasswdSuccess()) {
            jumpToState(context,UI_STATE_INPUT_AMOUNT,  InputAmountActivity.class);
        }
    }

    private void ui_state_input_amount(Context context, UIFlowControlData controlData) {
        if (controlData.isNeedInputTipAmount()) {
            jumpToState(context,UI_STATE_INPUT_TIP_AMOUNT,  InputTipAmountActivity.class);
        } else {
            jumpToState(context,UI_STATE_CHECK_CARD,  CheckCardActivity.class);
        }
    }

    private void ui_state_input_tip_amount(Context context, UIFlowControlData controlData) {
        jumpToState(context,UI_STATE_CHECK_CARD,  CheckCardActivity.class);
    }

    private void ui_state_check_card(Context context, UIFlowControlData controlData) {
        if (controlData.isManualInput()) {
            jumpToState(context,UI_STATE_INPUT_CARD_NUM,  InputCardNumActivity.class);
        } else if (controlData.isSwipeCard()) {
            jumpToState(context, UI_STATE_SELECT_HOST_MERCHANT, SelectHostMerchantActivity.class);
        } else if (controlData.isTransFailed()) {
            jumpToState(context,UI_STATE_TRANS_FAILED,  TransFailedActivity.class);
        } else {
            // EMV flow
            jumpToState(context,UI_STATE_EMV,  EmvProcessActivity.class);
        }
    }

    private void ui_state_input_cardnum(Context context, UIFlowControlData controlData) {
        jumpToState(context, UI_STATE_SELECT_HOST_MERCHANT, SelectHostMerchantActivity.class);
    }

    private void ui_state_input_card_expiry_date(Context context, UIFlowControlData controlData) {
        if (controlData.isNeedInputCVV2()) {
            jumpToState(context,UI_STATE_INPUT_CVV2,  InputCVV2Activity.class);
        } else {
            jumpToState(context,UI_STATE_INPUT_ORG_TRANS_DATE,  InputOrgTransDateActivity.class);
        }
    }

    private void ui_state_select_host_merchant(Context context, UIFlowControlData controlData) {
        if (controlData.isManualInput()) {
            jumpToState(context, UI_STATE_INPUT_CARD_EXPIRY_DATE, InputCardExpiryDateActivity.class);
        } else {
            jumpToState(context,UI_STATE_INPUT_ORG_TRANS_DATE,  InputOrgTransDateActivity.class);
        }
    }

    private void ui_state_input_org_trans_date(Context context, UIFlowControlData controlData) {
        jumpToState(context,UI_STATE_INPUT_ORG_AUTH_CODE,  InputOrgAuthCodeActivity.class);
    }

    private void ui_state_input_org_auth_code(Context context, UIFlowControlData controlData) {
        jumpToState(context,UI_STATE_INPUT_ORG_REF_NUM,  InputOrgRefNumActivity.class);
    }

    private void ui_state_input_org_ref_num(Context context, UIFlowControlData controlData) {
        if (controlData.isManualInput() || controlData.isSwipeCard()) {
            jumpToState(context, UI_STATE_TRANS_NETWORK_PROCESS, NetworkProcessActivity.class);
        } else {
            importCardConfirmResult(true);
            controlData.backToUIState(UI_STATE_EMV);
        }
    }

    private void ui_state_input_cvv2(Context context, UIFlowControlData controlData) {
        jumpToState(context,UI_STATE_INPUT_ORG_TRANS_DATE,  InputOrgTransDateActivity.class);
    }

    private void ui_state_DCC_request(Context context, UIFlowControlData controlData) {
        if (controlData.isSwipeCard() || controlData.isManualInput()) {
            if (controlData.isNeedInputPin()) {
                jumpToState(context,UI_STATE_INPUT_PIN,  InputPinActivity.class);
            } else {
                jumpToState(context,UI_STATE_TRANS_NETWORK_PROCESS,  NetworkProcessActivity.class);
            }
        } else {
            AndroidUtil.finishActivity(context);
            controlData.setNeedDCC(false);
            controlData.popUIState();
        }
    }

    private void ui_state_start_emv(Context context, UIFlowControlData controlData) {
        if (controlData.isNeedSelectHostMerchant()) {
            controlData.setNeedSelectHostMerchant(false);
            jumpToState(context,UI_STATE_SELECT_HOST_MERCHANT,  SelectHostMerchantActivity.class);
        } else if (controlData.isNeedInputPin()) {
            jumpToState(context,UI_STATE_INPUT_PIN,  InputPinActivity.class);
        } else if (controlData.isTransFailed()) {
            jumpToState(context,UI_STATE_TRANS_FAILED,  TransFailedActivity.class);
        } else if (controlData.isNeedOnline()) {
            jumpToState(context,UI_STATE_TRANS_NETWORK_PROCESS,  NetworkProcessActivity.class);
        } else if (controlData.isEmvOfflineApprovaled()) {
            if (controlData.isWearAble()) {
                LogUtil.i(TAG, "Wearable device no need to sign");
                jumpToState(context,UI_STATE_TRANS_SUCCESS,  TransSuccessActivity.class);
            } else {
                if (controlData.isNeedESign()) {
                    if (controlData.isVerifiedSmallAmt()) {
                        jumpToState(context,UI_STATE_TRANS_SUCCESS,  TransSuccessActivity.class);

                    } else {
                        jumpToState(context,UI_STATE_ESIGN,  ElectronicSignActivity.class);
                    }
                } else {
                    jumpToState(context,UI_STATE_TRANS_SUCCESS,  TransSuccessActivity.class);
                }
            }
        } else if (controlData.isEmvBackToCheckCard()) {
            AndroidUtil.finishActivity(context);
            controlData.setEmvBackToCheckCard(false);
            controlData.popUIState();
        } else if (controlData.isNeedDCC() && controlData.isSysParamIsSupportDCC()) {
//            jumpToState(context,UI_STATE_DCC_REQUEST,  DCCFlowActivity.class);
//        } else if (controlData.getPaymentType() == PaymentType.INSTALMENT) {
//              jumpToState(context,UI_STATE_CHOOSE_INSTALLMENT_PROMO,  ChooseInstallmentPromoPromoActivity.class);
        }
    }

    private void ui_state_input_pin(Context context, UIFlowControlData controlData) {
        if (controlData.isTransFailed()) {
            jumpToState(context,UI_STATE_TRANS_FAILED,  TransFailedActivity.class);
        } else if (controlData.isInputPinFinished()) {
            if (controlData.isSwipeCard() || controlData.isManualInput()) {
                jumpToState(context,UI_STATE_TRANS_NETWORK_PROCESS,  NetworkProcessActivity.class);
            } else {
                controlData.setNeedInputPin(false);
                AndroidUtil.finishActivity(context);
                controlData.popUIState();
            }
        }
    }

    private void ui_state_network_process(Context context, UIFlowControlData controlData) {
        boolean isTransFailed = controlData.isTransFailed();
        if (isTransFailed) {
            jumpToState(context,UI_STATE_TRANS_FAILED,  TransFailedActivity.class);
        } else if (controlData.isWearAble()) {
            LogUtil.i(TAG, "Wearable device no need to sign");
            jumpToState(context,UI_STATE_TRANS_SUCCESS,  TransSuccessActivity.class);
        } else {
            if (controlData.isNeedESign()) {
                LogUtil.i(TAG, "isVerifiedSmallAmt : " + controlData.isVerifiedSmallAmt());
                if (controlData.isVerifiedSmallAmt()) {
                    LogUtil.i(TAG, "Success");
                    jumpToState(context,UI_STATE_TRANS_SUCCESS,  TransSuccessActivity.class);
                } else {
                    jumpToState(context,UI_STATE_ESIGN,  ElectronicSignActivity.class);
                }
            } else {
                jumpToState(context,UI_STATE_TRANS_SUCCESS,  TransSuccessActivity.class);
            }
        }
    }

    private void ui_state_esign(Context context, UIFlowControlData controlData) {
        jumpToState(context,UI_STATE_TRANS_SUCCESS,  TransSuccessActivity.class);
    }
}
