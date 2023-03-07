package com.vfi.android.domain.entities.databeans;

import java.util.List;

/**
 * Created by fusheng.z on 2017/11/30.
 */

public class PinPadInitPinInputCustomViewParamIn {
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
    private byte[] keyboardNumberPosition; // number position in keyboard, if it is null, display random number

    private byte[] random; // only used for pup project.

    private List<PinKeyCoordinate> pinKeyCoordinates;
    private PinpadListener pinpadListener;
    private int maxPinLen = 12;

    public interface PinpadListener {
        public void onInput(int len, int key);
        public void onConfirm(byte[] data, boolean isNonePin);
        public void onCancel();
        public void onError(int errorCode, String errorMsg);
    }

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

    public List<PinKeyCoordinate> getPinKeyCoordinates() {
        return pinKeyCoordinates;
    }

    public void setPinKeyCoordinates(List<PinKeyCoordinate> pinKeyCoordinates) {
        this.pinKeyCoordinates = pinKeyCoordinates;
    }

    public void setPinpadListener(PinpadListener pinpadListener) {
        this.pinpadListener = pinpadListener;
    }

    public PinpadListener getPinpadListener() {
        return pinpadListener;
    }

    public int getMaxPinLen() {
        return maxPinLen;
    }

    public void setMaxPinLen(int maxPinLen) {
        this.maxPinLen = maxPinLen;
    }

    public byte[] getRandom() {
        return random;
    }

    public void setRandom(byte[] random) {
        this.random = random;
    }

    public byte[] getKeyboardNumberPosition() {
        return keyboardNumberPosition;
    }

    public void setKeyboardNumberPosition(byte[] keyboardNumberPosition) {
        this.keyboardNumberPosition = keyboardNumberPosition;
    }
}
