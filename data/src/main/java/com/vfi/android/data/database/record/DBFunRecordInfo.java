package com.vfi.android.data.database.record;

import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.vfi.android.data.database.reversal.DBFunReversalInfo;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.entities.consts.CardType;
import com.vfi.android.domain.entities.consts.CurrencyCode;
import com.vfi.android.domain.entities.consts.HostType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.databeans.HistoryConstraint;
import com.vfi.android.domain.entities.databeans.SettlePrintItem;
import com.vfi.android.domain.entities.databeans.SettlementRecord;
import com.vfi.android.domain.entities.databeans.SettlementRecordItem;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.raizlabs.android.dbflow.sql.language.Method.count;
import static com.raizlabs.android.dbflow.sql.language.Method.sum;

/**
 * Created by RuihaoS on 2019/8/13.
 */
public class DBFunRecordInfo {
    private static final String TAG = TAGS.DataBase;

    /**
     * insert a record
     *
     * @param recordInfo
     */
    public static void save(RecordInfo recordInfo) {
        if (recordInfo != null) {
            object2DBModel(recordInfo).save();
        }
    }

    public static void deleteOrigTransRecord(String traceNum, String invoiceNum) {
        LogUtil.d(TAG, "Delete Record traceNum[" + traceNum + "] invoiceNum[" + invoiceNum + "]");
        SQLite.delete().from(DBModelRecordInfo.class)
                .where(DBModelRecordInfo_Table.traceNum.eq(traceNum))
                .and(DBModelRecordInfo_Table.invoiceNum.eq(invoiceNum))
                .execute();
    }

    public static RecordInfo getRecordInfoByInvoiceNum(String invoiceNum) {
        LogUtil.d("invoiceNum = " + invoiceNum);
        if (invoiceNum == null) {
            return null;
        }

        DBModelRecordInfo dbModelRecordInfo = new Select().from(DBModelRecordInfo.class)
                .where(DBModelRecordInfo_Table.invoiceNum.eq(invoiceNum))
                .querySingle();

        if (dbModelRecordInfo == null) {
            LogUtil.d(TAG, "Record invoice[" + invoiceNum + "] not found");
            return null;
        }

        return DBModel2Object(dbModelRecordInfo);
    }

    public static List<RecordInfo> getBatchUploadRecordInfoList(int hostType, int merchantIndex) {
        List<Integer> batchUploadTransTypeList = new ArrayList<>();
        batchUploadTransTypeList.add(TransType.SALE);
        batchUploadTransTypeList.add(TransType.REFUND);
        batchUploadTransTypeList.add(TransType.OFFLINE);
        batchUploadTransTypeList.add(TransType.PREAUTH_COMP);
        batchUploadTransTypeList.add(TransType.TIP_ADJUST);
        batchUploadTransTypeList.add(TransType.INSTALLMENT);

        List<DBModelRecordInfo> dbModelRecordInfoList = new Select().from(DBModelRecordInfo.class)
                .where(DBModelRecordInfo_Table.hostType.eq(hostType))
                .and(DBModelRecordInfo_Table.merchantIndex.eq(merchantIndex))
                .and(DBModelRecordInfo_Table.transType.in(batchUploadTransTypeList))
                .orderBy(DBModelRecordInfo_Table.traceNum,true)
                .queryList();

        List<RecordInfo> recordInfoList = new ArrayList<>();
        if (dbModelRecordInfoList == null) {
            LogUtil.d(TAG, "No record found, hostType=[" + HostType.toDebugString(hostType) + " merchantIndex=" + merchantIndex);
            return recordInfoList;
        }

        for (DBModelRecordInfo recordInfo : dbModelRecordInfoList) {
            recordInfoList.add(DBModel2Object(recordInfo));
        }

        return recordInfoList;
    }

