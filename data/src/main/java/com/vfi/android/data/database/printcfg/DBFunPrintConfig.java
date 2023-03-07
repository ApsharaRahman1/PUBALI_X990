package com.vfi.android.data.database.printcfg;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.vfi.android.domain.entities.businessbeans.PrintConfig;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RuihaoS on 2019/8/13.
 */
public class DBFunPrintConfig {
    private static final String TAG = TAGS.PRINT;

    public static void save( PrintConfig printConfig) {
        object2DBModel(printConfig).save();
    }

    public static void clear() {
        SQLite.delete().from(DBModelPrintConfig.class).execute();
    }

    public static PrintConfig get(int index) {
        LogUtil.d(TAG, "get print config, index=" + index);

        DBModelPrintConfig printConfig = new Select().from(DBModelPrintConfig.class)
                .where(DBModelPrintConfig_Table.index.eq(index))
                .querySingle();

        if (printConfig == null) {
            LogUtil.d(TAG, "Print config not found");
            return null;
        }

        return DBModel2Object(printConfig);
    }

    public static List<PrintConfig> getAllPrintConfigs() {
        LogUtil.d(TAG, "get all print config");
        List<PrintConfig> printConfigs = new ArrayList<>();

        List<DBModelPrintConfig> printConfigList = new Select().from(DBModelPrintConfig.class)
                .queryList();

        if (printConfigList == null) {
            LogUtil.d(TAG, "No print config found");
            return printConfigs;
        }

        for (DBModelPrintConfig printConfig : printConfigList) {
            printConfigs.add(DBModel2Object(printConfig));
        }

        return printConfigs;
    }

    private static DBModelPrintConfig object2DBModel(PrintConfig printConfig) {
        DBModelPrintConfig dbModelPrintConfig = new DBModelPrintConfig();
        dbModelPrintConfig.setIndex(printConfig.getIndex());
        dbModelPrintConfig.setPrintCustomerCopy(printConfig.isPrintCustomerCopy());
        dbModelPrintConfig.setPrintMerchantCopy(printConfig.isPrintMerchantCopy());
        dbModelPrintConfig.setPrintBankCopy(printConfig.isPrintBankCopy());
        dbModelPrintConfig.setHeader1(printConfig.getHeader1());
        dbModelPrintConfig.setHeader2(printConfig.getHeader2());
        dbModelPrintConfig.setHeader3(printConfig.getHeader3());
        dbModelPrintConfig.setHeader4(printConfig.getHeader4());
        dbModelPrintConfig.setHeader5(printConfig.getHeader5());
        dbModelPrintConfig.setHeader6(printConfig.getHeader6());
        dbModelPrintConfig.setFooter1(printConfig.getFooter1());
        dbModelPrintConfig.setFooter2(printConfig.getFooter2());
        dbModelPrintConfig.setFooter3(printConfig.getFooter3());
        dbModelPrintConfig.setFooter4(printConfig.getFooter4());
        dbModelPrintConfig.setFooter5(printConfig.getFooter5());
        dbModelPrintConfig.setFooter6(printConfig.getFooter6());

       return dbModelPrintConfig;
    }

    private static PrintConfig DBModel2Object(DBModelPrintConfig dbModel) {
        PrintConfig printConfig = new PrintConfig();
        printConfig.setIndex(dbModel.getIndex());
        printConfig.setPrintMerchantCopy(dbModel.isPrintMerchantCopy());
        printConfig.setPrintCustomerCopy(dbModel.isPrintCustomerCopy());
        printConfig.setPrintBankCopy(dbModel.isPrintBankCopy());
        printConfig.setHeader1(dbModel.getHeader1());
        printConfig.setHeader2(dbModel.getHeader2());
        printConfig.setHeader3(dbModel.getHeader3());
        printConfig.setHeader4(dbModel.getHeader4());
        printConfig.setHeader5(dbModel.getHeader5());
        printConfig.setHeader6(dbModel.getHeader6());
        printConfig.setFooter1(dbModel.getFooter1());
        printConfig.setFooter2(dbModel.getFooter2());
        printConfig.setFooter3(dbModel.getFooter3());
        printConfig.setFooter4(dbModel.getFooter4());
        printConfig.setFooter5(dbModel.getFooter5());
        printConfig.setFooter6(dbModel.getFooter6());

        return printConfig;
    }
}
