package com.vfi.android.data.database.tst;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.vfi.android.domain.entities.businessbeans.SwitchParameter;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by RuihaoS on 2019/8/14.
 */
public class DBFunTransactionSwitch {
    private static final String TAG = TAGS.DataBase;

    public static void save(SwitchParameter switchParameter) {
        DBModelTransactionSwitch dbModelTransactionSwitch = object2DBModel(switchParameter);
        dbModelTransactionSwitch.save();
    }

    public static void clear() {
        SQLite.delete().from(DBModelTransactionSwitch.class).execute();
    }

    public static SwitchParameter getSwitchParameter(int transType) {
        DBModelTransactionSwitch dbModelTransactionSwitch = new Select().from(DBModelTransactionSwitch.class)
                .where(DBModelTransactionSwitch_Table.transType.eq(transType))
                .querySingle();

        if (dbModelTransactionSwitch == null) {
            LogUtil.d(TAG, "No switch parameter found, transType[" + transType + "]");
            return null;
        }

        return DBModel2Object(dbModelTransactionSwitch);
    }

    public static List<SwitchParameter> getAllSwitchParameters() {
        List<SwitchParameter> switchParameters = new ArrayList<>();
        List<DBModelTransactionSwitch> dbModelTransactionSwitchList = new Select().from(DBModelTransactionSwitch.class)
                .queryList();

        if (dbModelTransactionSwitchList == null) {
            LogUtil.d(TAG, "No switch parameter found");
            return switchParameters;
        }

        for (DBModelTransactionSwitch dbModelTransactionSwitch : dbModelTransactionSwitchList) {
            switchParameters.add(DBModel2Object(dbModelTransactionSwitch));
        }

        LogUtil.d(TAG, "switchParameters size=" + switchParameters.size());
        return switchParameters;
    }

    private static DBModelTransactionSwitch object2DBModel(SwitchParameter switchParameter) {
        DBModelTransactionSwitch dbModelTransactionSwitch = new DBModelTransactionSwitch();

        dbModelTransactionSwitch.setTransType(switchParameter.getTransType());
        dbModelTransactionSwitch.setEnableTrans(switchParameter.isEnableTrans());
        dbModelTransactionSwitch.setEnableEnterTip(switchParameter.isEnableEnterTip());
        dbModelTransactionSwitch.setEnableManual(switchParameter.isEnableManual());
        dbModelTransactionSwitch.setEnableSwipeCard(switchParameter.isEnableSwipeCard());
        dbModelTransactionSwitch.setEnableInsertCard(switchParameter.isEnableInsertCard());
        dbModelTransactionSwitch.setEnableTapCard(switchParameter.isEnableTapCard());
        dbModelTransactionSwitch.setEMVForceOnline(switchParameter.isEMVForceOnline());
        dbModelTransactionSwitch.setEnableCheckReversal(switchParameter.isEnableCheckReversal());
        dbModelTransactionSwitch.setEnableInputManagerPwd(switchParameter.isEnableInputManagerPwd());

        return dbModelTransactionSwitch;
    }

    private static SwitchParameter DBModel2Object(DBModelTransactionSwitch dbModelTransactionSwitch) {
        SwitchParameter switchParameter = new SwitchParameter();

        switchParameter.setTransType(dbModelTransactionSwitch.getTransType());
        switchParameter.setEnableTrans(dbModelTransactionSwitch.isEnableTrans());
        switchParameter.setEnableEnterTip(dbModelTransactionSwitch.isEnableEnterTip());
        switchParameter.setEnableManual(dbModelTransactionSwitch.isEnableManual());
        switchParameter.setEnableSwipeCard(dbModelTransactionSwitch.isEnableSwipeCard());
        switchParameter.setEnableInsertCard(dbModelTransactionSwitch.isEnableInsertCard());
        switchParameter.setEnableTapCard(dbModelTransactionSwitch.isEnableTapCard());
        switchParameter.setEMVForceOnline(dbModelTransactionSwitch.isEMVForceOnline());
        switchParameter.setEnableCheckReversal(dbModelTransactionSwitch.isEnableCheckReversal());
        switchParameter.setEnableInputManagerPwd(dbModelTransactionSwitch.isEnableInputManagerPwd());

        return switchParameter;
    }
}
