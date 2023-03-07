package com.vfi.android.domain.entities.consts;

import com.vfi.android.domain.BuildConfig;

public class ExceptionType {
    // BindObservable
    public static final int DEVICE_SERVICE_NOT_EXIST = 1;
    // CheckCardObservable
    public static final int CHECK_CARD_FAILED = 2;
    public static final int CHECK_CARD_TIMEOUT = 3;
    // DeviceInfoObservable
    public static final int TERMINAL_DOES_NOT_HAVE_SN = 4;
    public static final int TERMINAL_DOES_NOT_HAVE_TUSN = 5;
    // EmvObservable
    public static final int EMV_FAILED = 6;
    // PinpadObservable
    public static final int LOAD_WORK_KEY_FAILED = 7;
    public static final int LOAD_MAC_KEY_FAILED = 8;
    public static final int LOAD_PIN_KEY_FAILED = 9;
    public static final int LOAD_TRACK_KEY_FAILED = 10;
    public static final int CALCULATE_MAC_FAILED = 11;
    public static final int ENCRYPT_TRACK_INFO_FAILED = 12;
    public static final int INPUT_PIN_CANCELED = 13;
    public static final int INPUT_PIN_TIMEOUT = 14;
    public static final int INPUT_PIN_FAILED = 15;
    // PrinterObservable
    public static final int PRINT_FAILED = 16;
    // ScannerObservable
    public static final int SCAN_FAILED = 17;

    public static final int PACKAGE_FAILED = 18;

    public static final int TLE_KEY_NOT_FOUND = 19;
    public static final int TRANS_RECORD_NOT_FOUND = 20;

    // ICOMM
    public static final int COMM_EXCEPTION = 21;

    // UsecaseCheckCardStart
    public static final int FALL_BACK = 22;
    public static final int TRACK_KEY_NOT_FOUND = 23;
    public static final int PIN_KEY_NOT_FOUND = 24;

    // ISO Package
    public static final int PACKAGE_EXCEPTION = 25;
    // Transaction Exception
    public static final int TRANS_FAILED = 26;
    //Card bin
    public static final int GET_CARD_BIN_FAILED= 27;
    // Reversal
    public static final int REVERSAL_FAILED = 28;
    //Host
    public static final int GET_HOST_INFO_FAILED = 29;
    //Merchant
    public static final int GET_MERCHANT_INFO_FAILED = 30;

    // Settlement
    public static final int SETTLEMENT_NEED_BATCH_UPLOAD = 31;
    public static final int SETTLEMENT_FAILED = 32;

    // UseCaseIsExistAdjustTransRecord,UseCaseIsExistVoidTransRecord
    public static final int TRANS_NOT_ALLOW = 33;

    public static final int GET_DEVICE_INFO_FAILED = 34;

    public static final int RKI_KEY_DOANLOAD_FAILED = 35;

    public static String toDebugString(int exceptionType) {
        if (!BuildConfig.DEBUG) {
            return "" + exceptionType;
        }

        switch (exceptionType) {
            case DEVICE_SERVICE_NOT_EXIST:
                return "DEVICE_SERVICE_NOT_EXIST";
            case CHECK_CARD_FAILED:
                return "CHECK_CARD_FAILED";
            case CHECK_CARD_TIMEOUT:
                return "CHECK_CARD_TIMEOUT";
            case TERMINAL_DOES_NOT_HAVE_SN:
                return "TERMINAL_DOES_NOT_HAVE_SN";
            case TERMINAL_DOES_NOT_HAVE_TUSN:
                return "TERMINAL_DOES_NOT_HAVE_TUSN";
            case EMV_FAILED:
                return "EMV_FAILED";
            case LOAD_WORK_KEY_FAILED:
                return "LOAD_WORK_KEY_FAILED";
            case LOAD_MAC_KEY_FAILED:
                return "LOAD_MAC_KEY_FAILED";
            case LOAD_PIN_KEY_FAILED:
                return "LOAD_PIN_KEY_FAILED";
            case LOAD_TRACK_KEY_FAILED:
                return "LOAD_TRACK_KEY_FAILED";
            case CALCULATE_MAC_FAILED:
                return "CALCULATE_MAC_FAILED";
            case ENCRYPT_TRACK_INFO_FAILED:
                return "ENCRYPT_TRACK_INFO_FAILED";
            case INPUT_PIN_CANCELED:
                return "INPUT_PIN_CANCELED";
            case INPUT_PIN_TIMEOUT:
                return "INPUT_PIN_TIMEOUT";
            case INPUT_PIN_FAILED:
                return "INPUT_PIN_FAILED";
            case PRINT_FAILED:
                return "PRINT_FAILED";
            case SCAN_FAILED:
                return "SCAN_FAILED";
            case PACKAGE_FAILED:
                return "PACKAGE_FAILED";
            case TLE_KEY_NOT_FOUND:
                return "TLE_KEY_NOT_FOUND";
            case TRANS_RECORD_NOT_FOUND:
                return "TRANS_RECORD_NOT_FOUND";
            case COMM_EXCEPTION:
                return "COMM_EXCEPTION";
            case FALL_BACK:
                return "FALL_BACK";
            case TRACK_KEY_NOT_FOUND:
                return "TRACK_KEY_NOT_FOUND";
            case PIN_KEY_NOT_FOUND:
                return "PIN_KEY_NOT_FOUND";
            case PACKAGE_EXCEPTION:
                return "PACKAGE_EXCEPTION";
            case TRANS_FAILED:
                return "TRANS_FAILED";
            case GET_CARD_BIN_FAILED:
                return "GET_CARD_BIN_FAILED";
            case REVERSAL_FAILED:
                return "REVERSAL_FAILED";
            case GET_HOST_INFO_FAILED:
                return "GET_HOST_INFO_FAILED";
            case GET_MERCHANT_INFO_FAILED:
                return "GET_MERCHANT_INFO_FAILED";
            case SETTLEMENT_NEED_BATCH_UPLOAD:
                return "SETTLEMENT_NEED_BATCH_UPLOAD";
            case SETTLEMENT_FAILED:
                return "SETTLEMENT_FAILED";
            case TRANS_NOT_ALLOW:
                return "TRANS_NOT_ALLOW";
            case GET_DEVICE_INFO_FAILED:
                return "GET_DEVICE_INFO_FAILED";
            case RKI_KEY_DOANLOAD_FAILED:
                return "RKI_KEY_DOANLOAD_FAILED";
        }

        return "" + exceptionType;
    }
}
