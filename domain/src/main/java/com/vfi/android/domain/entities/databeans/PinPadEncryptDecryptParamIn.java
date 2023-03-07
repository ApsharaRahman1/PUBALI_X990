package com.vfi.android.domain.entities.databeans;

/**
 * Created by yichao.t on 2018/1/12.
 */

public class PinPadEncryptDecryptParamIn {

    public static final int MODE_ENCRYPT = 0x00;
    public static final int MODE_DECRYPT = 0x01;


    public static final int TYPE_DES = 0x00;
    public static final int TYPE_3DES = 0x01;
    public static final int TYPE_SM4 = 0x02;
    public static final int TYPE_AES = 0x03;


    /** 加解密 | encrypt or decrypt data <br/>
    * @param mode - 加解密模式 | the mode of encrypt or decrypt
    * <ul>
    * <li>MODE_ENCRYPT - 加密 | </li>
    * <li>MODE_DECRYPT - 解密 | </li>
    * </ul>
    **/
    int mode;

    /**
    * @param desType - 加解密类型 | the type of encrypt or decrypt
    * <ul>
    * <li>TYPE_DES - DES Type | </li>
    * <li>TYPE_3DES - 3DES Type | </li>
    * <li>TYPE_SM4 - SM4 Type | </li>
    * <li>TYPE_AES - AES Type| </li>
    * </ul>
    **/
    int desTpye;

    /**
    * @param key - 计算key | the source key
    **/
    byte[] inKey;

    /**
    * @param data - 计算数据 | the source data
    **/
    byte[] inData;

    public PinPadEncryptDecryptParamIn() {
        this.mode = MODE_ENCRYPT;
        this.desTpye = TYPE_3DES;
        this.inKey = new byte[16];
        this.inData = new byte[16];
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getDesTpye() {
        return desTpye;
    }

    public void setDesTpye(int desTpye) {
        this.desTpye = desTpye;
    }

    public byte[] getInKey() {
        return inKey;
    }

    public void setInKey(byte[] inKey) {
        this.inKey = inKey;
    }

    public byte[] getInData() {
        return inData;
    }

    public void setInData(byte[] inData) {
        this.inData = inData;
    }
}
