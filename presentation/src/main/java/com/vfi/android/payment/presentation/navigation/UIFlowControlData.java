package com.vfi.android.payment.presentation.navigation;

import android.content.Context;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.presentation.utils.AndroidUtil;

import java.util.Stack;

public class UIFlowControlData {
    private final String TAG = TAGS.Navigator;

    private int transType;
    private int paymentType; // please refer to PaymentType.java
    private int redeemType; // refer to RedemptionType.java
    private boolean isSwipeCard;
    private boolean isNeedInputPin;
    private boolean isTransFailed;
    private boolean isNeedUIBack;
    private boolean isGoBackToMainMenu;
    private boolean isNeedOnline;
    private boolean isInputPinFinished;
    private boolean isNeedESign;
    private boolean isWearAble;
    private boolean isTransRecordFound;
    private boolean isScan;
    private boolean isManualInput;
    private boolean isEmvSimpleFlowFinished;
    private boolean isCheckPasswdSuccess;
    private boolean isNeedConfirmCardInfo;
    private boolean isEmvOfflineApprovaled;
    private boolean isEmvBackToCheckCard;
    private boolean isNeedDCC;
    private boolean isSupportQrRefund;
    private boolean isAmexSaleCompletion;
    private int installmentType;
    private int cvmResult;
    private boolean isNeedInputTipAmount;
    private boolean isNeedInputCVV2;

    private boolean sysParamIsSupportDCC;
    private int sysParamOperatorTimeout;
    private boolean sysParamIsNeedCheckPasswd;
    private boolean sysParamIsRefundNeedInputRefNum;
    private boolean sysParamIsRefundNeedInputApprovalCode;
    private int installmentInputStatus;
    private boolean isGotoQrPayment;

    //small amount
    private boolean isVerifiedSmallAmt;
    private boolean isNeedInputPinBySmallAmt;
    private boolean isNeedSignBySmallAmt;
    private boolean isPrintMerchant;
    private boolean isPrintCustomer;

    //amex parameter
    private boolean isNeedInput4DBC;

    //need popup confirm dialog when perform cancel transaction.
    private boolean notNeedDialogConfirmUIBack;

    private Stack<UIStateEntry> uiStateStack;
    private Context currentUIContext;

    private boolean isNeedMutiHost;
    private boolean isNeedMutiMerchant;
    private boolean isNeedSelectHostMerchant;
    private boolean isNeedChooseInstallmentPromo;


    public int getTransType() {
        return transType;
    }

    public void setTransType(int transType) {
        this.transType = transType;
    }

    public boolean isNeedInputTipAmount() {
        return isNeedInputTipAmount;
    }

    public void setNeedInputTipAmount(boolean needInputTipAmount) {
        isNeedInputTipAmount = needInputTipAmount;
    }

    public boolean isNeedInputCVV2() {
        return isNeedInputCVV2;
    }

    public void setNeedInputCVV2(boolean needInputCVV2) {
        isNeedInputCVV2 = needInputCVV2;
    }

    public boolean isNeedSelectHostMerchant() {
        return isNeedSelectHostMerchant;
    }

    public void setNeedSelectHostMerchant(boolean needSelectHostMerchant) {
        isNeedSelectHostMerchant = needSelectHostMerchant;
    }

    public boolean isNeedChooseInstallmentPromo() {
        return isNeedChooseInstallmentPromo;
    }

    public void setNeedChooseInstallmentPromo(boolean needChooseInstallmentPromo) {
        isNeedChooseInstallmentPromo = needChooseInstallmentPromo;
    }

    private class UIStateEntry {
        int uiState;
        Context previousStateUiContext;

        public UIStateEntry(int uiState, Context previousStateUiContext) {
            this.uiState = uiState;
            this.previousStateUiContext = previousStateUiContext;
        }
    }

    public UIFlowControlData() {
        uiStateStack = new Stack();
        initControlData();
    }

