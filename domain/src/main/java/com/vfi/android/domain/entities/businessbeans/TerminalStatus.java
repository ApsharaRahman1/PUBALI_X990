package com.vfi.android.domain.entities.businessbeans;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TerminalStatus {
    private boolean isNeedForceSettlement;
    private String reqIsoLog;
    private String respIsoLog;
    private String lastTransStatus;
    private boolean isAutoSettlementFailed;
    private String lastAutoSettleDateYYYYMMDD;
    private String lastForceSettleDateYYYYMMDD;
    private String lastTransPrintData;
    private String lastSettlementPrintData;
    private String lastEmvDebugInfo;

    public TerminalStatus() {
        isNeedForceSettlement = false;
    }

    public RecordInfo getLastTransPrintDataToRecordInfo() {
        if (lastTransPrintData == null) {
            return null;
        }

        Gson gson = new GsonBuilder().create();
        try {
            RecordInfo recordInfo = gson.fromJson(lastTransPrintData, RecordInfo.class);
            return recordInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isNeedForceSettlement() {
        return isNeedForceSettlement;
    }

    public void setNeedForceSettlement(boolean needForceSettlement) {
        isNeedForceSettlement = needForceSettlement;
    }

    public String getReqIsoLog() {
        return reqIsoLog;
    }

    public void setReqIsoLog(String reqIsoLog) {
        this.reqIsoLog = reqIsoLog;
    }

    public String getRespIsoLog() {
        return respIsoLog;
    }

    public void setRespIsoLog(String respIsoLog) {
        this.respIsoLog = respIsoLog;
    }

    public String getLastTransStatus() {
        return lastTransStatus;
    }

    public void setLastTransStatus(String lastTransStatus) {
        this.lastTransStatus = lastTransStatus;
    }

    public boolean isAutoSettlementFailed() {
        return isAutoSettlementFailed;
    }

    public void setAutoSettlementFailed(boolean autoSettlementFailed) {
        isAutoSettlementFailed = autoSettlementFailed;
    }

    public String getLastTransPrintData() {
        return lastTransPrintData;
    }

    public void setLastTransPrintData(String lastTransPrintData) {
        this.lastTransPrintData = lastTransPrintData;
    }

    public String getLastSettlementPrintData() {
        return lastSettlementPrintData;
    }

    public void setLastSettlementPrintData(String lastSettlementPrintData) {
        this.lastSettlementPrintData = lastSettlementPrintData;
    }

    public String getLastEmvDebugInfo() {
        return lastEmvDebugInfo;
    }

    public void setLastEmvDebugInfo(String lastEmvDebugInfo) {
        this.lastEmvDebugInfo = lastEmvDebugInfo;
    }

    public String getLastAutoSettleDateYYYYMMDD() {
        return lastAutoSettleDateYYYYMMDD;
    }

    public void setLastAutoSettleDateYYYYMMDD(String lastAutoSettleDateYYYYMMDD) {
        this.lastAutoSettleDateYYYYMMDD = lastAutoSettleDateYYYYMMDD;
    }

    public String getLastForceSettleDateYYYYMMDD() {
        return lastForceSettleDateYYYYMMDD;
    }

    public void setLastForceSettleDateYYYYMMDD(String lastForceSettleDateYYYYMMDD) {
        this.lastForceSettleDateYYYYMMDD = lastForceSettleDateYYYYMMDD;
    }
}
