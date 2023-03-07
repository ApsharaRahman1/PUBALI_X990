package com.vfi.android.payment.presentation.utils.xmlparse;

import android.os.Environment;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.entities.consts.CVV2ControlType;
import com.vfi.android.domain.interactor.repository.UseCaseSaveCardBinInfoList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CardConfigParser implements ConfigParser<CardBinInfo> {
    private final String fileName = "/demo_xml/CardBins.xml";
    private final String beanTAG = "Card";
    private final UseCaseSaveCardBinInfoList useCaseSaveCardBinInfoList;

    @Inject
    public CardConfigParser(UseCaseSaveCardBinInfoList useCaseSaveCardBinInfoList) {
         this.useCaseSaveCardBinInfoList = useCaseSaveCardBinInfoList;
    }

    @Override
    public File getConfigFilePath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        return new File(sdcardPath + fileName);
    }

    @Override
    public List<CardBinInfo> getBeanList() {
        return new ArrayList<>();
    }

    @Override
    public String getBeanTag() {
        return beanTAG;
    }

    @Override
    public CardBinInfo createBean() {
        CardBinInfo cardBinInfo = new CardBinInfo();
        // set default value
        cardBinInfo.setCvv2Min(3);
        cardBinInfo.setCvv2Max(3);
        cardBinInfo.setCvv2Control(CVV2ControlType.NO_NEED);
        cardBinInfo.setAllowInstallment(true);
        cardBinInfo.setAllowOfflineSale(true);
        cardBinInfo.setAllowRefund(true);
        return cardBinInfo;
    }

    @Override
    public List<String> getBeanAttributes() {
        List<String> beanAttrs = new ArrayList<>();
        beanAttrs.add("Index");
        return beanAttrs;
    }

    @Override
    public void setBeanData(CardBinInfo bean, String beanTagName, String value) {
        if (bean != null && beanTagName != null && value != null) {
            switch (beanTagName) {
                case "Index":
                    bean.setCardIndex(parseIntValue(value));
                    break;
                case "CARD_NAME":
                    bean.setCardName(value);
                    break;
                case "CARD_TYPE":
                    bean.setCardType(parseIntValue(value));
                    break;
                case "CARD_LABEL":
                    bean.setCardLabel(value);
                    break;
                case "PAN_LOW":
                    bean.setPanLow(value);
                    break;
                case "PAN_HIGH":
                    bean.setPanHigh(value);
                    break;
                case "HOST_INDEXS":
                    bean.setHostIndexs(value);
                    break;
                case "CVV2_CONTROL":
                    bean.setCvv2Control(parseIntValue(value));
                    break;
                case "CVV2_MIN":
                    bean.setCvv2Min(parseIntValue(value));
                    break;
                case "CVV2_MAX":
                    bean.setCvv2Max(parseIntValue(value));
                    break;
                case "ISSUER_ID":
                    bean.setIssueId(value);
                    break;
                case "ALLOW_INSTALLMENT":
                    bean.setAllowInstallment(parseBooleanValue(value));
                    break;
                case "ALLOW_OFFLINE_SALE":
                    bean.setAllowOfflineSale(parseBooleanValue(value));
                    break;
                case "ALLOW_REFUND":
                    bean.setAllowRefund(parseBooleanValue(value));
                    break;
                case "ENABLE_CHECK_LUHN":
                    bean.setEnableCheckLuhn(parseBooleanValue(value));
                    break;
                case "TIP_PERCENT":
                    bean.setTipPercent(parseIntValue(value));
                    break;
                case "ALLOW_PIN_BYPASS":
                    bean.setAllowPinBypass(parseBooleanValue(value));
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
    public void doFinishProcess(List<CardBinInfo> beanList) {
        if (beanList.size() <= 0) {
            return;
        }

        useCaseSaveCardBinInfoList.execute(beanList);
    }
}
