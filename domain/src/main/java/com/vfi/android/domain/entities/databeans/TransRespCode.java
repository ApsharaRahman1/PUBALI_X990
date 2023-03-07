package com.vfi.android.domain.entities.databeans;

import io.reactivex.annotations.NonNull;

/**
 * Created by Yaping.z on 2018/1/4.
 */

public enum TransRespCode {
    ERR_RESP_01("01", "REFER TO CARD ISSUER"),
    ERR_RESP_02("02", "REFER TO CARD ISSUER’S SPECIAL CONDITION"),
    ERR_RESP_03("03", "INVALID MERCHANT"),
    ERR_RESP_04("04", "PICK UP CARD"),
    ERR_RESP_05("05", "DECLINED"),
    ERR_RESP_06("06", "TRANACTION ERROR "),
    ERR_RESP_07("07", "PICK UP CARD"),
    ERR_RESP_12("12", "INVALID TRANSACTION"),
    ERR_RESP_13("13", "INVALID AMOUNT"),
    ERR_RESP_14("14", "INVALID CARD NUMBER"),
    ERR_RESP_15("15", "INVALID ISSUER"),
    ERR_RESP_16("16", "CUSTOMER CANCEL"),
    ERR_RESP_18("18", "REVERSAL ERROR"),
    ERR_RESP_19("19", "RE-ENTER TRANSACTION"),
    ERR_RESP_20("20", "INVALID RESPONSE"),
    ERR_RESP_21("21", "NO TRANSACTIONS"),
    ERR_RESP_22("22", "DUKPT ERROR"),
    ERR_RESP_24("24", "DE55 FILE UPDATE ERROR "),
    ERR_RESP_25("25", "UNABLE TO LOCATE RECORD ON FILE "),
    ERR_RESP_30("30", "FORMAT ERROR"),
    ERR_RESP_31("31", "BANK NOT SUPPORTED BY SWITCH"),
    ERR_RESP_33("33", "EXPIRED CARD"),
    ERR_RESP_38("38", "PLEASE CALL BANK"),
    ERR_RESP_41("41", "LOST CARD"),
    ERR_RESP_43("43", "STOLEN CARD PICK UP "),
    ERR_RESP_46("46", "PLEASE INSERT CARD"),
    ERR_RESP_51("51", "NOT SUFFICIENT FUNDS "),
    ERR_RESP_54("54", "EXPIRED CARD  "),
    ERR_RESP_55("55", "INCORRECT PIN "),
    ERR_RESP_56("56", "NO CARD RECORD "),
    ERR_RESP_57("56", "TRANSACTION DECLINED "),
    ERR_RESP_58("58", "TRANSACTION NOT PERMITTED IN TERMINAL"),
    ERR_RESP_61("61", "EXCEEDED WITHDRAWAL LIMIT "),
    ERR_RESP_63("63", "SECURITY VIOLATION "),
    ERR_RESP_65("65", "PLEASE INSERT CARD "),
    ERR_RESP_75("75", "PIN RETRY EXCEEDED "),
    ERR_RESP_76("76", "INVALID PRODUCT CODES  "),
    ERR_RESP_77("77", "RECONCILE ERROR "),
    ERR_RESP_78("78", "TRACE# NOT FOUND "),
    ERR_RESP_79("79", "SUPERVISOR PIN ERROR "),
    ERR_RESP_80("80", "BATCH NUMBER NOT FOUND "),
    ERR_RESP_81("81", "EXCEED REDEMPTION LIMIT"),
    ERR_RESP_82("82", "INVALID GIFT CODE"),
    ERR_RESP_83("83", "ZERO PTS BALANCE "),
    ERR_RESP_84("84", "INSUFFICIENT POINTS "),
    ERR_RESP_85("85", "INVALID ACCOUNT "),
    ERR_RESP_89("89", "BAD TERMINAL ID "),
    ERR_RESP_91("91", "ISSUER/SWITCH INOPERATIVE "),
    ERR_RESP_92("92", "ISSUER SYSTEM DOWN "),
    ERR_RESP_93("93", "ISSUER TIMEOUT "),
    ERR_RESP_94("94", "DUPLICATE TRANSMISSION "),
    ERR_RESP_95("95", "BATCH UPLOAD"),
    ERR_RESP_96("96", "SYSTEM MALFUNCTION "),
    ERR_RESP_N7("N7", "INVALID CVV2"),
    ERR_RESP_Y1("Y1", "EMV APPROVED"),
    ERR_RESP_Y3("Y3", "EMV APPROVED"),
    ERR_RESP_Z1("Z1", "EMV DECLINED"),
    ERR_RESP_Z3("Z3", "EMV DECLINED "),


    ERR_RESP_FF("FF", "FAILURE:");


    private String id;
    private String msg;

    TransRespCode(String id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static TransRespCode findPbocResultById(@NonNull String id) {
        for (TransRespCode transRespCode : TransRespCode.values()) {
            if (transRespCode.getId().equals(id)) {
                return transRespCode;
            }
        }
        return ERR_RESP_FF;
    }
}
