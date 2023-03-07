package com.vfi.android.domain.interactor.transaction.tle.apdu;

import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

public class RequestSessionResponse extends ApduResponse {
    private String samIdHex;
    private String encSSKeyHex;
    private String kcvHex;

    public RequestSessionResponse(byte[] response) {
        super(response);
        if (isSuccess()) {
            String dataHex = StringUtil.byte2HexStr(getData());
            LogUtil.d(TAG, "RequestSessionResponse dataHex=" + dataHex);
            if (dataHex != null && dataHex.length() == 76) {
                samIdHex = dataHex.substring(0, 40);
                encSSKeyHex = dataHex.substring(40, 72);
                kcvHex = dataHex.substring(72, 76);
            } else {
                LogUtil.e(TAG, "Request session failed.");
                setSuccess(false);
            }
        }
    }

    public String getSamIdHex() {
        return samIdHex;
    }

    public String getEncSSKeyHex() {
        return encSSKeyHex;
    }

    public String getKcvHex() {
        return kcvHex;
    }
}
