package com.vfi.android.payment.presentation.utils.xmlparse;

import android.os.Environment;

import com.vfi.android.domain.entities.businessbeans.TLEConfig;
import com.vfi.android.domain.interactor.repository.UseCaseSaveTleConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TleConfigParser implements ConfigParser<TLEConfig> {
    private final String fileName = "/rcbc_xml/TLEConfig.xml";
    private final String beanTAG = "TleConfig";
    private final UseCaseSaveTleConfig useCaseSaveTleConfig;

    @Inject
    public TleConfigParser(UseCaseSaveTleConfig useCaseSaveTleConfig) {
        this.useCaseSaveTleConfig = useCaseSaveTleConfig;
    }

    @Override
    public File getConfigFilePath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        return new File(sdcardPath + fileName);
    }

    @Override
    public List<TLEConfig> getBeanList() {
        return new ArrayList<TLEConfig>();
    }

    @Override
    public String getBeanTag() {
        return beanTAG;
    }

    @Override
    public TLEConfig createBean() {
        return new TLEConfig();
    }

    @Override
    public List<String> getBeanAttributes() {
        List<String> beanAttrs = new ArrayList<>();
        beanAttrs.add("Index");
        return beanAttrs;
    }

    @Override
    public void setBeanData(TLEConfig bean, String beanTagName, String value) {
        if (bean != null && beanTagName != null && value != null) {
            switch (beanTagName) {
                case "Index":
                    bean.setIndex(parseIntValue(value));
                    break;
                case "TPDU":
                    bean.setRkiTPDU(value);
                    break;
                case "APP_ID":
                    bean.setAppId(value);
                    break;
                case "RKI_VENDOR_ID":
                    bean.setRkiVendorId(value);
                    break;
                case "RKI_ACQUIRER_ID":
                    bean.setRkiAcquirerId(value);
                    break;
                case "ENCRYPTION_ALGO":
                    bean.setEncryptionAlgo(value);
                    break;
                case "KEY_MANAGEMENT":
                    bean.setKeyManagement(value);
                    break;
                case "MAC_ALGO":
                    bean.setMacAlgo(value);
                    break;
                case "COMMAND_CODE":
                    bean.setCmdCode(value);
                    break;
                case "DEVICE_MODEL_CODE":
                    bean.setDeviceModelCode(value);
                    break;
            }
        }
    }

    private int parseIntValue(String valueStr) {
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

    @Override
    public void doFinishProcess(List<TLEConfig> beanList) {
        if (beanList.size() <= 0) {
            return;
        }

        for (TLEConfig tleConfig : beanList) {
            useCaseSaveTleConfig.execute(tleConfig);
        }
    }
}