    public static List<RecordInfo> getUnUploadTCRecords() {
        List<DBModelRecordInfo> dbModelRecordInfoList = new Select().from(DBModelRecordInfo.class)
                .where(DBModelRecordInfo_Table.field55ForTC.isNotNull())
                .and(DBModelRecordInfo_Table.transType.notEq(TransType.VOID))
                .and(DBModelRecordInfo_Table.transType.notEq(TransType.INSTALLMENT))
                .and(DBModelRecordInfo_Table.transType.notEq(TransType.PREAUTH))
                .and(DBModelRecordInfo_Table.isTcUploaded.eq(false))
                .queryList();

        List<RecordInfo> recordInfoList = new ArrayList<>();
        if (dbModelRecordInfoList == null) {
            LogUtil.d(TAG, "No un-upload TC record found");
            return recordInfoList;
        }

        for (DBModelRecordInfo recordInfo : dbModelRecordInfoList) {
            recordInfoList.add(DBModel2Object(recordInfo));
        }

        return recordInfoList;
    }

    public static List<RecordInfo> getUnUploadOfflineRecords() {
        List<DBModelRecordInfo> dbModelRecordInfoList = new Select().from(DBModelRecordInfo.class)
                .where(DBModelRecordInfo_Table.transType.eq(TransType.OFFLINE))
                .and(DBModelRecordInfo_Table.isOfflineTransUploaded.eq(false))
                .queryList();

        List<RecordInfo> recordInfoList = new ArrayList<>();
        if (dbModelRecordInfoList == null) {
            LogUtil.d(TAG, "No un-upload offline record found");
            return recordInfoList;
        }

        for (DBModelRecordInfo recordInfo : dbModelRecordInfoList) {
            recordInfoList.add(DBModel2Object(recordInfo));
        }

        return recordInfoList;
    }

    public static List<RecordInfo> getUnUploadTipAdjustRecords() {
        List<DBModelRecordInfo> dbModelRecordInfoList = new Select().from(DBModelRecordInfo.class)
                .where(DBModelRecordInfo_Table.tipAdjustTimes.greaterThan(0))
                .and(DBModelRecordInfo_Table.isOfflineTransUploaded.eq(false))
                .queryList();

        List<RecordInfo> recordInfoList = new ArrayList<>();
        if (dbModelRecordInfoList == null) {
            LogUtil.d(TAG, "No un-upload TipAdjust record found");
            return recordInfoList;
        }

        for (DBModelRecordInfo recordInfo : dbModelRecordInfoList) {
            recordInfoList.add(DBModel2Object(recordInfo));
        }

        return recordInfoList;
    }

    public static List<RecordInfo> getRecordInfoList(int hostType, int merchantIndex) {
        List<DBModelRecordInfo> dbModelRecordInfoList = new Select().from(DBModelRecordInfo.class)
                .where(DBModelRecordInfo_Table.hostType.eq(hostType))
                .and(DBModelRecordInfo_Table.merchantIndex.eq(merchantIndex))
                .queryList();

        List<RecordInfo> recordInfoList = new ArrayList<>();
        if (dbModelRecordInfoList == null) {
            LogUtil.d(TAG, "No record found, hostType=[" + HostType.toDebugString(hostType) + " merchantIndex=" + merchantIndex);
            return recordInfoList;
        }

        for (DBModelRecordInfo recordInfo : dbModelRecordInfoList) {
            recordInfoList.add(DBModel2Object(recordInfo));
        }

        return recordInfoList;
    }

    public static List<RecordInfo> getRecordInfoListByConstraint(HistoryConstraint constraint) {
        List<RecordInfo> recordInfos = new ArrayList<>();
        List<DBModelRecordInfo> dbModelRecordInfos = new ArrayList<>();
        if (constraint.getConstraintType() == HistoryConstraint.CONSTRAINT_INVOICE_NUM) {
            LogUtil.d("invoiceNum = " + constraint.getInvoiceNum());
            dbModelRecordInfos = new Select().from(DBModelRecordInfo.class)
                    .where(DBModelRecordInfo_Table.invoiceNum.eq(constraint.getInvoiceNum()))
                    .queryList();
        } else if (constraint.getConstraintType() == HistoryConstraint.CONSTRAINT_DATE){
            LogUtil.d("date = " +constraint.getDate());
            dbModelRecordInfos = new Select().from(DBModelRecordInfo.class)
                    .where(DBModelRecordInfo_Table.transDate.eq(constraint.getDate()))
                    .orderBy(DBModelRecordInfo_Table.invoiceNum, false)
                    .queryList();
        } else {
            dbModelRecordInfos = new Select().from(DBModelRecordInfo.class)
                    .orderBy(DBModelRecordInfo_Table.invoiceNum, false)
                    .queryList();
        }

        if (dbModelRecordInfos != null) {
            for (DBModelRecordInfo dbModelRecordInfo : dbModelRecordInfos) {
                recordInfos.add(DBModel2Object(dbModelRecordInfo));
            }
        }

        LogUtil.d("recordInfos size = " + recordInfos.size());
        return recordInfos;
    }

