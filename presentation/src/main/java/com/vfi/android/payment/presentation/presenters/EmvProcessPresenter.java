package com.vfi.android.payment.presentation.presenters;


import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.EMVCallback;
import com.vfi.android.domain.entities.businessbeans.EmvInformation;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.entities.consts.EMVResult;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.deviceservice.UseCaseEmvStart;
import com.vfi.android.domain.interactor.deviceservice.UseCaseEmvStop;
import com.vfi.android.domain.interactor.deviceservice.UseCaseImportAppSelected;
import com.vfi.android.domain.interactor.deviceservice.UseCaseImportCardConfirmResult;
import com.vfi.android.domain.interactor.deviceservice.UseCaseImportCertConfirmResult;
import com.vfi.android.domain.interactor.deviceservice.UseCaseWaitingCardRemoved;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalCfg;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.EmvErrorCodeMapper;
import com.vfi.android.payment.presentation.mappers.ExceptionErrMsgMapper;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TvShowUtil;
import com.vfi.android.payment.presentation.view.contracts.EmvProcessUI;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

public class EmvProcessPresenter extends BasePresenter<EmvProcessUI> {
    private final String TAG = TAGS.EmvFlow;
    private final UseCaseEmvStart useCaseEmvStart;
    private final UseCaseEmvStop useCaseEmvStop;
    private final UseCaseImportAppSelected useCaseImportAppSelected;
    private final UseCaseImportCardConfirmResult useCaseImportCardConfirmResult;
    private final UseCaseImportCertConfirmResult useCaseImportCertConfirmResult;
    private final UseCaseWaitingCardRemoved useCaseWaitingCardRemoved;
    private final UINavigator uiNavigator;
    private final CurrentTranData currentTranData;
    private final int CONFIRM_CERT_TYPE_CANCEL = 0;
    private final int CONFIRM_CERT_TYPE_CONFIRM = 1;
    private final int CONFIRM_CERT_TYPE_NOTMATCH = 2;

    private boolean isAlreadyInputOfflinePin = false;
    private int selectAppCancelBtnIdex = -1;
    private EmvInformation emvInformation;
    private TerminalCfg terminalCfg;

    @Inject
    public EmvProcessPresenter(UseCaseEmvStart useCaseEmvStart,
                               UseCaseEmvStop useCaseEmvStop,
                               UseCaseGetCurTranData useCaseGetCurTranData,
                               UseCaseImportCertConfirmResult useCaseImportCertConfirmResult,
                               UINavigator uiNavigator,
                               UseCaseImportAppSelected useCaseImportAppSelected,
                               UseCaseImportCardConfirmResult useCaseImportCardConfirmResult,
                               UseCaseWaitingCardRemoved useCaseWaitingCardRemoved,
                               UseCaseGetTerminalCfg useCaseGetTerminalCfg) {
        this.useCaseEmvStart = useCaseEmvStart;
        this.useCaseEmvStop = useCaseEmvStop;
        this.currentTranData = useCaseGetCurTranData.execute(null);
        this.useCaseImportAppSelected = useCaseImportAppSelected;
        this.useCaseImportCardConfirmResult = useCaseImportCardConfirmResult;
        this.useCaseImportCertConfirmResult = useCaseImportCertConfirmResult;
        this.terminalCfg = useCaseGetTerminalCfg.execute(null);
        this.uiNavigator = uiNavigator;
        this.emvInformation = currentTranData.getEmvInfo();
        this.useCaseWaitingCardRemoved = useCaseWaitingCardRemoved;
    }

    @Override
    protected void onFirstUIAttachment() {
        startEmvProcess();
    }

    private void startEmvProcess() {
        emvInformation.setInEmvFlow(true);
        Disposable disposable = useCaseEmvStart.asyncExecute(null)
                .subscribe(this::doEmvFlow, throwable -> {
                    doErrorProcess(throwable);
                    emvInformation.setInEmvFlow(false);
                });
    }

