package com.vfi.android.payment.presentation.presenters;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.PrintConfig;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.HostType;
import com.vfi.android.domain.entities.consts.PrintType;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.databeans.PrintInfo;
import com.vfi.android.domain.entities.databeans.SettlementRecord;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.print.UseCaseClearPrintBuffer;
import com.vfi.android.domain.interactor.print.UseCaseStartPrintSlip;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.interactor.repository.UseCaseGetPrintConfig;
import com.vfi.android.domain.interactor.repository.UseCaseGetSettlementHostList;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.ExceptionErrMsgMapper;
import com.vfi.android.payment.presentation.mappers.PrintErrorMapper;
import com.vfi.android.payment.presentation.models.SettlementResultModel;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TvShowUtil;
import com.vfi.android.payment.presentation.view.contracts.TransSuccessUI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

public class TransSuccessPresenter extends BasePresenter<TransSuccessUI> {
    private final String TAG = this.getClass().getSimpleName();
    private final CurrentTranData currentTranData;
    private SettlementRecord settlementRecord;
    private final UseCaseGetCurTranData useCaseGetCurTranData;
    private final UseCaseStartPrintSlip useCaseStartPrintSlip;
    private final UseCaseClearPrintBuffer useCaseClearPrintBuffer;
    private final UseCaseGetSettlementHostList useCaseGetSettlementHostList;
    private final UseCaseGetPrintConfig useCaseGetPrintConfig;

    private final UINavigator uiNavigator;
    private PrintConfig printConfig = null;

    private int currentPrintSlipType; // MERCHANT,BANK,CUSTOMER

    @Inject
    public TransSuccessPresenter(
            UseCaseGetCurTranData useCaseGetCurTranData,
            UseCaseStartPrintSlip useCaseStartPrintSlip,
            UseCaseClearPrintBuffer useCaseClearPrintBuffer,
            UseCaseGetSettlementHostList useCaseGetSettlementHostList,
            UseCaseGetPrintConfig useCaseGetPrintConfig,
            UINavigator uiNavigator) {
        this.currentTranData = useCaseGetCurTranData.execute(null);
        this.settlementRecord = currentTranData.getCurrentSettlementRecord();
        this.useCaseGetCurTranData = useCaseGetCurTranData;
        this.useCaseStartPrintSlip = useCaseStartPrintSlip;
        this.useCaseClearPrintBuffer = useCaseClearPrintBuffer;
        this.uiNavigator = uiNavigator;
        this.useCaseGetSettlementHostList = useCaseGetSettlementHostList;
        this.useCaseGetPrintConfig = useCaseGetPrintConfig;
        if (currentTranData.getMerchantInfo() != null) {
            printConfig = useCaseGetPrintConfig.execute(currentTranData.getMerchantInfo().getMerchantIndex());
        }
    }

