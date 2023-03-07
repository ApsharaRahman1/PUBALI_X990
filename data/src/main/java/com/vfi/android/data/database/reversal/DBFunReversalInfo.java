package com.vfi.android.data.database.reversal;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.vfi.android.data.database.record.DBFunRecordInfo;
import com.vfi.android.data.database.record.DBModelRecordInfo;
import com.vfi.android.data.database.record.DBModelRecordInfo_Table;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.raizlabs.android.dbflow.sql.language.Method.count;

/**
 * Created by RuihaoS on 2019/8/14.
 */
public class DBFunReversalInfo {
    private static final String TAG = TAGS.DataBase;

    public static void save(RecordInfo reversalInfo) {
        if (reversalInfo != null) {
            object2DBModel(reversalInfo).save();
        }
    }

    public static void deleteReversal(String traceNum, String invoiceNum) {
        SQLite.delete().from(DBModelReversalInfo.class)
                .where(DBModelReversalInfo_Table.traceNum.eq(traceNum))
                .and(DBModelReversalInfo_Table.invoiceNum.eq(invoiceNum))
                .execute();
    }

    public static void clearAllReversal() {
        SQLite.delete().from(DBModelReversalInfo.class).execute();
    }

    public static List<RecordInfo> getAllReversalRecords() {
        List<DBModelReversalInfo> dbModelReversalInfos = new Select().from(DBModelReversalInfo.class).queryList();
        List<RecordInfo> reversalInfos = new ArrayList<>();

        if (dbModelReversalInfos == null) {
            LogUtil.d(TAG, "No reversal found");
            return reversalInfos;
        }

        for (DBModelReversalInfo reversalInfo: dbModelReversalInfos) {
            if (reversalInfo.getTransType() != TransType.REVERSAL) {
                reversalInfo.setReversalOrgTransType(reversalInfo.getTransType());
                reversalInfo.setTransType(TransType.REVERSAL);
            }
            reversalInfos.add(DBModel2Object(reversalInfo));
        }

        return reversalInfos;
    }

    public static boolean isExistReversal(int hostType, int merchantIndex) {
        DBModelReversalInfo dbModelReversalInfo = new Select().from(DBModelReversalInfo.class)
                .where(DBModelReversalInfo_Table.hostType.eq(hostType))
                .and(DBModelReversalInfo_Table.merchantIndex.eq(merchantIndex))
                .querySingle();

        if (dbModelReversalInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    public static void clear() {
        SQLite.delete().from(DBModelReversalInfo.class).execute();
    }

    public static boolean isReversalTableEmpty() {
        long reversalNum = new Select(count()).from(DBModelReversalInfo.class).count();
        LogUtil.d(TAG, "isReversalTableEmpty reversalNum=" + reversalNum);
        if (reversalNum > 0) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isHostReversalEmpty(int hostType) {
        long recordNum = new Select(count()).from(DBModelReversalInfo.class)
                .where(DBModelReversalInfo_Table.hostType.eq(hostType)).count();
        LogUtil.d(TAG, "isHostReversalEmpty hostType=" + hostType);
        if (recordNum > 0) {
            return false;
        } else {
            return true;
        }
    }

    private static DBModelReversalInfo object2DBModel(RecordInfo reversalInfo) {
        DBModelReversalInfo dbModelReversalInfo = new DBModelReversalInfo();
        return (DBModelReversalInfo) DBFunRecordInfo.object2DBModel(dbModelReversalInfo, reversalInfo);
    }

    private static RecordInfo DBModel2Object(DBModelReversalInfo dbModelReversalInfo) {
        return DBFunRecordInfo.DBModel2Object(dbModelReversalInfo);
    }
}


