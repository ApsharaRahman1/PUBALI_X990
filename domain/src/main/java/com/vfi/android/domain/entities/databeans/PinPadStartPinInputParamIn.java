package com.vfi.android.domain.entities.databeans;

/**
 * Created by fusheng.z on 2017/11/30.
 */

public class PinPadStartPinInputParamIn {
    public static final int TDES_TYPE = 0;
    public static final int SM4_TYPE = 1;

    private int keyId;                //Pin密钥索引
    private byte[] pinLimit;          //允许输入密码的长度
    private int timeout;              //输入超时时间，单位（秒）
    private boolean isOnline;         //是否联机PIN
    private String promptString;      //提示信息
    private String pan;               //用于加密联机PIN的主帐号（卡号）
    private int keyType;              //密钥类型/输入方式
    private int desType;              //算法计算方式

    public int getKeyId() {
        return keyId;
    }

    public byte[] getPinLimit() {
        return pinLimit;
    }

    public boolean getOnLineState() {
        return isOnline;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getPromptString() {
        return promptString;
    }

    public String getPan() {
        return pan;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public void setPinLimit(byte[] pinLimit) {
        this.pinLimit = pinLimit;
    }

    public void setOnlineState(boolean online) {
        isOnline = online;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setPromptString(String promptString) {
        this.promptString = promptString;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public int getDesType() {
        return desType;
    }

    public void setDesType(int desType) {
        this.desType = desType;
    }
}
