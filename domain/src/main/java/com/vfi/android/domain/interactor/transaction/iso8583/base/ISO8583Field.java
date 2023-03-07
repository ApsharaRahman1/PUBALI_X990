package com.vfi.android.domain.interactor.transaction.iso8583.base;

public interface ISO8583Field {
    // message Type
    String f0_messageType();

    // pan
    String f2_pan();

    // process code
    String f3_processCode();

    // amount
    String f4_amount();

    // foreignAmount
    String f6_foreignAmount();

    // transaction date time
    String f7_transDateTime();

    // system trace number
    String f11_traceNum();

    // local time
    String f12_localTime();

    // local date
    String f13_localDate();

    // expire date
    String f14_expireDate();

    // settlement date
    String f15_settleDate();

    // pos entry mode
    String f22_posEntryMode();

    // card seq number
    String f23_cardSeqNum();

    // NII
    String f24_nii();

    // Pos condition code
    String f25_posConditionCode();

    // service pcode
    String f26_servicePCode();

    // fee amount
    String f28_feeAmount();

    // track2 data
    String f35_track2Data();

    // track3 data
    String f36_track3Data();

    // Retrieval ref No
    String f37_retrievalRefNo();

    // Auth id response
    String f38_authIdResp();

    // response code
    String f39_responseCode();

    // terminal id
    String f41_terminalId();

    // Card acquirer id
    String f42_cardAcquirerId();

    // Card acquirer name
    String f43_cardAcquirerName();

    // track1 data
    String f45_track1Data();

    // additional text data, for example rcbc project cvv2
    String f48_additionalTextData();

    // currency code
    String f49_currencyCode();

    // pin data
    String f52_pinData();

    // security control information
    String f53_secControlInfo();

    // additional amount
    String f54_additionalAmount();

    // ICC related data
    String f55_icc_chip_data();

    // Terminal line encrypted data
    String f57_tleData();

    // private field
    String field_059();

    // private field
    String field_060();

    // private field
    String field_061();

    // private field
    String field_062();

    // private field
    String field_063();

    // private field
    String field_064();
}
