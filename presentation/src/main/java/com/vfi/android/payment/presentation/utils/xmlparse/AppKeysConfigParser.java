package com.vfi.android.payment.presentation.utils.xmlparse;

import android.os.Environment;

import com.vfi.android.domain.entities.databeans.AppKeys;
import com.vfi.android.domain.entities.databeans.PinPadGeneralParamIn;
import com.vfi.android.domain.interactor.deviceservice.UseCaseLoadAppKeys;
import com.vfi.android.domain.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AppKeysConfigParser implements ConfigParser<AppKeys> {
    private final String fileName = "/demo_xml/App_Keys.xml";
    private final String beanTAG = "APP_KEYS";
    private final UseCaseLoadAppKeys useCaseLoadAppKeys;

    private int keyIndex;
    private int tmkIndex;
    private String keyHexString;
    private String kcvHexString;

    private final int PIN_KEY = 1;
    private final int MAC_KEY = 2;
    private final int DATA_KEY = 3;

    @Inject
    public AppKeysConfigParser(UseCaseLoadAppKeys useCaseLoadAppKeys) {
        this.useCaseLoadAppKeys = useCaseLoadAppKeys;
    }

    @Override
    public File getConfigFilePath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        return new File(sdcardPath + fileName);
    }

    @Override
    public List<AppKeys> getBeanList() {
        return new ArrayList<AppKeys>();
    }

    @Override
    public String getBeanTag() {
        return beanTAG;
    }

    @Override
    public AppKeys createBean() {
        AppKeys appKeys = new AppKeys();
        return appKeys;
    }

    @Override
    public List<String> getBeanAttributes() {
        List<String> beanAttrs = new ArrayList<>();
        beanAttrs.add("keyIndex");
        beanAttrs.add("tmkIndex");
        beanAttrs.add("kcv");
        return beanAttrs;
    }

    private int parseIntValue(String valueStr) {
        if (valueStr == null || valueStr.equals("null")) {
            return -1;
        }

        int value = 0;
        try {
            value = Integer.parseInt(valueStr);
        } catch (Exception e) {
            e.printStackTrace();
            value = 0;
        }

        return value;
    }

    private boolean parseBooleanValue(String valueBoolean) {
        boolean value = false;

        try {
            value = Boolean.parseBoolean(valueBoolean);
        } catch (Exception e) {
            value = false;
        }

        return value;
    }

    private long parseLongValue(String valueStr) {
        long value = 0;
        try {
            value = Long.parseLong(valueStr);
        } catch (Exception e) {
            e.printStackTrace();
            value = 0;
        }

        return value;
    }

    @Override
    public void setBeanData(AppKeys bean, String beanTagName, String value) {
        if (bean != null && beanTagName != null && value != null) {
            switch (beanTagName) {
                case "keyIndex":
                    keyIndex = parseIntValue(value);
                    break;
                case "tmkIndex":
                    tmkIndex = parseIntValue(value);
                    break;
                case "kcv":
                    kcvHexString = value;
                    break;
                case "TEK":
                    keyHexString = value;
                    bean.setTek(getTekPinpadGeneralParamIn());
                    break;
                case "TMK":
                    keyHexString = value;
                    bean.addTmk(getTmkPinpadGeneralParamIn());
                    break;
                case "DATA_KEY":
                    keyHexString = value;
                    bean.addDataKey(getTwkPinpadGeneralParamIn(DATA_KEY));
                    break;
                case "MAC_KEY":
                    keyHexString = value;
                    bean.addDataKey(getTwkPinpadGeneralParamIn(MAC_KEY));
                    break;
                case "PIN_KEY":
                    keyHexString = value;
                    bean.addDataKey(getTwkPinpadGeneralParamIn(PIN_KEY));
                    break;
            }
        }
    }

    @Override
    public void doFinishProcess(List<AppKeys> beanList) {
        if (beanList.size() <= 0) {
            return;
        }

        for (AppKeys appKeys : beanList) {
            useCaseLoadAppKeys.asyncExecuteWithoutResult(appKeys);
        }
    }

    private PinPadGeneralParamIn getTekPinpadGeneralParamIn() {
        int tekId = 0;
        PinPadGeneralParamIn tekParamIn = new PinPadGeneralParamIn();
        tekParamIn.setKeyIdx(tekId);
        tekParamIn.setKey(StringUtil.hexStr2Bytes(keyHexString));
        tekParamIn.setKey(StringUtil.hexStr2Bytes(keyHexString));
        if (kcvHexString == null || kcvHexString.length() == 0) {
            tekParamIn.setCheckValue(StringUtil.hexStr2Bytes(kcvHexString));
        }
        return tekParamIn;
    }

    private PinPadGeneralParamIn getTmkPinpadGeneralParamIn() {
        PinPadGeneralParamIn masterKeyParamIn = new PinPadGeneralParamIn();
        masterKeyParamIn.setKeyIdx(keyIndex);
        masterKeyParamIn.setAlgorithmType(PinPadGeneralParamIn.ALG_3DES);
        masterKeyParamIn.setKey(StringUtil.hexStr2Bytes(keyHexString));
        if (kcvHexString == null || kcvHexString.length() == 0) {
            kcvHexString = null;
        }
        masterKeyParamIn.setCheckValue(StringUtil.hexStr2Bytes(kcvHexString));
        return masterKeyParamIn;
    }

    private PinPadGeneralParamIn getTwkPinpadGeneralParamIn(int workKeyType) {
        PinPadGeneralParamIn workKeyParamIn = new PinPadGeneralParamIn();
        switch (workKeyType) {
            case DATA_KEY:
                workKeyParamIn.setKeyType(PinPadGeneralParamIn.TD_KEY);
                break;
            case PIN_KEY:
                workKeyParamIn.setKeyType(PinPadGeneralParamIn.PIN_KEY);
                break;
            case MAC_KEY:
                workKeyParamIn.setKeyType(PinPadGeneralParamIn.MAC_KEY);
                break;
        }
        workKeyParamIn.setMkIdx(tmkIndex);
        workKeyParamIn.setKeyIdx(keyIndex);
        workKeyParamIn.setKey(StringUtil.hexStr2Bytes(keyHexString));
        if (kcvHexString == null || kcvHexString.length() == 0) {
            kcvHexString = null;
        }
        workKeyParamIn.setCheckValue(StringUtil.hexStr2Bytes(kcvHexString));
        return workKeyParamIn;
    }
}
