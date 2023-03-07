package com.vfi.android.payment.presentation.presenters;

import android.content.Context;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.PinInformation;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.consts.CVMResult;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransErrorCode;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.databeans.SettlementStatus;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalCfg;
import com.vfi.android.domain.interactor.transaction.UseCaseDoNormalTrans;
import com.vfi.android.domain.interactor.transaction.UseCaseDoPostTrans;
import com.vfi.android.domain.interactor.transaction.UseCaseDoPreTrans;
import com.vfi.android.domain.interactor.transaction.UseCaseDoSettlementTrans;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.CommErrorMapper;
import com.vfi.android.payment.presentation.mappers.ExceptionErrMsgMapper;
import com.vfi.android.payment.presentation.mappers.HostRespErrorMapper;
import com.vfi.android.payment.presentation.mappers.HostTypeMapper;
import com.vfi.android.payment.presentation.mappers.PackageErrorMapper;
import com.vfi.android.payment.presentation.mappers.TransErrorCodeMapper;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.receivers.AutoSettlementReceiver;
import com.vfi.android.payment.presentation.utils.AndroidUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.NetworkProcessUI;

import javax.inject.Inject;

import io.reactivex.Observable;

public class NetworkProcessPresenter extends BasePresenter<NetworkProcessUI> {
    private final String TAG = TAGS.Transaction;
    private final UseCaseDoPreTrans useCaseDoPreTrans;
    private final UseCaseDoPostTrans useCaseDoPostTrans;
    private final UseCaseDoNormalTrans useCaseDoNormalTrans;
    private final UseCaseDoSettlementTrans useCaseDoSettlementTrans;
    private final UseCaseGetCurTranData useCaseGetCurTranData;
    private final CurrentTranData currentTranData;
    private final TerminalCfg terminalCfg;
    private final UINavigator uiNavigator;

    StringBuffer stringBuffer = new StringBuffer();

    @Inject
    public NetworkProcessPresenter(UseCaseDoPreTrans useCaseDoPreTrans,
                                   UseCaseDoPostTrans useCaseDoPostTrans,
                                   UseCaseDoNormalTrans useCaseDoNormalTrans,
                                   UseCaseGetCurTranData useCaseGetCurTranData,
                                   UseCaseDoSettlementTrans useCaseDoSettlementTrans,
                                   UseCaseGetTerminalCfg useCaseGetTerminalCfg,
                                   UINavigator uiNavigator) {
        this.useCaseDoNormalTrans = useCaseDoNormalTrans;
        this.currentTranData = useCaseGetCurTranData.execute(null);
        this.useCaseGetCurTranData = useCaseGetCurTranData;
        this.useCaseDoPreTrans = useCaseDoPreTrans;
        this.useCaseDoPostTrans = useCaseDoPostTrans;
        this.uiNavigator = uiNavigator;
        this.useCaseDoSettlementTrans = useCaseDoSettlementTrans;
        this.terminalCfg = useCaseGetTerminalCfg.execute(null);
    }

    @Override
    protected void onFirstUIAttachment() {
        if (!isOnlineTransaction()) {
            doUICmd_setProcessHint(ResUtil.getString(R.string.tv_hint_processing));
        }

        startNetworkProcess();
    }

    private boolean isOnlineTransaction() {
        int transType = currentTranData.getTransType();
        if (transType == TransType.OFFLINE || transType == TransType.TIP_ADJUST) {
            return false;
        }

        RecordInfo recordInfo = currentTranData.getRecordInfo();
        if (transType == TransType.VOID
                && (recordInfo.getTipAdjustTimes() > 0 || recordInfo.getVoidOrgTransType() == TransType.OFFLINE)
                && !currentTranData.getRecordInfo().isOfflineTransUploaded()) {
            return false;
        }

        return true;
    }

    private void startNetworkProcess() {
        if (currentTranData.getTransType() == TransType.SETTLEMENT) {
            doSettlementNetworkProcess();
        } else {
            doTransNetworkProcess();
        }
    }

    private void doSettlementNetworkProcess() {
        doPreTransaction().flatMap(unused -> {
            return doSettlementTransaction();
        }).doOnError(throwable -> {
            doTransErrorProcess(throwable);
        }).doOnComplete(() -> {
            if (isAllSettlementFailed()) {
                currentTranData.setErrorMsg(ResUtil.getString(R.string.tv_hint_all_host_settle_failed));
                uiNavigator.getUiFlowControlData().setTransFailed(true);
            } else {
                uiNavigator.getUiFlowControlData().setTransFailed(false);
            }
            if (isAllSettlementSuccess()) {
                AndroidUtil.sendBroadcast(AutoSettlementReceiver.ACTION_CLEAR_AUTO_SETTLEMENT_FAILED_TIMER);
            }
            doUICmd_navigatorToNext();
        }).subscribe();
    }

