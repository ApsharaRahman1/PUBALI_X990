package com.vfi.android.data.database.nmx;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.vfi.android.domain.entities.businessbeans.TLEConfig;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RuihaoS on 2019/8/13.
 */
public class DBFunTLE {
    private static String TAG = TAGS.DataBase;

    public static void save(TLEConfig tleConfig) {
        if (tleConfig != null) {
            object2DBModel(tleConfig).save();
        }
    }

    public static void clear() {
        SQLite.delete().from(DBModelTLE.class).execute();
    }

    public static TLEConfig getTleConfig(int tleIndex) {
        DBModelTLE dbModelTLE = new Select().from(DBModelTLE.class)
                .where(DBModelTLE_Table.index.eq(tleIndex))
                .querySingle();

        if (dbModelTLE == null) {
            LogUtil.d(TAG, "No Tle config found.");
            return null;
        }

        return DBModel2Object(dbModelTLE);
    }

    public static List<TLEConfig> getAllTleConfigs() {
        List<DBModelTLE> dbModelTLEList = new Select().from(DBModelTLE.class)
                .queryList();

        List<TLEConfig> tleConfigList = new ArrayList<>();
        if (dbModelTLEList == null) {
            LogUtil.d(TAG, "No Tle config found.");
            return tleConfigList;
        }

        for (DBModelTLE modelTLE : dbModelTLEList) {
            tleConfigList.add(DBModel2Object(modelTLE));
        }
        return tleConfigList;
    }

    private static DBModelTLE object2DBModel(TLEConfig TLEConfig) {
        DBModelTLE dbModelTLE = new DBModelTLE();

        dbModelTLE.setIndex(TLEConfig.getIndex());
        dbModelTLE.setAppId(TLEConfig.getAppId());
        dbModelTLE.setKeyId(TLEConfig.getKeyId());
        dbModelTLE.setEncryptionAlgo(TLEConfig.getEncryptionAlgo());
        dbModelTLE.setMacAlgo(TLEConfig.getMacAlgo());
        dbModelTLE.setKeyManagement(TLEConfig.getKeyManagement());
        dbModelTLE.setDeviceModelCode(TLEConfig.getDeviceModelCode());
        dbModelTLE.setVersion(TLEConfig.getVersion());
        dbModelTLE.setFlag(TLEConfig.getFlag());
        dbModelTLE.setCmdCode(TLEConfig.getCmdCode());
        dbModelTLE.setRkiAcquirerId(TLEConfig.getRkiAcquirerId());
        dbModelTLE.setRkiKeyId(TLEConfig.getRkiKeyId());
        dbModelTLE.setRkiNII(TLEConfig.getRkiNII());
        dbModelTLE.setRkiTerminalType(TLEConfig.getRkiTerminalType());
        dbModelTLE.setRkiTPDU(TLEConfig.getRkiTPDU());
        dbModelTLE.setRkiVendorId(TLEConfig.getRkiVendorId());
        dbModelTLE.setDerivedKeys(TLEConfig.getDerivedKeys());
        dbModelTLE.setEncryptionType(TLEConfig.getEncryptionType());

        return dbModelTLE;
    }

    private static TLEConfig DBModel2Object(DBModelTLE dbModelTLE) {
        TLEConfig TLEConfig = new TLEConfig();

        TLEConfig.setIndex(dbModelTLE.getIndex());
        TLEConfig.setAppId(dbModelTLE.getAppId());
        TLEConfig.setKeyId(dbModelTLE.getKeyId());
        TLEConfig.setEncryptionAlgo(dbModelTLE.getEncryptionAlgo());
        TLEConfig.setMacAlgo(dbModelTLE.getMacAlgo());
        TLEConfig.setKeyManagement(dbModelTLE.getKeyManagement());
        TLEConfig.setDeviceModelCode(dbModelTLE.getDeviceModelCode());
        TLEConfig.setVersion(dbModelTLE.getVersion());
        TLEConfig.setFlag(dbModelTLE.getFlag());
        TLEConfig.setCmdCode(dbModelTLE.getCmdCode());
        TLEConfig.setRkiAcquirerId(dbModelTLE.getRkiAcquirerId());
        TLEConfig.setRkiKeyId(dbModelTLE.getRkiKeyId());
        TLEConfig.setRkiNII(dbModelTLE.getRkiNII());
        TLEConfig.setRkiTerminalType(dbModelTLE.getRkiTerminalType());
        TLEConfig.setRkiTPDU(dbModelTLE.getRkiTPDU());
        TLEConfig.setRkiVendorId(dbModelTLE.getRkiVendorId());
        TLEConfig.setDerivedKeys(dbModelTLE.getDerivedKeys());
        TLEConfig.setEncryptionType(dbModelTLE.getEncryptionType());

        return TLEConfig;
    }
}
