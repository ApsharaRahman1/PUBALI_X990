package com.vfi.android.payment.presentation.models;


import com.vfi.android.payment.presentation.mappers.HostTypeMapper;

public class SettlementItemModel {
    private int hostType;
    private String settlementItemTitle;
    private String settlementItemTotalAmount;
    private boolean isAllowSelected = true;
    private boolean isSelected;

    public SettlementItemModel(int hostType, String settlementItemTotalAmount) {
        this.hostType = hostType;
        this.settlementItemTotalAmount = settlementItemTotalAmount;
        this.settlementItemTitle = HostTypeMapper.toSettlementItemTitle(hostType);
    }

    public SettlementItemModel(int hostType, String settlementItemTotalAmount, boolean isAllowSelected, boolean isDefaultSelected) {
        this.hostType = hostType;
        this.settlementItemTotalAmount = settlementItemTotalAmount;
        this.settlementItemTitle = HostTypeMapper.toSettlementItemTitle(hostType);
        this.isAllowSelected = isAllowSelected;
        this.isSelected = isDefaultSelected;
    }

    public int getHostType() {
        return hostType;
    }

    public String getSettlementItemTitle() {
        return settlementItemTitle;
    }

    public String getSettlementItemTotalAmount() {
        return settlementItemTotalAmount;
    }

    public void setHostType(int hostType) {
        this.hostType = hostType;
    }

    public void setSettlementItemTitle(String settlementItemTitle) {
        this.settlementItemTitle = settlementItemTitle;
    }

    public void setSettlementItemTotalAmount(String settlementItemTotalAmount) {
        this.settlementItemTotalAmount = settlementItemTotalAmount;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean isAllowSelected() {
        return isAllowSelected;
    }

    public void setAllowSelected(boolean allowSelected) {
        isAllowSelected = allowSelected;
    }
}
