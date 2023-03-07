package com.vfi.android.domain.entities.databeans;

/**
 * Created by Yaping on 29/11/2017.
 */

public class AIDParams {

    /* 1：新增  | append
     * 2：删除  | remove
     * 3：清空  | clear*/
    private int operation;

     /* 1：接触式  | contact
     * 2：非接  | contactless*/
    private int aidPrmType;

    private String aidStr;

    public int getAIDOperation() {
        return operation;
    }

    public void setAIDOperation(int operation) {
        this.operation = operation;
    }

    public int getAIDPrmType() {
        return aidPrmType;
    }

    public void setAIDPrmType(int aidPrmType) {
        this.aidPrmType = aidPrmType;
    }

    public String getAIDStr() {
        return aidStr;
    }

    public void setAIDStr(String aidStr) {
        this.aidStr = aidStr;
    }

    @Override
    public String toString() {
        return "AIDParams{" +
                "operation=" + operation +
                ", aidPrmType=" + aidPrmType +
                ", aidStr='" + aidStr + '\'' +
                '}';
    }
}
