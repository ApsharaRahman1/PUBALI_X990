package com.vfi.android.domain.entities.databeans;

/**
 * Created by chong.z on 2018/1/22.
 */

public class ScannerParamOut {
    public static final int SCAN_SUCCESS  = 0;
    public static final int SCAN_ERROR   = 1;
    public static final int SCAN_TIMEOUT = 2;
    public static final int SCAN_CANCEL  = 3;

    private int scanResult;
    private String scanCode;

    public int getScanResult() {
        return scanResult;
    }

    public void setScanResult(int scanResult) {
        this.scanResult = scanResult;
    }

    public String getScanCode() {
        return scanCode;
    }

    public void setScanCode(String scanCode) {
        this.scanCode = scanCode;
    }
}
