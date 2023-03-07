package com.vfi.android.domain.entities.databeans;

/**
 * Created by fusheng.z on 2017/11/30.*/

public class PinPadGeneralParamIn {
    private int keyIdx;                //密钥Idx
    private int keyType;               //密钥类型(Key_Type) 0-主密钥, 1-MAC密钥，2-PIN密钥，3-TD密钥
    private int mkIdx;                 //解密工作密钥的主密钥ID// a msterkey to decrypt work key
    private byte[] key;                //密钥  下载TEK、下载明文主密钥、下装密文主密钥 Download plaintext master key, download ciphertext master key
    private byte[] checkValue;         //校验值
    private int mkeyFormat;            //主密钥格式(MasterKey_Format) 0-CLEAR_FORMAT, 1-ENCRYPTED_FORMAT
    private int algorithmType;         // 1-3des密文 3-SM4密文 5-AES密文

    private boolean isMasterEncMasterMode; // pup project
    private int encryptMasterKeyIdex; // only used in master encrypt master mode, use this key decrypted key.

    public static final int MASTER_KEY = 0;
    public static final int MAC_KEY = 1;
    public static final int PIN_KEY = 2;
    public static final int TD_KEY = 3;

    public enum MasterKey_Format {
        CLEAR_FORMAT, ENCRYPTED_FORMAT
    }

    public static final int ALG_3DES = 1;
    public static final int ALG_SM4 = 3;
    public static final int ALG_AES = 5;

    public int getKeyType() {
        return keyType;
    }

    public int getKeyIdx() {
        return keyIdx;
    }

    public int getMkIdx() {
        return mkIdx;
    }

    public int getMkeyFormat() {
        return mkeyFormat;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getCheckValue() {
        return checkValue;
    }

    public PinPadGeneralParamIn setKeyType(int keyType) {
        this.keyType = keyType;
        return this;
    }

    public PinPadGeneralParamIn setKeyIdx(int keyIdx) {
        this.keyIdx = keyIdx;
        return this;
    }

    public PinPadGeneralParamIn setMkIdx(int mkIdx) {
        this.mkIdx = mkIdx;
        return this;
    }

    public PinPadGeneralParamIn setMkFormat(int mkeyFormat) {
        this.mkeyFormat = mkeyFormat;
        return this;
    }

    public PinPadGeneralParamIn setKey(byte[] key) {
        this.key = key;
        return this;
    }

    public PinPadGeneralParamIn setCheckValue(byte[] checkValue) {
        this.checkValue = checkValue;
        return this;
    }

    public static PinPadGeneralParamIn getInstance(){
        return new PinPadGeneralParamIn();
    }
        public int getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(int algorithmType) {
        this.algorithmType = algorithmType;
    }

    public boolean isMasterEncMasterMode() {
        return isMasterEncMasterMode;
    }

    public void setMasterEncMasterMode(boolean masterEncMasterMode) {
        isMasterEncMasterMode = masterEncMasterMode;
    }

    public int getEncryptMasterKeyIdex() {
        return encryptMasterKeyIdex;
    }

    public void setEncryptMasterKeyIdex(int encryptMasterKeyIdex) {
        this.encryptMasterKeyIdex = encryptMasterKeyIdex;
    }

}
