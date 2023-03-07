package com.vfi.android.domain.entities.consts;

import com.vfi.android.domain.utils.LogUtil;

public class CurrencyCode {

    public static final String AUD = "036"; // AUSTRALIAN_DOLLAR
    public static final String BHD = "048"; // BAHRAINI_DOLLAR
    public static final String BDT = "050"; // TAKA
    public static final String BND = "096"; // BRUNEI_DOLLAR
    public static final String CAD = "124"; // CANADIAN_DOLLAR
    public static final String LKR = "144"; // SRI_LANKA_RUPEE
    public static final String CNY = "156"; // CHINESE_YUAN
    public static final String DKK = "208"; // DANISH_KRONE
    public static final String HKD = "344"; // HONG_KONG_DOLLAR
    public static final String INR = "356"; // INDIAN_RUPEE_Y_INR
    public static final String ILS = "376"; // ISRAELI_SHEKEL
    public static final String JPY = "392"; // JAPANESE_YEN
    public static final String KRW = "410"; // KOREAN_WON
    public static final String MYR = "458"; // MALAYSIAN_RINGGIT
    public static final String OMR = "512"; // OMANI_RIAL
    public static final String NPR = "524"; // NEPALEASE_RUPEE
    public static final String NZD = "554"; // NEWZEALAND_DOLLAR
    public static final String NOK = "578"; // NORWEGIAN_KRONE
    public static final String PKR = "586"; // PAKISTAN_RUPEE
    public static final String PHP = "608"; // PHILIPPINE_PESO
    public static final String QAR = "634"; // QATAR_RIAL
    public static final String SAR = "682"; // SAUDI_ARABIAN_RIYAL
    public static final String SGD = "702"; // SINGAPORE_DOLLAR
    public static final String ZAR = "710"; // SOUTH_AFRICAN_RAND
    public static final String SEK = "752"; // SWEDISH_KRONA
    public static final String CHF = "756"; // SWISS_FRANC
    public static final String THB = "764"; // THAI_BAHT
    public static final String AED = "784"; // UAE_DIRHAM_Y_AED
    public static final String EGP = "818"; // EGYPTIAN_POUND_Y_EGP
    public static final String GBP = "826"; // BRITISH_POUND_STERLING
    public static final String USD = "840"; // US_DOLLAR
    public static final String TWD = "901"; // TAIWAN_DOLLAR_Y
    public static final String EUR = "978"; // EURO

    public static String toEngString(String currencyCode) {
        LogUtil.d("TAG", "toEngString currencyCode=[" + currencyCode + "]");

        if (currencyCode == null) {
            return "";
        }

        if (currencyCode.length() > 3) {
            currencyCode = currencyCode.substring(currencyCode.length() - 3);
        }

        switch (currencyCode) {
            case AUD:
                return "AUD";
            case BHD:
                return "BHD";
            case BDT:
                return "BDT";
            case BND:
                return "BND";
            case CAD:
                return "CAD";
            case LKR:
                return "LKR";
            case CNY:
                return "CNY";
            case DKK:
                return "DKK";
            case HKD:
                return "HKD";
            case INR:
                return "INR";
            case ILS:
                return "ILS";
            case JPY:
                return "JPY";
            case KRW:
                return "KRW";
            case MYR:
                return "MYR";
            case OMR:
                return "OMR";
            case NPR:
                return "NPR";
            case NZD:
                return "NZD";
            case NOK:
                return "NOK";
            case PKR:
                return "PKR";
            case PHP:
                return "PHP";
            case QAR:
                return "QAR";
            case SAR:
                return "SAR";
            case SGD:
                return "SGD";
            case ZAR:
                return "ZAR";
            case SEK:
                return "SEK";
            case CHF:
                return "CHF";
            case THB:
                return "THB";
            case AED:
                return "AED";
            case EGP:
                return "EGP";
            case GBP:
                return "GBP";
            case USD:
                return "USD";
            case TWD:
                return "TWD";
            case EUR:
                return "EUR";
            default:
                return "UNKNOWN";
        }
    }
}
