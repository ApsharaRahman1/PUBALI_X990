package com.vfi.android.domain.entities.databeans;

/**
 * Created by Yaping on 29/11/2017.
 */

public class CAPKParams {

    private int operation;

    private String capkStr;

    public int getCAPKOperation() {
        return operation;
    }

    public void setCAPKOperation(int operation) {
        this.operation = operation;
    }

    public String getCAPKStr() {
        return capkStr;
    }

    public void setCAPKStr(String aidStr) {
        this.capkStr = aidStr;
    }

}
