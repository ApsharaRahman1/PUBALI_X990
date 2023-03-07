package com.vfi.android.payment.presentation.models;

public class HostItemModel {
    private String hostName;
    private String hostIp;
    private int hostPort;

    private boolean isShowOperationListSelected;

    public HostItemModel(String hostName, String hostIp, int hostPort) {
        this.hostName = hostName;
        this.hostIp = hostIp;
        this.hostPort = hostPort;
    }

    public int getHostPort() {
        return hostPort;
    }

    public String getHostIp() {
        return hostIp;
    }

    public String getHostName() {
        return hostName;
    }

    public boolean isShowOperationListSelected() {
        return isShowOperationListSelected;
    }

    public void setShowOperationListSelected(boolean showOperationListSelected) {
        isShowOperationListSelected = showOperationListSelected;
    }
}
