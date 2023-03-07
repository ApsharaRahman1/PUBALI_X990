package com.vfi.android.domain.interactor.transaction.tle.apdu;

import com.vfi.android.domain.utils.EncryptionUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

public class AuthSessionResponse extends ApduResponse {
    private String encryptedKeyInfoHex;

    // RKI or TLE key set
    private String rkiKId;
    private String rkiDekHex;
    private String rkiDekKcvHex;
    private String rkiMekHex;
    private String rkiMekKcvHex;
    private String tleKId;
    private String tleDekHex;
    private String tleDekKcvHex;
    private String tleMekHex;
    private String tleMekKcvHex;

    public AuthSessionResponse(byte[] response) {
        super(response);
        if (isSuccess()) {
            encryptedKeyInfoHex = StringUtil.byte2HexStr(getData());
            LogUtil.d(TAG, "encrypted keyInfoHex=[" + encryptedKeyInfoHex + "]");
        }
    }

    public void decryptionByTsk(String sessionKey) {
        String keyInfoHex = EncryptionUtil.decCBC3DesNoPadding(sessionKey, encryptedKeyInfoHex);
        LogUtil.d(TAG, "keyInfoHex with padding=[" + keyInfoHex + "]");
        keyInfoHex = EncryptionUtil.removeISO9797Method2Padding(keyInfoHex);
        LogUtil.d(TAG, "keyInfoHex=[" + keyInfoHex + "]");

        int index = 0;
        String rkiDekKeyIdHex = keyInfoHex.substring(index, index + 12);
        rkiKId = StringUtil.hexStr2Str(rkiDekKeyIdHex);
        LogUtil.d(TAG, "rkiDekKeyId=[" + rkiKId + "]");
        index += 12;
        index += 2; // delimiter (0x01) hex length

        rkiDekHex = keyInfoHex.substring(index, index + 32);
        LogUtil.d(TAG, "rkiDekHex=[" + rkiDekHex + "]");
        index += 32;
        index += 2; // delimiter (0x01) hex length

        rkiDekKcvHex = keyInfoHex.substring(index, index + 8);
        LogUtil.d(TAG, "rkiDekKcvHex=" + rkiDekKcvHex);
        index += 8;
        index += 2; // delimiter (0x01) hex length

        rkiMekHex = keyInfoHex.substring(index, index + 32);
        LogUtil.d(TAG, "rkiMekHex=[" + rkiMekHex + "]");
        index += 32;
        index += 2; // delimiter (0x01) hex length

        rkiMekKcvHex = keyInfoHex.substring(index, index + 8);
        LogUtil.d(TAG, "rkiMekKcvHex=[" + rkiMekKcvHex + "]");
        index += 8;
        index += 2; // delimiter (0x01) hex length

        String tleKidHex = keyInfoHex.substring(index, index + 12);
        LogUtil.d(TAG, "tleKidHex=[" + tleKidHex + "]");
        tleKId = StringUtil.hexStr2Str(tleKidHex);
        LogUtil.d(TAG, "tleKid=[" + tleKId + "]");
        index += 12;
        index += 2;

        tleDekHex = keyInfoHex.substring(index, index + 32);
        LogUtil.d(TAG, "tleDekHex=[" + tleDekHex + "]");
        index += 32;
        index += 2; // delimiter (0x01) hex length

        tleDekKcvHex = keyInfoHex.substring(index, index + 8);
        LogUtil.d(TAG, "tleDekKcvHex=" + tleDekKcvHex);
        index += 8;
        index += 2; // delimiter (0x01) hex length

        tleMekHex = keyInfoHex.substring(index, index + 32);
        LogUtil.d(TAG, "tleMekHex=[" + tleMekHex + "]");
        index += 32;
        index += 2; // delimiter (0x01) hex length

        tleMekKcvHex = keyInfoHex.substring(index, index + 8);
        LogUtil.d(TAG, "tleMekKcvHex=[" + tleMekKcvHex + "]");

        String kcv = EncryptionUtil.caculateKcv(rkiDekHex);
        LogUtil.e(TAG, "rki dek kcv=[" + kcv + "] rkiDekKcvHex=[" + rkiDekKcvHex + "]");
        if (!kcv.startsWith(rkiDekKcvHex)) {
            LogUtil.e(TAG, "rki dek not correct, kcv not match");
            setSuccess(false);
            return;
        }

        kcv = EncryptionUtil.caculateKcv(rkiMekHex);
        LogUtil.e(TAG, "rki mek kcv=[" + kcv + "] rkiMekHex=[" + rkiMekKcvHex + "]");
        if (!kcv.startsWith(rkiMekKcvHex)) {
            LogUtil.e(TAG, "rki mek not correct, kcv not match");
            setSuccess(false);
            return;
        }

        kcv = EncryptionUtil.caculateKcv(tleDekHex);
        LogUtil.e(TAG, "tle dek kcv=[" + kcv + "] tleDekKcvHex=[" + tleDekKcvHex + "]");
        if (!kcv.startsWith(tleDekKcvHex)) {
            LogUtil.e(TAG, "tle dek not correct, kcv not match");
            setSuccess(false);
            return;
        }

        kcv = EncryptionUtil.caculateKcv(tleMekHex);
        LogUtil.e(TAG, "tle mek kcv=[" + kcv + "] tleMekKcvHex=[" + tleMekKcvHex + "]");
        if (!kcv.startsWith(tleMekKcvHex)) {
            LogUtil.e(TAG, "tle mek not correct, kcv not match");
            setSuccess(false);
            return;
        }
    }

    public String getRkiKId() {
        return rkiKId;
    }

    public String getRkiDekHex() {
        return rkiDekHex;
    }

    public String getRkiDekKcvHex() {
        return rkiDekKcvHex;
    }

    public String getRkiMekHex() {
        return rkiMekHex;
    }

    public String getRkiMekKcvHex() {
        return rkiMekKcvHex;
    }

    public String getTleKId() {
        return tleKId;
    }

    public String getTleDekHex() {
        return tleDekHex;
    }

    public String getTleDekKcvHex() {
        return tleDekKcvHex;
    }

    public String getTleMekHex() {
        return tleMekHex;
    }

    public String getTleMekKcvHex() {
        return tleMekKcvHex;
    }
}
