package com.vfi.android.domain.entities.databeans;

import com.vfi.android.domain.entities.consts.HostType;

public class SettlementStatus {
    private int currentSettlementHostType;
    private int currentSettleMerchantIndex;

    private int currentHostSettleStatus;
    public static final int START           = 1;
    public static final int BATCH_UPLOAD    = 2;
    public static final int SUCCESS         = 3;
    public static final int FAILED          = 4;

    public SettlementStatus(int hostType, int merchantIndex, int settleStatus) {
        this.currentSettlementHostType = hostType;
        this.currentSettleMerchantIndex = merchantIndex;
        this.currentHostSettleStatus = settleStatus;
    }

    public int getCurrentSettlementHostType() {
        return currentSettlementHostType;
    }

    public int getCurrentHostSettleStatus() {
        return currentHostSettleStatus;
    }

    public String toDebugStatusString() {
        switch (currentHostSettleStatus) {
            case START:
                return "START";
            case BATCH_UPLOAD:
                return "BATCH_UPLOAD";
            case SUCCESS:
                return "SUCCESS";
            case FAILED:
                return "FAILED";
        }

        return "Unknown status[" + currentHostSettleStatus + "]";
    }

    public String toDebugHostString() {
        return HostType.toDebugString(currentSettlementHostType);
    }

    public int getCurrentSettleMerchantIndex() {
        return currentSettleMerchantIndex;
    }

    public void setCurrentSettleMerchantIndex(int currentSettleMerchantIndex) {
        this.currentSettleMerchantIndex = currentSettleMerchantIndex;
    }
}