    public static SettlementRecord getSettlementInfo() {
        SettlementRecord settlementRecord = new SettlementRecord();
        Map<String, SettlementRecordItem> map = new HashMap<>();

        List<Integer> saleRelatedTransList = TransType.getSettlementSaleRelatedTransList();
        List<SettleQueryModel> saleRelatedSettleQueryModels =  SQLite.select(count().as("totalCount"),
                sum(DBModelRecordInfo_Table.amount).as("totalAmount"),
                sum(DBModelRecordInfo_Table.tipAmount).as("totalTipAmount"),
                DBModelRecordInfo_Table.hostType.as("hostType"),
                DBModelRecordInfo_Table.batchNo.as("batchNo"),
                DBModelRecordInfo_Table.merchantIndex.as("merchantIndex"))
                .from(DBModelRecordInfo.class)
                .where(DBModelRecordInfo_Table.transType.in(saleRelatedTransList))
                .groupBy(DBModelRecordInfo_Table.hostType, DBModelRecordInfo_Table.merchantIndex)
                .queryCustomList(SettleQueryModel.class);

        Iterator<SettleQueryModel> iterator = saleRelatedSettleQueryModels.iterator();
        while (iterator.hasNext()) {
            SettleQueryModel queryModel = iterator.next();
            String key = "HostType" + queryModel.getHostType() + "Merchant" + queryModel.getMerchantIndex();
            SettlementRecordItem item = null;
            if (!map.containsKey(key)) {
                item = new SettlementRecordItem(queryModel.getHostType(), queryModel.getMerchantIndex(), queryModel.getBatchNo());
                map.put(key, item);
            } else {
                item = map.get(key);
            }

            item.setSaleTotalCount(queryModel.getTotalCount());
            item.setSaleTotalAmount(queryModel.getTotalAmount() + queryModel.getTotalTipAmount());
            item.setHostType(queryModel.getHostType());
            item.setMerchantIndex(queryModel.getMerchantIndex());
            LogUtil.d(TAG, "HostType[" + HostType.toDebugString(item.getHostType()) + "] MerchantIndex[" + item.getMerchantIndex() + "] TotalSaleCount[" + item.getSaleTotalCount() + "] TotalSaleAmount[" + item.getSaleTotalAmount() + "]");
        }

        List<Integer> voidRelatedTransList = Arrays.asList(TransType.VOID);
        List<SettleQueryModel> voidRelatedSettleQueryModels =  SQLite.select(count().as("totalCount"),
                sum(DBModelRecordInfo_Table.amount).as("totalAmount"),
                sum(DBModelRecordInfo_Table.tipAmount).as("totalTipAmount"),
                DBModelRecordInfo_Table.hostType.as("hostType"),
                DBModelRecordInfo_Table.batchNo.as("batchNo"),
                DBModelRecordInfo_Table.merchantIndex.as("merchantIndex"))
                .from(DBModelRecordInfo.class)
                .where(DBModelRecordInfo_Table.transType.in(voidRelatedTransList))
                .groupBy(DBModelRecordInfo_Table.hostType, DBModelRecordInfo_Table.merchantIndex)
                .queryCustomList(SettleQueryModel.class);

        iterator = voidRelatedSettleQueryModels.iterator();
        while (iterator.hasNext()) {
            SettleQueryModel queryModel = iterator.next();
            String key = "HostType" + queryModel.getHostType() + "Merchant" + queryModel.getMerchantIndex();
            SettlementRecordItem item = null;
            if (!map.containsKey(key)) {
                item = new SettlementRecordItem(queryModel.getHostType(), queryModel.getMerchantIndex(), queryModel.getBatchNo());
                map.put(key, item);
            } else {
                item = map.get(key);
            }

            item.setVoidTotalCount(queryModel.getTotalCount());
            item.setVoidTotalAmount(queryModel.getTotalAmount() + queryModel.getTotalTipAmount());
            item.setHostType(queryModel.getHostType());
            item.setMerchantIndex(queryModel.getMerchantIndex());
            LogUtil.d(TAG, "HostType[" + HostType.toDebugString(item.getHostType()) + "] MerchantIndex[" + item.getMerchantIndex() + "] TotalVoidCount[" + item.getVoidTotalCount() + "] TotalVoidAmount[" + item.getVoidTotalAmount() + "]");
        }

        List<RecordInfo> reversals = DBFunReversalInfo.getAllReversalRecords();
        Iterator<RecordInfo> recordInfoIterator = reversals.iterator();
        while (recordInfoIterator.hasNext()) {
            RecordInfo recordInfo = recordInfoIterator.next();
            String key = "HostType" + recordInfo.getHostType() + "Merchant" + recordInfo.getMerchantIndex();
            SettlementRecordItem item = null;
            if (!map.containsKey(key)) {
                item = new SettlementRecordItem(recordInfo.getHostType(), recordInfo.getMerchantIndex(), recordInfo.getBatchNo());
                map.put(key, item);
            } else {
                item = map.get(key);
            }

            item.setExistReversal(true);
            item.setHostType(recordInfo.getHostType());
            item.setMerchantIndex(recordInfo.getMerchantIndex());
            LogUtil.d(TAG, "HostType[" + HostType.toDebugString(item.getHostType()) + "] MerchantIndex[" + item.getMerchantIndex() + "] isExistReversal[" + item.isExistReversal() + "]");
        }

        Iterator<Map.Entry<String, SettlementRecordItem>> itemIterator = map.entrySet().iterator();
        while (itemIterator.hasNext()) {
            SettlementRecordItem item = itemIterator.next().getValue();
            List<SettlePrintItem> settlePrintItems = getSettlePrintItems(item.getHostType(), item.getMerchantIndex());
            item.setSettlePrintItemList(settlePrintItems);
            item.setExistReversal(DBFunReversalInfo.isExistReversal(item.getHostType(), item.getMerchantIndex()));
            settlementRecord.addSettlementRecordItem(item);
        }

        return settlementRecord;
    }

