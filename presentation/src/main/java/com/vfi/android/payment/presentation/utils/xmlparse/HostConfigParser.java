package com.vfi.android.payment.presentation.utils.xmlparse;

import android.os.Environment;

import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.consts.HostType;
import com.vfi.android.domain.interactor.repository.UseCaseSaveHostInfoList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class HostConfigParser implements ConfigParser<HostInfo> {
    private final String fileName = "/demo_xml/Hosts.xml";
    private final String beanTAG = "Host";
    private final UseCaseSaveHostInfoList useCaseSaveHostInfoList;

    @Inject
    public HostConfigParser(UseCaseSaveHostInfoList useCaseSaveHostInfoList) {
        this.useCaseSaveHostInfoList = useCaseSaveHostInfoList;
    }

    @Override
    public File getConfigFilePath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        return new File(sdcardPath + fileName);
    }

    @Override
    public List<HostInfo> getBeanList() {
        return new ArrayList<HostInfo>();
    }

    @Override
    public String getBeanTag() {
        return beanTAG;
    }

    @Override
    public HostInfo createBean() {
        return new HostInfo();
    }

    @Override
    public List<String> getBeanAttributes() {
        List<String> beanAttrs = new ArrayList<>();
        beanAttrs.add("Index");
        return beanAttrs;
    }

    @Override
    public void setBeanData(HostInfo bean, String beanTagName, String value) {
        if (bean != null && beanTagName != null && value != null) {
            switch (beanTagName) {
                case "Index":
                    bean.setHostIndex(parseIntValue(value));
                    break;
                case "HOST_NAME":
                    bean.setHostName(value);
                    bean.setHostType(HostType.toHostType(value));
                    break;
                case "NII":
                    bean.setNII(value);
                    break;
                case "TPDU":
                    bean.setTPDU(value);
                    break;
                case "PRIMARY_IP":
                    bean.setPrimaryIp(value);
                    break;
                case "PRIMARY_PORT":
                    bean.setPrimaryPort(parseIntValue(value));
                    break;
                case "SECONDARY_IP":
                    bean.setSecondaryIp(value);
                    break;
                case "SECONDARY_PORT":
                    bean.setSecondaryPort(parseIntValue(value));
                    break;
                case "MERCHANT_INDEXS":
                    bean.setMerchantIndexs(value);
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
    public void doFinishProcess(List<HostInfo> beanList) {
        if (beanList.size() <= 0) {
            return;
        }

        useCaseSaveHostInfoList.execute(beanList);
    }
}
