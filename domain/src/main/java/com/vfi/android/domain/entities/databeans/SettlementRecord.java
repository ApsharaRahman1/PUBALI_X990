package com.vfi.android.domain.entities.databeans;

import com.vfi.android.domain.entities.consts.HostType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SettlementRecord {
    private final String TAG = TAGS.Settlement;
    private List<SettlementRecordItem> settlementRecordItems;

    public static final int SETTLE_STAUTS_SUCCESS         = 1;
    public static final int SETTLE_STAUTS_FAILED          = 2;
    public static final int SETTLE_STAUTS_BATCH_EMPTY     = 3;
    public static final int SETTLE_STAUTS_NO_NEED_SETTLE  = 4;

    public SettlementRecord() {
        settlementRecordItems = new ArrayList<>();
    }

    public long getTotalAmount() {
        long totalAmount = 0;
        Iterator<SettlementRecordItem> iterator = settlementRecordItems.iterator();
        while (iterator.hasNext()) {
            SettlementRecordItem item = iterator.next();
            if (item.isNeedSettlement() && item.isSettlementSuccess()) {
                totalAmount += item.getTotalAmount();
            }
        }

        return totalAmount;
    }

    public void addSettlementRecordItem(SettlementRecordItem settlementRecordItem) {
        if (settlementRecordItem != null) {
            settlementRecordItems.add(settlementRecordItem);
        }
    }

    public void removeSettlementRecordItem(int hostType) {
        Iterator<SettlementRecordItem> iterator = settlementRecordItems.iterator();
        while (iterator.hasNext()) {
            SettlementRecordItem settlementRecordItem = iterator.next();
            if (settlementRecordItem.getHostType() == hostType) {
                iterator.remove();
            }
        }
    }

    public long getSettlementItemTotalAmount(int hostType) {
        long totalAmount = 0;
        Iterator<SettlementRecordItem> iterator = settlementRecordItems.iterator();
        while (iterator.hasNext()) {
            SettlementRecordItem settlementRecordItem = iterator.next();
            if (settlementRecordItem.getHostType() == hostType) {
                totalAmount += settlementRecordItem.getTotalAmount();
            }
        }

        return totalAmount;
    }

    public void setNeedSettlementFlag(SettlementRecordItem item, boolean isEmptyBatchNeedSettlement) {
        if (item.isEmptyBatch(true)) {
            if (isEmptyBatchNeedSettlement) {
                item.setNeedSettlement(true);
            } else {
                item.setNeedSettlementButBatchEmpty(true);
                item.setNeedSettlement(false);
            }
        } else {
            item.setNeedSettlement(true);
        }
    }

    public void markSelectedSettlementRecordItem(int hostType, boolean isEmptyBatchNeedSettlement) {
        LogUtil.d(TAG, "markSelectedSettlementRecordItem hostType=" + hostType + " isEmptyBatchNeedSettlement=" + isEmptyBatchNeedSettlement);
        Iterator<SettlementRecordItem> iterator = settlementRecordItems.iterator();
        while (iterator.hasNext()) {
            SettlementRecordItem settlementRecordItem = iterator.next();
            if (settlementRecordItem.getHostType() == hostType) {
                setNeedSettlementFlag(settlementRecordItem, isEmptyBatchNeedSettlement);
            }
        }
    }

    public boolean isAnyItemNeedSettlement() {
        boolean isAnyItemNeedSettlement = false;
        Iterator<SettlementRecordItem> iterator = settlementRecordItems.iterator();
        while (iterator.hasNext()) {
            SettlementRecordItem settlementRecordItem = iterator.next();
            if (settlementRecordItem.isNeedSettlement()) {
                isAnyItemNeedSettlement = true;
                break;
            }
        }

        return isAnyItemNeedSettlement;
    }

    public boolean isAllSettlementSuccess() {
        boolean isAllSettlementSuccess = true;
        Iterator<SettlementRecordItem> iterator = settlementRecordItems.iterator();
        while (iterator.hasNext()) {
            SettlementRecordItem settlementRecordItem = iterator.next();
            if (!settlementRecordItem.isSettlementSuccess()) {
                LogUtil.d(TAG, "Host[" + HostType.toDebugString(settlementRecordItem.getHostType()) + "] settle failed.");
                isAllSettlementSuccess = false;
                break;
            }
        }

        LogUtil.d(TAG, "isAllSettlementSuccess=" + isAllSettlementSuccess);
        return isAllSettlementSuccess;
    }

    public boolean isAllSettlementFailed() {
        boolean isAllSettlementFailed = true;
        Iterator<SettlementRecordItem> iterator = settlementRecordItems.iterator();
        while (iterator.hasNext()) {
            SettlementRecordItem settlementRecordItem = iterator.next();
            if (settlementRecordItem.isSettlementSuccess()) {
                LogUtil.d(TAG, "Host[" + HostType.toDebugString(settlementRecordItem.getHostType()) + "] settle success.");
                isAllSettlementFailed = false;
                break;
            }
        }

        LogUtil.d(TAG, "isAllSettlementFailed=" + isAllSettlementFailed);
        return isAllSettlementFailed;
    }

    private boolean isSettlementItemNeedSettlement(SettlementRecordItem item) {
        if (item.isNeedSettlement()) {
            return true;
        } else {
            return false;
        }
    }

    public List<SettlementRecordItem> getSettlementRecordItems() {
        return settlementRecordItems;
    }

    public int getHostSettleStatus(int hostType) {
        int settleStatus = SETTLE_STAUTS_SUCCESS;

        boolean isFailed = false;
        boolean isNoNeedSettle = false;
        int merchantCount = 0;
        int emptyMerchantCount = 0;

        Iterator<SettlementRecordItem> iterator = settlementRecordItems.iterator();
        while (iterator.hasNext()) {
            SettlementRecordItem item = iterator.next();
            if (hostType != item.getHostType()) {
                continue;
            }

            merchantCount ++;

            LogUtil.d(TAG, "isNeedSettlementButBatchEmpty=" + item.isNeedSettlementButBatchEmpty());
            if (item.isNeedSettlementButBatchEmpty()) {
                emptyMerchantCount ++;
                continue;
            }

            if (item.isNeedSettlement()) {
                if (!item.isSettlementSuccess()) {
                    isFailed = true;
                }
            } else {
                isNoNeedSettle = true;
            }
        }

        LogUtil.d(TAG, "emptyMerchantCount=" + emptyMerchantCount + " merchantCount=" + merchantCount + " isNoNeedSettle=" + isNoNeedSettle);
        if ((emptyMerchantCount > 0 && merchantCount == emptyMerchantCount) || merchantCount == 0) {
            settleStatus = SETTLE_STAUTS_BATCH_EMPTY;
        } else if (isNoNeedSettle) {
            settleStatus = SETTLE_STAUTS_NO_NEED_SETTLE;
        } else if (isFailed) {
            settleStatus = SETTLE_STAUTS_FAILED;
        } else {
            settleStatus = SETTLE_STAUTS_SUCCESS;
        }

        LogUtil.d(TAG, "HostType[" + HostType.toDebugString(hostType) + "] Settlement Status=[" + settleStatus + "]");
        return settleStatus;
    }
}
