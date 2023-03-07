package com.vfi.android.data.database.cst;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.vfi.android.domain.entities.businessbeans.CurrencyInfo;

/**
 * Created by RuihaoS on 2019/8/13.
 */
public class DBFunCurrencySymbol {

    public static void save(DBModelCurrencySymbol DBModelCurrencySymbol) {
        DBModelCurrencySymbol.save();
    }

    public static void clear() {
        SQLite.delete().from(DBModelCurrencySymbol.class).execute();
    }

    private static DBModelCurrencySymbol object2DBModel(CurrencyInfo currencyInfo) {
        DBModelCurrencySymbol dbModelCurrencySymbol = new DBModelCurrencySymbol();

        dbModelCurrencySymbol.setIndex(currencyInfo.getIndex());
        dbModelCurrencySymbol.setCurrencySymbol(currencyInfo.getCurrencySymbol());
        dbModelCurrencySymbol.setCurrencyCode(Integer.parseInt("0050"));//apshara
       // dbModelCurrencySymbol.setCurrencyCode(currencyInfo.getCurrencyCode());
        dbModelCurrencySymbol.setProductCode(currencyInfo.getProductCode());

        return dbModelCurrencySymbol;
    }

    private static CurrencyInfo DBModel2Object(DBModelCurrencySymbol dbModelCurrencySymbol) {
        CurrencyInfo currencyInfo = new CurrencyInfo();

        currencyInfo.setIndex(dbModelCurrencySymbol.getIndex());
        currencyInfo.setCurrencySymbol(dbModelCurrencySymbol.getCurrencySymbol());
        currencyInfo.setCurrencyCode(dbModelCurrencySymbol.getCurrencyCode());
        currencyInfo.setProductCode(dbModelCurrencySymbol.getProductCode());

        return currencyInfo;
    }
}
