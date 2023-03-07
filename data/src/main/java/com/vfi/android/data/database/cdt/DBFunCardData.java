package com.vfi.android.data.database.cdt;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RuihaoS on 2019/8/13.
 */
public class DBFunCardData {
    private static final String TAG = TAGS.DataBase;

    public static final int CARD_BIN_STR_LEN = 10;

    public static void save(CardBinInfo cardBinInfo) {
        if (cardBinInfo == null) {
            return;
        }

        int panRangeLowLen = cardBinInfo.getPanLow().length();
        if (panRangeLowLen > CARD_BIN_STR_LEN) {
            cardBinInfo.setPanLow(cardBinInfo.getPanLow().substring(0, CARD_BIN_STR_LEN));
        } else if (panRangeLowLen < CARD_BIN_STR_LEN) {
            cardBinInfo.setPanLow(cardBinInfo.getPanLow() + "0000000000".substring(0, CARD_BIN_STR_LEN - panRangeLowLen));
        }

        int panRangeHighLen = cardBinInfo.getPanHigh().length();
        if (panRangeHighLen > CARD_BIN_STR_LEN) {
            cardBinInfo.setPanHigh(cardBinInfo.getPanHigh().substring(0, CARD_BIN_STR_LEN));
        } else if (panRangeHighLen < CARD_BIN_STR_LEN) {
            cardBinInfo.setPanHigh(cardBinInfo.getPanHigh() + "9999999999".substring(0, CARD_BIN_STR_LEN - panRangeHighLen));
        }

        if (get(cardBinInfo.getCardIndex()) != null) {
            object2DBModel(cardBinInfo).update();
        } else {
            object2DBModel(cardBinInfo).save();
        }
    }

    public static void clear() {
        SQLite.delete().from(DBModelCardData.class).execute();
    }

    public static DBModelCardData get(int cardIndex) {
        DBModelCardData dbModelCardData = new Select().from(DBModelCardData.class)
                .where(DBModelCardData_Table.cardIndex.is(cardIndex))
                .querySingle();

        return dbModelCardData;
    }

    /**
     * Find a card data according to card number
     *
     * @param pan card number
     * @return card table
     */
    public static List<CardBinInfo> getCardDataByPan(String pan) {
        LogUtil.d("pan = " + pan);
        List<CardBinInfo> cardBinInfoList = new ArrayList<>();

        if (TextUtils.isEmpty(pan) || pan.length() < CARD_BIN_STR_LEN) {
            return cardBinInfoList;
        }

        String cardBin = pan.substring(0, CARD_BIN_STR_LEN);

        List<DBModelCardData> dbModelCardDataList = new Select().from(DBModelCardData.class)
                .where(DBModelCardData_Table.panLow.lessThanOrEq(cardBin))
                .and(DBModelCardData_Table.panHigh.greaterThanOrEq(cardBin))
                .queryList();

        if (dbModelCardDataList == null) {
            LogUtil.i(TAG, "out of card bin range");
        } else {
            for (DBModelCardData dbModelCardData : dbModelCardDataList) {
                cardBinInfoList.add(DBModel2Object(dbModelCardData));
            }

            if (cardBinInfoList.size() > 1) {
                cardBinInfoList = getSmallPanRangeList(cardBinInfoList);
            }
        }

        return cardBinInfoList;
    }

    private static List<CardBinInfo> getSmallPanRangeList(List<CardBinInfo> cardIssuerInfos) {
        List<CardBinInfo> smallRangeList = new ArrayList<>();

        for (int i = 0; i < cardIssuerInfos.size(); i++) {
            CardBinInfo cardBinInfoA = cardIssuerInfos.get(i);
            boolean isLowRange = true;
            for (int j = 0; j < cardIssuerInfos.size(); j++) {
                if (j != i) {
                    CardBinInfo cardBinInfoB = cardIssuerInfos.get(j);
                    long panLowA = StringUtil.parseLong(cardBinInfoA.getPanLow(), 0);
                    long panHighA = StringUtil.parseLong(cardBinInfoA.getPanHigh(), 0);
                    long panLowB = StringUtil.parseLong(cardBinInfoB.getPanLow(), 0);
                    long panHighB = StringUtil.parseLong(cardBinInfoB.getPanHigh(), 0);

                    if ((panLowA < panLowB && panHighA >= panHighB)
                            || (panLowA <= panLowB && panHighA > panHighB)) {
                        isLowRange = false;
                        break;
                    }
                }
            }

            if (isLowRange) {
                smallRangeList.add(cardBinInfoA);
                LogUtil.d(TAG, "Add small range " + cardBinInfoA.getPanLow() + " - " + cardBinInfoA.getPanHigh());
            } else {
                LogUtil.d(TAG, "Remove high range " + cardBinInfoA.getPanLow() + " - " + cardBinInfoA.getPanHigh());
            }
        }

        return smallRangeList;
    }

