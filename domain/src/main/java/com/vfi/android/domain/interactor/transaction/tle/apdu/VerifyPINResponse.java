package com.vfi.android.domain.interactor.transaction.tle.apdu;

import com.vfi.android.domain.utils.StringUtil;

public class VerifyPINResponse extends ApduResponse {
    private int remainRetrys;

    public VerifyPINResponse(byte[] response) {
        super(response);
        remainRetrys = -1;

        if (getStatus().startsWith("65")) {
            remainRetrys = StringUtil.parseInt(getStatus().substring(2, 4), 16, -1);
        }
    }

    public boolean isCorrectPin() {
        return isSuccess();
    }

    public int getRemainRetrys() {
        return remainRetrys;
    }
}
