package com.vfi.android.domain.entities.databeans;


import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;

import java.util.List;

public class SettlementRecordItem {
    private final static String TAG = TAGS.Settlement;

    private int hostType;
    private int merchantIndex;
    private String batchNum;

    private boolean isNeedSettlement = false;
    private boolean isNeedSettlementButBatchEmpty = false;
    private boolean isExistReversal = false;

    private int saleTotalCount;
    private long saleTotalAmount;
    private int voidTotalCount;
    private long voidTotalAmount;

    private boolean isSettlementSuccess;

    private List<SettlePrintItem> settlePrintItemList;

    public SettlementRecordItem(int hostType, int merchantIndex, String batchNum) {
        this.hostType = hostType;
        this.merchantIndex = merchantIndex;
        this.batchNum = batchNum;
    }

    public boolean isNeedSettlement() {
        return isNeedSettlement;
    }

    public void setNeedSettlement(boolean needSettlement) {
        isNeedSettlement = needSettlement;
    }

    public boolean isNeedSettlementButBatchEmpty() {
        return isNeedSettlementButBatchEmpty;
    }

    public void setNeedSettlementButBatchEmpty(boolean needSettlementButBatchEmpty) {
        isNeedSettlementButBatchEmpty = needSettlementButBatchEmpty;
    }

    public boolean isEmptyBatch(boolean isReversalNotEmptyBatch) {
        boolean isEmptyBatch = true;
        if (saleTotalCount + voidTotalCount > 0) {
            isEmptyBatch = false;
        }

        if (isReversalNotEmptyBatch && isExistReversal) {
            isEmptyBatch = false;
        }

        LogUtil.d(TAG, "isEmptyBatch=" + isEmptyBatch);
        return isEmptyBatch;
    }

    public int getHostType() {
        return hostType;
    }

    public void setHostType(int hostType) {
        this.hostType = hostType;
    }

    public int getMerchantIndex() {
        return merchantIndex;
    }

    public void setMerchantIndex(int merchantIndex) {
        this.merchantIndex = merchantIndex;
    }

    public long getTotalAmount() {
        return saleTotalAmount;
    }

    public int getSaleTotalCount() {
        return saleTotalCount;
    }

    public void setSaleTotalCount(int saleTotalCount) {
        this.saleTotalCount = saleTotalCount;
    }

    public int getVoidTotalCount() {
        return voidTotalCount;
    }

    public void setVoidTotalCount(int voidTotalCount) {
        this.voidTotalCount = voidTotalCount;
    }

    public long getSaleTotalAmount() {
        return saleTotalAmount;
    }

    public void setSaleTotalAmount(long saleTotalAmount) {
        this.saleTotalAmount = saleTotalAmount;
    }

    public long getVoidTotalAmount() {
        return voidTotalAmount;
    }

    public void setVoidTotalAmount(long voidTotalAmount) {
        this.voidTotalAmount = voidTotalAmount;
    }

    public boolean isSettlementSuccess() {
        return isSettlementSuccess;
    }

    public void setSettlementSuccess(boolean settlementSuccess) {
        LogUtil.d(TAG, "settlementSuccess is " + settlementSuccess);
        isSettlementSuccess = settlementSuccess;
    }

    public boolean isExistReversal() {
        return isExistReversal;
    }

    public void setExistReversal(boolean existReversal) {
        isExistReversal = existReversal;
    }

    public List<SettlePrintItem> getSettlePrintItemList() {
        return settlePrintItemList;
    }

    public void setSettlePrintItemList(List<SettlePrintItem> settlePrintItemList) {
        this.settlePrintItemList = settlePrintItemList;
    }

    public String getBatchNum() {
        return batchNum;
    }
}
