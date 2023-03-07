package com.vfi.android.domain.entities.consts;

/**
 * Created by Tony
 * 2018/12/17
 * email: chong.z@verifone.cn
 */

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.utils.LogUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Transaction Name.
 * Warning: Some names can not be modified, they are fixed, and match to xml files
 */
public class TransType {
    /*--------------------------------------- Normal ---------------------------------------------*/
    public static final int SALE          = 10001;
    public static final int VOID          = 10002;
    public static final int OFFLINE       = 10003;   // just use to do offline sale transaction.
    public static final int PREAUTH       = 10004;
    public static final int PREAUTH_COMP  = 10005;
    public static final int TIP_ADJUST    = 10006;   // just use to do tip adjust transaction.
    public static final int REFUND        = 10007;
    public static final int REVERSAL      = 10008;
    public static final int BALANCE_INQ   = 10009;
    public static final int CASH_ADV      = 10010;
    public static final int DEBIT         = 10011;
    public static final int REDEMPTION    = 10012;
    public static final int INSTALLMENT   = 10013;
    public static final int QR            = 10014;

    /*--------------------------------------- Management -----------------------------------------*/
    public static final int LOGON             = 20001;
    public static final int TEST               = 20002;
    public static final int TC_UPLOAD          = 20003;
    public static final int BATCH_UPLOAD       = 20004;
    public static final int OFFLINE_UPLOAD     = 20005;  // use to upload offline trans.
    public static final int TIP_ADJUST_UPLOAD  = 20006;  // use to upload tip adjust trans.
    public static final int TLE_LTMK_DOWNLOAD  = 20007;
    public static final int TLE_LTWK_DOWNLOAD  = 20008;
    public static final int SETTLEMENT         = 20009;
    public static final int SETTLEMENT_TAILER  = 20010;
    public static final int PTS_INQUIRY        = 20011; // point inquiry
    public static final int REPORT             = 40001;
    /*--------------------------------------- Feature --------------------------------------------*/
    public static final int RKI_KEY_DOWNLOAD   = 30001;
    public static final int TLE_KEY_DOWNLOAD   = 30002;

    public static String toDebugString(int transType) {
        switch (transType) {
            case LOGON:
                return "LOGON";
            case SALE:
                return "SALE";
            case VOID:
                return "VOID";
            case OFFLINE:
                return "OFFLINE";
            case PREAUTH:
                return "PREAUTH";
            case PREAUTH_COMP:
                return "PREAUTH_COMP";
            case TIP_ADJUST:
                return "TIP_ADJUST";
            case REFUND:
                return "REFUND";
            case REVERSAL:
                return "REVERSAL";
            case BALANCE_INQ:
                return "BALANCE_INQ";
            case CASH_ADV:
                return "CASH_ADV";
            case DEBIT:
                return "DEBIT";
            case REDEMPTION:
                return "REDEMPTION";
            case INSTALLMENT:
                return "INSTALLMENT";
            case QR:
                return "QR";
            case SETTLEMENT:
                return "SETTLEMENT";
            case RKI_KEY_DOWNLOAD:
                return "RKI_KEY_DOWNLOAD";
            case TLE_KEY_DOWNLOAD:
                return "TLE_KEY_DOWNLOAD";
            case REPORT:
                return "REPORT";
        }

        return "" + transType;
    }

    public static List<Integer> getSettlementSaleRelatedTransList() {
        return Arrays.asList(TransType.SALE,
                TransType.OFFLINE, TransType.PREAUTH_COMP, TransType.TIP_ADJUST, TransType.INSTALLMENT);
    }

    public static boolean isSettlementSaleRelatedTrans(int transType) {
        LogUtil.d(TAGS.Settlement, "isSettlementSaleRelatedTrans = " +  TransType.toDebugString(transType));

        List<Integer> settlementSaleRelatedTransList = getSettlementSaleRelatedTransList();
        for (int saleRelatedTransType : settlementSaleRelatedTransList) {
            if (transType == saleRelatedTransType) {
                return true;
            }
        }

        return false;
    }