    private void doEmvFlow(EMVCallback emvCallback) {
        switch (emvCallback.getCallbackType()) {
            case ON_REQUEST_AMOUNT:
                LogUtil.i(TAG, "ON_REQUEST_AMOUNT");
                // TODO 待做，以前做的时候没有import金额，况且我们有金额传入不会让输入金额
                break;
            case ON_SELECT_APP:
                LogUtil.i(TAG, "ON_SELECT_APP : " + emvCallback.getAppList());
                selectApp(emvCallback);
                break;
            case ON_REQUEST_ONLINE:
                LogUtil.i(TAG, "ON_REQUEST_ONLINE");
                doOnlineProcess(emvCallback);
                break;
            case ON_CONFIRM_CARDINFO:
                LogUtil.i(TAG, "ON_CONFIRM_CARDINFO");
                String pan = emvCallback.getCardInformation().getPan();
                LogUtil.d(TAG, "Pan=[" + pan + "]");
                if (pan != null && pan.length() > 19) {
                    doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_invalid_pan_number));
                    confirmCardInfo(false);
                } else {
                    doCardConfirmProcess();
                }
                break;
            case ON_CONFIRM_CERTINFO:
                LogUtil.i(TAG, "ON_CONFIRM_CERTINFO");
                confirmCertInfo(CONFIRM_CERT_TYPE_CONFIRM);
                break;
            case ON_REQUEST_INPUTPIN:
                LogUtil.i(TAG, "ON_REQUEST_INPUTPIN");
                startInputOnlinePin();
                break;
            case ON_REQUEST_INPUTOFFLINEPIN:
                LogUtil.i(TAG, "ON_REQUEST_INPUTOFFLINEPIN : PIN,  retryTimes" + emvCallback.getRetryTimes());
                startInputOfflinePin(emvCallback);
                break;
            case ON_TRADING_RES:
                LogUtil.i(TAG, "ON_TRADING_RES : " + emvCallback.getOnlineResult());
                doTradingResult(emvCallback);
                break;
        }
    }

    private String getFormatConfirmHintMsg() {
        String preferredName = currentTranData.getCardInfo().getApplicationLabel();
        String panInfo = ResUtil.getString(R.string.tv_hint_card_no) + TvShowUtil.formatAcount(currentTranData.getCardInfo().getPan());
        String expireDateInfo = ResUtil.getString(R.string.tv_hint_exp_date) + currentTranData.getCardInfo().getExpiredDate();
        String hint = preferredName + "\n" + panInfo + "\n" + expireDateInfo;

        return hint;
    }

    private void doCardConfirmProcess() {
        int cardEntryMode = currentTranData.getCardInfo().getCardEntryMode();
        if (cardEntryMode == CardEntryMode.IC) {
            String preferredName = currentTranData.getCardInfo().getApplicationLabel();

            String pan = currentTranData.getCardInfo().getPan();
            String expireDate = currentTranData.getCardInfo().getExpiredDate();
            doUICmd_showCardConfirmDialog(pan, expireDate, isSure -> {
                if (isSure) {
                    uiNavigator.getUiFlowControlData().setNeedSelectHostMerchant(true);
                    doUICmd_navigatorToNext();
                } else {
                    confirmCardInfo(false);
                    uiNavigator.getUiFlowControlData().setNotNeedDialogConfirmUIBack(true);
                    uiNavigator.getUiFlowControlData().setGoBackToMainMenu(true);
                    doUICmd_navigatorToNext();
                }
            });
        } else {
            uiNavigator.getUiFlowControlData().setNeedSelectHostMerchant(true);
            doUICmd_navigatorToNext();
        }
    }

    public void selectAppIdex(int index) {
        index++; // service aidl request (index=0 means cancel)

        if (index == selectAppCancelBtnIdex) {
            useCaseImportAppSelected.asyncExecuteWithoutResult(0);
        } else {
            useCaseImportAppSelected.asyncExecuteWithoutResult(index);
        }
    }

    private void confirmCardInfo(boolean isConfirm) {
        LogUtil.d(TAG, "confirmCardInfo=" + isConfirm);
        useCaseImportCardConfirmResult.asyncExecuteWithoutResult(isConfirm);
    }

    private void confirmCertInfo(int option) {
        useCaseImportCertConfirmResult.asyncExecuteWithoutResult(option);
    }

    private void selectApp(EMVCallback emvCallback) {
        List<String> applist = emvCallback.getAppList();
        List<String> appNameList = new ArrayList<>(applist.size());
        LogUtil.d("App list size=" + applist.size());

        for (int i = 0; i < applist.size(); i++) {
            String appName = applist.get(i);
            LogUtil.d("App" + i + "=" + appName);

            int bracketsIdex = appName.indexOf(":");
            if (bracketsIdex == -1) {
                bracketsIdex = appName.length();
            }
            LogUtil.d("bracketsIdex=" + bracketsIdex);

            appNameList.add(i, appName.substring(0, bracketsIdex));
        }

        appNameList.add(ResUtil.getString(R.string.btn_hint_cancel));
        selectAppCancelBtnIdex = appNameList.size();

        doUICmd_showSelectAppItems(appNameList);
    }

    private void startInputOnlinePin() {
        uiNavigator.getUiFlowControlData().setNeedInputPin(true);
        doUICmd_navigatorToNext();
    }

    private void startInputOfflinePin(EMVCallback emvCallback) {
        int retryTimes = emvCallback.getRetryTimes();
        LogUtil.d(TAG, "Offline pin, retry time =" + retryTimes);
        if (isAlreadyInputOfflinePin) {
            if (retryTimes == 1) {
                doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_last_attempt_enter_pin) + ", " + ResUtil.getString(R.string.toast_hint_retry_times) + "[" + retryTimes + "]");
            } else {
                doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_wrong_pin) + ", " + ResUtil.getString(R.string.toast_hint_retry_times) + "[" + retryTimes + "]");
            }
        }

        isAlreadyInputOfflinePin = true;

        if (retryTimes > 0) {
            uiNavigator.getUiFlowControlData().setNeedInputPin(true);
            doUICmd_navigatorToNext();
        }
    }

    private void doOnlineProcess(EMVCallback emvCallback) {
        LogUtil.i(TAG, "isNeedOnline=" + true);
        uiNavigator.getUiFlowControlData().setNeedOnline(true);
        doUICmd_navigatorToNext();
    }

    private void doTradingResult(EMVCallback emvCallback) {
        emvInformation = currentTranData.getEmvInfo();
        LogUtil.i(TAG, "ON_TRADING_RES : " + emvCallback.getOnlineResult());
        if (emvCallback.getOnlineResult() == EMVResult.EMV_COMPLETE.getId()) {
            LogUtil.i(TAG, "ON_TRADING_RES :EMV_COMPLETE");
            emvInformation.setInEmvFlow(false);
            uiNavigator.getUiFlowControlData().setEmvSimpleFlowFinished(true);
            boolean isNeedInputPin = isNeedInputPinByBinRouting();
            uiNavigator.getUiFlowControlData().setNeedInputPin(isNeedInputPin);
            doUICmd_navigatorToNext();
        } else if (emvCallback.getOnlineResult() == EMVResult.AARESULT_TC.getId()
                || emvCallback.getOnlineResult() == EMVResult.CTLS_TC.getId()) {
            LogUtil.i(TAG, "ON_TRADING_RES: AARESULT_TC/CTLS_TC");
            emvInformation.setEmvResult(EmvInformation.EMV_OFFLINE_APPROVED);
            emvInformation.setInEmvFlow(false);
            uiNavigator.getUiFlowControlData().setTransFailed(false);
            uiNavigator.getUiFlowControlData().setEmvOfflineApprovaled(true);
            uiNavigator.getUiFlowControlData().setNeedESign(isNeedESign());
            doUICmd_navigatorToNext();
        } else if (emvCallback.getOnlineResult() == EMVResult.CTLS_ARQC.getId()) {
            LogUtil.i(TAG, "ON_TRADING_RES: QPBOC_ARQC");
            uiNavigator.getUiFlowControlData().setNeedOnline(true);
            doUICmd_navigatorToNext();
        } else if (emvCallback.getOnlineResult() == EMVResult.CTLS_SEE_PHONE.getId()) {
            LogUtil.i(TAG, "ON_TRADING_RES: CTLS_SEE_PHONE");
            doUICmd_showAskBackToCheckCardDialog(ResUtil.getString(R.string.tv_hint_please_see_phone));
        } else if (emvCallback.getOnlineResult() == EMVResult.EMV_COMM_TIMEOUT.getId()) {
            LogUtil.i(TAG, "ON_TRADING_RES: EMV_COMM_TIMEOUT");
            doUICmd_showAskBackToCheckCardDialog(ResUtil.getString(R.string.tv_hint_try_again));
        } else {
            LogUtil.i(TAG, "ON_TRADING_RES: ERROR : " + EMVResult.findPbocResultById(emvCallback.getOnlineResult()).getMsg());
            emvInformation.setInEmvFlow(false);
            if (emvCallback.getOnlineResult() != EMVResult.AARESULT_AAC.getId() && emvCallback.getOnlineResult() < EMVResult.EMV_COMPLETE.getId()) {
                emvInformation.setEmvResult(EmvInformation.EMV_DECLINED);
            }
            uiNavigator.getUiFlowControlData().setTransFailed(true);
            currentTranData.setErrorMsg(ResUtil.getString(R.string.tv_hint_transaction_reject));
            doUICmd_navigatorToNext();
        }
    }

    private boolean isNeedInputPinByBinRouting() {
        boolean isNeedOnlinePin = currentTranData.isBinRoutingNeedOnlinePin();
        LogUtil.d(TAG, "isNeedInputPinByBinRouting = " + isNeedOnlinePin);
        return isNeedOnlinePin;
    }

    private boolean isNeedESign() {
        boolean isNeedESign = currentTranData.getEmvInfo().isRequestSignature();
        LogUtil.d(TAG, "isSupport esign=" + isNeedESign);
        return isNeedESign;
    }

    private void checkAndSyncWaitingCardRemove() {
        boolean needDelay = false;
        if (currentTranData.getCardInfo().getCardEntryMode() == CardEntryMode.RF) {
            needDelay = true;
        }
        Disposable disposable = useCaseWaitingCardRemoved.asyncExecute(needDelay).subscribe(isCardRemoved -> {
            if (isCardRemoved) {
                doUICmd_dismissFallbackRemoveCardDialog();
                backToCheckCard();
            }
        }, throwable -> {
            doUICmd_dismissFallbackRemoveCardDialog();
            backToCheckCard();
        });
    }

    private void doICFallbackProcess(CurrentTranData currentTranData) {
        useCaseEmvStop.execute(null);

        int cardEntryMode = currentTranData.getCardInfo().getCardEntryMode();
        if (cardEntryMode == CardEntryMode.IC) {
            currentTranData.setEMVNeedTapToInsert(false);
            currentTranData.setEMVNeedFallback(true);
            if (currentTranData.getIcCardfallbackRemainTimes() <= 0) {
                String hintMsg = "";
                if (!currentTranData.getEmvInfo().isEmvConfirmCard()) {
                    hintMsg += ResUtil.getString(R.string.tv_hint_chip_cannot_be_read) + "\n";
                    hintMsg += ResUtil.getString(R.string.tv_hint_card_type_not_support) + "\n";
                }
                hintMsg += ResUtil.getString(R.string.tv_hint_swipe_magnetic_card);
                doUICmd_showFallbackRemoveCardDialog(hintMsg);
            } else {
                doUICmd_showFallbackRemoveCardDialog(ResUtil.getString(R.string.tv_hint_remove_card_try_another_card));
            }
        } else {
            currentTranData.setEMVNeedTapToInsert(true);
            currentTranData.setEMVNeedFallback(false);
            if (currentTranData.getRfCardFallbackRemainTimes() <= 0) {
                doUICmd_showFallbackRemoveCardDialog(ResUtil.getString(R.string.tv_hint_swipe_inset_type_card));
            } else {
                doUICmd_showFallbackRemoveCardDialog(ResUtil.getString(R.string.tv_hint_card_not_support) + "\n" + ResUtil.getString(R.string.tv_hint_try_another_card));
            }
        }

        checkAndSyncWaitingCardRemove();
    }

    public void backToCheckCard() {
        uiNavigator.getUiFlowControlData().setEmvBackToCheckCard(true);
        doUICmd_navigatorToNext();
    }

    public void abortTrans() {
        useCaseEmvStop.execute(null);
        uiNavigator.getUiFlowControlData().setEmvBackToCheckCard(false);
        uiNavigator.getUiFlowControlData().setNeedUIBack(true);
        uiNavigator.getUiFlowControlData().setNotNeedDialogConfirmUIBack(true);
        doUICmd_navigatorToNext();
    }

    private void doErrorProcess(Throwable exception) {
        LogUtil.d(TAG, "doErrorProcess");
        if (exception instanceof CommonException) {
            CommonException commonException = (CommonException) exception;
            int exceptionType = commonException.getExceptionType();
            LogUtil.d(TAG, "exceptionType=" + ExceptionType.toDebugString(exceptionType));
            int subErrType = commonException.getSubErrType();
            LogUtil.d(TAG, "subErrType=" + subErrType);

            if (exceptionType == ExceptionType.EMV_FAILED) {
                if (terminalCfg.isAllowFallback()
                        && currentTranData.getCardInfo().getCardEntryMode() == CardEntryMode.RF
                        && subErrType == EMVResult.CTLS_CONT.getId()) {
                    // need CTLS -> Insert
                    useCaseEmvStop.execute(null);
                    /**
                     * Read rf failed will retry 1 time, except emv request CTLS -> Insert.
                     */
                    currentTranData.setRfCardFallbackRemainTimes(0);
                    doICFallbackProcess(currentTranData);
                    return;
                } else if (terminalCfg.isAllowFallback()
                        && (subErrType == EMVResult.CTLS_NO_APP.getId()
                        || subErrType == EMVResult.EMV_NO_APP.getId()
                        || subErrType == EMVResult.EMV_INITIAL_FAILED.getId() // NO AID RID
                        || (subErrType == EMVResult.EMV_FALLBACK.getId()))) {
                    if (subErrType == EMVResult.CTLS_NO_APP.getId()
                            || subErrType == EMVResult.EMV_INITIAL_FAILED.getId()
                            || subErrType == EMVResult.EMV_NO_APP.getId()) {
                        doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_application_not_available));
                    }
                    doICFallbackProcess(currentTranData);
                    return;
                } else if (currentTranData.getCardInfo().getCardEntryMode() == CardEntryMode.RF
                        && !currentTranData.getEmvInfo().isEmvConfirmCard()) {
                    doICFallbackProcess(currentTranData);
                    return;
                }

                currentTranData.setErrorMsg(EmvErrorCodeMapper.toErrorString(subErrType));
                uiNavigator.getUiFlowControlData().setTransFailed(true);
                doUICmd_navigatorToNext();
            } else if (exceptionType == ExceptionType.GET_CARD_BIN_FAILED) {
                doUICmd_showTransNotAllowDialog();
            } else {
                currentTranData.setErrorMsg(ExceptionErrMsgMapper.toErrorMsg(exceptionType));
                uiNavigator.getUiFlowControlData().setTransFailed(true);
                doUICmd_navigatorToNext();
            }
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

        emvInformation = currentTranData.getEmvInfo();
        emvInformation.setEmvResult(EmvInformation.EMV_DECLINED);
    }

    private void doUICmd_showSelectAppItems(List<String> appItems) {
        execute(ui -> ui.showSelectAppItems(appItems));
    }



    private void doUICmd_showTransNotAllowDialog() {
        execute(ui -> ui.showTransNotAllowDialog());
    }

    private void doUICmd_showFallbackRemoveCardDialog(String msg) {
        execute(ui -> ui.showFallbackRemoveCardDialog(msg));
    }

    private void doUICmd_showCardNotSupportDialog() {
        execute(ui -> ui.showCardNotSupportDialog());
    }

    private void doUICmd_dismissFallbackRemoveCardDialog() {
        execute(ui -> ui.dismissFallbackRemoveCardDialog());
    }

    private void doUICmd_showCardConfirmDialog(String pan, String expireDate, DialogUtil.AskDialogListener listener) {
        execute(ui -> ui.showCardConfirmDialog(pan, expireDate, listener));
    }

    private void doUICmd_showAskBackToCheckCardDialog(String msg) {
        execute(ui -> ui.showAskBackToCheckCardDialog(msg));
    }
}
