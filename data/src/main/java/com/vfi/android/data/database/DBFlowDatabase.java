package com.vfi.android.data.database;

import android.content.Context;
import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.vfi.android.data.BuildConfig;
import com.vfi.android.data.database.mdt.DBModelMerchantData;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;

/**
 * Created by chong.z on 2018/3/26.
 */

@Database(name = DBFlowDatabase.NAME, version = DBFlowDatabase.VERSION)
public class DBFlowDatabase {

    public static final String NAME = "DBFlowDatabase";

    public static final int VERSION = BuildConfig.dbVersion;

    public static void dbInit(@NonNull Context context) {
        LogUtil.d(TAGS.DataBase, "dbInit");
        FlowManager.init(new FlowConfig.Builder(context).build());
    }

    public static void setLogLevel() {
        FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
    }

    @Migration(version = VERSION, database = DBFlowDatabase.class)
    public static class Migration2UserData1 extends AlterTableMigration<DBModelMerchantData> {

        public Migration2UserData1(Class<DBModelMerchantData> table) {
            super(table);
            LogUtil.d(TAGS.DataBase, "DBVersion=" + VERSION);
        }

        //change model [2]
        @Override
        public void onPreMigrate() {
            LogUtil.d(TAGS.DataBase, "DBVersion=" + VERSION);
            addColumn(SQLiteType.INTEGER, "amountDigits"); //V2
            addColumn(SQLiteType.INTEGER, "minAmount"); //V2
            addColumn(SQLiteType.INTEGER, "maxAmount"); //V2
        }
    }
}