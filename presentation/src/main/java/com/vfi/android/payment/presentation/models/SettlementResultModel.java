package com.vfi.android.payment.presentation.models;


import com.vfi.android.payment.presentation.mappers.HostTypeMapper;

public class SettlementResultModel {
    private int hostType;
    private String settlementItemTitle;
    private int settlementStatus;

    public static final int SETTLEMENT_SUCCESS = 0;
    public static final int SETTLEMENT_FAILED = 1;
    public static final int SETTLEMENT_EMPTY_BATCH = 2;

    public SettlementResultModel(int hostType, int settlementStatus) {
        this.hostType = hostType;
        this.settlementItemTitle = HostTypeMapper.toSettlementItemTitle(hostType);
        this.settlementStatus = settlementStatus;
    }

    public int getHostType() {
        return hostType;
    }

    public String getSettlementItemTitle() {
        return settlementItemTitle;
    }

    public void setHostType(int hostType) {
        this.hostType = hostType;
    }

    public void setSettlementItemTitle(String settlementItemTitle) {
        this.settlementItemTitle = settlementItemTitle;
    }

    public int getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(int settlementStatus) {
        this.settlementStatus = settlementStatus;
    }
}
