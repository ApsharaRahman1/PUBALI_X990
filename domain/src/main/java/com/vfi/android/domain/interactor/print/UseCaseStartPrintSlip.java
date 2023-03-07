package com.vfi.android.domain.interactor.print;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.businessbeans.PrintConfig;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.entities.consts.CVMResult;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.entities.consts.CardType;
import com.vfi.android.domain.entities.consts.HostType;
import com.vfi.android.domain.entities.consts.PrintType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.databeans.PrintInfo;
import com.vfi.android.domain.entities.databeans.PrintTask;
import com.vfi.android.domain.entities.databeans.PrinterParamIn;
import com.vfi.android.domain.entities.databeans.SettlePrintItem;
import com.vfi.android.domain.entities.databeans.SettlementRecord;
import com.vfi.android.domain.entities.databeans.SettlementRecordItem;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interactor.print.tasks.InstallmentPrintTask;
import com.vfi.android.domain.interactor.print.tasks.CommonPrintTask;
import com.vfi.android.domain.interactor.print.tasks.VoidPrintTask;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.domain.utils.Tlv2Map;
import com.vfi.android.domain.utils.ZlibUtil;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;


public class UseCaseStartPrintSlip extends UseCase<Integer, PrintInfo> {
    private final String TAG = TAGS.PRINT;
    private final IRepository iRepository;
    private final PrintTaskManager printTaskManager;
    private final CurrentTranData currentTranData;
    private RecordInfo recordInfo;

    @Inject
    UseCaseStartPrintSlip(PrintTaskManager printTaskManager,
                          IRepository iRepository,
                          ThreadExecutor threadExecutor,
                          PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
        this.printTaskManager = printTaskManager;
        this.currentTranData = iRepository.getCurrentTranData();
        this.recordInfo = currentTranData.getRecordInfo();
    }


    @Override
    public Observable<Integer> buildUseCaseObservable(PrintInfo printInfo) {
        LogUtil.d(TAG, "UseCaseStartPrintSlip executed.");
        return Observable.create(emitter -> {
            if (printInfo == null) {
                LogUtil.d(TAG, "No print info");
                emitter.onComplete();
            }

            printInfo.setiRepository(iRepository);

            if (printInfo.isContinueFromPrintError()) {
                LogUtil.d(TAG, "isContinueFromPrintError true");
                printTaskManager.continuePrint(new IPrintListener() {
                    @Override
                    public void onSuccess(int taskId) {
                        emitter.onNext(taskId);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        emitter.onError(throwable);
                    }
                });
                return;
            }

            // set reprint record.
            if (printInfo.isDuplicateSlip()) {
                recordInfo = printInfo.getRecordInfo();
                LogUtil.d(TAG, "recordInfo = " + recordInfo.toString());
            }

            if (isEnableISOLog() && !printInfo.isDuplicateSlip() && !printInfo.isContinueFromPrintError()) {
                startPrintISOLog();
            }

            if (printInfo.getPrintTask() != null) {
                LogUtil.d(TAG, "Print presentation print task.");
                startPrintTask(emitter, printInfo.getPrintTask());
                return;
            } else {
                LogUtil.d(TAG, "Print type[" + PrintType.toDebugString(printInfo.getPrintType()) + "]");
                switch (printInfo.getPrintType()) {
                    case PrintType.SETTLEMENT:
                        saveSettlementPrintData();
                        printSettlementSlip(emitter, printInfo);
                        break;
                    case PrintType.SUMMARY_REPORT:
                        printSummaryReportSlip(emitter, printInfo);
                        break;
                    case PrintType.DETAIL_REPORT:
                        printDetailReportSlip(emitter, printInfo);
                        break;
                    case PrintType.LAST_SETTLEMENT:
                        printLastSettlementData(emitter, printInfo);
                        break;
                    case PrintType.LAST_TRANS:
                        printLastTransData(emitter, printInfo);
                        break;
                    case PrintType.EMV_DEBUG_INFO:
                        printEmvDebugInfo(emitter, printInfo);
                        break;
                    case PrintType.ISO_LOG:
                        printLastISOLog(emitter, printInfo);
                        break;
                    default:
                        saveTransPrintData(printInfo);
                        printNormalSlip(emitter, printInfo);
                        break;
                }
            }
        });
    }

