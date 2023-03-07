package com.vfi.android.payment.presentation.presenters.history;

import android.text.TextUtils;

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.databeans.HistoryConstraint;
import com.vfi.android.domain.interactor.repository.UseCaseGetRecordInfosByConstraint;
import com.vfi.android.domain.interactor.repository.UseCaseGetReversalInfos;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.CurrencySelectorMapper;
import com.vfi.android.payment.presentation.models.HistoryItemModel;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.HistoryListUI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;


public class HistoryListPresenter extends BasePresenter<HistoryListUI> {
    public static final String TAG = "HistoryListPresenter";
    public static final int PAGE_SIZE = 10;

    private final UseCaseGetRecordInfosByConstraint useCaseGetRecordInfosByConstraint;
    private final UseCaseGetReversalInfos useCaseGetReversalInfos;
    private final HistoryConstraint historyConstraint;

    @Inject
    HistoryListPresenter(UseCaseGetRecordInfosByConstraint useCaseGetRecordInfosByConstraint,
                         HistoryConstraint historyConstraint,
                         UseCaseGetReversalInfos useCaseGetReversalInfos) {
        this.useCaseGetRecordInfosByConstraint = useCaseGetRecordInfosByConstraint;
        this.historyConstraint = historyConstraint;
        this.useCaseGetReversalInfos = useCaseGetReversalInfos;
    }

    @Override
    protected void onFirstUIAttachment() {
        super.onFirstUIAttachment();
        addReversalInfo();
        addRecordInfoList(0);
    }

    public void getRecordInfoListByType(int paymentType) {
        historyConstraint.init();
        historyConstraint.setConstraintType(HistoryConstraint.CONSTRAINT_PAY_TYPE);
        LogUtil.d(TAG, "getRecordInfoListByType constraint:" + historyConstraint.toString());
        showRecordList(historyConstraint, true);
    }

    public void getRecordInfoListByDate(String date) {
        historyConstraint.init();
        historyConstraint.setConstraintType(HistoryConstraint.CONSTRAINT_DATE);
        historyConstraint.setDate(date);
        LogUtil.d(TAG, "getRecordInfoListByDate constraint:" + historyConstraint.toString());
        showRecordList(historyConstraint, true);
    }

    public void getRecordInfoByInvoiceNo(String invoiceNum) {
        String input = StringUtil.formatInvoiceNo(invoiceNum);
        if (TextUtils.isEmpty(input)) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.wrong_trans_no));
            return;
        }
        historyConstraint.init();
        historyConstraint.setConstraintType(HistoryConstraint.CONSTRAINT_INVOICE_NUM);
        historyConstraint.setInvoiceNum(input);
        LogUtil.d(TAG, "getRecordInfoByInvoiceNo constraint:" + historyConstraint.toString());
        showRecordList(historyConstraint, true);
    }

    public void initRecordInfoList() {
        historyConstraint.init();
        LogUtil.d(TAG, "initRecordInfoList constraint:" + historyConstraint.toString());
        showRecordList(historyConstraint, false);
    }

    public void addReversalInfo() {
        useCaseGetReversalInfos.asyncExecute(null).doOnNext(reversalInfos -> {
            doUICmd_addRecordInfoList(convertToHistoryItemModes(reversalInfos));
        }).doOnError(throwable -> {

        }).subscribe();
    }

    public void addRecordInfoList(int start) {
        LogUtil.d(TAG, "addRecordInfoList constraint:" + historyConstraint.toString());
        showRecordList(historyConstraint, false);
    }

    private List<HistoryItemModel> convertToHistoryItemModes(List<RecordInfo> recordInfoList) {
        List<HistoryItemModel> historyItemModels = new ArrayList<>();
        if (recordInfoList == null) {
            return historyItemModels;
        }

        Iterator<RecordInfo> iterator = recordInfoList.iterator();
        while (iterator.hasNext()) {
            RecordInfo recordInfo = iterator.next();
            String totalAmountText = getTotalAmountText(recordInfo);
            String currencyText = CurrencySelectorMapper.view2ShowInMultilingual(recordInfo.getCurrencyCode());
            String transTypeText = getTransTypeText(recordInfo);
            String transDateTimeText = StringUtil.formatDate(recordInfo.getTransDate()) + "  " + StringUtil.formatTime(recordInfo.getTransTime());
            HistoryItemModel model = new HistoryItemModel(totalAmountText, currencyText, transTypeText, transDateTimeText, recordInfo.getInvoiceNum(), recordInfo.getTraceNum());
            model.setNeedRedColor(isShowInRedColor(recordInfo));
            historyItemModels.add(model);
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

    private String getTransTypeText(RecordInfo recordInfo) {
        int orgTransType;

        switch (recordInfo.getTransType()) {
            case TransType.SALE:
                return "SALE";
            case TransType.REVERSAL:
                return "REVERSAL";
            case TransType.TIP_ADJUST:
                orgTransType = recordInfo.getTipAdjOrgTransType();
                if (orgTransType == TransType.SALE) {
                    return "TIP ADJUST SALE";
                } else if (orgTransType == TransType.OFFLINE) {
                    return "TIP ADJUST OFFLINE";
                } else {
                    return "TIP ADJUST";
                }
            case TransType.VOID:
                orgTransType = recordInfo.getVoidOrgTransType();
                if (orgTransType == TransType.SALE ||
                        (orgTransType == TransType.TIP_ADJUST && recordInfo.getTipAdjOrgTransType() == TransType.SALE)) {
                    return "VOID SALE";
                } else if (orgTransType == TransType.OFFLINE ||
                        (orgTransType == TransType.TIP_ADJUST && recordInfo.getTipAdjOrgTransType() == TransType.OFFLINE)) {
                    return "VOID OFFLINE SALE";
                } else if (orgTransType == TransType.PREAUTH_COMP) {
                    return "VOID PREAUTH COMP";
                } else {
                    return "VOID";
                }
            case TransType.PREAUTH:
                return "PREAUTH";
            case TransType.PREAUTH_COMP:
                return "PREAUTH COMP";
            case TransType.OFFLINE:
                return "OFFLINE";
        }

        return "Unknown TransType";
    }

    private boolean isShowInRedColor(RecordInfo recordInfo) {
        switch (recordInfo.getTransType()) {
            case TransType.VOID:
            case TransType.REVERSAL:
                return true;
        }

        return false;
    }

    private void showRecordList(HistoryConstraint historyConstraint, boolean isResetList) {
        useCaseGetRecordInfosByConstraint.asyncExecute(historyConstraint).doOnNext(recordInfos -> {
            if (isResetList) {
                doUICmd_resetRecordInfoList(convertToHistoryItemModes(recordInfos));
            } else {
                doUICmd_addRecordInfoList(convertToHistoryItemModes(recordInfos));
            }
        }).doOnError(throwable -> {

        }).subscribe();
    }

    public void printSummaryReport() {
    }

    public void printDetailReport() {

    }

    public void rePrint(boolean isSummaryReport) {
    }

    private void doUICmd_addRecordInfoList(List<HistoryItemModel> historyItemModels) {
        execute(ui -> ui.addRecordInfoList(historyItemModels));
    }


    private void doUICmd_resetRecordInfoList(List<HistoryItemModel> historyItemModels) {
        execute(ui -> ui.resetRecordInfoList(historyItemModels));
    }

    private void doUICmd_showPrintFailedDialog(String errMsg, boolean isSummaryReport) {
        execute(ui -> ui.showPrintFailedDialog(errMsg, isSummaryReport));
    }

}
