package com.vfi.android.data.database.itp;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.vfi.android.domain.entities.businessbeans.InstallmentPromo;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by RuihaoS on 2019/8/13.
 */
public class DBFunInstallmentPromo {
    private final static String TAG = TAGS.DataBase;

    public static void save(InstallmentPromo installmentPromo) {
        object2DBModel(installmentPromo).save();
    }

    public static void clear() {
        SQLite.delete().from(DBModelInstallmentPromo.class).execute();
    }

    public static List<InstallmentPromo> getAllEnabledInstalmentPromo(boolean isOnlyEnabled) {
        List<InstallmentPromo> installmentPromos = new ArrayList<>();

        List<DBModelInstallmentPromo> dbModelInstallmentPromos;
        if (isOnlyEnabled) {
            dbModelInstallmentPromos = new Select().from(DBModelInstallmentPromo.class)
                    .where(DBModelInstallmentPromo_Table.isEnablePromo.eq(true))
                    .queryList();
        } else {
            dbModelInstallmentPromos = new Select().from(DBModelInstallmentPromo.class)
                    .queryList();
        }

        if (dbModelInstallmentPromos == null) {
            LogUtil.d(TAG, "No enabled Installment promo found.");
        } else {
            LogUtil.d(TAG, "installmentPromo list size=" + installmentPromos.size());
            for (DBModelInstallmentPromo installmentPromo : dbModelInstallmentPromos) {
                installmentPromos.add(DBModel2Object(installmentPromo));
            }
        }

        return installmentPromos;
    }

    private static DBModelInstallmentPromo object2DBModel(InstallmentPromo installmentPromo) {
        DBModelInstallmentPromo dbModelInstallmentPromo = new DBModelInstallmentPromo();

        dbModelInstallmentPromo.setIndex(installmentPromo.getIndex());
        dbModelInstallmentPromo.setEnablePromo(installmentPromo.isEnablePromo());
        dbModelInstallmentPromo.setPromoLabel(installmentPromo.getPromoLabel());
        dbModelInstallmentPromo.setTermList(installmentPromo.getTermList());
        dbModelInstallmentPromo.setPromoCode(installmentPromo.getPromoCode());

        return dbModelInstallmentPromo;
    }

    private static InstallmentPromo DBModel2Object(DBModelInstallmentPromo dbModelInstallmentPromo) {
        InstallmentPromo installmentPromo = new InstallmentPromo();

        installmentPromo.setIndex(dbModelInstallmentPromo.getIndex());
        installmentPromo.setEnablePromo(dbModelInstallmentPromo.isEnablePromo());
        installmentPromo.setPromoLabel(dbModelInstallmentPromo.getPromoLabel());
        installmentPromo.setTermList(dbModelInstallmentPromo.getTermList());
        installmentPromo.setPromoCode(dbModelInstallmentPromo.getPromoCode());

        return installmentPromo;
    }
}
