package com.vfi.android.data.database.record;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;
import com.vfi.android.data.database.DBFlowDatabase;

@QueryModel(database = DBFlowDatabase.class)
public class SettleQueryModel {
    @Column
    private int hostType;
    @Column
    private int merchantIndex;
    @Column
    private int totalCount;
    @Column
    private long totalAmount;
    @Column
    private long totalTipAmount;
    @Column
    private String batchNo;

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

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getTotalTipAmount() {
        return totalTipAmount;
    }

    public void setTotalTipAmount(long totalTipAmount) {
        this.totalTipAmount = totalTipAmount;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }
}
