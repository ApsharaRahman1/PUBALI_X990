package com.vfi.android.data.repository;

import android.content.Context;

import com.vfi.android.data.database.cdt.DBFunCardData;
import com.vfi.android.data.database.hdt.DBFunHostData;
import com.vfi.android.data.database.itp.DBFunInstallmentPromo;
import com.vfi.android.data.database.mdt.DBFunMerchantData;
import com.vfi.android.data.database.nmx.DBFunTLE;
import com.vfi.android.data.database.printcfg.DBFunPrintConfig;
import com.vfi.android.data.database.record.DBFunRecordInfo;
import com.vfi.android.data.database.reversal.DBFunReversalInfo;
import com.vfi.android.data.database.tct.DBFunTerminalConfiguration;
import com.vfi.android.data.database.terminalstatus.DBFunTerminalStatus;
import com.vfi.android.data.database.tst.DBFunTransactionSwitch;
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
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.databeans.HistoryConstraint;
import com.vfi.android.domain.entities.databeans.MenuOrder;
import com.vfi.android.domain.entities.databeans.SettlementRecord;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.memory.GlobalMemoryCache;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RepositoryImpl implements IRepository {
    private GlobalMemoryCache globalMemoryCache;
    private Context context;

    @Inject
    public RepositoryImpl(Context context, GlobalMemoryCache globalMemoryCache) {
        this.context = context;
        this.globalMemoryCache = globalMemoryCache;
    }


    @Override
    public TerminalCfg getTerminalCfg() {
        TerminalCfg terminalCfg = globalMemoryCache.getTerminalCfg();
        if (terminalCfg == null) {
            terminalCfg = DBFunTerminalConfiguration.get();
            if (terminalCfg == null) {
                terminalCfg = new TerminalCfg();
                terminalCfg.initDefaultSystemParameter();
                DBFunTerminalConfiguration.save(terminalCfg);
            }

            globalMemoryCache.setTerminalCfg(terminalCfg);
        }

        return terminalCfg;
    }

    @Override
    public void putTerminalCfg(TerminalCfg terminalCfg) {
        globalMemoryCache.setTerminalCfg(terminalCfg);
        DBFunTerminalConfiguration.save(terminalCfg);
    }

    @Override
    public List<RecordInfo> getReversalRecords() {
        return DBFunReversalInfo.getAllReversalRecords();
    }

    @Override
    public List<RecordInfo> getUnUploadTCRecords() {
        return DBFunRecordInfo.getUnUploadTCRecords();
    }

    @Override
    public List<RecordInfo> getUnUploadOfflineRecords() {
        return DBFunRecordInfo.getUnUploadOfflineRecords();
    }

    @Override
    public List<RecordInfo> getUnUploadTipAdjustRecords() {
        return DBFunRecordInfo.getUnUploadTipAdjustRecords();
    }

    @Override
    public void putReversalInfo(RecordInfo reversalRecordInfo) {
        DBFunReversalInfo.save(reversalRecordInfo);
    }

    @Override
    public void deleteReversalInfo(String traceNum, String invoiceNum) {
        DBFunReversalInfo.deleteReversal(traceNum, invoiceNum);
    }

    @Override
    public void putRecordInfo(RecordInfo recordInfo) {
        DBFunRecordInfo.save(recordInfo);
    }

    @Override
    public HostInfo getHostInfo(int hostIndex) {
        return DBFunHostData.get(hostIndex);
    }

    @Override
    public HostInfo getHostInfoByHostType(int hostType) {
        return DBFunHostData.getHostInfoByHostType(hostType);
    }

    @Override
    public List<HostInfo> getMultiHostInfo(String hostIndexs) {
        return DBFunHostData.getHostInfoByIndexs(hostIndexs);
    }

    @Override
    public void putHostInfo(HostInfo hostInfo) {
        DBFunHostData.save(hostInfo);
    }

    @Override
    public CurrentTranData getCurrentTranData() {
        return globalMemoryCache.getCurrentTranData();
    }

    @Override
    public void putCurrentTranData(CurrentTranData currentTranData) {
        globalMemoryCache.setCurrentTranData(currentTranData);
    }

    @Override
    public MerchantInfo getMerchantInfo(int merchantIndex) {
        return DBFunMerchantData.get(merchantIndex);
    }

    @Override
    public void putMerchantInfo(MerchantInfo merchantInfo) {
        DBFunMerchantData.save(merchantInfo);
    }

    @Override
    public List<MerchantInfo> getMultiMerchantInfo(String merchantIndexs) {
        return DBFunMerchantData.getMerchantInfoByIndexs(merchantIndexs);
    }

    @Override
    public void putDeviceInfomation(DeviceInformation deviceInfo) {
        globalMemoryCache.setDeviceInformation(deviceInfo);
    }

    @Override
    public List<MenuOrder> getMenuOrder() {
        return new ArrayList<>();
    }

    @Override
    public void putMenuOrder() {

    }

    @Override
    public TerminalStatus getTerminalStatus() {
        TerminalStatus terminalStatus = DBFunTerminalStatus.get();
        if (terminalStatus == null) {
            terminalStatus = new TerminalStatus();
            putTerminalStatus(terminalStatus);
        }

        return terminalStatus;
    }

    @Override
    public void putTerminalStatus(TerminalStatus terminalStatus) {
        DBFunTerminalStatus.save(terminalStatus);
    }

    @Override
    public void lockCheckCardLock() {
        globalMemoryCache.lockCheckCardLock();
    }

    @Override
    public void unlockCheckCardLock() {
        globalMemoryCache.unlockCheckCardLock();
    }

    @Override
    public RecordInfo getRecordInfoByInvoice(String invoice) {
        return DBFunRecordInfo.getRecordInfoByInvoiceNum(invoice);
    }

    @Override
    public void putCardBinInfo(CardBinInfo cardBinInfo) {
        DBFunCardData.save(cardBinInfo);
    }

    @Override
    public void clearCardBinTable() {
        DBFunCardData.clear();
    }

    @Override
    public SwitchParameter getSwitchParameter(int transType) {
        SwitchParameter switchParameter = DBFunTransactionSwitch.getSwitchParameter(transType);
        if (switchParameter == null) {
            switchParameter = new SwitchParameter();
            switchParameter.setTransType(transType);
            switchParameter.initDefaultSwitchParameter();
            if (transType == TransType.OFFLINE
                    || transType == TransType.PREAUTH
                    || transType == TransType.PREAUTH_COMP
                    || transType == TransType.INSTALLMENT) {
                switchParameter.setEnableEnterTip(false);
            }
            if (transType == TransType.SETTLEMENT
                    || transType == TransType.VOID
                    || transType == TransType.TIP_ADJUST) {
                switchParameter.setEnableInputManagerPwd(true);
            }
            if (transType == TransType.INSTALLMENT) {
                switchParameter.setEnableManual(false);
            }
            putSwitchParameter(switchParameter);
        }

        return switchParameter;
    }

    @Override
    public List<SwitchParameter> getAllSwitchParameters() {
        return DBFunTransactionSwitch.getAllSwitchParameters();
    }

    @Override
    public void putSwitchParameter(SwitchParameter switchParameter) {
        DBFunTransactionSwitch.save(switchParameter);
    }

    @Override
    public List<CardBinInfo> getCardInfos(String pan) {
        return DBFunCardData.getCardDataByPan(pan);
    }

    @Override
    public CardBinInfo getCardBinInfoByIndex(int cardIndex) {
        return DBFunCardData.getCardDataByIndex(cardIndex);
    }

    @Override
    public SettlementRecord getSettlementInformation() {
        return DBFunRecordInfo.getSettlementInfo();
    }

    @Override
    public List<RecordInfo> getBatchUploadRecords(int hostType, int merchantIndex) {
        return DBFunRecordInfo.getBatchUploadRecordInfoList(hostType, merchantIndex);
    }

    public List<RecordInfo> getRecordInfos(HistoryConstraint historyConstraint) {
        return DBFunRecordInfo.getRecordInfoListByConstraint(historyConstraint);
    }

    @Override
    public List<RecordInfo> getRecordInfoList(int hostType, int merchantIndex) {
        return DBFunRecordInfo.getRecordInfoList(hostType, merchantIndex);
    }

    @Override
    public void clearHostDataTable() {
        DBFunHostData.clear();
    }

    @Override
    public void clearMerchantDataTable() {
        DBFunMerchantData.clear();
    }

    @Override
    public void deleteOrigTransRecord(String traceNum, String invoiceNum) {
        DBFunRecordInfo.deleteOrigTransRecord(traceNum, invoiceNum);
    }

    @Override
    public List<CardBinInfo> getAllCardInfo() {
        return DBFunCardData.getAllCardInfo();
    }

    @Override
    public List<Integer> getHostTypeList() {
        return DBFunHostData.getHostTypeList();
    }

    @Override
    public void clearBatch(int hostType, int merchantIndex) {
        DBFunRecordInfo.clearBatch(hostType, merchantIndex);
    }

    @Override
    public void clearAllBatches() {
        DBFunRecordInfo.clearAllRecords();
        DBFunReversalInfo.clearAllReversal();
    }

    @Override
    public void clearAllReversals() {
        DBFunReversalInfo.clearAllReversal();
    }

    @Override
    public void markOfflineTransUploaded(String traceNum, String invoiceNum) {
        DBFunRecordInfo.markOfflineTransUploaded(traceNum, invoiceNum);
    }

    @Override
    public void markTCUploaded(String traceNum, String invoiceNum) {
        DBFunRecordInfo.markTCUploaded(traceNum, invoiceNum);
    }

    @Override
    public boolean isAllHostEmptyBatch() {
        return (DBFunRecordInfo.isRecordTableEmpty() && DBFunReversalInfo.isReversalTableEmpty());
    }

    @Override
    public List<HostInfo> getAllHostInfos() {
        return DBFunHostData.getAllHostInfos();
    }

    @Override
    public List<MerchantInfo> getAllMerchants() {
        return DBFunMerchantData.getAllMerchants();
    }

    @Override
    public DeviceInformation getDeviceInformation() {
        return globalMemoryCache.getDeviceInformation();
    }

    @Override
    public boolean isEmptyBatch(int hostType) {
        if (DBFunRecordInfo.isHostBatchEmpty(hostType)
                && DBFunReversalInfo.isHostReversalEmpty(hostType)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public PrintConfig getPrintConfig(int merchantIndex) {
        MerchantInfo merchantInfo = DBFunMerchantData.get(merchantIndex);
        if (merchantInfo == null) {
            return null;
        }

        return DBFunPrintConfig.get(merchantInfo.getPrintParamIndex());
    }

    @Override
    public void putPrintConfig(PrintConfig printConfig) {
        DBFunPrintConfig.save(printConfig);
    }

    @Override
    public List<InstallmentPromo> getAllEnabledInstalmentPromo(boolean isOnlyEnabled) {
        return DBFunInstallmentPromo.getAllEnabledInstalmentPromo(isOnlyEnabled);
    }

    @Override
    public void clearInstallmentPromoTable() {
        DBFunInstallmentPromo.clear();
    }

    @Override
    public void putInstallmentPromo(InstallmentPromo installmentPromo) {
        DBFunInstallmentPromo.save(installmentPromo);
    }

    @Override
    public List<PrintConfig> getAllPrintConfigs() {
        return DBFunPrintConfig.getAllPrintConfigs();
    }

    @Override
    public TLEConfig getTleConfig(int tleIndex) {
        TLEConfig tleConfig = DBFunTLE.getTleConfig(tleIndex);
        if (tleConfig == null) {
            tleConfig = new TLEConfig();
            tleConfig.setIndex(tleIndex);
            DBFunTLE.save(tleConfig);
        }

        return tleConfig;
    }

    @Override
    public List<TLEConfig> getAllTleConfigs() {
        return DBFunTLE.getAllTleConfigs();
    }

    @Override
    public void putTleConfig(TLEConfig tleConfig) {
        DBFunTLE.save(tleConfig);
    }
}