    public static CardBinInfo getCardDataByIndex(int cardBinIndex) {
        DBModelCardData dbModelCardData = get(cardBinIndex);
        if (dbModelCardData != null) {
            return DBModel2Object(dbModelCardData);
        }

        return null;
    }


    public static List<CardBinInfo> getAllCardInfo() {
        List<DBModelCardData> dbModelCardDataList = new Select()
                .from(DBModelCardData.class)
                .orderBy(DBModelCardData_Table.cardIndex,true)
                .queryList();
        List<CardBinInfo> cardBinInfoList = new ArrayList<>();
        for(DBModelCardData dbModelCardData : dbModelCardDataList){
            cardBinInfoList.add(DBModel2Object(dbModelCardData));
        }
        return cardBinInfoList;
    }

    private static DBModelCardData object2DBModel(CardBinInfo cardBinInfo) {
        DBModelCardData dbModelCardData = new DBModelCardData();

        dbModelCardData.setCardIndex(cardBinInfo.getCardIndex());
        dbModelCardData.setHostIndexs(cardBinInfo.getHostIndexs());
        dbModelCardData.setCardName(cardBinInfo.getCardName());
        dbModelCardData.setCardType(cardBinInfo.getCardType());
        dbModelCardData.setCardLabel(cardBinInfo.getCardLabel());
        dbModelCardData.setCvv2Control(cardBinInfo.getCvv2Control());
        dbModelCardData.setCvv2Min(cardBinInfo.getCvv2Min());
        dbModelCardData.setCvv2Max(cardBinInfo.getCvv2Max());
        dbModelCardData.setPanLow(cardBinInfo.getPanLow());
        dbModelCardData.setPanHigh(cardBinInfo.getPanHigh());
        dbModelCardData.setIssueId(cardBinInfo.getIssueId());
        dbModelCardData.setAllowInstallment(cardBinInfo.isAllowInstallment());
        dbModelCardData.setAllowOfflineSale(cardBinInfo.isAllowOfflineSale());
        dbModelCardData.setAllowRefund(cardBinInfo.isAllowRefund());
        dbModelCardData.setEnableCheckLuhn(cardBinInfo.isEnableCheckLuhn());
        dbModelCardData.setEnableMultiCurrency(cardBinInfo.isEnableMultiCurrency());
        dbModelCardData.setSignLimitAmount(cardBinInfo.getSignLimitAmount());
        dbModelCardData.setPrintLimitAmount(cardBinInfo.getPrintLimitAmount());
        dbModelCardData.setTipPercent(cardBinInfo.getTipPercent());
        dbModelCardData.setAllowPinBypass(cardBinInfo.isAllowPinBypass());

        return dbModelCardData;
    }

    private static CardBinInfo DBModel2Object(DBModelCardData dbModelCardData) {
        CardBinInfo cardBinInfo = new CardBinInfo();

        cardBinInfo.setCardIndex(dbModelCardData.getCardIndex());
        cardBinInfo.setHostIndexs(dbModelCardData.getHostIndexs());
        cardBinInfo.setCardName(dbModelCardData.getCardName());
        cardBinInfo.setCardType(dbModelCardData.getCardType());
        cardBinInfo.setCardLabel(dbModelCardData.getCardLabel());
        cardBinInfo.setCvv2Control(dbModelCardData.getCvv2Control());
        cardBinInfo.setCvv2Max(dbModelCardData.getCvv2Max());
        cardBinInfo.setCvv2Min(dbModelCardData.getCvv2Min());
        cardBinInfo.setPanLow(dbModelCardData.getPanLow());
        cardBinInfo.setPanHigh(dbModelCardData.getPanHigh());
        cardBinInfo.setIssueId(dbModelCardData.getIssueId());
        cardBinInfo.setAllowInstallment(dbModelCardData.isAllowInstallment());
        cardBinInfo.setAllowOfflineSale(dbModelCardData.isAllowOfflineSale());
        cardBinInfo.setAllowRefund(dbModelCardData.isAllowRefund());
        cardBinInfo.setEnableCheckLuhn(dbModelCardData.isEnableCheckLuhn());
        cardBinInfo.setEnableMultiCurrency(dbModelCardData.isEnableMultiCurrency());
        cardBinInfo.setSignLimitAmount(dbModelCardData.getSignLimitAmount());
        cardBinInfo.setPrintLimitAmount(dbModelCardData.getPrintLimitAmount());
        cardBinInfo.setTipPercent(dbModelCardData.getTipPercent());
        cardBinInfo.setAllowPinBypass(dbModelCardData.isAllowPinBypass());

        return cardBinInfo;
    }
}