    private boolean isAllSettlementFailed() {
        return currentTranData.getCurrentSettlementRecord().isAllSettlementFailed();
    }

    private boolean isAllSettlementSuccess() {
        return currentTranData.getCurrentSettlementRecord().isAllSettlementSuccess();
    }

    private Observable<Boolean> doSettlementTransaction() {
        return Observable.create(emitter -> {
            useCaseDoSettlementTrans.asyncExecute(null).doOnNext(settlementStatus -> {
                showSettlementInfo(settlementStatus);
            }).doOnError(throwable -> {
                throwable.printStackTrace();
                emitter.onError(throwable);
            }).doOnComplete(() -> {
                emitter.onComplete();
            }).subscribe();
        });
    }

    private void showSettlementInfo(SettlementStatus settlementStatus) {
        String hintInfo = stringBuffer.toString();
        String hostName = HostTypeMapper.toHintString(settlementStatus.getCurrentSettlementHostType());
        String statusHint = "";
        switch (settlementStatus.getCurrentHostSettleStatus()) {
            case SettlementStatus.START:
                LogUtil.d(TAG, "Host[" + hostName + "] Processing");
                statusHint = hostName + " " + ResUtil.getString(R.string.tv_hint_processing);
                break;
            case SettlementStatus.BATCH_UPLOAD:
                LogUtil.d(TAG, "Host[" + hostName + "] Batch Uploading");
                statusHint = hostName + " " + ResUtil.getString(R.string.tv_hint_batch_uploading);
                break;
            case SettlementStatus.FAILED:
                LogUtil.d(TAG, "Host[" + hostName + "] Failed");
                statusHint = hostName + " " + ResUtil.getString(R.string.tv_hint_failed);
                stringBuffer.append(statusHint + "\n");
                break;
            case SettlementStatus.SUCCESS:
                LogUtil.d(TAG, "Host[" + hostName + "] Success");
                statusHint = hostName + " " + ResUtil.getString(R.string.tv_hint_success);
                stringBuffer.append(statusHint + "\n");
                break;
        }

        hintInfo += statusHint;
        LogUtil.d(TAG, "hintInfo=[" + hintInfo + "]");

        doUICmd_setProcessHint(hintInfo);
    }

    private void doTransNetworkProcess() {
        doPreTransaction().flatMap(unused -> {
            return doTransaction();
        }).doOnError(throwable -> {
            LogUtil.d(TAG, "doTransNetworkProcess onError=" + throwable.getMessage());
            doTransErrorProcess(throwable);
        }).doOnComplete(()->{
            LogUtil.d(TAG, "doTransNetworkProcess oncomplete");
            uiNavigator.getUiFlowControlData().setTransFailed(false);
            uiNavigator.getUiFlowControlData().setNeedESign(isNeedESign());
            doUICmd_navigatorToNext();
        }).subscribe();
    }

    private void doTransErrorProcess(Throwable throwable) {
        LogUtil.d(TAG, "doTransErrorProcess");
        if (throwable instanceof CommonException) {
            CommonException commonException = (CommonException) throwable;
            int exceptionType = commonException.getExceptionType();
            int subErrorCode = commonException.getSubErrType();
            LogUtil.e(TAG, "exceptionType=" + exceptionType);
            LogUtil.e(TAG, "subErrorCode=" + subErrorCode);
            if (exceptionType == ExceptionType.COMM_EXCEPTION) {
                currentTranData.setErrorMsg(CommErrorMapper.toErrorString(subErrorCode));
                uiNavigator.getUiFlowControlData().setTransFailed(true);
                doUICmd_navigatorToNext();
            } else if (exceptionType == ExceptionType.TRANS_FAILED) {
                if (subErrorCode == TransErrorCode.TRANS_REJECT && isHostDecline()) {
                    currentTranData.setErrorMsg(HostRespErrorMapper.toErrorString(StringUtil.parseInt(currentTranData.getRecordInfo().getRspCode(), -1)));
                } else {
                    currentTranData.setErrorMsg(TransErrorCodeMapper.toErrorString(subErrorCode));
                }
                uiNavigator.getUiFlowControlData().setTransFailed(true);
                doUICmd_navigatorToNext();
            } else if (exceptionType == ExceptionType.PACKAGE_EXCEPTION) {
                currentTranData.setErrorMsg(PackageErrorMapper.toErrorString(subErrorCode));
                uiNavigator.getUiFlowControlData().setTransFailed(true);
                doUICmd_navigatorToNext();
            } else if (exceptionType == ExceptionType.REVERSAL_FAILED) {
                doUICmd_showReversalFailedDialog(ResUtil.getString(R.string.dialog_hint_reversal_failed_retry));
                return;
            } else {
                currentTranData.setErrorMsg(ExceptionErrMsgMapper.toErrorMsg(exceptionType));
                uiNavigator.getUiFlowControlData().setTransFailed(true);
                doUICmd_navigatorToNext();
            }
        } else {
            throwable.printStackTrace();
            doUICmd_showToastMessage(throwable.getMessage());
            currentTranData.setErrorMsg(ResUtil.getString(R.string.tv_hint_trans_reject));
            uiNavigator.getUiFlowControlData().setTransFailed(true);
            doUICmd_navigatorToNext();
        }
    }

