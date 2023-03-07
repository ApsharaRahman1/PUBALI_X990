package com.vfi.android.domain.interactor.transaction.tle.apdu;

import com.vfi.android.domain.utils.EncryptionUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

public class GetAppKeyResponse extends ApduResponse {
    private String eAppKeyHexStr; // eAPPKEY encrypted by TSK
    private String plainAppKeyHexStr;
    private String appKeyKcvHexStr;

    public GetAppKeyResponse(byte[] response) {
        super(response);
        if (isSuccess()) {
            String dataHex = StringUtil.byte2HexStr(getData());
            if (dataHex != null && dataHex.length() == 52) {
                eAppKeyHexStr = dataHex.substring(0, 48);
                appKeyKcvHexStr = dataHex.substring(48, 52);
            } else {
                setSuccess(false);
            }
        }
    }

    public void decryptionETsk(String iTsk) {
        plainAppKeyHexStr = EncryptionUtil.decCBC3DesNoPadding(iTsk, eAppKeyHexStr);
        plainAppKeyHexStr = plainAppKeyHexStr.substring(0, 32);
        LogUtil.d(TAG, "plainAppKeyHexStr=" + plainAppKeyHexStr);
    }

    public boolean isCorrectKcv() {
        String kcv = EncryptionUtil.caculateKcv(plainAppKeyHexStr);
        LogUtil.d(TAG, "isCorrectKcv kcv=[" + kcv + "] appKeyKcvHexStr=[" + appKeyKcvHexStr + "]");
        if (kcv.startsWith(appKeyKcvHexStr)) {
            return true;
        } else {
            return false;
        }
    }

    public String getPlainAppKeyHexStr() {
        return plainAppKeyHexStr;
    }
}
