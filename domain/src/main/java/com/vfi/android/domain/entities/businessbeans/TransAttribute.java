package com.vfi.android.domain.entities.businessbeans;

import com.vfi.android.domain.entities.consts.TransSort;
import com.vfi.android.domain.entities.consts.TransType;

import io.reactivex.annotations.NonNull;

/**
 * Created by yunlongg1 on 12/5/17.
 */

public enum TransAttribute {
    /***************************************************************************
     * TransType,         MSGID,     NeedIncreaseTrace, NeedIncreaseInvoice,     ProcessCode,           Sort of Trans
     ***************************************************************************/
    //apshara
    SALE("0200", "000000", true, true, true, true, true, true, true, TransSort.NORMAL, TransType.SALE),
    VOID("0200", "020000", false, true, true, true, false, false, false, TransSort.NORMAL, TransType.VOID),
    PREAUTH("0100", "300000", true, true, true, true, false, true, false, TransSort.NORMAL, TransType.PREAUTH),
    PREAUTH_COMP(
            "0100", "020000", true, true, true, true, false, false, false, TransSort.NORMAL, TransType.PREAUTH_COMP),
    TIP_ADJUST("0220", "024000", false, true, true, false, false, false, false, TransSort.NORMAL, TransType.TIP_ADJUST),
    OFFLINE("0220", "004000", true, true, true, false, false, false, false, TransSort.NORMAL, TransType.OFFLINE),
    SETTLEMENT("0500", "920000", true, true, false, false, false, false, false, TransSort.NORMAL, TransType.SETTLEMENT),
    INSTALLMENT("0200", "009700", true, true, true, true, false, true, false, TransSort.NORMAL, TransType.INSTALLMENT),

    REVERSAL("0400", "", false, true, true, false, false, false, false, TransSort.NORMAL, TransType.REVERSAL),
    BATCH_UPLOAD("0320", "", false, true, false, false, false, false, false, TransSort.NORMAL, TransType.BATCH_UPLOAD),
    TC_UPLOAD("0320", "944000", false, true, false, false, false, false, false, TransSort.NORMAL, TransType.TC_UPLOAD),
    OFFLINE_UPLOAD("0220", "004000", false, false, true, false, false, false, false, TransSort.NORMAL, TransType.OFFLINE_UPLOAD),
    TIP_ADJUST_UPLOAD("0220", "024000", false, true, true, false, false, false, false, TransSort.NORMAL, TransType.TIP_ADJUST_UPLOAD),
    SETTLEMENT_TAILER("0500", "960000", false, true, false, false, false, false, false, TransSort.NORMAL, TransType.SETTLEMENT_TAILER),
    LOGON("0800", "920000", true, true, true, true, true, true, true, TransSort.MANAGE, TransType.LOGON),

    REPORT("0200", "000000", true, true, true, true, true, true, true, TransSort.NORMAL, TransType.SALE),

//    TEST("0800", "990000", TransSort.MANAGE, TransType.TEST);

    // tle related
    RKI_KEY_DOWNLOAD("0800", "950001", false, true, false, false, false, false, false, TransSort.NORMAL, TransType.RKI_KEY_DOWNLOAD),
    TLE_KEY_DOWNLOAD("0800", "950000", false, true, false, false, false, false, false, TransSort.NORMAL, TransType.TLE_KEY_DOWNLOAD);

    private String msgId;
    private String processCode;
    private int transSort;
    private int transType;
    private boolean isNeedIncreaseTrace;
    private boolean isNeedIncreaseInvoice;
    private boolean isUseRecordTraceNum;
    private boolean isSupportReversal;
    private boolean isNeedOnlineFailedTryOffline;
    private boolean isNeedInputOnlineResult;
    private boolean isNeedSaveTCForUpload;

    TransAttribute(String msgId,
                   String processCode,
                   boolean isNeedIncreaseInvoice,
                   boolean isNeedIncreaseTrace,
                   boolean isUseRecordTraceNum,
                   boolean isSupportReversal,
                   boolean isNeedOnlineFailedTryOffline,
                   boolean isNeedInputOnlineResult,
                   boolean isNeedSaveTCForUpload,
                   int transSort,
                   int transType) {
        this.msgId = msgId;
        this.processCode = processCode;
        this.transSort = transSort;
        this.transType = transType;
        this.isNeedIncreaseInvoice = isNeedIncreaseInvoice;
        this.isNeedIncreaseTrace = isNeedIncreaseTrace;
        this.isUseRecordTraceNum = isUseRecordTraceNum;
        this.isSupportReversal = isSupportReversal;
        this.isNeedOnlineFailedTryOffline = isNeedOnlineFailedTryOffline;
        this.isNeedInputOnlineResult = isNeedInputOnlineResult;
        this.isNeedSaveTCForUpload = isNeedSaveTCForUpload;
    }

    public static TransAttribute findTypeByType(@NonNull int transType) {
        for (TransAttribute type :
                TransAttribute.values()) {
            if (type.getTransType() == transType) {
                return type;
            }
        }
        return null;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getProcessCode() {
        return processCode;
    }

    public int getTransType() {
        return transType;
    }

    public int getTransSort() {
        return transSort;
    }

    public boolean isNeedIncreaseTrace() {
        return isNeedIncreaseTrace;
    }

    public boolean isNeedIncreaseInvoice() {
        return isNeedIncreaseInvoice;
    }

    public boolean isUseRecordTraceNum() {
        return isUseRecordTraceNum;
    }

    public void setUseRecordTraceNum(boolean useRecordTraceNum) {
        isUseRecordTraceNum = useRecordTraceNum;
    }

    public boolean isSupportReversal() {
        return isSupportReversal;
    }

    public void setSupportReversal(boolean supportReversal) {
        isSupportReversal = supportReversal;
    }

    public boolean isNeedOnlineFailedTryOffline() {
        return isNeedOnlineFailedTryOffline;
    }

    public void setNeedOnlineFailedTryOffline(boolean needOnlineFailedTryOffline) {
        isNeedOnlineFailedTryOffline = needOnlineFailedTryOffline;
    }

    public boolean isNeedInputOnlineResult() {
        return isNeedInputOnlineResult;
    }

    public void setNeedInputOnlineResult(boolean needInputOnlineResult) {
        isNeedInputOnlineResult = needInputOnlineResult;
    }

    public boolean isNeedSaveTCForUpload() {
        return isNeedSaveTCForUpload;
    }

    public void setNeedSaveTCForUpload(boolean needSaveTCForUpload) {
        isNeedSaveTCForUpload = needSaveTCForUpload;
    }
}
