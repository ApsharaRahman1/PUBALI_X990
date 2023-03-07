package com.vfi.android.payment.presentation.presenters.option;


import android.text.TextUtils;

import com.vfi.android.domain.entities.businessbeans.PrintConfig;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.PrintType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.PrintInfo;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.print.UseCaseClearPrintBuffer;
import com.vfi.android.domain.interactor.print.UseCaseStartPrintSlip;
import com.vfi.android.domain.interactor.repository.UseCaseGetPrintConfig;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalStatus;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.ExceptionErrMsgMapper;
import com.vfi.android.payment.presentation.mappers.PrintErrorMapper;
import com.vfi.android.payment.presentation.presenters.base.BaseSettingItemPresenter;
import com.vfi.android.payment.presentation.utils.DialogUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.ReprintUI;
import com.vfi.android.payment.presentation.models.SettingItemViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

public class ReprintPresenter extends BaseSettingItemPresenter<ReprintUI> {
    private final String TAG = TAGS.Setting;

    private final UseCaseClearPrintBuffer useCaseClearPrintBuffer;
    private final UseCaseStartPrintSlip useCaseStartPrintSlip;
    private final UseCaseGetTerminalStatus useCaseGetTerminalStatus;
    private final UseCaseGetPrintConfig useCaseGetPrintConfig;

    private int currentPrintSlipType; // MERCHANT,BANK,CUSTOMER
    private int currentPrintType;
    private Map<Integer,List<SettingItemViewModel>> listMap;

    private int tViewtype = SettingItemViewModel.ViewType.LABEL_AND_TEXT;

    private static final int ID_ROOT = 0;
    private static final int ID_1_REPRINT_LAST_TRANS    = 01;
    private static final int ID_2_REPRINT_LAST_SETTLE   = 02;
    private static final int ID_3_REPRINT_ANY_TRANS     = 03;

    private PrintConfig printConfig = null;

    @Inject
    public ReprintPresenter( UseCaseClearPrintBuffer useCaseClearPrintBuffer,
                             UseCaseGetTerminalStatus useCaseGetTerminalStatus,
                             UseCaseGetPrintConfig useCaseGetPrintConfig,
                             UseCaseStartPrintSlip useCaseStartPrintSlip ){
        this.useCaseClearPrintBuffer = useCaseClearPrintBuffer;
        this.useCaseStartPrintSlip = useCaseStartPrintSlip;
        this.useCaseGetTerminalStatus = useCaseGetTerminalStatus;
        this.useCaseGetPrintConfig = useCaseGetPrintConfig;

        initData();
    }

    @Override
    protected void onFirstUIAttachment() {
        super.onFirstUIAttachment();
        doUICmd_showTitle(ResUtil.getString(R.string.option_reprint));
    }

    private void initData() {
        listMap = new HashMap<>();
        List<SettingItemViewModel> list = new ArrayList<>();
        list.add(new SettingItemViewModel(ID_1_REPRINT_LAST_TRANS, tViewtype, ResUtil.getString(R.string.setting_reprint_last_transaction), ""));
        list.add(new SettingItemViewModel(ID_2_REPRINT_LAST_SETTLE, tViewtype, ResUtil.getString(R.string.setting_reprint_last_settlement), ""));
        list.add(new SettingItemViewModel(ID_3_REPRINT_ANY_TRANS, tViewtype, ResUtil.getString(R.string.setting_reprint_any_transaction), ""));
        listMap.put(ID_ROOT,list);
    }

    @Override
    public List<SettingItemViewModel> initSettingItems() {
        return listMap.get(ID_ROOT);
    }

    @Override
    public void onItemSelected(SettingItemViewModel settingItemViewModel) {
        switch (settingItemViewModel.getId()) {
            case ID_ROOT:
                doUICmd_showSettingItem(listMap.get(ID_ROOT));
                break;
            case ID_3_REPRINT_ANY_TRANS:
                doUICmd_showHistory();
                break;
            case ID_2_REPRINT_LAST_SETTLE:
                currentPrintType = PrintType.LAST_SETTLEMENT;
                checkAndStartPrint();
                break;
            case ID_1_REPRINT_LAST_TRANS:
                currentPrintType = PrintType.LAST_TRANS;
                checkAndStartPrint();
                break;
        }
    }

    @Override
    public void onItemValueChanged(SettingItemViewModel settingItemViewModel) {

    }

    @Override
    public void processUnSavedItems(List<SettingItemViewModel> unSavedItemList) {

    }

    private void checkAndStartPrint() {
        TerminalStatus terminalStatus = useCaseGetTerminalStatus.execute(null);
        if (TextUtils.isEmpty(terminalStatus.getLastTransPrintData())
                && currentPrintType == PrintType.LAST_TRANS) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_no_exist_last_print_data));
            return;
        }

        if (TextUtils.isEmpty(terminalStatus.getLastSettlementPrintData())
                && currentPrintType == PrintType.LAST_SETTLEMENT) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_no_exist_last_print_data));
            return;
        }

        if (currentPrintType == PrintType.LAST_TRANS) {
            RecordInfo recordInfo = terminalStatus.getLastTransPrintDataToRecordInfo();
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
        }

        startPrintSlip(false);
    }

    public void startPrintSlip(boolean isContinueFromPrintError) {
        switch (currentPrintType) {
            case PrintType.LAST_SETTLEMENT:
                startPrintSettlementSlip(isContinueFromPrintError);
                break;
            case PrintType.LAST_TRANS:
                startPrintNormalTransSlip(isContinueFromPrintError);
                break;
        }
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

    public void startPrintNormalTransSlip(boolean isContinueFromPrintError) {
        PrintInfo printInfo = new PrintInfo(PrintType.LAST_TRANS, currentPrintSlipType);
        setPrintLogo(printInfo);
        printInfo.setContinueFromPrintError(isContinueFromPrintError);
        doUICmd_setLoadingDialogStatus(true);
        Disposable disposable = useCaseStartPrintSlip.asyncExecute(printInfo).subscribe(unused -> {
            LogUtil.d(TAG, "currentPrintSlipType=" + printInfo.getPrintSlipType());
            doUICmd_setLoadingDialogStatus(false);
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
            doUICmd_setLoadingDialogStatus(false);
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

    public void startPrintSettlementSlip(boolean isContinueFromPrintError) {
        doUICmd_setLoadingDialogStatus(true);
        PrintInfo printInfo = new PrintInfo(PrintType.LAST_SETTLEMENT, PrintInfo.SLIP_TYPE_MERCHANT);
        setPrintLogo(printInfo);
        printInfo.setContinueFromPrintError(isContinueFromPrintError);
        Disposable disposable = useCaseStartPrintSlip.asyncExecute(printInfo).subscribe(unused -> {
            doUICmd_setLoadingDialogStatus(false);
        }, throwable -> {
            doUICmd_setLoadingDialogStatus(false);
            doPrintErrorProcess(throwable);
        });
    }

    private void setPrintLogo(PrintInfo printLogo) {
        printLogo.setPrintLogoData(ResUtil.getByteFromDrawable(R.drawable.print_logo, 384f));
    }

    public void clearPrintBuffer() {
        useCaseClearPrintBuffer.asyncExecuteWithoutResult(null);
    }

    private void doUICmd_showPrintFaildDialog(String errMsg) {
        execute(ui -> ui.showPrintFailedDialog(errMsg));
    }

    private void doUICmd_showHistory() {
        execute(ui -> ui.showHistory());
    }
}
