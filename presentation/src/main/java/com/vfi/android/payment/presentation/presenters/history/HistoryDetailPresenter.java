package com.vfi.android.payment.presentation.presenters.history;

import android.text.TextUtils;
import android.view.View;


import com.vfi.android.data.database.hdt.DBFunHostData;
import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.businessbeans.PrintConfig;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TransAttribute;
import com.vfi.android.domain.entities.consts.CurrencyCode;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.PrintType;
import com.vfi.android.domain.entities.databeans.PrintInfo;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.DefaultObserver;
import com.vfi.android.domain.interactor.print.UseCaseStartPrintSlip;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.interactor.repository.UseCaseGetPrintConfig;
import com.vfi.android.domain.interactor.repository.UseCaseGetRecordInfoByInvoice;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.CurrencySelectorMapper;
import com.vfi.android.payment.presentation.mappers.ExceptionErrMsgMapper;
import com.vfi.android.payment.presentation.mappers.PrintErrorMapper;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TvShowUtil;
import com.vfi.android.payment.presentation.view.contracts.HistoryDetailUI;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;


public class HistoryDetailPresenter extends BasePresenter<HistoryDetailUI> {
    private final String TAG = "HistoryDetailPresenter";
    private RecordInfo recordInfo;
    private UINavigator uiNavigator;

    private String traceNo;
    private String invoiceNum;

    private UseCaseGetRecordInfoByInvoice useCaseGetRecordInfoByInvoice;
    private UseCaseGetCurTranData useCaseGetCurTranData;
    private UseCaseStartPrintSlip useCaseStartPrintSlip;
    private UseCaseGetPrintConfig useCaseGetPrintConfig;
    private int currentPrintSlipType; // MERCHANT,BANK,CUSTOMER
    private PrintConfig printConfig = null;

    @Inject
    public HistoryDetailPresenter(
            UINavigator uiNavigator,
            UseCaseGetCurTranData useCaseGetCurTranData,
            UseCaseGetRecordInfoByInvoice useCaseGetRecordInfoByInvoice,
            UseCaseGetPrintConfig useCaseGetPrintConfig,
            UseCaseStartPrintSlip useCaseStartPrintSlip) {
        this.uiNavigator = uiNavigator;
        this.useCaseGetRecordInfoByInvoice = useCaseGetRecordInfoByInvoice;
        this.useCaseGetCurTranData = useCaseGetCurTranData;
        this.useCaseStartPrintSlip = useCaseStartPrintSlip;
        this.useCaseGetPrintConfig = useCaseGetPrintConfig;
    }

    @Override
    protected void onFirstUIAttachment() {
        super.onFirstUIAttachment();
        showHistoryDetail();
    }


    public void initialize(String traceNo, String invoiceNum) {
        this.traceNo = traceNo;
        this.invoiceNum = invoiceNum;
    }

    public void showHistoryDetail() {
        if (TextUtils.isEmpty(invoiceNum))
            return;

        useCaseGetRecordInfoByInvoice.execute(new GetRecordInfoObserver(), invoiceNum);
    }