    public void initControlData() {
        paymentType = -1;
        isSwipeCard = false;
        isNeedInputPin = false;
        isTransFailed = false;
        isNeedUIBack = false;
        isGoBackToMainMenu = false;
        isNeedOnline = false;
        isInputPinFinished = false;
        isNeedESign = false;
        isTransRecordFound = false;
        isScan = false;
        isManualInput = false;
        isEmvSimpleFlowFinished = false;
        isCheckPasswdSuccess = false;
        isNeedConfirmCardInfo = false;
        isEmvOfflineApprovaled = false;
        isEmvBackToCheckCard = false;
        sysParamIsNeedCheckPasswd = false;
        isSupportQrRefund = false;

        isVerifiedSmallAmt = false;
        isPrintMerchant = true;
        isPrintCustomer = true;
        isNeedInputPinBySmallAmt = true;
        isNeedSignBySmallAmt = true;

        isNeedMutiHost = false;
        isNeedMutiMerchant = false;
        isNeedSelectHostMerchant = false;
        isNeedChooseInstallmentPromo = false;

        sysParamOperatorTimeout = 60; // default 60
    }

    public boolean isUIStateStackEmpty() {
        return uiStateStack.empty();
    }

    public int popUIState() {
        if (uiStateStack.empty()) {
            return UIState.UI_STATE_UNKOWN;
        }

        UIStateEntry uiStateEntry = uiStateStack.pop();
        return uiStateEntry.uiState;
    }

    public int backToUIState(int uiState) {
        Context lastStateContext = currentUIContext;

        while (!uiStateStack.empty()) {
            UIStateEntry uiStateEntry = uiStateStack.peek();
            if (uiStateEntry.uiState == uiState) {
                break;
            } else {
                if (uiStateEntry.uiState != UIState.UI_STATE_TRANS_ENTRY) {
                    AndroidUtil.finishActivity(lastStateContext);
                    LogUtil.d(TAG, "backToUIState finish uiState[" + uiStateEntry.uiState + "] finishActivity " + lastStateContext.getClass().getSimpleName());
                }
                lastStateContext = uiStateEntry.previousStateUiContext;
                uiStateStack.pop();
            }
        }

        if (uiStateStack.empty()) {
            LogUtil.d(TAG, "UIState=" + uiState + " not found in uiStateStack");
            return UIState.UI_STATE_UNKOWN;
        } else {
            LogUtil.d(TAG, "back to state[" + uiState + "] success");
            return uiState;
        }
    }

    public void pushUIState(int uiState) {
        LogUtil.d(TAG, "===> Jump to state[" + UIState.toString(uiState) + "]");
        if (!uiStateStack.empty() && uiStateStack.peek().uiState == uiState) {
            return;
        }

        uiStateStack.push(new UIStateEntry(uiState, currentUIContext));
    }

    public int getStackTopUIState() {
        if (uiStateStack.empty()) {
            return UIState.UI_STATE_UNKOWN;
        }

        return uiStateStack.peek().uiState;
    }