    private void saveSettlementPrintData() {
        SettlementRecord settlementRecord = currentTranData.getCurrentSettlementRecord();
        if (settlementRecord != null) {
            TerminalStatus terminalStatus = iRepository.getTerminalStatus();
            Gson gson = new GsonBuilder().create();
            String value = gson.toJson(settlementRecord);
            terminalStatus.setLastSettlementPrintData(value);
            iRepository.putTerminalStatus(terminalStatus);
        }
    }

    private void saveTransPrintData(PrintInfo printInfo) {
        if (!printInfo.isDuplicateSlip() && recordInfo != null) {
            TerminalStatus terminalStatus = iRepository.getTerminalStatus();
            Gson gson = new GsonBuilder().create();
            String value = gson.toJson(recordInfo);
            terminalStatus.setLastTransPrintData(value);
            iRepository.putTerminalStatus(terminalStatus);
        }
    }

    private void printLastTransData(ObservableEmitter emitter, PrintInfo printInfo) {
        TerminalStatus terminalStatus = iRepository.getTerminalStatus();
        Gson gson = new GsonBuilder().create();
        try {
            recordInfo = gson.fromJson(terminalStatus.getLastTransPrintData(), RecordInfo.class);
            currentTranData.setRecordInfo(recordInfo);
            printInfo.setDuplicateSlip(true);
            printNormalSlip(emitter, printInfo);
        } catch (Exception e) {
            e.printStackTrace();
            emitter.onNext(0);
            emitter.onComplete();
        }
    }

