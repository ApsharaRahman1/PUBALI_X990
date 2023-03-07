package com.vfi.android.domain.entities.consts;

import com.vfi.android.domain.BuildConfig;
import com.vfi.android.domain.utils.LogUtil;

public class PrintType {
    public static final int COMMON              = 0;
    public static final int SALE                = 1;
    public static final int VOID                = 2;
    public static final int SETTLEMENT          = 3;
    public static final int SUMMARY_REPORT      = 4;
    public static final int DETAIL_REPORT       = 5;
    public static final int PREAUTH             = 6;
    public static final int PREAUTH_COMP        = 7;
    public static final int OFFLINE             = 8;
    public static final int TIP_ADJUST          = 9;
    public static final int LAST_TRANS          = 10;
    public static final int LAST_SETTLEMENT     = 11;
    public static final int EMV_DEBUG_INFO      = 12;
    public static final int INSTALLMENT         = 13;
    public static final int ISO_LOG             = 14;

    public static int getPrintType(int transType) {
        switch (transType){
            case TransType.SALE:
                return PrintType.SALE;
            case TransType.VOID:
                return  PrintType.VOID;
            case TransType.PREAUTH:
                return  PrintType.PREAUTH;
            case TransType.PREAUTH_COMP:
                return  PrintType.PREAUTH_COMP;
            case TransType.OFFLINE:
                return PrintType.OFFLINE;
            case TransType.TIP_ADJUST:
                return PrintType.TIP_ADJUST;
            case TransType.INSTALLMENT:
                return PrintType.INSTALLMENT;
        }

        LogUtil.w(TAGS.PRINT, "Use PrintType[COMMON] to print.");
        return PrintType.COMMON;
    }

    public static String toDebugString(int printType) {
        if (!BuildConfig.DEBUG) {
            return "" + printType;
        }

        switch (printType) {
            case COMMON:
                return "COMMON";
            case SALE:
                return "SALE";
            case VOID:
                return "VOID";
            case SETTLEMENT:
                return "SETTLEMENT";
            case SUMMARY_REPORT:
                return "SUMMARY_REPORT";
            case DETAIL_REPORT:
                return "DETAIL_REPORT";
            case PREAUTH:
                return "PREAUTH";
            case PREAUTH_COMP:
                return "PREAUTH_COMP";
            case OFFLINE:
                return "OFFLINE";
            case TIP_ADJUST:
                return "TIP_ADJUST";
            case LAST_TRANS:
                return "LAST_TRANS";
            case LAST_SETTLEMENT:
                return "LAST_SETTLEMENT";
            case EMV_DEBUG_INFO:
                return "EMV_DEBUG_INFO";
            case INSTALLMENT:
                return "INSTALLMENT";
            case ISO_LOG:
                return "ISO_LOG";
        }

        return "PrintType[" + printType + "]";
    }

}
