package com.vfi.android.payment.presentation.utils.xmlparse;

import android.os.Environment;

import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.interactor.repository.UseCaseSaveMerchantInfoList;
import com.vfi.android.domain.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MerchantConfigParser implements ConfigParser<MerchantInfo> {
    private final String fileName = "/demo_xml/Merchants.xml";
    private final String beanTAG = "Merchant";
    private final UseCaseSaveMerchantInfoList useCaseSaveMerchantInfoList;

    @Inject
    public MerchantConfigParser(UseCaseSaveMerchantInfoList useCaseSaveMerchantInfoList) {
        this.useCaseSaveMerchantInfoList = useCaseSaveMerchantInfoList;
    }

    @Override
    public File getConfigFilePath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        return new File(sdcardPath + fileName);
    }

    @Override
    public List<MerchantInfo> getBeanList() {
        return new ArrayList<MerchantInfo>();
    }

    @Override
    public String getBeanTag() {
        return beanTAG;
    }

    @Override
    public MerchantInfo createBean() {
        return new MerchantInfo();
    }

    @Override
    public List<String> getBeanAttributes() {
        List<String> beanAttrs = new ArrayList<>();
        beanAttrs.add("Index");
        return beanAttrs;
    }

    @Override
    public void setBeanData(MerchantInfo bean, String beanTagName, String value) {
        if (bean != null && beanTagName != null && value != null) {
            switch (beanTagName) {
                case "Index":
                    bean.setMerchantIndex(parseIntValue(value));
                    break;
                case "MERCHANT_NAME":
                    bean.setMerchantName(StringUtil.getNonNullString(value));
                    break;
                case "TERMINAL_ID":
                    bean.setTerminalId(StringUtil.getNonNullStringLeftPadding(value, 8));
                    break;
                case "MERCHANT_ID":
                    bean.setMerchantId(StringUtil.getNonNullStringLeftPadding(value, 15));
                    break;
                case "TRACE_NUM":
                    bean.setTraceNum(StringUtil.getNonNullStringLeftPadding(value, 6));
                    break;
                case "BATCH_NUM":
                    bean.setBatchNum(StringUtil.getNonNullStringLeftPadding(value, 6));
                    break;
                case "CURRENCY_INDEX":
                    bean.setCurrencyIndex(parseIntValue(value));
                    break;
                case "ENABLE_MULTI_CURRENCY":
                    bean.setEnableMultiCurrency(parseBooleanValue(value));
                    break;
                case "PRINT_PARAM_INDEX":
                    bean.setPrintParamIndex(parseIntValue(value));
                    break;
                case "AMOUNT_DIGITS":
                    bean.setAmountDigits(parseIntValue(value));
                    break;
                case "MIN_AMOUNT":
                    bean.setMinAmount(parseLongValue(value));
                    break;
                case "MAX_AMOUNT":
                    bean.setMaxAmount(parseLongValue(value));
                    break;
                case "ENABLE_NMX":
                    bean.setEnableTle(parseBooleanValue(value));
                    break;
                case "NMX_INDEX":
                    bean.setTleIndex(parseIntValue(value));
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
    public void doFinishProcess(List<MerchantInfo> beanList) {
        if (beanList.size() <= 0) {
            return;
        }

        useCaseSaveMerchantInfoList.execute(beanList);
    }
}
