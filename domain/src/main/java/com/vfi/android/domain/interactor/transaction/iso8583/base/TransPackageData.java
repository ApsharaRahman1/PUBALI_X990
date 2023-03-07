package com.vfi.android.domain.interactor.transaction.iso8583.base;

import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TLEConfig;
import com.vfi.android.domain.entities.databeans.SettlementPackInfo;
import com.vfi.android.domain.entities.tle.TLEPackageInfo;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;

public class TransPackageData {
    private int transType;
    private RecordInfo recordInfo;
    private SettlementPackInfo settlementPackInfo;
    private MerchantInfo merchantInfo;
    private boolean isLastBatchUploadRecord;
    private boolean isTLEEnabled;
    private String tpdu;
    private IRepository iRepository;
    private TLEPackageInfo tlePackageInfo;

    public TransPackageData(int transType) {
        this.transType = transType;
    }

    public RecordInfo getRecordInfo() {
        return recordInfo;
    }

    public void setRecordInfo(RecordInfo recordInfo) {
        this.recordInfo = recordInfo;
    }

    public SettlementPackInfo getSettlementPackInfo() {
        return settlementPackInfo;
    }

    public void setSettlementPackInfo(SettlementPackInfo settlementPackInfo) {
        this.settlementPackInfo = settlementPackInfo;
    }

    public int getTransType() {
        return transType;
    }

    public void setTransType(int transType) {
        this.transType = transType;
    }

    public boolean isLastBatchUploadRecord() {
        return isLastBatchUploadRecord;
    }

    public void setLastBatchUploadRecord(boolean lastBatchUploadRecord) {
        isLastBatchUploadRecord = lastBatchUploadRecord;
    }

    public boolean isTLEEnabled() {
        return isTLEEnabled;
    }

    public void setTLEEnabled(boolean TLEEnabled) {
        isTLEEnabled = TLEEnabled;
    }

    public String getTpdu() {
        return tpdu;
    }

    public void setTpdu(String tpdu) {
        this.tpdu = tpdu;
    }

    public MerchantInfo getMerchantInfo() {
        return merchantInfo;
    }

    public void setMerchantInfo(MerchantInfo merchantInfo) {
        this.merchantInfo = merchantInfo;
    }

    public IRepository getiRepository() {
        return iRepository;
    }

    public void setiRepository(IRepository iRepository) {
        this.iRepository = iRepository;
    }

    public void setTlePackageInfo(TLEPackageInfo tlePackageInfo) {
        this.tlePackageInfo = tlePackageInfo;
    }

    public TLEPackageInfo getTlePackageInfo() {
        return tlePackageInfo;
    }
}
