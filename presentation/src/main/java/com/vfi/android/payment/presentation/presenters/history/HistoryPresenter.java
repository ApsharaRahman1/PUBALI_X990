package com.vfi.android.payment.presentation.presenters.history;

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.PrintType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.databeans.HistoryConstraint;
import com.vfi.android.domain.entities.databeans.PrintInfo;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.print.UseCaseStartPrintSlip;
import com.vfi.android.domain.interactor.repository.UseCaseGetRecordInfosByConstraint;
import com.vfi.android.domain.interactor.repository.UseCaseGetReversalInfos;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.CurrencySelectorMapper;
import com.vfi.android.payment.presentation.mappers.ExceptionErrMsgMapper;
import com.vfi.android.payment.presentation.mappers.PrintErrorMapper;
import com.vfi.android.payment.presentation.models.HistoryItemModel;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.HistoryUI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HistoryPresenter extends BasePresenter<HistoryUI> {
    private final String TAG = TAGS.HISTORY;

    private final UseCaseGetRecordInfosByConstraint useCaseGetRecordInfosByConstraint;
    private final UseCaseGetReversalInfos useCaseGetReversalInfos;
    private final UseCaseStartPrintSlip useCaseStartPrintSlip;

    public static final int OPTYPE_RESET  = 0;
    public static final int OPTYPE_APPEND = 1;

    @Inject
    public HistoryPresenter(UseCaseGetRecordInfosByConstraint useCaseGetRecordInfosByConstraint,
                            UseCaseStartPrintSlip useCaseStartPrintSlip,
                            UseCaseGetReversalInfos useCaseGetReversalInfos) {
        this.useCaseGetRecordInfosByConstraint = useCaseGetRecordInfosByConstraint;
        this.useCaseGetReversalInfos = useCaseGetReversalInfos;
        this.useCaseStartPrintSlip = useCaseStartPrintSlip;
    }

    @Override
    protected void onFirstUIAttachment() {
        super.onFirstUIAttachment();
        showAllHistoryList();
    }

    public void showHistoryListByPayType(int payType) {
        HistoryConstraint historyConstraint = new HistoryConstraint();
        historyConstraint.initByPayType(payType);
        showRecordListWithReversal(historyConstraint);
    }

    public void showHistoryListByInvoiceNo(String invoiceNo) {
        invoiceNo = String.format("%06d", StringUtil.parseInt(invoiceNo, 0));
        LogUtil.d(TAG, "showHistoryListByInvoiceNo invoiceNo=[" + invoiceNo + "]");
        HistoryConstraint historyConstraint = new HistoryConstraint();
        historyConstraint.initByInvoiceNum(invoiceNo);
        showRecordListWithReversal(historyConstraint);
    }

    public void showHistoryListByDate(String date) {
        HistoryConstraint historyConstraint = new HistoryConstraint();
        historyConstraint.initByDate(date);
        showRecordListWithReversal(historyConstraint);
    }

    public void showAllHistoryList() {
        HistoryConstraint historyConstraint = new HistoryConstraint();
        historyConstraint.init();
        showRecordListWithReversal(historyConstraint);
    }

    private Observable<Boolean> showReversalList() {
        return Observable.create(emitter -> {
            useCaseGetReversalInfos.asyncExecute(null).doOnNext(recordInfoList -> {
                LogUtil.d(TAG, "showReversalList size = " + recordInfoList.size());
                doUICmd_showRecordInfoList(OPTYPE_RESET, convertToHistoryItemModes(recordInfoList));
                emitter.onNext(true);
                emitter.onComplete();
            }).doOnError(throwable -> {
                doUICmd_showRecordInfoList(OPTYPE_RESET, new ArrayList<>());
                emitter.onNext(false);
                emitter.onComplete();
            }).subscribe();
        });
    }

    private Observable<Boolean> showRecordList(HistoryConstraint historyConstraint, int opType) {
        return Observable.create(emitter -> {
            useCaseGetRecordInfosByConstraint.asyncExecute(historyConstraint).doOnNext(recordInfos -> {
                LogUtil.d(TAG, "showRecordList size = " + recordInfos.size());
                doUICmd_showRecordInfoList(opType, convertToHistoryItemModes(recordInfos));
                emitter.onNext(true);
                emitter.onComplete();
            }).doOnError(throwable -> {
                emitter.onNext(false);
                emitter.onComplete();
            }).subscribe();
        });
    }

    private void showRecordListWithReversal(HistoryConstraint historyConstraint) {
        showReversalList().flatMap(unused -> {
            return showRecordList(historyConstraint, OPTYPE_APPEND);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private List<HistoryItemModel> convertToHistoryItemModes(List<RecordInfo> recordInfoList) {
        List<HistoryItemModel> historyItemModels = new ArrayList<>();
        if (recordInfoList == null) {
            return historyItemModels;
        }

        try {
            Iterator<RecordInfo> iterator = recordInfoList.iterator();
            while (iterator.hasNext()) {
                RecordInfo recordInfo = iterator.next();
                String totalAmountText = getTotalAmountText(recordInfo);
                String currencyText = CurrencySelectorMapper.view2ShowInMultilingual(recordInfo.getCurrencyCode());
                String transTypeText = TransType.getTransTypeText(recordInfo.getTransType(), recordInfo.getVoidOrgTransType(), recordInfo.getTipAdjOrgTransType());
                String transDateTimeText = StringUtil.formatDate(recordInfo.getTransDate()) + "  " + StringUtil.formatTime(recordInfo.getTransTime());
                HistoryItemModel model = new HistoryItemModel(totalAmountText, currencyText, transTypeText, transDateTimeText, recordInfo.getInvoiceNum(), recordInfo.getTraceNum());
                model.setNeedRedColor(isShowInRedColor(recordInfo));
                historyItemModels.add(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return historyItemModels;
    }

    private String getTotalAmountText(RecordInfo recordInfo) {
        long totalAmount = StringUtil.parseLong(recordInfo.getAmount(), 0) + StringUtil.parseLong(recordInfo.getTipAmount(), 0);
        String totalAmountText = StringUtil.formatAmount("" + totalAmount);
        switch (recordInfo.getTransType()) {
            case TransType.REVERSAL:
            case TransType.VOID:
                totalAmountText = "-" + totalAmountText;
                break;
        }

        return totalAmountText;
    }

    private boolean isShowInRedColor(RecordInfo recordInfo) {
        switch (recordInfo.getTransType()) {
            case TransType.VOID:
            case TransType.REVERSAL:
                return true;
        }

        return false;
    }

    public void printSummaryReport() {
        startPrintReportSlip(PrintType.SUMMARY_REPORT, false);
    }

    public void printDetailReport() {
        startPrintReportSlip(PrintType.DETAIL_REPORT, false);
    }

    public void rePrint(boolean isSummaryReport) {
        if (isSummaryReport) {
            startPrintReportSlip(PrintType.SUMMARY_REPORT, true);
        } else {
            startPrintReportSlip(PrintType.DETAIL_REPORT, true);
        }
    }

    private void startPrintReportSlip(int printType, boolean isContinueFromPrintError) {
        doUICmd_setLoadingDialogStatus(true);
        PrintInfo printInfo = new PrintInfo(printType, PrintInfo.SLIP_TYPE_MERCHANT);
        setPrintLogo(printInfo);
        printInfo.setContinueFromPrintError(isContinueFromPrintError);
        Disposable disposable = useCaseStartPrintSlip.asyncExecute(printInfo).subscribe(unused -> {
            doUICmd_setLoadingDialogStatus(false);
        }, throwable -> {
            doUICmd_setLoadingDialogStatus(false);
            doPrintErrorProcess(printType, throwable);
        });
    }

    private void setPrintLogo(PrintInfo printLogo) {
        printLogo.setPrintLogoData(ResUtil.getByteFromDrawable(R.drawable.print_logo, 384f));
    }

    private void doPrintErrorProcess(int printType, Throwable throwable) {
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

        boolean isSummaryReport = false;
        if (printType == PrintType.SUMMARY_REPORT) {
            isSummaryReport = true;
        }
        doUICmd_showPrintFailedDialog(printErrorText, isSummaryReport);
    }

    private void doUICmd_showRecordInfoList(int opType, List<HistoryItemModel> historyItemModels) {
        execute(ui -> ui.showRecordInfoList(opType, historyItemModels));
    }

    private void doUICmd_showPrintFailedDialog(String errMsg, boolean isSummaryReport) {
        execute(ui -> ui.showPrintFailedDialog(errMsg, isSummaryReport));
    }
}
