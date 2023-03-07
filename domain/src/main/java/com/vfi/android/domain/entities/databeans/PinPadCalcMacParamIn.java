package com.vfi.android.domain.entities.databeans;

/**
 * Created by fusheng.z on 2017/11/30.
 */

public class PinPadCalcMacParamIn {
    private int keyId;                //密钥ID
    private int calcType;             //计算类型 0x00-MAC X99; 0x01-MAC X919;
                                      // 0x02 - ECB cup standard ECB algorithm; 0x03 - MAC 9606;
    private byte[] CBCInitVec;        //CBC initial vector. fixed length 8,
                                      //can be null, default 8 bytes 0x00
    private byte[] data;              //计算MAC的数据

    private int desType;              //0x00-des 0x01-3des  0x02-sm4  0x03-aes

    public int getKeyId(){
        return keyId;
    }

    public byte[] getData(){
        return data;
    }

    public int getCalcType() {
        return calcType;
    }

    public byte[] getCBCInitVec() {
        return CBCInitVec;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setCalcType(int calcType) {
        this.calcType = calcType;
    }

    public void setCBCInitVec(byte[] CBCInitVec) {
        this.CBCInitVec = CBCInitVec;
    }

    public int getDesType() {
        return desType;
    }

    public void setDesType(int desType) {
        this.desType = desType;
    }
}