    public static void clearBatch(int hostType, int merchantIndex) {
        LogUtil.d(TAG, "Clear batch hostType[" + hostType + "] merchantIndex[" + merchantIndex + "]");
        SQLite.delete().from(DBModelRecordInfo.class)
                .where(DBModelRecordInfo_Table.hostType.eq(hostType))
                .and(DBModelRecordInfo_Table.merchantIndex.eq(merchantIndex))
                .execute();
    }

    public static void clearAllRecords() {
        LogUtil.d(TAG, "Clear All Record");
        SQLite.delete().from(DBModelRecordInfo.class)
                .execute();
    }

    public static void markOfflineTransUploaded(String traceNum, String invoiceNum) {
        DBModelRecordInfo dbModelRecordInfo = new Select().from(DBModelRecordInfo.class)
                .where(DBModelRecordInfo_Table.traceNum.eq(traceNum))
                .and(DBModelRecordInfo_Table.invoiceNum.eq(invoiceNum))
                .querySingle();

        if (dbModelRecordInfo != null) {
            dbModelRecordInfo.setOfflineTransUploaded(true);
            dbModelRecordInfo.update();
        } else {
            LogUtil.d(TAG, "Missing record, should never be here, markOfflineTransUploaded");
            throw new RuntimeException("Missing record");
        }
    }

