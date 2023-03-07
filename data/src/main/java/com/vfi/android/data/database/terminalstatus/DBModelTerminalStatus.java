package com.vfi.android.data.database.terminalstatus;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.vfi.android.data.database.DBFlowDatabase;

@Table(database = DBFlowDatabase.class)
public class DBModelTerminalStatus extends BaseModel {
    @PrimaryKey
    @Column
    private long id;

    @Column
    private boolean isNeedForceSettlement;

    @Column
    private String reqIsoLog;

    @Column
    private String respIsoLog;

    @Column
    private String lastTransStatus;

    @Column
    private boolean isAutoSettlementFailed;

    @Column
    private String lastSettleDateYYYYMMDD;

    @Column
    private String lastForceSettleDateYYYYMMDD;

    @Column
    private String lastTransPrintData;

    @Column
    private String lastSettlementPrintData;

    @Column
    private String lastEmvDebugInfo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getLastSettleDateYYYYMMDD() {
        return lastSettleDateYYYYMMDD;
    }

    public void setLastSettleDateYYYYMMDD(String lastSettleDateYYYYMMDD) {
        this.lastSettleDateYYYYMMDD = lastSettleDateYYYYMMDD;
    }

    public String getLastForceSettleDateYYYYMMDD() {
        return lastForceSettleDateYYYYMMDD;
    }

    public void setLastForceSettleDateYYYYMMDD(String lastForceSettleDateYYYYMMDD) {
        this.lastForceSettleDateYYYYMMDD = lastForceSettleDateYYYYMMDD;
    }
}