    private void printEmvDebugInfo(ObservableEmitter emitter, PrintInfo printInfo) {
        TerminalStatus terminalStatus = iRepository.getTerminalStatus();

        PrintTask printTask = new PrintTask();
        addLogoImage(printTask, printInfo);
        addOneBlackLine(printTask);
        printTask.addPrintLine(PrinterParamIn.FONT_NORMAL, PrinterParamIn.ALIGN_CENTER, false, "Last Emv Debug Info");
        addOneDividingLine(printTask, '-');

        Map<String, String> map = Tlv2Map.tlv2Map(terminalStatus.getLastEmvDebugInfo());
        if (map.size() == 0) {
            printTask.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, "- - - -  No Data - - - -");
        } else {
            Iterator<Map.Entry<String, String>> tlvIterator = map.entrySet().iterator();
            while (tlvIterator.hasNext()) {
                Map.Entry<String, String> entry = tlvIterator.next();
                String type = entry.getKey();
                String tlvValue = entry.getValue();
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, false, "   " + type, tlvValue);
            }
        }
        addOneDividingLine(printTask, '-');
        addOneBlackLine(printTask);
        addOneBlackLine(printTask);
        addOneBlackLine(printTask);

        printTaskManager.addPrintTask(printTask, new IPrintListener() {
            @Override
            public void onSuccess(int taskId) {
                emitter.onNext(taskId);
                emitter.onComplete();
            }

            @Override
            public void onError(Throwable throwable) {
                emitter.onError(throwable);
            }
        });
    }

    private void printLastSettlementData(ObservableEmitter emitter, PrintInfo printInfo) {
        TerminalStatus terminalStatus = iRepository.getTerminalStatus();
        Gson gson = new GsonBuilder().create();
        try {
            SettlementRecord settlementRecord = gson.fromJson(terminalStatus.getLastSettlementPrintData(), SettlementRecord.class);
            currentTranData.setCurrentSettlementRecord(settlementRecord);
            printInfo.setDuplicateSlip(true);
            printSettlementSlip(emitter, printInfo);
        } catch (Exception e) {
            e.printStackTrace();
            emitter.onNext(0);
            emitter.onComplete();
        }
    }

    private void printLastISOLog(ObservableEmitter emitter, PrintInfo printInfo) {
        TerminalStatus terminalStatus = iRepository.getTerminalStatus();
        String reqISOLog = terminalStatus.getReqIsoLog();
        String respISOLog = terminalStatus.getRespIsoLog();
        printISOLog("ISO Request Message", reqISOLog, null);
        printISOLog("ISO Response Message", respISOLog, new IPrintListener() {
            @Override
            public void onSuccess(int taskId) {
                emitter.onNext(-1);
                emitter.onComplete();
            }

            @Override
            public void onError(Throwable throwable) {
                emitter.onError(throwable);
            }
        });
    }

    private boolean isEnableISOLog() {
        TerminalCfg terminalCfg = iRepository.getTerminalCfg();
        LogUtil.d(TAG, "isEnableISOLog=" + terminalCfg.isEnableISOLog());
        return terminalCfg.isEnableISOLog();
    }

    private void startPrintISOLog() {
        TerminalStatus terminalStatus = iRepository.getTerminalStatus();
        String reqISOLog = terminalStatus.getReqIsoLog();
        String respISOLog = terminalStatus.getRespIsoLog();
        printISOLog("ISO Request Message", reqISOLog, null);
        printISOLog("ISO Response Message", respISOLog, null);
    }

    private void printISOLog(String header, String isoLog, IPrintListener printListener) {
        PrintTask printTask = new PrintTask();
        printTask.addPrintLine(PrinterParamIn.FONT_NORMAL, PrinterParamIn.ALIGN_CENTER, false, header);
        addOneDividingLine(printTask, '-');

        if (isoLog == null || isoLog.length() == 0) {
            LogUtil.d(TAG, "ISO Log[" + header + "] empty.");
            printTask.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, "- - - - Empty ISO Log - - - -");
            addOneDividingLine(printTask, '-');
            addOneBlackLine(printTask);
            if (printListener != null) {
                addOneBlackLine(printTask);
                addOneBlackLine(printTask);
            }
            printTaskManager.addPrintTask(printTask, printListener);
            return;
        }

        try {
            JSONObject isoLogJson = new JSONObject(isoLog);
            Iterator<String> iterator = isoLogJson.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = isoLogJson.getString(key);
                if (key.equals("F55")) {
                    printTask.addPrintLine(PrinterParamIn.FONT_SMALL, false, key, value);
                    Map<String, String> map = Tlv2Map.tlv2Map(value);
                    Iterator<Map.Entry<String, String>> tlvIterator = map.entrySet().iterator();
                    while (tlvIterator.hasNext()) {
                        Map.Entry<String, String> entry = tlvIterator.next();
                        String type = entry.getKey();
                        String tlvValue = entry.getValue();
                        printTask.addPrintLine(PrinterParamIn.FONT_SMALL, false, "   " + type, tlvValue);
                    }
                } else {
                    printTask.addPrintLine(PrinterParamIn.FONT_SMALL, false, key, value);
                }
            }
            addOneBlackLine(printTask);
            addOneBlackLine(printTask);
        } catch (Exception e) {
            e.printStackTrace();
        }

        printTaskManager.addPrintTask(printTask, null);
    }

    private void printNormalSlip(ObservableEmitter emitter, PrintInfo printInfo) {
        LogUtil.d(TAG, "printNormalSlip excuted.");

        if (recordInfo != null) {
            printInfo.setPrintConfig(iRepository.getPrintConfig(recordInfo.getMerchantIndex()));
        }

        PrintTask printTask = getPrintTask(printInfo);
        if (printTask == null) {
            throw new RuntimeException("Create print task failed.");
        }

        printTaskManager.addPrintTask(printTask, new IPrintListener() {
            @Override
            public void onSuccess(int taskId) {
                emitter.onNext(taskId);
                emitter.onComplete();
            }

            @Override
            public void onError(Throwable throwable) {
                emitter.onError(throwable);
            }
        });
    }

    private void printSettlementSlip(ObservableEmitter emitter, PrintInfo printInfo) {
        int taskId = 0;
        SettlementRecord settlementRecord = currentTranData.getCurrentSettlementRecord();
        Iterator<SettlementRecordItem> iterator = settlementRecord.getSettlementRecordItems().iterator();
        PrintTask printTask = new PrintTask(taskId++);
        while (iterator.hasNext()) {
            SettlementRecordItem recordItem = iterator.next();
            if (!recordItem.isNeedSettlement() && !recordItem.isNeedSettlementButBatchEmpty()) {
                LogUtil.d(TAG, "Host[" + HostType.toDebugString(recordItem.getHostType()) + "] not require settlement");
                continue;
            }

            printInfo.setPrintConfig(iRepository.getPrintConfig(recordItem.getMerchantIndex()));

            addLogoImage(printTask, printInfo);
            if (printInfo.isDuplicateSlip()) {
                addDuplicateLabel(printTask);
            }
            addSettleHostMerchantHeader(printInfo, printTask, recordItem);

            addOneBlackLine(printTask);
            if (recordItem.isEmptyBatch(false)) {
                addOneDividingLine(printTask, '-');
                addOneBlackLine(printTask);
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, "- - - - Empty Batch - - - -");
                addOneBlackLine(printTask);
                addOneDividingLine(printTask, '-');
            } else if (!recordItem.isSettlementSuccess()) {
                addOneDividingLine(printTask, '-');
                addOneBlackLine(printTask);
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, "- - - - Settlement Failed - - - -");
                addOneBlackLine(printTask);
                addOneDividingLine(printTask, '-');
            } else {
                List<SettlePrintItem> settlePrintItems = recordItem.getSettlePrintItemList();
                if (settlePrintItems.size() > 0) {
                    printTask.addTwoTextPrintLine("CARD TYPE", "CARD NUMBER");
                    printTask.addTwoTextPrintLine("EXP DATE", "INVOICE NUM");
                    printTask.addTwoTextPrintLine("TRANSACTION", "AMOUNT");
                    printTask.addTwoTextPrintLine("APPR CODE", "DATE/TIME");
                    addOneDividingLine(printTask, '-');
                    for(int i = 0; i < settlePrintItems.size(); i++) {
                        if (i != 0 && i % 10 == 0) {
                            addSettlePrintInfoToPrinter(printTask, emitter, false);
                            printTask = new PrintTask(taskId++);
                        }
                        addOneSettleRecord(printTask, settlePrintItems.get(i));
                    }

                    int[] cardTypeList = CardType.getCardTypeList();
                    for (int i = 0; i < cardTypeList.length; i++) {
                        addOneCardTypeSettleInfo(printTask, settlePrintItems, recordItem.getHostType(), cardTypeList[i]);
                    }
                    addOneBlackLine(printTask);
                    addGrandTotalSettleInfo(printTask, settlePrintItems, recordItem.getHostType());
                } else {
                    LogUtil.e(TAG, "No Print Item Found.");
                }
            }

            addSettlePrintInfoToPrinter(printTask, emitter, false);
            printTask = new PrintTask(taskId++);
        }

        addOneBlackLine(printTask);
        printTask.addPrintLine(PrinterParamIn.FONT_NORMAL, PrinterParamIn.ALIGN_CENTER, true, "SETTLEMENT CONFIRMED");
        addOneBlackLine(printTask);
        addOneBlackLine(printTask);
        addOneBlackLine(printTask);
        addSettlePrintInfoToPrinter(printTask, emitter, true);
    }

    private void printDetailReportSlip(ObservableEmitter emitter, PrintInfo printInfo) {
        int taskId = 0;
        SettlementRecord settlementRecord = iRepository.getSettlementInformation();
        Iterator<SettlementRecordItem> iterator = settlementRecord.getSettlementRecordItems().iterator();
        PrintTask printTask = new PrintTask(taskId++);
        while (iterator.hasNext()) {
            SettlementRecordItem recordItem = iterator.next();

            printInfo.setPrintConfig(iRepository.getPrintConfig(recordItem.getMerchantIndex()));

            addLogoImage(printTask, printInfo);
            addOneBlackLine(printTask);
            addHeader(printTask, printInfo);
            addDuplicateLabel(printTask);
            addSettleHostMerchantHeader(printInfo, printTask, recordItem);

            addOneBlackLine(printTask);
            if (recordItem.isEmptyBatch(false)) {
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, "Empty Batch");
                addOneDividingLine(printTask, '-');
            } else {
                List<SettlePrintItem> settlePrintItems = recordItem.getSettlePrintItemList();
                if (settlePrintItems.size() > 0) {
                    printTask.addTwoTextPrintLine("CARD TYPE", "CARD NUMBER");
                    printTask.addTwoTextPrintLine("EXP DATE", "INVOICE NUM");
                    printTask.addTwoTextPrintLine("TRANSACTION", "AMOUNT");
                    printTask.addTwoTextPrintLine("APPR CODE", "DATE/TIME");
                    addOneDividingLine(printTask, '-');
                    for(int i = 0; i < settlePrintItems.size(); i++) {
                        if (i != 0 && i % 10 == 0) {
                            addSettlePrintInfoToPrinter(printTask, emitter, false);
                            printTask = new PrintTask(taskId++);
                        }
                        addOneSettleRecord(printTask, settlePrintItems.get(i));
                    }
                }
            }

            addSettlePrintInfoToPrinter(printTask, emitter, false);
            printTask = new PrintTask(taskId++);
        }

        addOneBlackLine(printTask);
        addOneBlackLine(printTask);
        addOneBlackLine(printTask);
        addSettlePrintInfoToPrinter(printTask, emitter, true);
    }

    private void printSummaryReportSlip(ObservableEmitter emitter, PrintInfo printInfo) {
        int taskId = 0;
        SettlementRecord settlementRecord = iRepository.getSettlementInformation();
        Iterator<SettlementRecordItem> iterator = settlementRecord.getSettlementRecordItems().iterator();
        PrintTask printTask = new PrintTask(taskId++);
        while (iterator.hasNext()) {
            SettlementRecordItem recordItem = iterator.next();

            printInfo.setPrintConfig(iRepository.getPrintConfig(recordItem.getMerchantIndex()));

            addLogoImage(printTask, printInfo);
            addOneBlackLine(printTask);
            addHeader(printTask, printInfo);
            addDuplicateLabel(printTask);
            addSettleHostMerchantHeader(printInfo, printTask, recordItem);

            addOneBlackLine(printTask);
            if (recordItem.isEmptyBatch(false)) {
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, "Empty Batch");
                addOneDividingLine(printTask, '-');
            } else {
                List<SettlePrintItem> settlePrintItems = recordItem.getSettlePrintItemList();
                if (settlePrintItems.size() > 0) {
                    int[] cardTypeList = CardType.getCardTypeList();
                    for (int i = 0; i < cardTypeList.length; i++) {
                        addOneCardTypeSettleInfo(printTask, settlePrintItems, recordItem.getHostType(), cardTypeList[i]);
                    }
                    addOneBlackLine(printTask);
                    addGrandTotalSettleInfo(printTask, settlePrintItems, recordItem.getHostType());
                }
            }

            addSettlePrintInfoToPrinter(printTask, emitter, false);
            printTask = new PrintTask(taskId++);
        }

        addOneBlackLine(printTask);
        addOneBlackLine(printTask);
        addOneBlackLine(printTask);
        addSettlePrintInfoToPrinter(printTask, emitter, true);
    }

    private void addSettlePrintInfoToPrinter(PrintTask printTask, ObservableEmitter emitter, boolean isLastTask) {
        if (isLastTask) {
            printTask.setOnlyNotifyPrintError(false);
        } else {
            printTask.setOnlyNotifyPrintError(true);
        }
        LogUtil.d(TAG, "===addSettlePrintInfoToPrinter taskId[" + printTask.getTaskId() + "]");
        printTaskManager.addPrintTask(printTask, new IPrintListener() {
            @Override
            public void onSuccess(int taskId) {
                emitter.onNext(taskId);
                emitter.onComplete();
            }

            @Override
            public void onError(Throwable throwable) {
                emitter.onError(throwable);
            }
        });
    }

    private void addSettleHostMerchantHeader(PrintInfo printInfo, PrintTask printTask, SettlementRecordItem recordItem) {
        SimpleDateFormat transTime = new SimpleDateFormat("HHmmss", Locale.getDefault());
        SimpleDateFormat transDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        String settlementTime = StringUtil.formatTime(transTime.format(date));
        String settlementDate = StringUtil.formatDate(transDate.format(date));
        addHeader(printTask, printInfo);
        printTask.addPrintLine(PrinterParamIn.FONT_SMALL, String.format("DATE: %s", settlementDate), String.format("TIME: %s", settlementTime));
        MerchantInfo merchantInfo = iRepository.getMerchantInfo(recordItem.getMerchantIndex());
        printTask.addPrintLine(PrinterParamIn.FONT_SMALL, String.format("TID: %s", merchantInfo.getTerminalId()), String.format("MID: %s", merchantInfo.getMerchantId()));
        printTask.addPrintLine(PrinterParamIn.FONT_SMALL, String.format("BATCH#: %s", recordItem.getBatchNum()));
        HostInfo hostInfo = iRepository.getHostInfoByHostType(recordItem.getHostType());
      //  printTask.addPrintLine(PrinterParamIn.FONT_SMALL, String.format("HOST        #: %s", hostInfo.getHostName()));
        switch (printInfo.getPrintType()) {
            case PrintType.SETTLEMENT:
                printTask.addPrintLine(PrinterParamIn.FONT_NORMAL, PrinterParamIn.ALIGN_CENTER, true, "SETTLEMENT REPORT");
                break;
            case PrintType.SUMMARY_REPORT:
                printTask.addPrintLine(PrinterParamIn.FONT_NORMAL, PrinterParamIn.ALIGN_CENTER, true, "SUMMARY REPORT");
                break;
            case PrintType.DETAIL_REPORT:
                printTask.addPrintLine(PrinterParamIn.FONT_NORMAL, PrinterParamIn.ALIGN_CENTER, true, "DETAIL REPORT");
                break;
        }
    }

    private void addOneSettleRecord(PrintTask printTask, SettlePrintItem settlePrintItem) {
        printTask.addTwoTextPrintLine(settlePrintItem.getCardTypeAbbr(), getPrintPanText(settlePrintItem.getPan(), settlePrintItem.getCardEntryMode()));
        printTask.addTwoTextPrintLine("XX/XX", settlePrintItem.getInvoiceNum());
        printTask.addTwoTextPrintLine(settlePrintItem.getTransaction(), settlePrintItem.getCurrencyAmountText());
        printTask.addTwoTextPrintLine(settlePrintItem.getApprovalCode(), settlePrintItem.getDateTime());
        addOneBlackLine(printTask);
    }

    private void addOneCardTypeSettleInfo(PrintTask printTask, List<SettlePrintItem> settlePrintItems, int hostType, int cardType) {
        int saleCount = 0;
        int voidSaleCount = 0;
        int preAuthCompCount = 0;
        int voidPreAuthCompCount = 0;
        long saleAmountTotal = 0;
        long voidSaleAmountTotal = 0;
        long preAuthCompAmountTotal = 0;
        long voidPreAuthCompAmountTotal = 0;

        Iterator<SettlePrintItem> iterator = settlePrintItems.iterator();
        while (iterator.hasNext()) {
            SettlePrintItem item = iterator.next();
            if (item.getCardType() == cardType) {
                switch (item.getRecordType()) {
                    case SettlePrintItem.SALE:
                        saleCount++;
                        saleAmountTotal += item.getTotalAmountLong();
                        break;
                    case SettlePrintItem.VOID_SALE:
                        voidSaleCount++;
                        voidSaleAmountTotal += item.getTotalAmountLong();
                        break;
                    case SettlePrintItem.PREAUTH_COMP:
                        preAuthCompCount++;
                        preAuthCompAmountTotal += item.getTotalAmountLong();
                        break;
                    case SettlePrintItem.VOID_PREAUTH_COMP:
                        voidPreAuthCompCount++;
                        voidPreAuthCompAmountTotal += item.getTotalAmountLong();
                        break;
                }
            }
        }

        if (saleCount + voidSaleCount + preAuthCompCount + voidPreAuthCompCount > 0) {
            addOneDividingLine(printTask, '-');
            printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "CARDTYPE :" + CardType.toCardTypeText(cardType));
            printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "TRANS                  CNT", "AMT");
            addOneBlackLine(printTask);
            if (hostType == HostType.INSTALLMENT) {
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "INSTALLMENT            " + getCountText(saleCount) + StringUtil.formatAmount("" + saleAmountTotal));
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "VOID INST              " + getCountText(voidSaleCount)  , StringUtil.formatAmount("" + voidSaleAmountTotal));
            } else {
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "SALE                   " + getCountText(saleCount) , StringUtil.formatAmount("" + saleAmountTotal));
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "VOID SALE              " + getCountText(voidSaleCount), StringUtil.formatAmount("" + voidSaleAmountTotal));
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "PREAUTH COMP           " + getCountText(preAuthCompCount), StringUtil.formatAmount("" + preAuthCompAmountTotal));
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "VDAUTH COMP            " + getCountText(voidPreAuthCompCount), StringUtil.formatAmount("" + voidPreAuthCompAmountTotal));
            }
            addOneBlackLine(printTask);
        }
    }

    private void addGrandTotalSettleInfo(PrintTask printTask, List<SettlePrintItem> settlePrintItems, int hostType) {
        int saleCount = 0;
        int voidSaleCount = 0;
        int preAuthCompCount = 0;
        int voidPreAuthCompCount = 0;
        long saleAmountTotal = 0;
        long voidSaleAmountTotal = 0;
        long preAuthCompAmountTotal = 0;
        long voidPreAuthCompAmountTotal = 0;

        Iterator<SettlePrintItem> iterator = settlePrintItems.iterator();
        while (iterator.hasNext()) {
            SettlePrintItem item = iterator.next();
            switch (item.getRecordType()) {
                case SettlePrintItem.SALE:
                    saleCount++;
                    saleAmountTotal += item.getTotalAmountLong();
                    break;
                case SettlePrintItem.VOID_SALE:
                    voidSaleCount++;
                    voidSaleAmountTotal += item.getTotalAmountLong();
                    break;
                case SettlePrintItem.PREAUTH_COMP:
                    preAuthCompCount++;
                    preAuthCompAmountTotal += item.getTotalAmountLong();
                    break;
                case SettlePrintItem.VOID_PREAUTH_COMP:
                    voidPreAuthCompCount++;
                    voidPreAuthCompAmountTotal += item.getTotalAmountLong();
                    break;
            }
        }

        printTask.addPrintLine(PrinterParamIn.FONT_NORMAL, true, "GRAND TOTAL", "");
        printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "TRANS                  CNT", "AMT");
        addOneBlackLine(printTask);
        if (hostType == HostType.INSTALLMENT) {
            printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "INSTALLMENT            " + getCountText(saleCount) , StringUtil.formatAmount("" + saleAmountTotal));
            printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "VOID INST              " + getCountText(voidSaleCount) , StringUtil.formatAmount("" + voidSaleAmountTotal));
        } else {
            printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "SALE                   " + getCountText(saleCount) , StringUtil.formatAmount("" + saleAmountTotal));
            printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "VOID SALE              " + getCountText(voidSaleCount) , StringUtil.formatAmount("" + voidSaleAmountTotal));
            printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "PREAUTH COMP           " + getCountText(preAuthCompCount), StringUtil.formatAmount("" + preAuthCompAmountTotal));
            printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "VDAUTH COMP            " + getCountText(voidPreAuthCompCount) , StringUtil.formatAmount("" + voidPreAuthCompAmountTotal));
        }

        addOneBlackLine(printTask);
    }

    private String getCountText(int count) {
        if (count >= 100) {
            return "" + count;
        } else if (count >= 10) {
            return " " + count;
        } else {
            return " " + count + " ";
        }
    }

    private PrintTask getPrintTask(PrintInfo printInfo) {
        switch (printInfo.getPrintType()) {
            case PrintType.SALE:
            case PrintType.PREAUTH:
            case PrintType.PREAUTH_COMP:
            case PrintType.OFFLINE:
            case PrintType.TIP_ADJUST:
                return new CommonPrintTask(recordInfo, printInfo);
            case PrintType.INSTALLMENT:
                return new InstallmentPrintTask(recordInfo, printInfo);
            case PrintType.VOID:
                return new VoidPrintTask(recordInfo, printInfo);
        }

        return new CommonPrintTask(recordInfo, printInfo);
    }

    private void startPrintTask(ObservableEmitter emitter, PrintTask printTask) {

    }

    private void addLogoImage(PrintTask printTask, PrintInfo printInfo) {
        if (printInfo.getPrintLogoData() != null) {
            printTask.addPrintImage(0, 146, 384, printInfo.getPrintLogoData());
        }
    }

    private void addHeader(PrintTask printTask, PrintInfo printInfo) {
        PrintConfig printConfig = printInfo.getPrintConfig();
        if (printConfig != null) {
            if (StringUtil.getNonNullString(printConfig.getHeader1()).length() > 0) {
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getHeader1());
            }
            if (StringUtil.getNonNullString(printConfig.getHeader2()).length() > 0) {
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getHeader2());
            }
            if (StringUtil.getNonNullString(printConfig.getHeader3()).length() > 0) {
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getHeader3());
            }
            if (StringUtil.getNonNullString(printConfig.getHeader4()).length() > 0) {
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getHeader4());
            }
            if (StringUtil.getNonNullString(printConfig.getHeader5()).length() > 0) {
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getHeader5());
            }
            if (StringUtil.getNonNullString(printConfig.getHeader6()).length() > 0) {
                printTask.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getHeader6());
            }
        }
    }

    private void addDuplicateLabel(PrintTask printTask) {
        final int bgHeight = 40;
        final int bgWidth = 380;
        final int fontSize = 30;
        Bitmap bitmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);

        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        paint.setColor(Color.WHITE);
        float textWidth = paint.measureText("DUPLICATE");
        float baseLineY = bgHeight / 2 - (paint.ascent() + paint.descent()) / 2;
        canvas.drawText("DUPLICATE", bgWidth / 2 - textWidth / 2, baseLineY, paint);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, bos); // 40 is ok, I tested it.
        printTask.addPrintImage(0, bgHeight, bgWidth, bos.toByteArray());
    }

    private void addOneBlackLine(PrintTask printTask) {
        printTask.addPrintLine(PrinterParamIn.FONT_NORMAL, " ");
    }

    private void addOneDividingLine(PrintTask printTask, char dividingChar) {
        printTask.addPrintLine(PrinterParamIn.FONT_SMALL, "- - - - - - - - - - - - - - - - - - - - - -");
    }

    private String getPrintPanText(String pan, int cardEntryMode) {
        String panLast4Digit = pan.substring(pan.length() - 4);

        String entryModeText = "";
        switch (cardEntryMode) {
            case CardEntryMode.IC:
            case CardEntryMode.RF:
                entryModeText = "C";
                break;
            case CardEntryMode.MAG:
                entryModeText = "S";
                break;
            case CardEntryMode.MANUAL:
                entryModeText = "M";
                break;
        }
        return String.format("**** **** **** %s (%s)", panLast4Digit, entryModeText);
    }
}