    public static void markTCUploaded(String traceNum, String invoiceNum) {
        DBModelRecordInfo dbModelRecordInfo = new Select().from(DBModelRecordInfo.class)
                .where(DBModelRecordInfo_Table.traceNum.eq(traceNum))
                .and(DBModelRecordInfo_Table.invoiceNum.eq(invoiceNum))
                .querySingle();

        if (dbModelRecordInfo != null) {
            dbModelRecordInfo.setTcUploaded(true);
            dbModelRecordInfo.update();
        } else {
            LogUtil.d(TAG, "Missing record, should never be here, markTCUploaded");
            throw new RuntimeException("Missing record");
        }
    }

    public static boolean isRecordTableEmpty() {
        long recordNum = new Select(count()).from(DBModelRecordInfo.class)
                .where(DBModelRecordInfo_Table.transType.notEq(TransType.PREAUTH)).count();
        LogUtil.d(TAG, "isRecordTableEmpty recordNum=" + recordNum);
        if (recordNum > 0) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isHostBatchEmpty(int hostType) {
        long recordNum = new Select(count()).from(DBModelRecordInfo.class)
                .where(DBModelRecordInfo_Table.hostType.eq(hostType)).count();
        LogUtil.d(TAG, "isHostBatchEmpty hostType=" + hostType);
        if (recordNum > 0) {
            return false;
        } else {
            return true;
        }
    }



    private static List<SettlePrintItem> getSettlePrintItems(int hostType, int merchantIndex) {
        List<RecordInfo> recordInfoList = getRecordInfoList(hostType, merchantIndex);
        List<SettlePrintItem> settlePrintItems = new ArrayList<>();

        for (RecordInfo recordInfo : recordInfoList) {
            SettlePrintItem settlePrintItem = new SettlePrintItem();
            int recordType;
            int transType = recordInfo.getTransType();
            int originTransType = recordInfo.getVoidOrgTransType();
            if (TransType.isSettlementSaleRelatedTrans(transType)) {
                recordType = SettlePrintItem.SALE;
            } else if (transType == TransType.VOID && TransType.isSettlementSaleRelatedTrans(originTransType)) {
                recordType = SettlePrintItem.VOID_SALE;
            } else {
                LogUtil.d(TAGS.PRINT, "###########Skip transType[" + transType + "] originTransType[" + originTransType + "] record.");
                continue;
            }
            settlePrintItem.setRecordType(recordType);
            int cardEntryMode = CardEntryMode.IC;
            String posEntryMode = recordInfo.getPosEntryMode();
            if (posEntryMode != null && posEntryMode.length() >= 2) {
                posEntryMode = posEntryMode.substring(0, 2);
                switch (posEntryMode) {
                    case "80":
                    case "02":
                        cardEntryMode = CardEntryMode.MAG;
                        break;
                    case "91":
                    case "07":
                        cardEntryMode = CardEntryMode.RF;
                        break;
                    case "05":
                        cardEntryMode = CardEntryMode.IC;
                        break;
                    case "01":
                        cardEntryMode = CardEntryMode.MANUAL;
                        break;
                }
            }
            settlePrintItem.setCardEntryMode(cardEntryMode);
            settlePrintItem.setCardType(recordInfo.getCardType());
            settlePrintItem.setTotalAmountLong(StringUtil.parseLong(recordInfo.getAmount(), 0) + StringUtil.parseLong(recordInfo.getTipAmount(), 0));
            settlePrintItem.setCardTypeAbbr(CardType.toCardTypeAbbrText(recordInfo.getCardType()));
            settlePrintItem.setPan(recordInfo.getPan());
            settlePrintItem.setExpireDate(recordInfo.getCardExpiryDate());
            settlePrintItem.setInvoiceNum(recordInfo.getInvoiceNum());
            String transactionText = TransType.getPrintSettleTransText(transType, recordInfo.getVoidOrgTransType(), recordInfo.getTipAdjOrgTransType());
            settlePrintItem.setTransaction(transactionText);
            settlePrintItem.setCurrencyAmountText(CurrencyCode.toEngString(recordInfo.getCurrencyCode()) + " " + StringUtil.formatAmount("" + settlePrintItem.getTotalAmountLong()));
            settlePrintItem.setApprovalCode(recordInfo.getAuthCode());
            settlePrintItem.setDateTime(StringUtil.formatDate(recordInfo.getTransDate()) + " " + StringUtil.formatTime(recordInfo.getTransTime()));

            settlePrintItems.add(settlePrintItem);
        }

        return settlePrintItems;
    }

    private static DBModelRecordInfo object2DBModel(RecordInfo recordInfo) {
        DBModelRecordInfo dbModelRecordInfo = new DBModelRecordInfo();
        return object2DBModel(dbModelRecordInfo, recordInfo);
    }

    public static DBModelRecordInfo object2DBModel(DBModelRecordInfo dbModelRecordInfo, RecordInfo recordInfo) {

        dbModelRecordInfo.setTransType(recordInfo.getTransType());
        dbModelRecordInfo.setVoidOrgTransType(recordInfo.getVoidOrgTransType());
        dbModelRecordInfo.setTipAdjOrgTransType(recordInfo.getTipAdjOrgTransType());
        dbModelRecordInfo.setReversalOrgTransType(recordInfo.getReversalOrgTransType());
        dbModelRecordInfo.setPan(recordInfo.getPan());
        dbModelRecordInfo.setProcessCode(recordInfo.getProcessCode());
        dbModelRecordInfo.setAmount(recordInfo.getAmount());
        dbModelRecordInfo.setTipAmount(recordInfo.getTipAmount());
        dbModelRecordInfo.setCurrencyAmount(recordInfo.getCurrencyAmount());
        dbModelRecordInfo.setTraceNum(recordInfo.getTraceNum());
        dbModelRecordInfo.setVoidOrgTraceNum(recordInfo.getVoidOrgTraceNum());
        dbModelRecordInfo.setTipAdjOrgTraceNum(recordInfo.getTipAdjOrgTraceNum());
        dbModelRecordInfo.setHostType(recordInfo.getHostType());
        dbModelRecordInfo.setCardBinIndex(recordInfo.getCardBinIndex());
        dbModelRecordInfo.setMerchantIndex(recordInfo.getMerchantIndex());
        dbModelRecordInfo.setCurrencyIndex(recordInfo.getCurrencyIndex());
        dbModelRecordInfo.setInvoiceNum(recordInfo.getInvoiceNum());
        dbModelRecordInfo.setOrgInvoiceNum(recordInfo.getOrgInvoiceNum());
        dbModelRecordInfo.setTransTime(recordInfo.getTransTime());
        dbModelRecordInfo.setTransDate(recordInfo.getTransDate());
        dbModelRecordInfo.setOrgTransDate(recordInfo.getOrgTransDate());
        dbModelRecordInfo.setCardExpiryDate(recordInfo.getCardExpiryDate());
        dbModelRecordInfo.setBatchSettleDate(recordInfo.getBatchSettleDate());
        dbModelRecordInfo.setCardSequenceNum(recordInfo.getCardSequenceNum());
        dbModelRecordInfo.setPosEntryMode(recordInfo.getPosEntryMode());
        dbModelRecordInfo.setNii(recordInfo.getNii());
        dbModelRecordInfo.setTrack2(recordInfo.getTrack2());
        dbModelRecordInfo.setTrack3(recordInfo.getTrack3());
        dbModelRecordInfo.setRefNo(recordInfo.getRefNo());
        dbModelRecordInfo.setOrgRefNo(recordInfo.getOrgRefNo());
        dbModelRecordInfo.setAuthCode(recordInfo.getAuthCode());
        dbModelRecordInfo.setOrgAuthCode(recordInfo.getOrgAuthCode());
        dbModelRecordInfo.setRspCode(recordInfo.getRspCode());
        dbModelRecordInfo.setTerminalId(recordInfo.getTerminalId());
        dbModelRecordInfo.setMerchantId(recordInfo.getMerchantId());
        dbModelRecordInfo.setField55(recordInfo.getField55());
        dbModelRecordInfo.setField55ForTC(recordInfo.getField55ForTC());
        dbModelRecordInfo.setBatchNo(recordInfo.getBatchNo());
        dbModelRecordInfo.setOrgBatchNo(recordInfo.getOrgBatchNo());
        dbModelRecordInfo.setCardOrgCode(recordInfo.getCardOrgCode());
        dbModelRecordInfo.setHostNote(recordInfo.getHostNote());
        dbModelRecordInfo.setCardHolderName(recordInfo.getCardHolderName());
        dbModelRecordInfo.setAppLabel(recordInfo.getAppLabel());
        dbModelRecordInfo.setAID(recordInfo.getAID());
        dbModelRecordInfo.setCurrencyCode("0050");//apshara
       // dbModelRecordInfo.setCurrencyCode(recordInfo.getCurrencyCode());
        dbModelRecordInfo.setFallBack(recordInfo.isFallBack());
        dbModelRecordInfo.setCvv2(recordInfo.getCvv2());
        dbModelRecordInfo.setCardType(recordInfo.getCardType());
        dbModelRecordInfo.setTc(recordInfo.getTc());
        dbModelRecordInfo.setTcUploaded(recordInfo.isTcUploaded());
        dbModelRecordInfo.setEmvOfflineApproval(recordInfo.isEmvOfflineApproval());
        dbModelRecordInfo.setOfflineTransUploaded(recordInfo.isOfflineTransUploaded());
        dbModelRecordInfo.setTipAdjustTimes(recordInfo.getTipAdjustTimes());
        dbModelRecordInfo.setCvmResult(recordInfo.getCvmResult());
        dbModelRecordInfo.setSignData(recordInfo.getSignData());
        dbModelRecordInfo.setPromoCode(recordInfo.getPromoCode());
        dbModelRecordInfo.setInstallmentTerm(recordInfo.getInstallmentTerm());
        dbModelRecordInfo.setPromoLabel(recordInfo.getPromoLabel());
        dbModelRecordInfo.setInstallmentFactorRate(recordInfo.getInstallmentFactorRate());
        dbModelRecordInfo.setTotalAmountDue(recordInfo.getTotalAmountDue());
        dbModelRecordInfo.setMonthlyDue(recordInfo.getMonthlyDue());

        return dbModelRecordInfo;
    }

    public static RecordInfo DBModel2Object(DBModelRecordInfo dbModelRecordInfo) {
        RecordInfo recordInfo = new RecordInfo();

        recordInfo.setTransType(dbModelRecordInfo.getTransType());
        recordInfo.setVoidOrgTransType(dbModelRecordInfo.getVoidOrgTransType());
        recordInfo.setTipAdjOrgTransType(dbModelRecordInfo.getTipAdjOrgTransType());
        recordInfo.setReversalOrgTransType(dbModelRecordInfo.getReversalOrgTransType());
        recordInfo.setPan(dbModelRecordInfo.getPan());
        recordInfo.setProcessCode(dbModelRecordInfo.getProcessCode());
        recordInfo.setAmount(dbModelRecordInfo.getAmount());
        recordInfo.setTipAmount(dbModelRecordInfo.getTipAmount());
        recordInfo.setCurrencyAmount(dbModelRecordInfo.getCurrencyAmount());
        recordInfo.setTraceNum(dbModelRecordInfo.getTraceNum());
        recordInfo.setVoidOrgTraceNum(dbModelRecordInfo.getVoidOrgTraceNum());
        recordInfo.setTipAdjOrgTraceNum(dbModelRecordInfo.getTipAdjOrgTraceNum());
        recordInfo.setHostType(dbModelRecordInfo.getHostType());
        recordInfo.setCardBinIndex(dbModelRecordInfo.getCardBinIndex());
        recordInfo.setMerchantIndex(dbModelRecordInfo.getMerchantIndex());
        recordInfo.setCurrencyIndex(dbModelRecordInfo.getCurrencyIndex());
        recordInfo.setInvoiceNum(dbModelRecordInfo.getInvoiceNum());
        recordInfo.setOrgInvoiceNum(dbModelRecordInfo.getOrgInvoiceNum());
        recordInfo.setTransTime(dbModelRecordInfo.getTransTime());
        recordInfo.setTransDate(dbModelRecordInfo.getTransDate());
        recordInfo.setOrgTransDate(dbModelRecordInfo.getOrgTransDate());
        recordInfo.setCardExpiryDate(dbModelRecordInfo.getCardExpiryDate());
        recordInfo.setBatchSettleDate(dbModelRecordInfo.getBatchSettleDate());
        recordInfo.setCardSequenceNum(dbModelRecordInfo.getCardSequenceNum());
        recordInfo.setPosEntryMode(dbModelRecordInfo.getPosEntryMode());
        recordInfo.setNii(dbModelRecordInfo.getNii());
        recordInfo.setTrack2(dbModelRecordInfo.getTrack2());
        recordInfo.setTrack3(dbModelRecordInfo.getTrack3());
        recordInfo.setRefNo(dbModelRecordInfo.getRefNo());
        recordInfo.setOrgRefNo(dbModelRecordInfo.getOrgRefNo());
        recordInfo.setAuthCode(dbModelRecordInfo.getAuthCode());
        recordInfo.setOrgAuthCode(dbModelRecordInfo.getOrgAuthCode());
        recordInfo.setRspCode(dbModelRecordInfo.getRspCode());
        recordInfo.setTerminalId(dbModelRecordInfo.getTerminalId());
        recordInfo.setMerchantId(dbModelRecordInfo.getMerchantId());
        recordInfo.setField55(dbModelRecordInfo.getField55());
        recordInfo.setField55ForTC(dbModelRecordInfo.getField55ForTC());
        recordInfo.setBatchNo(dbModelRecordInfo.getBatchNo());
        recordInfo.setOrgBatchNo(dbModelRecordInfo.getOrgBatchNo());
        recordInfo.setCardOrgCode(dbModelRecordInfo.getCardOrgCode());
        recordInfo.setHostNote(dbModelRecordInfo.getHostNote());
        recordInfo.setCardHolderName(dbModelRecordInfo.getCardHolderName());
        recordInfo.setAppLabel(dbModelRecordInfo.getAppLabel());
        recordInfo.setAID(dbModelRecordInfo.getAID());
//        recordInfo.setCurrencyCode(dbModelRecordInfo.getCurrencyCode());
        recordInfo.setCurrencyCode("0050");//apshara
        recordInfo.setFallBack(dbModelRecordInfo.isFallBack());
        recordInfo.setCvv2(dbModelRecordInfo.getCvv2());
        recordInfo.setCardType(dbModelRecordInfo.getCardType());
        recordInfo.setTc(dbModelRecordInfo.getTc());
        recordInfo.setTcUploaded(dbModelRecordInfo.isTcUploaded());
        recordInfo.setEmvOfflineApproval(dbModelRecordInfo.isEmvOfflineApproval());
        recordInfo.setOfflineTransUploaded(dbModelRecordInfo.isOfflineTransUploaded());
        recordInfo.setTipAdjustTimes(dbModelRecordInfo.getTipAdjustTimes());
        recordInfo.setCvmResult(dbModelRecordInfo.getCvmResult());
        recordInfo.setSignData(dbModelRecordInfo.getSignData());
        recordInfo.setPromoCode(dbModelRecordInfo.getPromoCode());
        recordInfo.setInstallmentTerm(dbModelRecordInfo.getInstallmentTerm());
        recordInfo.setPromoLabel(dbModelRecordInfo.getPromoLabel());
        recordInfo.setInstallmentFactorRate(dbModelRecordInfo.getInstallmentFactorRate());
        recordInfo.setTotalAmountDue(dbModelRecordInfo.getTotalAmountDue());
        recordInfo.setMonthlyDue(dbModelRecordInfo.getMonthlyDue());
        return recordInfo;
    }
}
