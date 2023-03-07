package com.vfi.android.payment.presentation.presenters;

import android.annotation.SuppressLint;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.HostType;
import com.vfi.android.domain.entities.consts.PrintType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.PrintInfo;
import com.vfi.android.domain.entities.databeans.SettlementRecord;
import com.vfi.android.domain.entities.databeans.SettlementRecordItem;
import com.vfi.android.domain.entities.databeans.SettlementStatus;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.other.UseCaseTimer;
import com.vfi.android.domain.interactor.print.UseCaseClearPrintBuffer;
import com.vfi.android.domain.interactor.print.UseCaseStartPrintSlip;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.interactor.repository.UseCaseGetSettlementHostList;
import com.vfi.android.domain.interactor.repository.UseCaseGetSettlementRecords;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalCfg;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalStatus;
import com.vfi.android.domain.interactor.repository.UseCaseSaveAutoSettlementResult;
import com.vfi.android.domain.interactor.repository.UseCaseSetDoingTransactionFlag;
import com.vfi.android.domain.interactor.transaction.UseCaseDoNormalTrans;
import com.vfi.android.domain.interactor.transaction.UseCaseDoPostTrans;
import com.vfi.android.domain.interactor.transaction.UseCaseDoPreTrans;
import com.vfi.android.domain.interactor.transaction.UseCaseDoSettlementTrans;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.ExceptionErrMsgMapper;
import com.vfi.android.payment.presentation.mappers.HostTypeMapper;
import com.vfi.android.payment.presentation.mappers.PrintErrorMapper;
import com.vfi.android.payment.presentation.models.SettlementResultModel;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.AndroidUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TvShowUtil;
import com.vfi.android.payment.presentation.view.contracts.AutoSettlementUI;
import com.vfi.android.payment.presentation.receivers.AutoSettlementReceiver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class AutoSettlementPresenter extends BasePresenter<AutoSettlementUI> {
    private final String TAG = TAGS.AUTO_SETTLEMENT;
    private final UseCaseDoPreTrans useCaseDoPreTrans;
    private final UseCaseDoPostTrans useCaseDoPostTrans;
    private final UseCaseDoNormalTrans useCaseDoNormalTrans;
    private final UseCaseDoSettlementTrans useCaseDoSettlementTrans;
    private final UseCaseGetCurTranData useCaseGetCurTranData;
    private final TerminalCfg terminalCfg;
    private final UseCaseGetSettlementRecords useCaseGetSettlementRecords;

    private SettlementRecord settlementRecord;
    private final UseCaseStartPrintSlip useCaseStartPrintSlip;
    private final UseCaseClearPrintBuffer useCaseClearPrintBuffer;
    private final UseCaseGetSettlementHostList useCaseGetSettlementHostList;

    private final UseCaseGetTerminalStatus useCaseGetTerminalStatus;
    private final UseCaseSaveAutoSettlementResult useCaseSaveAutoSettlementResult;
    private final UseCaseTimer useCaseTimer;
    private final UseCaseSetDoingTransactionFlag useCaseSetDoingTransactionFlag;

    private CurrentTranData currentTranData;
    StringBuffer stringBuffer = new StringBuffer();

    @Inject
    public AutoSettlementPresenter(UseCaseDoPreTrans useCaseDoPreTrans,
                                   UseCaseDoPostTrans useCaseDoPostTrans,
                                   UseCaseDoNormalTrans useCaseDoNormalTrans,
                                   UseCaseGetCurTranData useCaseGetCurTranData,
                                   UseCaseDoSettlementTrans useCaseDoSettlementTrans,
                                   UseCaseGetTerminalCfg useCaseGetTerminalCfg,
                                   UseCaseGetSettlementRecords useCaseGetSettlementRecords,
                                   UseCaseStartPrintSlip useCaseStartPrintSlip,
                                   UseCaseClearPrintBuffer useCaseClearPrintBuffer,
                                   UseCaseGetSettlementHostList useCaseGetSettlementHostList,
                                   UseCaseGetTerminalStatus useCaseGetTerminalStatus,
                                   UseCaseSaveAutoSettlementResult useCaseSaveAutoSettlementResult,
                                   UseCaseTimer useCaseTimer,
                                   UseCaseSetDoingTransactionFlag useCaseSetDoingTransactionFlag,
                                   UINavigator uiNavigator) {
        this.useCaseDoNormalTrans = useCaseDoNormalTrans;
        this.useCaseGetCurTranData = useCaseGetCurTranData;
        this.useCaseDoPreTrans = useCaseDoPreTrans;
        this.useCaseDoPostTrans = useCaseDoPostTrans;
        this.useCaseDoSettlementTrans = useCaseDoSettlementTrans;
        this.terminalCfg = useCaseGetTerminalCfg.execute(null);
        this.useCaseGetSettlementRecords = useCaseGetSettlementRecords;
        this.useCaseStartPrintSlip = useCaseStartPrintSlip;
        this.useCaseClearPrintBuffer = useCaseClearPrintBuffer;
        this.useCaseGetSettlementHostList = useCaseGetSettlementHostList;
        this.useCaseGetTerminalStatus = useCaseGetTerminalStatus;
        this.useCaseSaveAutoSettlementResult = useCaseSaveAutoSettlementResult;
        this.useCaseTimer = useCaseTimer;
        this.useCaseSetDoingTransactionFlag = useCaseSetDoingTransactionFlag;
    }

    @Override
    protected void onFirstUIAttachment() {
        currentTranData = useCaseGetCurTranData.execute(null);
        LogUtil.d(TAG, "currentTranData address=" + currentTranData.toString());
        useCaseSetDoingTransactionFlag.execute(true);
        loadSettlementData();
        doSettlementNetworkProcess();
    }

    @SuppressLint("CheckResult")
    private void loadSettlementData() {
        useCaseGetSettlementRecords.asyncExecute(null).subscribe(settlementRecords -> {
            currentTranData.setCurrentSettlementRecord(settlementRecords);
            this.settlementRecord = settlementRecords;
            Iterator<SettlementRecordItem> iterator = settlementRecords.getSettlementRecordItems().iterator();
            while (iterator.hasNext()) {
                SettlementRecordItem settlementRecordItem = iterator.next();
                settlementRecords.setNeedSettlementFlag(settlementRecordItem, false);
            }
        }, throwable -> {
            doUICmd_showToastMessage("" + throwable.getMessage());
            LogUtil.d(TAG, "errmsg=" + throwable.getMessage());
            throwable.printStackTrace();
        });
    }

    private void doSettlementNetworkProcess() {
        doPreTransaction().flatMap(unused -> {
            return doSettlementTransaction();
        }).doOnError(throwable -> {
            if (throwable instanceof CommonException) {
                CommonException commonException = (CommonException) throwable;
                if (commonException.getExceptionType() == ExceptionType.REVERSAL_FAILED) {
                    doUICmd_showReversalFailedDialog(ResUtil.getString(R.string.dialog_hint_reversal_failed_retry));
                    return;
                }
            }

            showTransResult();
        }).doOnComplete(() -> {
            showTransResult();
        }).subscribe();
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

    public void doReversalAgain(boolean isNeedDoReversalAgain) {
        if (isNeedDoReversalAgain) {
            doSettlementNetworkProcess();
        } else {
            doUICmd_showToastMessage(ResUtil.getString(R.string.tv_hint_reversal_failed));
            showTransResult();
        }
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

    private Observable<Boolean> doPreTransaction() {
        LogUtil.d(TAG, "doPreTransaction");
        return Observable.create(e -> {
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

    private void showTransResult() {
        doUICmd_showTransResult(settlementRecord.isAllSettlementSuccess());
        useCaseSaveAutoSettlementResult.execute(settlementRecord.isAllSettlementSuccess());
        useCaseSetDoingTransactionFlag.execute(false);
        if (settlementRecord.isAllSettlementSuccess()) {
            AndroidUtil.sendBroadcast(AutoSettlementReceiver.ACTION_SET_AUTO_SETTLEMENT_TIMER);
        } else {
            AndroidUtil.sendBroadcast(AutoSettlementReceiver.ACTION_SET_AUTO_SETTLEMENT_FAILED_TIMER);
        }
        showSettlementResultList();
        loadAmount();
        startPrintSettlementSlip(false);
    }

    private void loadAmount() {
        String amount = "0.00";
        amount = TvShowUtil.formatAmount(String.format(Locale.getDefault(),"%012d", settlementRecord.getTotalAmount()));
        String currency = "PHP";
        doUICmd_showAmount(amount, currency);
    }

    private void showSettlementResultList() {
        useCaseGetSettlementHostList.asyncExecute(null).doOnNext(hostTypeList -> {
            List<SettlementResultModel> settlementResultModelList = new ArrayList<>();

            Iterator<Integer> iterator = hostTypeList.iterator();
            while (iterator.hasNext()) {
                int hostType = iterator.next();
                int settleStatus = settlementRecord.getHostSettleStatus(hostType);
                switch (settleStatus) {
                    case SettlementRecord.SETTLE_STAUTS_SUCCESS:
                        settlementResultModelList.add(new SettlementResultModel(hostType, SettlementResultModel.SETTLEMENT_SUCCESS));
                        break;
                    case SettlementRecord.SETTLE_STAUTS_FAILED:
                        settlementResultModelList.add(new SettlementResultModel(hostType, SettlementResultModel.SETTLEMENT_FAILED));
                        break;
                    case SettlementRecord.SETTLE_STAUTS_BATCH_EMPTY:
                        settlementResultModelList.add(new SettlementResultModel(hostType, SettlementResultModel.SETTLEMENT_EMPTY_BATCH));
                        break;
                    case SettlementRecord.SETTLE_STAUTS_NO_NEED_SETTLE:
                        break;
                }
                LogUtil.d(TAG, "HostType=[" + HostType.toDebugString(hostType) + "] settleStatus=[" + settleStatus + "]");
            }

            if (settlementResultModelList.size() > 0) {
                doUICmd_showSettlementResultList(settlementResultModelList);
            }
        }).doOnError(throwable -> {
            throwable.printStackTrace();
            LogUtil.e(TAG, "Get settlement host failed.");
        }).subscribe();
    }

    public void clearPrintBuffer() {
        useCaseClearPrintBuffer.asyncExecuteWithoutResult(null);
    }

    public void startPrintSettlementSlip(boolean isContinueFromPrintError) {
        doUICmd_setPrintingDialogStatus(true);
        PrintInfo printInfo = new PrintInfo(PrintType.SETTLEMENT, PrintInfo.SLIP_TYPE_MERCHANT);
        setPrintLogo(printInfo);
        printInfo.setContinueFromPrintError(isContinueFromPrintError);
        Disposable disposable = useCaseStartPrintSlip.asyncExecute(printInfo).subscribe(unused -> {
            doUICmd_setPrintingDialogStatus(false);
            startBackToMainMenuTimer();
        }, throwable -> {
            doUICmd_setPrintingDialogStatus(false);
            doPrintErrorProcess(throwable);
        });
    }

    public void startBackToMainMenuTimer() {
        useCaseTimer.asyncExecute(30000L).doOnError(throwable -> {
            doUICmd_backToMainMenu();
        }).doOnComplete(() -> {
            doUICmd_backToMainMenu();
        }).subscribe();
    }

    private void doPrintErrorProcess(Throwable throwable) {
        String printErrorText;

        if (throwable instanceof CommonException) {
            CommonException commonException = (CommonException) throwable;
            int exceptionType = commonException.getExceptionType();
            int subErrorCode = commonException.getSubErrType();
            if (exceptionType == ExceptionType.PRINT_FAILED) {
                printErrorText = PrintErrorMapper.toErrorString(subErrorCode);
            } else {
                printErrorText = ExceptionErrMsgMapper.toErrorMsg(exceptionType);
            }
        } else {
            throwable.printStackTrace();
            LogUtil.d(TAG, "Other exception.");
            printErrorText = ResUtil.getString(R.string.tv_hint_print_failed);
        }

        doUICmd_showPrintFaildDialog(printErrorText, true);
    }

    private void setPrintLogo(PrintInfo printLogo) {
        printLogo.setPrintLogoData(ResUtil.getByteFromDrawable(R.drawable.print_logo, 384f));
    }

    private void doUICmd_setProcessHint(String hintMsg) {
        execute(ui -> ui.setProcessHint(hintMsg));
    }

    private void doUICmd_showReversalFailedDialog(String msg) {
        execute(ui -> ui.showReversalFailedDialog(msg));
    }

    private void doUICmd_showAmount(String amount, String currencySymbol) {
        execute(ui -> ui.showAmount(amount,currencySymbol));
    }

    private void doUICmd_showPrintFaildDialog(String errMsg, boolean isNeedBackToMainMenu) {
        execute(ui -> ui.showPrintFailedDialog(errMsg, isNeedBackToMainMenu));
    }

    private void doUICmd_setPrintingDialogStatus(boolean isShow) {
        doUICmd_setLoadingDialogStatus(isShow);
    }

    private void doUICmd_showSettlementResultList(List<SettlementResultModel> settlementResultModelList) {
        execute(ui -> ui.showSettlementResultList(settlementResultModelList));
    }

    private void doUICmd_backToMainMenu() {
        execute(ui -> ui.backToMainMenu());
    }

    private void doUICmd_showTransResult(boolean isAllSettleSuccess) {
        execute(ui -> ui.showTransResult(isAllSettleSuccess));
    }
}
