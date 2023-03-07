package com.vfi.android.domain.entities.databeans;

/**
 * Created by yunlongg1 on 19/10/2017.*/

public class EmvProcessOnlineResult {

    private int result;
    private String reversalData;
    private String scriptResult;
    private String tcData;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getReversalData() {
        return reversalData;
    }

    public void setReversalData(String reversalData) {
        this.reversalData = reversalData;
    }

    public String getScriptResult() {
        return scriptResult;
    }

    public void setScriptResult(String scriptResult) {
        this.scriptResult = scriptResult;
    }

    public String getTcData() {
        return tcData;
    }

    public void setTcData(String tcData) {
        this.tcData = tcData;
    }
}
