package com.vfi.android.domain.entities.databeans;

public class InputTLEPinResult {
    private boolean isSuccess;
    private int remainedRetryTimes;

    public InputTLEPinResult(boolean isSuccess, int remainedRetryTimes) {
        this.isSuccess = isSuccess;
        this.remainedRetryTimes = remainedRetryTimes;
    }

    public int getRemainedRetryTimes() {
        return remainedRetryTimes;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
