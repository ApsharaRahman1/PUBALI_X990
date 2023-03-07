package com.vfi.android.domain.interactor.transaction.tle.apdu;

import com.vfi.android.domain.utils.EncryptionUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

public class EstablishSecureChannelResponse extends ApduResponse {
    private String eTSKHexStr;
    private String plainTSKHexStr;
    private String tskKcvHexStr;

    public EstablishSecureChannelResponse(byte[] response) {
        super(response);
        if (isSuccess()) {
            String dataHex = StringUtil.byte2HexStr(getData());
            if (dataHex != null && dataHex.length() == 36) {
                eTSKHexStr = dataHex.substring(0, 32);
                tskKcvHexStr = dataHex.substring(32, 36);
            }
        }
    }

    public void decryptionETsk(String IAK) {
        plainTSKHexStr = EncryptionUtil.encCBC3DesNoPadding(IAK, eTSKHexStr);
        LogUtil.d(TAG, "plainTSKHexStr=" + plainTSKHexStr);
    }

    public boolean isCorrectKcv() {
        String kcv = EncryptionUtil.caculateKcv(plainTSKHexStr);
        LogUtil.d(TAG, "isCorrectKcv kcv=[" + kcv + "] tskKcvHexStr=[" + tskKcvHexStr + "]");
        if (kcv.startsWith(tskKcvHexStr)) {
            return true;
        } else {
            return false;
        }
    }

    public String getPlainTSKHexStr() {
        return plainTSKHexStr;
    }
}
