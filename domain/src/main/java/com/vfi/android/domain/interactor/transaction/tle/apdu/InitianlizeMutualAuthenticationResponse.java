package com.vfi.android.domain.interactor.transaction.tle.apdu;

import com.vfi.android.domain.utils.EncryptionUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

public class InitianlizeMutualAuthenticationResponse extends ApduResponse {
    private String eRndA1B2B1A2;
    private String plainA1B2B1A2;
    private String A1A2;
    private String B1B2;

    public InitianlizeMutualAuthenticationResponse(byte[] response) {
        super(response);
        if (isSuccess()) {
            String dataHex = StringUtil.byte2HexStr(getData());
            if (dataHex != null && dataHex.length() == 32) {
                eRndA1B2B1A2 = dataHex;
            }
        }
    }

    public void decryptERndA1B2B1A2(String appKeyHexStr) {
        plainA1B2B1A2 = EncryptionUtil.encCBC3DesNoPadding(appKeyHexStr, eRndA1B2B1A2);
        LogUtil.d(TAG, "plainA1B2B1A2=[" + plainA1B2B1A2 + "]");
        if (plainA1B2B1A2.length() != 32) {
            LogUtil.e(TAG, "plainA1B2B1A2 not correct.");
            setSuccess(false);
            return;
        }
        A1A2 = plainA1B2B1A2.substring(0, 8) + plainA1B2B1A2.substring(24, 32);
        LogUtil.d(TAG, "A1A2=[" + A1A2 + "]");
        B1B2 = plainA1B2B1A2.substring(16, 24) + plainA1B2B1A2.substring(8, 16);
        LogUtil.d(TAG, "B1B2=[" + B1B2 + "]");
    }

    public String getA1A2() {
        return A1A2;
    }

    public String getB1B2() {
        return B1B2;
    }

    public String geteRndA1B2B1A2() {
        return eRndA1B2B1A2;
    }

    public String getPlainA1B2B1A2() {
        return plainA1B2B1A2;
    }
}
