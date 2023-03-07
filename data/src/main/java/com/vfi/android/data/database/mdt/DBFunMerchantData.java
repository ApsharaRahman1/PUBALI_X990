package com.vfi.android.data.database.mdt;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by RuihaoS on 2019/8/13.
 */
public class DBFunMerchantData {
    private static final String TAG = TAGS.DataBase;

    public static void save(MerchantInfo merchantInfo) {
        LogUtil.d("DBFunMerchantData save excuted.");
        if (merchantInfo != null) {
            object2DBModel(merchantInfo).save();
        }
    }

    public static MerchantInfo get(int merchantIndex) {

        DBModelMerchantData dbModelMerchantData = new Select()
                .from(DBModelMerchantData.class)
                .where(DBModelMerchantData_Table.merchantIndex.eq(merchantIndex))
                .querySingle();

        if (dbModelMerchantData == null) {
            LogUtil.e(TAG, "Merchant[" + merchantIndex + "] not found");
            return null;
        }

        return DBModel2Object(dbModelMerchantData);
    }

    public static List<MerchantInfo> getMerchantInfoByIndexs(String merchantIndexs) {
        List<MerchantInfo> list= new ArrayList<>();
        String[] split = merchantIndexs.split(",");
        for (String merchantIndexStr :split) {
            int merchantIndex = StringUtil.parseInt(merchantIndexStr, -1);
            if (merchantIndex == -1) {
                continue;
            }

            MerchantInfo merchantInfo = get(merchantIndex);
            if (merchantInfo != null) {
                list.add(merchantInfo);
            }
        }
        return list;
    }

    public static List<MerchantInfo> getAllMerchants() {
        List<DBModelMerchantData> dbModelMerchantDatas = new Select()
                .from(DBModelMerchantData.class).queryList();

        List<MerchantInfo> merchantInfoList = new ArrayList<>();
        if (dbModelMerchantDatas == null) {
            LogUtil.d(TAG, "No merchant found");
            return merchantInfoList;
        }

        Iterator<DBModelMerchantData> iterator = dbModelMerchantDatas.iterator();
        while (iterator.hasNext()) {
            DBModelMerchantData merchantData = iterator.next();
            merchantInfoList.add(DBModel2Object(merchantData));
        }

        LogUtil.d(TAG, "MerchantInfo list size=" + merchantInfoList.size());
        return merchantInfoList;
    }

    public static void clear() {
        SQLite.delete().from(DBModelMerchantData.class).execute();
    }

    private static DBModelMerchantData object2DBModel(MerchantInfo merchantInfo) {
        DBModelMerchantData dbModelMerchantData = new DBModelMerchantData();

        dbModelMerchantData.setMerchantIndex(merchantInfo.getMerchantIndex());
        dbModelMerchantData.setMerchantName(merchantInfo.getMerchantName());
        dbModelMerchantData.setMerchantId(merchantInfo.getMerchantId());
        dbModelMerchantData.setTerminalId(merchantInfo.getTerminalId());
        dbModelMerchantData.setBatchNum(merchantInfo.getBatchNum());
        dbModelMerchantData.setTraceNum(merchantInfo.getTraceNum());
        dbModelMerchantData.setCurrencyIndex(merchantInfo.getCurrencyIndex());
        dbModelMerchantData.setEnableMultiCurrency(merchantInfo.isEnableMultiCurrency());
        dbModelMerchantData.setPrintParamIndex(merchantInfo.getPrintParamIndex());
        dbModelMerchantData.setAmountDigits(merchantInfo.getAmountDigits());
        dbModelMerchantData.setMinAmount(merchantInfo.getMinAmount());
        dbModelMerchantData.setMaxAmount(merchantInfo.getMaxAmount());
        dbModelMerchantData.setEnableTle(merchantInfo.isEnableTle());
        dbModelMerchantData.setTleIndex(merchantInfo.getTleIndex());

        return dbModelMerchantData;
    }

    private static MerchantInfo DBModel2Object(DBModelMerchantData dbModelMerchantData) {
        MerchantInfo merchantInfo = new MerchantInfo();

        merchantInfo.setMerchantIndex(dbModelMerchantData.getMerchantIndex());
        merchantInfo.setMerchantName(dbModelMerchantData.getMerchantName());
        merchantInfo.setMerchantId(dbModelMerchantData.getMerchantId());
        merchantInfo.setTerminalId(dbModelMerchantData.getTerminalId());
        merchantInfo.setBatchNum(dbModelMerchantData.getBatchNum());
        merchantInfo.setTraceNum(dbModelMerchantData.getTraceNum());
        merchantInfo.setCurrencyIndex(dbModelMerchantData.getCurrencyIndex());
        merchantInfo.setEnableMultiCurrency(dbModelMerchantData.isEnableMultiCurrency());
        merchantInfo.setPrintParamIndex(dbModelMerchantData.getPrintParamIndex());
        merchantInfo.setAmountDigits(dbModelMerchantData.getAmountDigits());
        merchantInfo.setMinAmount(dbModelMerchantData.getMinAmount());
        merchantInfo.setMaxAmount(dbModelMerchantData.getMaxAmount());
        merchantInfo.setEnableTle(dbModelMerchantData.isEnableTle());
        merchantInfo.setTleIndex(dbModelMerchantData.getTleIndex());

        return merchantInfo;
    }
}
