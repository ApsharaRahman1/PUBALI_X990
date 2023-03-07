package com.vfi.android.domain.entities.databeans;

/**
 * Created by fusheng.z on 2017/11/30.
 */

public class PinPadEncryptTrackParamIn {
    private int keyId;                //密钥ID
    private int mode;                 //加密模式 0：ECB模式，1：CBC模式
    private int algorithmType;        //algorithmType type 0x01-3des 0x02-SM4 0x03-AES
    private byte[] trkData;           //磁道数据
    private byte[] iv;                //IV

    public PinPadEncryptTrackParamIn(int keyId, int mode, int algorithmType, byte[] trkData, byte[] iv){
        this.keyId = keyId;
        this.mode = mode;
        this.algorithmType = algorithmType;
        this.trkData = trkData;
        this.iv = iv;
    }

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(int algorithmType) {
        this.algorithmType = algorithmType;
    }

    public byte[] getTrkData() {
        return trkData;
    }

    public void setTrkData(byte[] trkData) {
        this.trkData = trkData;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }
}