    public void rePrintTransSlip(boolean isContinueFromPrintError) {
        PrintInfo printInfo  = new PrintInfo(PrintType.getPrintType(recordInfo.getTransType()),currentPrintSlipType);
        printInfo.setDuplicateSlip(true);
        printInfo.setRecordInfo(recordInfo);
        printInfo.setContinueFromPrintError(isContinueFromPrintError);
        setPrintLogo(printInfo);
        doUICmd_setPrintingDialogStatus(true);
        Disposable disposable = useCaseStartPrintSlip.asyncExecute(printInfo).subscribe(unused -> {
            LogUtil.d(TAG, "currentPrintSlipType=" + printInfo.getPrintSlipType());
            doUICmd_setPrintingDialogStatus(false);
            if (checkAndGetNextPrintSlipType() < 0) {
                // do nothing
            } else {
                doUICmd_showCountDownAskDialog(getPrintNextSlipHintMessage(), 15, new DialogUtil.AskDialogListener() {
                    @Override
                    public void onClick(boolean isSure) {
                        if (isSure) {
                            rePrintTransSlip(false);
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

        doUICmd_showPrintFaildDialog(printErrorText);
    }

    private void setPrintLogo(PrintInfo printLogo) {
        printLogo.setPrintLogoData(ResUtil.getByteFromDrawable(R.drawable.print_logo, 384f));
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

    private final class GetRecordInfoObserver extends DefaultObserver<RecordInfo> {
        @Override
        public void onNext(RecordInfo recordInformation) {
            recordInfo = recordInformation;

            if (recordInfo != null) {
                printConfig = useCaseGetPrintConfig.execute(recordInfo.getMerchantIndex());
                currentPrintSlipType = PrintInfo.SLIP_TYPE_MERCHANT;
                if (printConfig != null && !printConfig.isPrintMerchantCopy()) {
                    currentPrintSlipType = checkAndGetNextPrintSlipType();
                    if (currentPrintSlipType < 0) {
                        doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_no_print_copy_enabled));
                        return;
                    }
                }
            }

            TransAttribute transAttribute = TransAttribute.findTypeByType(recordInformation.getTransType());
            boolean isReversal = recordInfo.getTransType() == transAttribute.REVERSAL.getTransType();
            doUICmd_setViewVisibility(R.id.btn_reprint_down, true);
            doUICmd_setViewVisibility(R.id.btn_reprint_up, true);
            String currencySymbol = CurrencySelectorMapper.view2ShowInMultilingual(CurrencyCode.PHP);

            if (!TextUtils.isEmpty(recordInfo.getAmount())) {
                doUICmd_setViewText(R.id.tv_amount, currencySymbol + TvShowUtil.formatAmount(recordInfo.getAmount()));
            }

            if (!TextUtils.isEmpty(recordInfo.getTipAmount())) {
                doUICmd_setViewText(R.id.tv_tips, currencySymbol + TvShowUtil.formatAmount(recordInfo.getTipAmount()));
            }

            if (!TextUtils.isEmpty(recordInfo.getAmount()) || !TextUtils.isEmpty(recordInfo.getTipAmount())) {
                doUICmd_setViewText(R.id.tv_total, currencySymbol + TvShowUtil.formatAmount(recordInfo.getAmount(), recordInfo.getTipAmount()));
            }

            if (!TextUtils.isEmpty(recordInfo.getMerchantId())) {
                doUICmd_setViewText(R.id.tv_merchant_id, recordInfo.getMerchantId());
            }

            if (!TextUtils.isEmpty(recordInfo.getTerminalId())) {
                doUICmd_setViewText(R.id.tv_terminal_id, recordInfo.getTerminalId());
            }

            if (!TextUtils.isEmpty(recordInfo.getTraceNum())) {
                doUICmd_setViewText(R.id.tv_trace_no, recordInfo.getTraceNum());
            }

            if(!TextUtils.isEmpty(recordInfo.getInvoiceNum())){
                doUICmd_setViewText(R.id.tv_invoice_no, recordInfo.getInvoiceNum());
            }
            HostInfo hostInfo = DBFunHostData.getHostInfoByHostType(recordInfo.getHostType());
            if(null != hostInfo){
                String hostName =hostInfo.getHostName();
                if(!TextUtils.isEmpty(hostName)){
                    doUICmd_setViewText(R.id.tv_host_name, hostName);
                }
            }

            if (!TextUtils.isEmpty(recordInfo.getBatchNo())) {
                doUICmd_setViewText(R.id.tv_batch_id, recordInfo.getBatchNo());
            }

            if (!TextUtils.isEmpty(recordInfo.getPan())) {
                doUICmd_setViewText(R.id.tv_card_no, TvShowUtil.formatAcount(recordInfo.getPan()));
            }

            if (!TextUtils.isEmpty(recordInfo.getCardHolderName())) {
                doUICmd_setViewText(R.id.tv_cardholder_name, recordInfo.getCardHolderName().trim());
            }

            if (!TextUtils.isEmpty(recordInfo.getTransDate()) && !TextUtils.isEmpty(recordInfo.getTransTime())) {
                doUICmd_setViewText(R.id.tv_date_time, StringUtil.formatDateTime(recordInfo.getTransDate(), "yyyy/dd/MM/", recordInfo.getTransTime(), 4));
            }

            if (!TextUtils.isEmpty(recordInfo.getRefNo())) {
                doUICmd_setViewText(R.id.tv_ref_no, recordInfo.getRefNo());
            }

            if (!TextUtils.isEmpty(recordInfo.getAuthCode())) {
                doUICmd_setViewText(R.id.tv_appr_code, recordInfo.getAuthCode());
            }

            if (!TextUtils.isEmpty(recordInfo.getAppLabel())) {
                doUICmd_setViewVisibility(R.id.ll_app_label, true);
                doUICmd_setViewText(R.id.tv_app_label, recordInfo.getAppLabel());
            }

            if (!TextUtils.isEmpty(recordInfo.getAID())) {
                doUICmd_setViewVisibility(R.id.ll_aid, true);
                doUICmd_setViewText(R.id.tv_aid, recordInfo.getAID());
            }

            if (!TextUtils.isEmpty(recordInfo.getTc())) {
                doUICmd_setViewVisibility(R.id.ll_tc, true);
                doUICmd_setViewText(R.id.tv_tc, recordInfo.getTc());
            }

            //打开Print按钮
            doUICmd_setViewVisibility(R.id.btn_reprint_down, true);
        }

        @Override
        public void onError(Throwable exception) {
            exception.printStackTrace();
        }
    }



    private void doUICmd_showPrintFaildDialog(String errorMsg) {
        execute(ui -> ui.showPrintException(errorMsg));
    }

    private void doUICmd_navigatorToHistoryActivity() {
        execute(HistoryDetailUI::navigatorToHistoryActivity);
    }

    private void doUICmd_setViewVisibility(int resId, boolean isVisible) {
        execute(ui -> ui.setViewVisibility(resId, isVisible ? View.VISIBLE : View.GONE));
    }

    private void doUICmd_setViewText(int resId, String text) {
        execute(ui -> ui.setViewText(resId, text));
    }

    private void doUICmd_setViewVisibility(int resId, int visibility) {
        execute(ui -> ui.setViewVisibility(resId, visibility));
    }

    private void doUICmd_setPrintingDialogStatus(boolean isShow) {
        doUICmd_setLoadingDialogStatus(isShow);
    }
}
