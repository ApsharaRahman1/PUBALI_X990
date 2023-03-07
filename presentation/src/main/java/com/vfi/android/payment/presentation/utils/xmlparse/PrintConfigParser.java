package com.vfi.android.payment.presentation.utils.xmlparse;

import android.os.Environment;

import com.vfi.android.domain.entities.businessbeans.PrintConfig;
import com.vfi.android.domain.interactor.repository.UseCaseSavePrintConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

public class PrintConfigParser implements ConfigParser<PrintConfig> {
    private final String fileName = "/demo_xml/PrintConfig.xml";
    private final String beanTAG = "PrintConfig";
    private final UseCaseSavePrintConfig useCaseSavePrintConfig;

    @Inject
    public PrintConfigParser(UseCaseSavePrintConfig useCaseSavePrintConfig) {
        this.useCaseSavePrintConfig = useCaseSavePrintConfig;
    }

    @Override
    public File getConfigFilePath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        return new File(sdcardPath + fileName);
    }

    @Override
    public List<PrintConfig> getBeanList() {
        return new ArrayList<PrintConfig>();
    }

    @Override
    public String getBeanTag() {
        return beanTAG;
    }

    @Override
    public PrintConfig createBean() {
        PrintConfig printConfig = new PrintConfig();
        // set default value
        printConfig.setPrintBankCopy(true);
        printConfig.setPrintMerchantCopy(true);
        printConfig.setPrintCustomerCopy(true);
        return printConfig;
    }

    @Override
    public List<String> getBeanAttributes() {
        List<String> beanAttrs = new ArrayList<>();
        beanAttrs.add("Index");
        return beanAttrs;
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
    public void setBeanData(PrintConfig bean, String beanTagName, String value) {
        if (bean != null && beanTagName != null && value != null) {
            switch (beanTagName) {
                case "Index":
                    bean.setIndex(parseIntValue(value));
                    break;
                case "ENABLE_PRINT_CUSTOMER_COPY":
                    bean.setPrintCustomerCopy(parseBooleanValue(value));
                    break;
                case "ENABLE_PRINT_MERCHANT_COPY":
                    bean.setPrintMerchantCopy(parseBooleanValue(value));
                    break;
                case "ENABLE_PRINT_BANK_COPY":
                    bean.setPrintBankCopy(parseBooleanValue(value));
                    break;
                case "HEADER1":
                    bean.setHeader1(value);
                    break;
                case "HEADER2":
                    bean.setHeader2(value);
                    break;
                case "HEADER3":
                    bean.setHeader3(value);
                    break;
                case "HEADER4":
                    bean.setHeader4(value);
                    break;
                case "HEADER5":
                    bean.setHeader5(value);
                    break;
                case "HEADER6":
                    bean.setHeader6(value);
                    break;
                case "FOOTER1":
                    bean.setFooter1(value);
                    break;
                case "FOOTER2":
                    bean.setFooter2(value);
                    break;
                case "FOOTER3":
                    bean.setFooter3(value);
                    break;
                case "FOOTER4":
                    bean.setFooter4(value);
                    break;
                case "FOOTER5":
                    bean.setFooter5(value);
                    break;
                case "FOOTER6":
                    bean.setFooter6(value);
                    break;
            }
        }
    }

    @Override
    public void doFinishProcess(List<PrintConfig> beanList) {
        if (beanList.size() <= 0) {
            return;
        }

        Iterator<PrintConfig> iterator = beanList.iterator();
        while (iterator.hasNext()) {
            PrintConfig printConfig = iterator.next();
            useCaseSavePrintConfig.execute(printConfig);
        }
    }
}
