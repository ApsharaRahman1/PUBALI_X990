package com.vfi.android.payment.presentation.navigation;

public class UIState {
    public static final int UI_STATE_UNKOWN = -1;
    public static final int UI_STATE_TRANS_ENTRY = 0;
    public static final int UI_STATE_INPUT_AMOUNT = 1;
    public static final int UI_STATE_CHOICE_PAYMENT = 2;
    public static final int UI_STATE_CHECK_CARD = 3;
    public static final int UI_STATE_INPUT_PIN = 4;
    public static final int UI_STATE_CHECK_PASSWD = 5;
    public static final int UI_STATE_GET_CARD_ACOUNT = 6;
    public static final int UI_STATE_INPUT_CARD_NUM = 7;
    public static final int UI_STATE_EMV = 8;
    public static final int UI_STATE_TRANS_NETWORK_PROCESS = 9;
    public static final int UI_STATE_ESIGN = 10;
    public static final int UI_STATE_SHOW_STETTLEMENT_DETAIL = 11;
    public static final int UI_STATE_INPUT_INVOICE_NUM = 12;
    public static final int UI_STATE_SHOW_TRANS_INFO = 13;
    public static final int UI_STATE_SCAN = 14;
    public static final int UI_STATE_TRANS_FAILED = 15;
    public static final int UI_STATE_TRANS_SUCCESS = 16;
    public static final int UI_STATE_TRANS_END = 17;
    public static final int UI_STATE_INPUT_TIP_AMOUNT = 18;
    public static final int UI_STATE_INPUT_APPROVAL_CODE = 19;
    public static final int UI_STATE_INPUT_CARD_EXPIRY_DATE = 20;
    public static final int UI_STATE_INPUT_REFERENCE_NUM = 21;
    public static final int UI_STATE_INPUT_ORG_TRANS_DATE = 22;
    public static final int UI_STATE_OPTIONPAYMENT_CODE_SCAN = 23;
    public static final int UI_STATE_C_SCAN_B = 24;
    public static final int UI_STATE_DCC_REQUEST = 25;
    public static final int UI_STATE_CHOOSE_INSTALLMENT_PROMO = 26;
    public static final int UI_STATE_INPUT_NUM_OF_MONTH = 27;
    public static final int UI_STATE_INPUT_PROMOTION_CODE = 28;
    public static final int UI_STATE_INPUT_REF1_REF2_NUM = 29;
    public static final int UI_STATE_INPUT_PRODUCT_SN = 30;
    public static final int UI_STATE_CHOOSE_REDEMPTION = 31;
    public static final int UI_STATE_INPUT_REWARD_CODE = 32;
    public static final int UI_STATE_INPUT_QUANTITY = 33;
    public static final int UI_STATE_INPUT_POINT_VALUE = 34;
    public static final int UI_STATE_INPUT_AMEX_APPROVE_CODE = 35;
    public static final int UI_STATE_INPUT_4DBC = 36;
    public static final int UI_STATE_INPUT_CVV2 = 37;
    public static final int UI_STATE_INPUT_ORG_AUTH_CODE = 38;
    public static final int UI_STATE_INPUT_ORG_REF_NUM =  39;
    public static final int UI_STATE_SELECT_HOST_MERCHANT = 40;
    public static final int UI_STATE_CHOOSE_INSTALLMENT_TERM = 41;

