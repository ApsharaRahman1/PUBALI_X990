package com.vfi.android.domain.interactor.transaction.tle.apdu;

public class SW12 {
    public static String OK = "9000";
    public static String OPFAIL_INCORRECT_PARAM = "6A86";
    public static String OPFAIL_FILE_FULL       = "6A84";
    public static String OPFAIL_SECURE_MSG_NOT_SUPPORT = "6882";
    public static String OPFAIL_CMD_NOT_ALLOW   = "6986";
    public static String OPFAIL_SECURITY_STATUS_NOT_SATIFIED = "6982";
    public static String OPFAIL_WRONG_DATA      = "6A80";

    public static String OPFAIL_MUTUAL_AUTH_FAILED = "6801";
    public static String OPFAIL_MUTUAL_AUTH_NEEDED = "6803";
    public static String OPFAIL_STATUS_NOT_SATISFIED = "6804";

    /**
     * 0x9000 OK. Command executed successfully
     * 0x6A86 Operation Failed. Incorrect Parameters P1 P2
     * 0x6A84 Operation Failed. File Full (Insufficient memory)
     * 0x6882 Operation Failed. Secure Messaging Not Supported
     * 0x6986 Operation Failed. Command Not Allowed
     * 0x6982 Operation Failed. Security status not satified
     * 0x6983 Operation Failed. Invalid File ID (or Key ID)
     * 0x6E00 Operation Failed. CLA (Class) not supported
     * 0x6A81 Operation Failed. Function not supported
     * 0x6B00 Operation Failed. Incorrect Parameters P1 P2
     * 0x6A83 Operation Failed. Record Not Found
     * 0x6A82 Operation Failed. File not found (or Key not found)
     * 0x6984 Operation Failed. Invalid Data
     * 0x6985 Operation Failed. Condition of use not satisfied
     * 0x6D00 Operation Failed. INS value not supported
     * 0x6700 Operation Failed. Wrong length
     * 0x6A80 Operation Failed. Wrong data
     * 0x65XX Response byte remaining XX
     * Application Defined SW12
     * 0x69XX Authentication Failed. XX indicates remaining tries
     * 0x6500 PIN Verification Failed
     * 0x6801 Mutual Auth Failed: Cryptograms (Random Number) Comparison Failed
     * 0x6802 KCV Check Failed
     * SAM Specification
     * 0x6803 Security Status Not Satisfied: Mutual Auth Needed
     * 0x6804 Status Not Satisfied: Command Not Allowed
     */
}
