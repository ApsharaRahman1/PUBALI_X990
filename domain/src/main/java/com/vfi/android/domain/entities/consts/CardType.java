package com.vfi.android.domain.entities.consts;

public class CardType {
    public static final int STA_LUCIA   = 1;
    public static final int VISA        = 2;
    public static final int MASTERCARD  = 3;
    public static final int JCB         = 4;
    public static final int CUP         = 5;
    public static final int CATCHALL    = 6;

    /**
     * If add a new card type, please add it to this list.
     * @return card type list.
     */
    public static int[] getCardTypeList() {
        return new int[] {STA_LUCIA, VISA, MASTERCARD, JCB, CUP, CATCHALL};
    }

    /**
     * Only used for print module, no need multi-language
     * @param cardType
     * @return
     */
    public static String toCardTypeAbbrText(int cardType) {
        switch (cardType) {
            case STA_LUCIA:
                return "SL";
            case VISA:
                return "VISA";
            case MASTERCARD:
                return "MASTERCARD";
            case JCB:
                return "JC";
            case CUP:
                return "CU";
            case CATCHALL:
                return "CA";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * Only used for print module, no need multi-language
     * @param cardType
     * @return
     */
    public static String toCardTypeText(int cardType) {
        switch (cardType) {
            case STA_LUCIA:
                return "STA_LUCIA";
            case VISA:
                return "VISA";
            case MASTERCARD:
                return "MASTERCARD";
            case JCB:
                return "JCB";
            case CUP:
                return "CUP";
            case CATCHALL:
                return "CATCHALL";
            default:
                return "UNKNOWN";
        }
    }
}