    public static String toString(int state) {
        switch (state) {
            case UI_STATE_UNKOWN:
                return "UI_STATE_UNKOWN";
            case UI_STATE_TRANS_ENTRY:
                return "UI_STATE_TRANS_ENTRY";
            case UI_STATE_INPUT_AMOUNT:
                return "UI_STATE_INPUT_AMOUNT";
            case UI_STATE_CHOICE_PAYMENT:
                return "UI_STATE_CHOICE_PAYMENT";
            case UI_STATE_CHECK_CARD:
                return "UI_STATE_CHECK_CARD";
            case UI_STATE_INPUT_PIN:
                return "UI_STATE_INPUT_PIN";
            case UI_STATE_CHECK_PASSWD:
                return "UI_STATE_CHECK_PASSWD";
            case UI_STATE_GET_CARD_ACOUNT:
                return "UI_STATE_GET_CARD_ACOUNT";
            case UI_STATE_INPUT_CARD_NUM:
                return "UI_STATE_INPUT_CARD_NUM";
            case UI_STATE_EMV:
                return "UI_STATE_EMV";
            case UI_STATE_TRANS_NETWORK_PROCESS:
                return "UI_STATE_TRANS_NETWORK_PROCESS";
            case UI_STATE_ESIGN:
                return "UI_STATE_ESIGN";
            case UI_STATE_SHOW_STETTLEMENT_DETAIL:
                return "UI_STATE_SHOW_STETTLEMENT_DETAIL";
            case UI_STATE_INPUT_INVOICE_NUM:
                return "UI_STATE_INPUT_INVOICE_NUM";
            case UI_STATE_SHOW_TRANS_INFO:
                return "UI_STATE_SHOW_TRANS_INFO";
            case UI_STATE_SCAN:
                return "UI_STATE_SCAN";
            case UI_STATE_TRANS_FAILED:
                return "UI_STATE_TRANS_FAILED";
            case UI_STATE_TRANS_SUCCESS:
                return "UI_STATE_TRANS_SUCCESS";
            case UI_STATE_TRANS_END:
                return "UI_STATE_TRANS_END";
            case UI_STATE_INPUT_TIP_AMOUNT:
                return "UI_STATE_INPUT_TIP_AMOUNT";
            case UI_STATE_INPUT_APPROVAL_CODE:
                return "UI_STATE_INPUT_APPROVAL_CODE";
            case UI_STATE_INPUT_CARD_EXPIRY_DATE:
                return "UI_STATE_INPUT_CARD_EXPIRY_DATE";
            case UI_STATE_INPUT_REFERENCE_NUM:
                return "UI_STATE_INPUT_REFERENCE_NUM";
            case UI_STATE_INPUT_ORG_TRANS_DATE:
                return "UI_STATE_INPUT_ORG_TRANS_DATE";
            case UI_STATE_OPTIONPAYMENT_CODE_SCAN:
                return "UI_STATE_OPTIONPAYMENT_CODE_SCAN";
            case UI_STATE_C_SCAN_B:
                return "UI_STATE_C_SCAN_B";
            case UI_STATE_DCC_REQUEST:
                return "UI_STATE_DCC_REQUEST";
            case UI_STATE_CHOOSE_INSTALLMENT_PROMO:
                return "UI_STATE_CHOOSE_INSTALLMENT_PROMO";
            case UI_STATE_INPUT_NUM_OF_MONTH:
                return "UI_STATE_INPUT_NUM_OF_MONTH";
            case UI_STATE_INPUT_PROMOTION_CODE:
                return "UI_STATE_INPUT_PROMOTION_CODE";
            case UI_STATE_INPUT_REF1_REF2_NUM:
                return "UI_STATE_INPUT_REF1_REF2_NUM";
            case UI_STATE_INPUT_PRODUCT_SN:
                return "UI_STATE_INPUT_PRODUCT_SN";
            case UI_STATE_CHOOSE_REDEMPTION:
                return "UI_STATE_CHOOSE_REDEMPTION";
            case UI_STATE_INPUT_REWARD_CODE:
                return "UI_STATE_INPUT_REWARD_CODE";
            case UI_STATE_INPUT_QUANTITY:
                return "UI_STATE_INPUT_QUANTITY";
            case UI_STATE_INPUT_POINT_VALUE:
                return "UI_STATE_INPUT_POINT_VALUE";
            case UI_STATE_INPUT_AMEX_APPROVE_CODE:
                return "UI_STATE_INPUT_AMEX_APPROVE_CODE";
            case UI_STATE_INPUT_4DBC:
                return "UI_STATE_INPUT_4DBC";
            case UI_STATE_INPUT_CVV2:
                return "UI_STATE_INPUT_CVV2";
            case UI_STATE_SELECT_HOST_MERCHANT:
                return "UI_STATE_SELECT_HOST_MERCHANT";
            case UI_STATE_CHOOSE_INSTALLMENT_TERM:
                return "UI_STATE_CHOOSE_INSTALLMENT_TERM";
        }

        return "";
    }
}
