package com.vfi.android.payment.presentation.utils.xmlparse;

import android.os.Environment;

import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.interactor.repository.UseCaseSaveTerminalCfg;
import com.vfi.android.domain.utils.EncryptionUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.presentation.receivers.AutoSettlementReceiver;
import com.vfi.android.payment.presentation.receivers.ForceSettlementReceiver;
import com.vfi.android.payment.presentation.utils.AndroidUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

public class TerminalConfigParser implements ConfigParser<TerminalCfg> {
    private final String fileName = "/demo_xml/TerminalConfig.xml";
    private final String beanTAG = "Terminal";
    private final UseCaseSaveTerminalCfg useCaseSaveTerminalCfg;

    @Inject
    public TerminalConfigParser(UseCaseSaveTerminalCfg useCaseSaveTerminalCfg) {
        this.useCaseSaveTerminalCfg = useCaseSaveTerminalCfg;
    }

    @Override
    public File getConfigFilePath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        return new File(sdcardPath + fileName);
    }

    @Override
    public List<TerminalCfg> getBeanList() {
        return new ArrayList<TerminalCfg>();
    }

    @Override
    public String getBeanTag() {
        return beanTAG;
    }

    @Override
    public TerminalCfg createBean() {
        TerminalCfg terminalCfg = new TerminalCfg();
        terminalCfg.initDefaultSystemParameter();
        return terminalCfg;
    }

    @Override
    public List<String> getBeanAttributes() {
        return null;
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
    public void setBeanData(TerminalCfg bean, String beanTagName, String value) {
        if (bean != null && beanTagName != null && value != null) {
            switch (beanTagName) {
                case "OPERATION_TIMEOUT":
                    bean.setOperationTimeout(parseIntValue(value));
                    break;
                case "SUPER_PASSWORD":
                    bean.setSuperPassword(EncryptionUtil.getMd5HexString(value.getBytes()));
                    break;
                case "TRANS_MANAGER_PASSWORD":
                    bean.setTransManagerPassword(EncryptionUtil.getMd5HexString(value.getBytes()));
                    break;
                case "SETTING_PASSWORD":
                    bean.setSettingPassword(EncryptionUtil.getMd5HexString(value.getBytes()));
                    break;
                case "MANUAL_PASSWORD":
                    bean.setManualPassword(EncryptionUtil.getMd5HexString(value.getBytes()));
                    break;
                case "INVOICE_NUM":
                    bean.setSysInvoiceNum(StringUtil.getNonNullStringLeftPadding(value, 6));
                    break;
                case "ENABLE_AUTO_SETTLEMENT":
                    bean.setEnableAutoSettlement(parseBooleanValue(value));
                    break;
                case "AUTO_SETTLE_TIME":
                    bean.setAutoSettlementTime(value);
                    break;
                case "ENABLE_FORCE_SETTLEMENT":
                    bean.setEnableForceSettlement(parseBooleanValue(value));
                    break;
                case "FORCE_SETTLE_TIME":
                    bean.setForceSettlementTime(value);
                    break;
                case "ALLOW_FALLBACK":
                    bean.setAllowFallback(parseBooleanValue(value));
                    break;
                case "FALLBACK_RETRY_TIME":
                    bean.setFallbackRetryTime(parseIntValue(value));
                    break;
                case "MAX_OFFLINE_UPLOAD_NUM":
                    bean.setMaxOfflineUploadNum(parseIntValue(value));
                    break;
                case "OFFLINE_UPLOAD_LIMIT":
                    bean.setOfflineUploadLimit(parseIntValue(value));
                    break;
                case "ENABLE_TRAINING_MODE":
                    bean.setEnableTrainingMode(parseBooleanValue(value));
                    break;
                case "MAX_PREAUTH_AMOUNT":
                    bean.setMaxPreAuthAmount(parseLongValue(value));
                    break;
                case "ENABLE_TIP":
                    bean.setEnableTip(parseBooleanValue(value));
                    break;
                case "MAX_ADJUST_TIMES":
                    bean.setMaxAdjustTimes(parseIntValue(value));
                    break;
                case "ENABLE_ISO_LOG":
                    bean.setEnableISOLog(parseBooleanValue(value));
                    break;
            }
        }
    }

    @Override
    public void doFinishProcess(List<TerminalCfg> beanList) {
        if (beanList.size() <= 0) {
            return;
        }

        Iterator<TerminalCfg> iterator = beanList.iterator();
        while (iterator.hasNext()) {
            TerminalCfg terminal = iterator.next();
            if (terminal.isEnableAutoSettlement()) {
                AndroidUtil.sendBroadcast(AutoSettlementReceiver.ACTION_SET_AUTO_SETTLEMENT_TIMER);
            } else {
                AndroidUtil.sendBroadcast(AutoSettlementReceiver.ACTION_CLEAR_AUTO_SETTLEMENT_TIMER);
            }
            if (terminal.isEnableForceSettlement()) {
                AndroidUtil.sendBroadcast(ForceSettlementReceiver.ACTION_SET_FORCE_SETTLEMENT_TIMER);
            } else {
                AndroidUtil.sendBroadcast(ForceSettlementReceiver.ACTION_CLEAR_FORCE_SETTLEMENT_TIMER);
            }
            useCaseSaveTerminalCfg.execute(terminal);
        }
    }
}