    @Override
    protected void onFirstUIAttachment() {
        setButtonSuccessText();
        if (currentTranData.getTransType() == TransType.SETTLEMENT) {
            showSettlementResultList();
        }

        resetUnlockStatus();

        loadAmount();

        if (currentTranData.getTransType() != TransType.SETTLEMENT) {
            currentPrintSlipType = PrintInfo.SLIP_TYPE_MERCHANT;
            if (printConfig != null && !printConfig.isPrintMerchantCopy()) {
                currentPrintSlipType = checkAndGetNextPrintSlipType();
                if (currentPrintSlipType < 0) {
                    doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_no_print_copy_enabled));
                    return;
                }
            }
        }

        startPrintSlip(false);
    }

    public void startPrintSlip(boolean isContinueFromPrintError) {
        if (currentTranData.getTransType() == TransType.SETTLEMENT) {
            startPrintSettlementSlip(isContinueFromPrintError);
        } else {
            startPrintNormalTransSlip(isContinueFromPrintError);
        }
    }

    public void clearPrintBuffer() {
        useCaseClearPrintBuffer.asyncExecuteWithoutResult(null);
    }

    private void resetUnlockStatus() {
    }

    private void showSettlementResultList() {
        doUICmd_showSettlementResultIcon(settlementRecord.isAllSettlementSuccess());

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
            LogUtil.e(TAG, "Get settlement host failed.");
        }).subscribe();
    }

    private void setButtonSuccessText() {
        doUICmd_showBottonText(ResUtil.getString(R.string.btn_hint_back_to_main_menu));
    }

    private void loadAmount() {
        String amount = "0.00";
        int transType = currentTranData.getTransType();
        LogUtil.d(TAG, "loadAmount transType=" + currentTranData.getTitle());
        if (transType == TransType.SETTLEMENT) {
            amount = TvShowUtil.formatAmount(String.format(Locale.getDefault(),"%012d", settlementRecord.getTotalAmount()));
        } else {
            long amountLong = StringUtil.parseLong(currentTranData.getTransAmount(), 0);
            long tipAmountLong = StringUtil.parseLong(currentTranData.getTransTipAmount(), 0);
            amount = String.format(Locale.getDefault(),"%012d", amountLong + tipAmountLong);
            if (transType == TransType.VOID) {
                amount = "-" + amount;
            }
            amount = TvShowUtil.formatAmount(amount);
        }

        String currency = "PHP";
        doUICmd_showAmount(amount, currency);
    }

    private int checkAndGetNextPrintSlipType() {
        switch (currentPrintSlipType) {
            case PrintInfo.SLIP_TYPE_MERCHANT:
                currentPrintSlipType = PrintInfo.SLIP_TYPE_BANK;
                if (printConfig != null && !printConfig.isPrintBankCopy()) {
                    return checkAndGetNextPrintSlipType();
                }
                break;
            case PrintInfo.SLIP_TYPE_BANK:
                currentPrintSlipType = PrintInfo.SLIP_TYPE_CUSTOMER;
                if (printConfig != null && !printConfig.isPrintCustomerCopy()) {
                    return checkAndGetNextPrintSlipType();
                }
                break;
            case PrintInfo.SLIP_TYPE_CUSTOMER:
                currentPrintSlipType = -1;
                break;
        }

        return currentPrintSlipType;
    }

    private String getPrintNextSlipHintMessage() {
        switch (currentPrintSlipType) {
            case PrintInfo.SLIP_TYPE_MERCHANT:
                return "";
            case PrintInfo.SLIP_TYPE_BANK:
                return ResUtil.getString(R.string.tv_hint_print_bank_copy);
            case PrintInfo.SLIP_TYPE_CUSTOMER:
                return ResUtil.getString(R.string.tv_hint_print_consumer_copy);
        }

        return "";
    }



    public void startPrintNormalTransSlip(boolean isContinueFromPrintError) {
        PrintInfo printInfo = new PrintInfo(PrintType.getPrintType(currentTranData.getTransType()), currentPrintSlipType);
        setPrintLogo(printInfo);
        printInfo.setContinueFromPrintError(isContinueFromPrintError);
        doUICmd_setPrintingDialogStatus(true);
        Disposable disposable = useCaseStartPrintSlip.asyncExecute(printInfo).subscribe(unused -> {
            LogUtil.d(TAG, "currentPrintSlipType=" + printInfo.getPrintSlipType());
            doUICmd_setPrintingDialogStatus(false);
            if (checkAndGetNextPrintSlipType() < 0) {
                doUICmd_navigatorToNext();
            } else {
                doUICmd_showCountDownAskDialog(getPrintNextSlipHintMessage(), 15, new DialogUtil.AskDialogListener() {
                    @Override
                    public void onClick(boolean isSure) {
                        if (isSure) {
                            startPrintNormalTransSlip(false);
                        } else {
                            doUICmd_navigatorToNext();
                        }
                    }
                });
            }
        }, throwable -> {
            doUICmd_setPrintingDialogStatus(false);
            doPrintErrorProcess(throwable);
        });
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

    public void startPrintSettlementSlip(boolean isContinueFromPrintError) {
        doUICmd_setPrintingDialogStatus(true);
        PrintInfo printInfo = new PrintInfo(PrintType.SETTLEMENT, PrintInfo.SLIP_TYPE_MERCHANT);
        setPrintLogo(printInfo);
        printInfo.setContinueFromPrintError(isContinueFromPrintError);
        Disposable disposable = useCaseStartPrintSlip.asyncExecute(printInfo).subscribe(unused -> {
            doUICmd_setPrintingDialogStatus(false);
        }, throwable -> {
            doUICmd_setPrintingDialogStatus(false);
            doPrintErrorProcess(throwable);
        });
    }

    public void downloadTLEKey() {
    }

    private void setPrintLogo(PrintInfo printLogo) {
        printLogo.setPrintLogoData(ResUtil.getByteFromDrawable(R.drawable.print_logo, 384f));
    }


    public void doPostTransProcessing() {
    }

    public void doClickBtnSuccessProcess() {
        LogUtil.i(TAG, "doClickBtnSuccessProcess");
        doUICmd_navigatorToNext();
    }

    private void doUICmd_showAmount(String amount, String currencySymbol) {
        execute(ui -> ui.showAmount(amount,currencySymbol));
    }

    private void doUICmd_showPrintFaildDialog(String errMsg, boolean isNeedBackToMainMenu) {
        execute(ui -> ui.showPrintFailedDialog(errMsg, isNeedBackToMainMenu));
    }

    private void doUICmd_showBottonText(String btnText) {
        execute(ui -> ui.showButtonText(btnText));
    }

    private void doUICmd_setPrintingDialogStatus(boolean isShow) {
        doUICmd_setLoadingDialogStatus(isShow);
    }

    private void doUICmd_showSettlementResultList(List<SettlementResultModel> settlementResultModelList) {
        execute(ui -> ui.showSettlementResultList(settlementResultModelList));
    }

    private void doUICmd_showIPPPaymentView(String interestRate, String paymentTerm) {
        execute( ui->ui.showIPPView(interestRate,paymentTerm));
    }

    private void doUICmd_showPointInfo(boolean isShowAmountLayout, boolean isShowPointTitle, String amount, String point) {
        execute(ui -> ui.showRedeemInfo(isShowAmountLayout,isShowPointTitle,amount,point));
    }

    private void doUICmd_HideAmount() {
        execute(TransSuccessUI::hideAmount);
    }

    private void doUICmd_showSettlementResultIcon(boolean isAllSettleSuccess) {
        execute(ui -> ui.showSettlementResultIcon(isAllSettleSuccess));
    }
}
