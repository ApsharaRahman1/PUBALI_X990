package com.vfi.android.domain.entities.databeans;

import java.util.ArrayList;
import java.util.List;

public class AppKeys {
    private PinPadGeneralParamIn tek;
    private List<PinPadGeneralParamIn> tmkList;
    private List<PinPadGeneralParamIn> dataKeyList;
    private List<PinPadGeneralParamIn> macKeyList;
    private List<PinPadGeneralParamIn> pinKeyList;

    public AppKeys() {
        tmkList = new ArrayList<>();
        dataKeyList = new ArrayList<>();
        macKeyList = new ArrayList<>();
        pinKeyList = new ArrayList<>();
    }

    public void addTmk(PinPadGeneralParamIn tmkParam) {
        tmkList.add(tmkParam);
    }

    public void addDataKey(PinPadGeneralParamIn dataKeyParam) {
        dataKeyList.add(dataKeyParam);
    }

    public void addMacKey(PinPadGeneralParamIn macKeyParam) {
        macKeyList.add(macKeyParam);
    }

    public void addPinKey(PinPadGeneralParamIn pinKeyParam) {
        pinKeyList.add(pinKeyParam);
    }

    public PinPadGeneralParamIn getTek() {
        return tek;
    }

    public void setTek(PinPadGeneralParamIn tek) {
        this.tek = tek;
    }

    public List<PinPadGeneralParamIn> getTmkList() {
        return tmkList;
    }

    public void setTmkList(List<PinPadGeneralParamIn> tmkList) {
        this.tmkList = tmkList;
    }

    public List<PinPadGeneralParamIn> getDataKeyList() {
        return dataKeyList;
    }

    public void setDataKeyList(List<PinPadGeneralParamIn> dataKeyList) {
        this.dataKeyList = dataKeyList;
    }

    public List<PinPadGeneralParamIn> getMacKeyList() {
        return macKeyList;
    }

    public void setMacKeyList(List<PinPadGeneralParamIn> macKeyList) {
        this.macKeyList = macKeyList;
    }

    public List<PinPadGeneralParamIn> getPinKeyList() {
        return pinKeyList;
    }

    public void setPinKeyList(List<PinPadGeneralParamIn> pinKeyList) {
        this.pinKeyList = pinKeyList;
    }
}
