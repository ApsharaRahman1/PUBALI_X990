package com.vfi.android.domain.entities.tle;

import com.vfi.android.domain.entities.businessbeans.TLEConfig;

public class TLEPackageInfo {
    private TLEConfig tleConfig;
    private String smartCardSNHex;
    private String encSSKeyHex;
    private String kcvHex;
    private String terminalSN;

    public TLEConfig getTleConfig() {
        return tleConfig;
    }

    public void setTleConfig(TLEConfig tleConfig) {
        this.tleConfig = tleConfig;
    }

    public String getSmartCardSNHex() {
        return smartCardSNHex;
    }

    public void setSmartCardSNHex(String smartCardSNHex) {
        this.smartCardSNHex = smartCardSNHex;
    }

    public String getEncSSKeyHex() {
        return encSSKeyHex;
    }

    public void setEncSSKeyHex(String encSSKeyHex) {
        this.encSSKeyHex = encSSKeyHex;
    }

    public String getKcvHex() {
        return kcvHex;
    }

    public void setKcvHex(String kcvHex) {
        this.kcvHex = kcvHex;
    }

    public String getTerminalSN() {
        return terminalSN;
    }

    public void setTerminalSN(String terminalSN) {
        this.terminalSN = terminalSN;
    }
}
