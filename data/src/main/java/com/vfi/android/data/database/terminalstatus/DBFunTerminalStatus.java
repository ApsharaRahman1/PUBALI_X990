package com.vfi.android.data.database.terminalstatus;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;

public class DBFunTerminalStatus {
    private static final String TAG = TAGS.DataBase;
    public static long PRIMARY_KEY_ID = 1;

    public static void save(TerminalStatus terminalStatus) {
        DBModelTerminalStatus dbModelTerminalStatus = object2DBModel(terminalStatus);
        dbModelTerminalStatus.setId(PRIMARY_KEY_ID);
        dbModelTerminalStatus.save();
    }

    public static TerminalStatus get() {
        DBModelTerminalStatus dbModelTerminalStatus = new Select().from(DBModelTerminalStatus.class)
                .where(DBModelTerminalStatus_Table.id.eq(PRIMARY_KEY_ID))
                .querySingle();

        if (dbModelTerminalStatus == null) {
            LogUtil.d(TAG, "Terminal status not found.");
            return null;
        }

        return DBModel2Object(dbModelTerminalStatus);
    }

    private static DBModelTerminalStatus object2DBModel(TerminalStatus terminalStatus) {
        DBModelTerminalStatus dbModelTerminalStatus = new DBModelTerminalStatus();
        dbModelTerminalStatus.setNeedForceSettlement(terminalStatus.isNeedForceSettlement());
        dbModelTerminalStatus.setReqIsoLog(terminalStatus.getReqIsoLog());
        dbModelTerminalStatus.setRespIsoLog(terminalStatus.getRespIsoLog());
        dbModelTerminalStatus.setLastTransStatus(terminalStatus.getLastTransStatus());
        dbModelTerminalStatus.setAutoSettlementFailed(terminalStatus.isAutoSettlementFailed());
        dbModelTerminalStatus.setLastSettleDateYYYYMMDD(terminalStatus.getLastAutoSettleDateYYYYMMDD());
        dbModelTerminalStatus.setLastForceSettleDateYYYYMMDD(terminalStatus.getLastForceSettleDateYYYYMMDD());
        dbModelTerminalStatus.setLastTransPrintData(terminalStatus.getLastTransPrintData());
        dbModelTerminalStatus.setLastSettlementPrintData(terminalStatus.getLastSettlementPrintData());
        dbModelTerminalStatus.setLastEmvDebugInfo(terminalStatus.getLastEmvDebugInfo());
        return dbModelTerminalStatus;
    }

    private static TerminalStatus DBModel2Object(DBModelTerminalStatus dbModelTerminalStatus) {
        TerminalStatus terminalStatus = new TerminalStatus();
        terminalStatus.setNeedForceSettlement(dbModelTerminalStatus.isNeedForceSettlement());
        terminalStatus.setReqIsoLog(dbModelTerminalStatus.getReqIsoLog());
        terminalStatus.setRespIsoLog(dbModelTerminalStatus.getRespIsoLog());
        terminalStatus.setLastTransStatus(dbModelTerminalStatus.getLastTransStatus());
        terminalStatus.setAutoSettlementFailed(dbModelTerminalStatus.isAutoSettlementFailed());
        terminalStatus.setLastAutoSettleDateYYYYMMDD(dbModelTerminalStatus.getLastSettleDateYYYYMMDD());
        terminalStatus.setLastForceSettleDateYYYYMMDD(dbModelTerminalStatus.getLastForceSettleDateYYYYMMDD());
        terminalStatus.setLastTransPrintData(dbModelTerminalStatus.getLastTransPrintData());
        terminalStatus.setLastSettlementPrintData(dbModelTerminalStatus.getLastSettlementPrintData());
        terminalStatus.setLastEmvDebugInfo(dbModelTerminalStatus.getLastEmvDebugInfo());
        return terminalStatus;
    }
}