    public static String getPrintTransTitle(int transType, int origVoidTransType, int origTipAdjTransType) {
        String printTransTypeText = "UNKNOWN";
        switch (transType) {
            case TransType.SALE:
                printTransTypeText = "SALE";
                break;
            case TransType.VOID:
                int origTransType = origVoidTransType;
                if (origVoidTransType == TransType.TIP_ADJUST) {
                    origTransType = origTipAdjTransType;
                }
                switch (origTransType) {
                    case TransType.SALE:
                        printTransTypeText = "VOID SALE";
                        break;
                    case TransType.OFFLINE:
                        printTransTypeText = "VOID OFFLINESALE";
                        break;
                    case TransType.PREAUTH_COMP:
                        printTransTypeText = "VOID PREAUTH COMP";
                        break;
                    case TransType.INSTALLMENT:
                        printTransTypeText = "VOID INST";
                        break;
                }
                break;
            case TransType.PREAUTH:
                printTransTypeText = "PREAUTH";
                break;
            case TransType.PREAUTH_COMP:
                printTransTypeText = "PREAUTH COMP";
                break;
            case TransType.OFFLINE:
                printTransTypeText = "OFFLINE SALE";
                break;
            case TransType.TIP_ADJUST:
                printTransTypeText = "TIP ADJUST";
                break;
            case TransType.INSTALLMENT:
                printTransTypeText = "INSTALLMENT";
                break;
        }

        return printTransTypeText;
    }

    public static String getPrintSettleTransText(int transType, int origVoidTransType, int origTipAdjTransType) {
        String transactionText = "Unknown";

        if (transType == TransType.SALE) {
            transactionText = "Sale";
        } else if (transType == TransType.VOID) {
            int origTransType = origVoidTransType;
            if (origVoidTransType == TransType.TIP_ADJUST) {
                origTransType = origTipAdjTransType;
            }
            switch (origTransType) {
                case TransType.SALE:
                    transactionText = "Void Sale";
                    break;
                case TransType.OFFLINE:
                    transactionText = "Void offline";
                    break;
                case TransType.PREAUTH_COMP:
                    transactionText = "Void PreComp";
                    break;
                case TransType.INSTALLMENT:
                    transactionText = "Void Installment";
                    break;
            }
        } else if (transType == TransType.PREAUTH) {
            transactionText = "PreAuth";
        } else if (transType == TransType.PREAUTH_COMP) {
            transactionText = "PreAuthComp";
        } else if (transType == TransType.OFFLINE) {
            transactionText = "Offline";
        } else if (transType == TransType.INSTALLMENT) {
            transactionText = "Installment";
        } else if (transType == TransType.TIP_ADJUST) {
            switch (origTipAdjTransType) {
                case TransType.SALE:
                    transactionText = "Tip Adj Sale";
                    break;
                case TransType.OFFLINE:
                    transactionText = "Tip Adj offline";
                    break;
                case TransType.PREAUTH_COMP:
                    transactionText = "Tip Adj PreComp";
                    break;
                case TransType.INSTALLMENT:
                    transactionText = "Tip Adj Inst";
                    break;
            }
        }

        return transactionText;
    }

    public static String getTransTypeText(int transType, int origVoidTransType, int origTipAdjTransType) {
        int orgTransType;

        switch (transType) {
            case TransType.SALE:
                return "SALE";
            case TransType.REVERSAL:
                return "REVERSAL";
            case TransType.TIP_ADJUST:
                orgTransType = origTipAdjTransType;
                if (orgTransType == TransType.SALE) {
                    return "TIP ADJUST SALE";
                } else if (orgTransType == TransType.OFFLINE) {
                    return "TIP ADJUST OFFLINE";
                } else {
                    return "TIP ADJUST";
                }
            case TransType.VOID:
                orgTransType = origVoidTransType;
                if (origVoidTransType == TransType.TIP_ADJUST) {
                    orgTransType = origTipAdjTransType;
                }
                if (orgTransType == TransType.SALE) {
                    return "VOID SALE";
                } else if (orgTransType == TransType.OFFLINE) {
                    return "VOID OFFLINE SALE";
                } else if (orgTransType == TransType.PREAUTH_COMP) {
                    return "VOID PREAUTH COMP";
                } else if (orgTransType == TransType.INSTALLMENT) {
                    return "VOID INSTALLMENT";
                } else {
                    return "VOID";
                }
            case TransType.PREAUTH:
                return "PREAUTH";
            case TransType.PREAUTH_COMP:
                return "PREAUTH COMP";
            case TransType.OFFLINE:
                return "OFFLINE";
            case TransType.INSTALLMENT:
                return "INSTALLMENT";
        }

        return "Unknown TransType";
    }
}