    public void clearUIStack() {
        if (uiStateStack != null) {
            LogUtil.d(TAG, "start clearUIStack");
            for (UIStateEntry uiState : uiStateStack) {
                LogUtil.d(TAG, "uiState[" + uiState.uiState + "]");
                uiState.previousStateUiContext = null;
            }
//            uiStateStack = null;
            LogUtil.d(TAG, "clearUIStack end");
        }
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setSwipeCard(boolean swipeCard) {
        isSwipeCard = swipeCard;
    }

    public boolean isSwipeCard() {
        return isSwipeCard;
    }

    public void setNeedInputPin(boolean needInputPin) {
        isNeedInputPin = needInputPin;
    }

    public boolean isNeedInputPin() {
        return isNeedInputPin;
    }

    public void setTransFailed(boolean transFailed) {
        isTransFailed = transFailed;
    }

    public boolean isTransFailed() {
        return isTransFailed;
    }

    public boolean isNeedUIBack() {
        return isNeedUIBack;
    }

    public void setNeedUIBack(boolean needUIBack) {
        isNeedUIBack = needUIBack;
    }

    public boolean isGoBackToMainMenu() {
        return isGoBackToMainMenu;
    }

    public void setGoBackToMainMenu(boolean goBackToMainMenu) {
        isGoBackToMainMenu = goBackToMainMenu;
    }

    public boolean isNeedOnline() {
        return isNeedOnline;
    }

    public void setNeedOnline(boolean needOnline) {
        isNeedOnline = needOnline;
    }

    public boolean isInputPinFinished() {
        return isInputPinFinished;
    }

    public void setInputPinFinished(boolean inputPinFinished) {
        isInputPinFinished = inputPinFinished;
    }

    public boolean isNeedESign() {
        return isNeedESign;
    }

    public void setNeedESign(boolean needESign) {
        isNeedESign = needESign;
    }

    public boolean isScan() {
        return isScan;
    }

    public void setScan(boolean scan) {
        isScan = scan;
    }

    public boolean isTransRecordFound() {
        return isTransRecordFound;
    }

    public void setTransRecordFound(boolean transRecordFound) {
        isTransRecordFound = transRecordFound;
    }

    public boolean isManualInput() {
        return isManualInput;
    }

    public void setManualInput(boolean manualInput) {
        isManualInput = manualInput;
    }

    public boolean isEmvSimpleFlowFinished() {
        return isEmvSimpleFlowFinished;
    }

    public void setEmvSimpleFlowFinished(boolean emvSimpleFlowFinished) {
        isEmvSimpleFlowFinished = emvSimpleFlowFinished;
    }

    public void setSysParamOperatorTimeout(int sysParamOperatorTimeout) {
        this.sysParamOperatorTimeout = sysParamOperatorTimeout;
    }

    public int getSysParamOperatorTimeout() {
        return sysParamOperatorTimeout;
    }

    public boolean isCheckPasswdSuccess() {
        return isCheckPasswdSuccess;
    }

    public void setCheckPasswdSuccess(boolean checkPasswdSuccess) {
        isCheckPasswdSuccess = checkPasswdSuccess;
    }

    public boolean isNeedConfirmCardInfo() {
        return isNeedConfirmCardInfo;
    }

    public void setNeedConfirmCardInfo(boolean needConfirmCardInfo) {
        isNeedConfirmCardInfo = needConfirmCardInfo;
    }

    public void setSysParamIsNeedCheckPasswd(boolean sysParamIsNeedCheckPasswd) {
        this.sysParamIsNeedCheckPasswd = sysParamIsNeedCheckPasswd;
    }

    public boolean isSysParamIsNeedCheckPasswd() {
        return sysParamIsNeedCheckPasswd;
    }

    public boolean isEmvOfflineApprovaled() {
        return isEmvOfflineApprovaled;
    }

    public void setEmvOfflineApprovaled(boolean emvOfflineApprovaled) {
        isEmvOfflineApprovaled = emvOfflineApprovaled;
    }

    public boolean isEmvBackToCheckCard() {
        return isEmvBackToCheckCard;
    }

    public void setEmvBackToCheckCard(boolean emvBackToCheckCard) {
        isEmvBackToCheckCard = emvBackToCheckCard;
    }

    public boolean isNotNeedDialogConfirmUIBack() {
        return notNeedDialogConfirmUIBack;
    }

    public void setNotNeedDialogConfirmUIBack(boolean notNeedDialogConfirmUIBack) {
        this.notNeedDialogConfirmUIBack = notNeedDialogConfirmUIBack;
    }

    public boolean isSysParamIsSupportDCC() {
        return sysParamIsSupportDCC;
    }

    public void setSysParamIsSupportDCC(boolean sysParamIsSupportDCC) {
        this.sysParamIsSupportDCC = sysParamIsSupportDCC;
    }

    public boolean isNeedDCC() {
        return isNeedDCC;
    }

    public void setNeedDCC(boolean needDCC) {
        isNeedDCC = needDCC;
    }

    public int getInstallmentType() {
        return installmentType;
    }

    public void setInstallmentType(int installmentType) {
        this.installmentType = installmentType;
    }

    public Context getCurrentUIContext() {
        return currentUIContext;
    }

    public void setCurrentUIContext(Context currentUIContext) {
        this.currentUIContext = currentUIContext;
    }

    public boolean isSupportQrRefund() {
        return isSupportQrRefund;
    }

    public void setSupportQrRefund(boolean supportQrRefund) {
        isSupportQrRefund = supportQrRefund;
    }

    public boolean isSysParamIsRefundNeedInputApprovalCode() {
        return sysParamIsRefundNeedInputApprovalCode;
    }

    public boolean isSysParamIsRefundNeedInputRefNum() {
        return sysParamIsRefundNeedInputRefNum;
    }

    public void setSysParamIsRefundNeedInputApprovalCode(boolean sysParamIsRefundNeedInputApprovalCode) {
        this.sysParamIsRefundNeedInputApprovalCode = sysParamIsRefundNeedInputApprovalCode;
    }

    public void setSysParamIsRefundNeedInputRefNum(boolean sysParamIsRefundNeedInputRefNum) {
        this.sysParamIsRefundNeedInputRefNum = sysParamIsRefundNeedInputRefNum;
    }

    public boolean isGotoQrPayment() {
        return isGotoQrPayment;
    }

    public void setGotoQrPayment(boolean gotoQrPayment) {
        isGotoQrPayment = gotoQrPayment;
    }

    public int getInstallmentInputStatus() {
        return installmentInputStatus;
    }

    public void setInstallmentInputStatus(int installmentInputStatus) {
        this.installmentInputStatus = installmentInputStatus;
    }

    public int getRedeemType() {
        return redeemType;
    }

    public void setRedeemType(int redeemType) {
        this.redeemType = redeemType;
    }

    public boolean isWearAble() {
        return isWearAble;
    }

    public void setWearAble(boolean wearAble) {
        isWearAble = wearAble;
    }

    public boolean isAmexSaleCompletion() {
        return isAmexSaleCompletion;
    }

    public void setAmexSaleCompletion(boolean amexSaleCompletion) {
        isAmexSaleCompletion = amexSaleCompletion;
    }


    public boolean isVerifiedSmallAmt() {
        return isVerifiedSmallAmt;
    }

    public void setVerifiedSmallAmt(boolean verifiedSmallAmt) {
        isVerifiedSmallAmt = verifiedSmallAmt;
    }

    public boolean isNeedSignBySmallAmt() {
        return isNeedSignBySmallAmt;
    }

    public void setNeedSignBySmallAmt(boolean needSignBySmallAmt) {
        isNeedSignBySmallAmt = needSignBySmallAmt;
    }

    public boolean isPrintMerchant() {
        return isPrintMerchant;
    }

    public void setPrintMerchant(boolean printMerchant) {
        isPrintMerchant = printMerchant;
    }

    public boolean isPrintCustomer() {
        return isPrintCustomer;
    }

    public void setPrintCustomer(boolean printCustomer) {
        isPrintCustomer = printCustomer;
    }

    public boolean isNeedInputPinBySmallAmt() {
        return isNeedInputPinBySmallAmt;
    }

    public void setNeedInputPinBySmallAmt(boolean needInputPinBySmallAmt) {
        isNeedInputPinBySmallAmt = needInputPinBySmallAmt;
    }

    public boolean isNeedInput4DBC() {
        return isNeedInput4DBC;
    }

    public void setNeedInput4DBC(boolean needInput4DBC) {
        isNeedInput4DBC = needInput4DBC;
    }

    public int getCvmResult() {
        return cvmResult;
    }

    public void setCvmResult(int cvmResult) {
        this.cvmResult = cvmResult;
    }

    public boolean isNeedMutiHost() {
        return isNeedMutiHost;
    }

    public void setNeedMutiHost(boolean needMutiHost) {
        isNeedMutiHost = needMutiHost;
    }

    public boolean isNeedMutiMerchant() {
        return isNeedMutiMerchant;
    }

    public void setNeedMutiMerchant(boolean needMutiMerchant) {
        isNeedMutiMerchant = needMutiMerchant;
    }


}
