package com.vfi.android.payment.presentation.utils.xmlparse;

import android.os.Environment;

import com.vfi.android.domain.entities.businessbeans.InstallmentPromo;
import com.vfi.android.domain.interactor.repository.UseCaseSaveInstallmentPromoList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class InstallmentPromoParser implements ConfigParser<InstallmentPromo> {
    private final String fileName = "/demo_xml/InstallmentPromos.xml";
    private final String beanTAG = "Promo";
    private final UseCaseSaveInstallmentPromoList useCaseSaveInstallmentPromoList;

    @Inject
    public InstallmentPromoParser(UseCaseSaveInstallmentPromoList useCaseSaveInstallmentPromoList) {
         this.useCaseSaveInstallmentPromoList = useCaseSaveInstallmentPromoList;
    }

    @Override
    public File getConfigFilePath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        return new File(sdcardPath + fileName);
    }

    @Override
    public List<InstallmentPromo> getBeanList() {
        return new ArrayList<>();
    }

    @Override
    public String getBeanTag() {
        return beanTAG;
    }

    @Override
    public InstallmentPromo createBean() {
        return new InstallmentPromo();
    }

    @Override
    public List<String> getBeanAttributes() {
        List<String> beanAttrs = new ArrayList<>();
        beanAttrs.add("Index");
        return beanAttrs;
    }

    @Override
    public void setBeanData(InstallmentPromo bean, String beanTagName, String value) {
        if (bean != null && beanTagName != null && value != null) {
            switch (beanTagName) {
                case "Index":
                    bean.setIndex(parseIntValue(value));
                    break;
                case "ENABLE":
                    bean.setEnablePromo(parseBooleanValue(value));
                    break;
                case "PROMO_LABEL":
                    bean.setPromoLabel(value);
                    break;
                case "TERM_LIST":
                    bean.setTermList(value);
                    break;
                case "PROMO_CODE":
                    bean.setPromoCode(value);
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
    public void doFinishProcess(List<InstallmentPromo> beanList) {
        if (beanList.size() <= 0) {
            return;
        }

        useCaseSaveInstallmentPromoList.execute(beanList);
    }
}
