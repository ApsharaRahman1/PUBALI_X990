package com.vfi.android.domain.interfaces.repository;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.DeviceInformation;
import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.businessbeans.InstallmentPromo;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.businessbeans.PrintConfig;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.SwitchParameter;
import com.vfi.android.domain.entities.businessbeans.TLEConfig;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.entities.databeans.HistoryConstraint;
import com.vfi.android.domain.entities.databeans.MenuOrder;
import com.vfi.android.domain.entities.databeans.SettlementRecord;

import java.util.List;

public interface IRepository {

    TerminalCfg getTerminalCfg();

    void putTerminalCfg(TerminalCfg terminalCfg);

    List<RecordInfo> getReversalRecords();

    List<RecordInfo> getUnUploadTCRecords();

    List<RecordInfo> getUnUploadOfflineRecords();

    List<RecordInfo> getUnUploadTipAdjustRecords();

    void putReversalInfo(RecordInfo reversalRecordInfo);

    void deleteReversalInfo(String traceNum, String invoiceNum);

    void putRecordInfo(RecordInfo recordInfo);

    HostInfo getHostInfo(int hostIndex);

    HostInfo getHostInfoByHostType(int hostType);

    List<HostInfo> getMultiHostInfo(String hostIndexs);

    void putHostInfo(HostInfo hostInfo);

    CurrentTranData getCurrentTranData();

    void putCurrentTranData(CurrentTranData currentTranData);

    MerchantInfo getMerchantInfo(int merchantIndex);

    void putMerchantInfo(MerchantInfo merchantInfo);

    List<MerchantInfo> getMultiMerchantInfo(String merchantIndexs);

    void putDeviceInfomation(DeviceInformation deviceInfo);

    List<MenuOrder> getMenuOrder();

    void putMenuOrder();

    TerminalStatus getTerminalStatus();

    void putTerminalStatus(TerminalStatus terminalStatus);

    void lockCheckCardLock();

    void unlockCheckCardLock();

    RecordInfo getRecordInfoByInvoice(String invoice);

    void putCardBinInfo(CardBinInfo cardBinInfo);

    void clearCardBinTable();

    SwitchParameter getSwitchParameter(int transType);

    List<SwitchParameter> getAllSwitchParameters();

    void putSwitchParameter(SwitchParameter switchParameter);

    List<CardBinInfo> getCardInfos(String pan);

    CardBinInfo getCardBinInfoByIndex(int cardIndex);

    SettlementRecord getSettlementInformation();

    List<RecordInfo> getBatchUploadRecords(int hostType, int merchantIndex);

    List<RecordInfo> getRecordInfos(HistoryConstraint historyConstraint);

    List<RecordInfo> getRecordInfoList(int hostType, int merchantIndex);

    void clearHostDataTable();

    void clearMerchantDataTable();

    void deleteOrigTransRecord(String traceNum, String invoiceNum);

    List<CardBinInfo> getAllCardInfo();

    List<Integer> getHostTypeList();

    void clearBatch(int hostType, int merchantIndex);

    void clearAllBatches();

    void clearAllReversals();

    void markOfflineTransUploaded(String traceNum, String invoiceNum);

    void markTCUploaded(String traceNum, String invoiceNum);

    boolean isAllHostEmptyBatch();

    List<HostInfo> getAllHostInfos();

    List<MerchantInfo> getAllMerchants();

    DeviceInformation getDeviceInformation();

    boolean isEmptyBatch(int hostType);

    PrintConfig getPrintConfig(int merchantIndex);

    void putPrintConfig(PrintConfig printConfig);

    List<InstallmentPromo> getAllEnabledInstalmentPromo(boolean isOnlyEnabled);

    void clearInstallmentPromoTable();

    void putInstallmentPromo(InstallmentPromo installmentPromo);

    List<PrintConfig> getAllPrintConfigs();

    TLEConfig getTleConfig(int tleIndex);

    List<TLEConfig> getAllTleConfigs();

    void putTleConfig(TLEConfig tleConfig);
}