    private boolean isHostDecline() {
        String hostRespCode = currentTranData.getRecordInfo().getRspCode();
        LogUtil.d(TAG, "hostRespCode=" + hostRespCode);
        if (hostRespCode != null && !hostRespCode.equals("00")) {
            return true;
        }

        return false;
    }

    private Observable<Boolean> doTransaction() {
        LogUtil.d(TAG, "Do doTransaction");
        return Observable.create(e -> {
            useCaseDoNormalTrans.asyncExecute(null).doOnNext(commData -> {
//            LogUtil.d(TAG, "Comm status=" + commData.getCommStatus());
            }).doOnComplete(() -> {
                e.onNext(true);
                e.onComplete();
            }).doOnError(e::onError).subscribe();
        });
    }

    private Observable<Boolean> doPreTransaction() {
        LogUtil.d(TAG, "doPreTransaction");
        return Observable.create(e -> {
            if (!isOnlineTransaction()) {
                LogUtil.d(TAG, "Offline transaction, skip do preTrans");
                e.onNext(true);
                e.onComplete();
                return;
            }

            useCaseDoPreTrans.asyncExecute(null).doOnNext(commStatus -> {
                LogUtil.d(TAG, "PreTrans Comm status=" + commStatus);
            }).doOnComplete(() -> {
                LogUtil.d(TAG, "PreTrans doOnComplete");
                e.onNext(true);
                e.onComplete();
            }).doOnError(throwable -> {
                LogUtil.d(TAG, "PreTrans doOnError");
                e.onError(throwable);
            }).subscribe();
        });
    }

    private boolean isNeedESign() {
        boolean isEnableESign = terminalCfg.isEnableESign();
        LogUtil.d(TAG, "isEnableESign=" + isEnableESign);

        boolean isEmvRequestSignature = currentTranData.getEmvInfo().isRequestSignature();
        LogUtil.d(TAG, "isEmvRequestSignature=" + isEmvRequestSignature);

        boolean isAmountBelowSignLimit = false;
        long totalAmount = StringUtil.parseLong(currentTranData.getTransAmount(), 0) + StringUtil.parseLong(currentTranData.getTransTipAmount(), 0);
        CardBinInfo cardBinInfo = currentTranData.getCardBinInfo();
        if (cardBinInfo != null && totalAmount <= cardBinInfo.getSignLimitAmount()) {
            isAmountBelowSignLimit = true;
        }
        LogUtil.d(TAG, "totalAmount=" + totalAmount + " signLimitAmount=" + currentTranData.getCardBinInfo().getSignLimitAmount());
        LogUtil.d(TAG, "isAmountBelowSignLimit=" + isAmountBelowSignLimit);

        boolean isNeedSign = false;
        PinInformation pinInfo = currentTranData.getPinInfo();
        if (!pinInfo.isInputPinRequested() || pinInfo.isPinBypassed()) {
            int cardEntryMode = currentTranData.getCardInfo().getCardEntryMode();
            if (cardEntryMode == CardEntryMode.IC || cardEntryMode == CardEntryMode.RF) {
                if (isEmvRequestSignature && !isAmountBelowSignLimit) {
                    isNeedSign = true;
                }
            } else if (!isAmountBelowSignLimit) {
                isNeedSign = true;
            }
        }

        // offline sale no need sign
        if (currentTranData.getTransType() == TransType.OFFLINE) {
            isNeedSign = false;
        }

        LogUtil.d(TAG, "isNeedSign=" + isNeedSign);
        if (isNeedSign) {
            currentTranData.getRecordInfo().setCvmResult(CVMResult.SIGNATURE);
        }

        boolean isNeedESign = false;
        if (isNeedSign && isEnableESign) {
            isNeedESign = true;
        }

        LogUtil.d(TAG, "isNeedESign=" + isNeedESign);
        return isNeedESign;
    }

    public void doReversalAgain(boolean isNeedDoReversalAgain) {
        if (isNeedDoReversalAgain) {
            startNetworkProcess();
        } else {
            currentTranData.setErrorMsg(ResUtil.getString(R.string.tv_hint_reversal_failed));
            uiNavigator.getUiFlowControlData().setTransFailed(true);
            doUICmd_navigatorToNext();
        }
    }

    private void doUICmd_setProcessHint(String hintMsg) {
        execute(ui -> ui.setProcessHint(hintMsg));
    }

    private void doUICmd_showReversalFailedDialog(String msg) {
        execute(ui -> ui.showReversalFailedDialog(msg));
    }

}
