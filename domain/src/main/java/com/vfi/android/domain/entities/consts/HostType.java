package com.vfi.android.domain.entities.consts;

public class HostType {
    public static final int UNKNOWN     = 0;
    public static final int LOCAL = 1;
    public static final int CUP         = 2;
    public static final int DEBIT       = 3;
    public static final int LOYALTY     = 4;
    public static final int INSTALLMENT = 5;
    public static final int DCC         = 6;

    public static String toDebugString(int hostType) {
        switch (hostType) {
            case LOCAL:
                return "LOCAL";
            case CUP:
                return "CUP";
            case DEBIT:
                return "DEBIT";
            case LOYALTY:
                return "LOYALTY";
            case INSTALLMENT:
                return "INSTALLMENT";
            case DCC:
                return "DCC";
        }

        return "Unknown hostType[" + hostType + "]";
    }

    public static int toHostType(String hostName) {
        switch (hostName) {
            case "LOCAL":
                return LOCAL;
            case "CUP":
                return CUP;
            case "DEBIT":
                return DEBIT;
            case "LOYALTY":
                return LOYALTY;
            case "INSTALLMENT":
                return INSTALLMENT;
            case "DCC":
                return DCC;
        }

        return UNKNOWN;
    }
}
