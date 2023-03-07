package com.vfi.android.payment.presentation.utils.xmlparse;

import android.os.Environment;

import com.vfi.android.domain.entities.databeans.CAPKParams;
import com.vfi.android.domain.entities.databeans.EmvKey;
import com.vfi.android.domain.interactor.deviceservice.UseCaseConfigCAPKs;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

public class EmvKeysConfigParser implements ConfigParser<EmvKey>{
    private final String fileName = "/demo_xml/EMV_keys.xml";
    private final String beanTAG = "CapKey";
    private final UseCaseConfigCAPKs useCaseConfigCAPKs;

    @Inject
    public EmvKeysConfigParser(UseCaseConfigCAPKs useCaseConfigCAPKs) {
        this.useCaseConfigCAPKs = useCaseConfigCAPKs;
    }

    @Override
    public File getConfigFilePath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        return new File(sdcardPath + fileName);
    }

    @Override
    public List<EmvKey> getBeanList() {
        return new ArrayList<EmvKey>();
    }

    @Override
    public String getBeanTag() {
        return beanTAG;
    }

    @Override
    public EmvKey createBean() {
        return new EmvKey();
    }

    @Override
    public List<String> getBeanAttributes() {
        List<String> beanAttrs = new ArrayList<>();
        beanAttrs.add("Index");
        beanAttrs.add("RID");
        return beanAttrs;
    }

    @Override
    public void setBeanData(EmvKey bean, String beanTagName, String value) {
        if (bean != null && beanTagName != null && value != null) {
            switch (beanTagName) {
                case "Index":
                    bean.setKeyIndex(value);
                    break;
                case "RID":
                    bean.setRid(value);
                    break;
                case "Exponent":
                    bean.setExponent(value);
                    break;
                case "KeyLen":
                    bean.setKeyLen(value);
                    break;
                case "Key":
                    bean.setKey(value);
                    break;
                case "Hash":
                    bean.setHash(value);
                    break;
                case "HashAlgoIndicator":
                    bean.setHashAlgoIndicator(value);
                    break;
                case "PKAlgoIndicator":
                    bean.setPkAlgoIndicator(value);
                    break;
                case "ExpiryDate":
                    bean.setExpiryDate(value);
                    break;
            }
        }
    }

    @Override
    public void doFinishProcess(List<EmvKey> beanList) {
        CAPKParams capks = new CAPKParams();
        capks.setCAPKOperation(3);
        capks.setCAPKStr("");
        useCaseConfigCAPKs.execute(capks);

        Iterator<EmvKey> iterator = beanList.iterator();
        while (iterator.hasNext()) {
            EmvKey emvKey = iterator.next();
            capks = new CAPKParams();
            capks.setCAPKOperation(1);
            capks.setCAPKStr(emvKey.toEmvKeyStr());
            useCaseConfigCAPKs.execute(capks);
        }
    }
}
