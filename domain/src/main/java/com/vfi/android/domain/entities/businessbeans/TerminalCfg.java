package com.vfi.android.domain.entities.businessbeans;

import com.vfi.android.domain.entities.annotations.Param;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;

import java.lang.reflect.Field;

/**
 * Created by Tony
 * 2018/12/6
 * email: chong.z@verifone.cn
 */
public class TerminalCfg  {

    /**
     * Transaction screen timeout (second)
     */
    @Param.Default(Integer = 60)
    private int operationTimeout;

    /**
     * Maximum number of retries for fallback
     */
    @Param.Default(Integer = 2)
    private int fallbackRetryTime;

    /**
     * Maximum number of offline upload in per online transaction.
     */
    private int maxOfflineUploadNum;

    /**
     * Maximum number of offline transaction which not upload.
     */
    private int offlineUploadLimit;

    /**
     * Enable/Disable automatic settlement
     */
    @Param.Default(Boolean = true)
    private boolean isEnableAutoSettlement;

    /**
     * Enable/Disable force settlement
     */
    @Param.Default(Boolean = true)
    private boolean isEnableForceSettlement;

    /**
     * Allow/Not allow fallback
     */
    @Param.Default(Boolean = true)
    private boolean isAllowFallback;

    /**
     * Enable/Disable training mode
     */
    @Param.Default(Boolean = false)
    private boolean isEnableTrainingMode;

    /**
     * Super manager password
     */
    @Param.Default(String = "E10ADC3949BA59ABBE56E057F20F883E")
    private String superPassword;

    /**
     * Setting password
     */
    @Param.Default(String = "A6B5CB94BE8FF4847D526B2ED5761FEE")
    private String settingPassword;

    /**
     * Transaction Manager password
     */
    @Param.Default(String = "C4CA4238A0B923820DCC509A6F75849B")
    private String transManagerPassword;

    /**
     * Password for manual card number
     */
    @Param.Default(String = "E10ADC3949BA59ABBE56E057F20F883E")
    private String manualPassword;

    /**
     * Global invoice number for all merchants and hosts.
     */
    @Param.Default(String = "000001")
    private String sysInvoiceNum;

    /**
     * Daily automatic settlement time
     */
    @Param.Default(String = "00:00")
    private String autoSettlementTime;

    /**
     * Daily force settlement time
     */
    @Param.Default(String = "00:00")
    private String forceSettlementTime;

    /**
     * Transaction communication connect timeout(secs).
     */
    @Param.Default(Integer = 30)
    private int connectTimeout;

    /**
     * Transaction communication receive timeout(secs).
     */
    @Param.Default(Integer = 60)
    private int receiveTimeout;

    /**
     * Whether remove final F in track data or not.
     */
    @Param.Default(Boolean = true)
    private boolean isNeedRemoveTrackTailF;

    /**
     * PIN_Alg_Type 0:3DES 1:SM4
     */
    @Param.Default(Integer = 0)
    private int pinAlgType;

    /**
     * If set it to true, pinpad keyboard will display 0 - 9 in random position.
     * otherwise display 0 - 9 from left top to right bottom like below:
     * 1 2 3
     * 4 5 6
     * 7 8 9
     * 0
     */
    @Param.Default(Boolean = true)
    private boolean isRandomPinpadKeyboard;

    /**
     * Enable/Disable electronic signature
     */
    @Param.Default(Boolean = true)
    private boolean isEnableESign;

    /**
     * Enable/Disable check luhn
     */
    @Param.Default(Boolean = true)
    private boolean isEnableCheckLuhn;

    /**
     * Enable/Disable tip
     */
    @Param.Default(Boolean = true)
    private boolean isEnableTip;

    /**
     * Max amount allow to do preAuth amount
     */
    @Param.Default(Long = 100000000)
    private long maxPreAuthAmount;

    /**
     * Max TipAdjust times
     */
    @Param.Default(Integer = 3)
    private int maxAdjustTimes;

    @Param.Default(Boolean = false)
    private boolean isEnableISOLog;

    /**
     * Interval retry time(mins) after auto settlement failed.
     */
    @Param.Default(Integer = 60)
    private int settleFailedRetryIntervalMins;

    /**
     * Use to process payment currency, for example Amount hint, print and so on.
     * If we not sure which currency should use, use this default value.
     */
    @Param.Default(String = "0608")
    private String defaultCurrencyCode;

    public void initDefaultSystemParameter() {
        String TAG = TAGS.Bean;
        Class<TerminalCfg> clazz = TerminalCfg.class;
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.isAnnotationPresent(Param.Default.class)) {
                try {
                    if (f.getType() == String.class) {
                        String value = f.getAnnotation(Param.Default.class).String();
                        f.set(this, value);
                        LogUtil.i(TAG, "String   name: " + String.format("%-30s", f.getName()) + "   value: " + value);
                    } else if (f.getType() == int.class) {
                        Integer value = f.getAnnotation(Param.Default.class).Integer();
                        f.set(this, value);
                        LogUtil.i(TAG, "Integer  name: " + String.format("%-30s", f.getName()) + "   value: " + value);
                    } else if (f.getType() == boolean.class) {
                        Boolean value = f.getAnnotation(Param.Default.class).Boolean();
                        f.set(this, value);
                        LogUtil.i(TAG, "Boolean  name: " + String.format("%-30s", f.getName()) + "   value: " + value);
                    } else if (f.getType() == long.class) {
                        Long value = f.getAnnotation(Param.Default.class).Long();
                        f.set(this, value);
                        LogUtil.i(TAG, "Long     name: " + String.format("%-30s", f.getName()) + "   value: " + value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getOperationTimeout() {
        return operationTimeout;
    }

    public void setOperationTimeout(int operationTimeout) {
        this.operationTimeout = operationTimeout;
    }

    public int getFallbackRetryTime() {
        return fallbackRetryTime;
    }

    public void setFallbackRetryTime(int fallbackRetryTime) {
        this.fallbackRetryTime = fallbackRetryTime;
    }

    public int getMaxOfflineUploadNum() {
        return maxOfflineUploadNum;
    }

    public void setMaxOfflineUploadNum(int maxOfflineUploadNum) {
        this.maxOfflineUploadNum = maxOfflineUploadNum;
    }

    public int getOfflineUploadLimit() {
        return offlineUploadLimit;
    }

    public void setOfflineUploadLimit(int offlineUploadLimit) {
        this.offlineUploadLimit = offlineUploadLimit;
    }

    public boolean isEnableAutoSettlement() {
        return isEnableAutoSettlement;
    }

    public void setEnableAutoSettlement(boolean enableAutoSettlement) {
        isEnableAutoSettlement = enableAutoSettlement;
    }

    public boolean isEnableForceSettlement() {
        return isEnableForceSettlement;
    }

    public void setEnableForceSettlement(boolean enableForceSettlement) {
        isEnableForceSettlement = enableForceSettlement;
    }

    public boolean isAllowFallback() {
        return isAllowFallback;
    }

    public void setAllowFallback(boolean allowFallback) {
        isAllowFallback = allowFallback;
    }

    public boolean isEnableTrainingMode() {
        return isEnableTrainingMode;
    }

    public void setEnableTrainingMode(boolean enableTrainingMode) {
        isEnableTrainingMode = enableTrainingMode;
    }

    public String getSuperPassword() {
        return superPassword;
    }

    public void setSuperPassword(String superPassword) {
        this.superPassword = superPassword;
    }

    public String getTransManagerPassword() {
        return transManagerPassword;
    }

    public void setTransManagerPassword(String transManagerPassword) {
        this.transManagerPassword = transManagerPassword;
    }

    public String getManualPassword() {
        return manualPassword;
    }

    public void setManualPassword(String manualPassword) {
        this.manualPassword = manualPassword;
    }

    public String getSysInvoiceNum() {
        return sysInvoiceNum;
    }

    public void setSysInvoiceNum(String sysInvoiceNum) {
        this.sysInvoiceNum = sysInvoiceNum;
    }

    public String getAutoSettlementTime() {
        return autoSettlementTime;
    }

    public void setAutoSettlementTime(String autoSettlementTime) {
        this.autoSettlementTime = autoSettlementTime;
    }

    public String getForceSettlementTime() {
        return forceSettlementTime;
    }

    public void setForceSettlementTime(String forceSettlementTime) {
        this.forceSettlementTime = forceSettlementTime;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public boolean isNeedRemoveTrackTailF() {
        return isNeedRemoveTrackTailF;
    }

    public void setNeedRemoveTrackTailF(boolean needRemoveTrackTailF) {
        isNeedRemoveTrackTailF = needRemoveTrackTailF;
    }

    public int getPinAlgType() {
        return pinAlgType;
    }

    public void setPinAlgType(int pinAlgType) {
        this.pinAlgType = pinAlgType;
    }

    public boolean isRandomPinpadKeyboard() {
        return isRandomPinpadKeyboard;
    }

    public void setRandomPinpadKeyboard(boolean randomPinpadKeyboard) {
        isRandomPinpadKeyboard = randomPinpadKeyboard;
    }

    public boolean isEnableESign() {
        return isEnableESign;
    }

    public void setEnableESign(boolean enableESign) {
        isEnableESign = enableESign;
    }

    public boolean isEnableCheckLuhn() {
        return isEnableCheckLuhn;
    }

    public void setEnableCheckLuhn(boolean enableCheckLuhn) {
        isEnableCheckLuhn = enableCheckLuhn;
    }

    public boolean isEnableTip() {
        return isEnableTip;
    }

    public void setEnableTip(boolean enableTip) {
        isEnableTip = enableTip;
    }

    public long getMaxPreAuthAmount() {
        return maxPreAuthAmount;
    }

    public void setMaxPreAuthAmount(long maxPreAuthAmount) {
        this.maxPreAuthAmount = maxPreAuthAmount;
    }

    public int getMaxAdjustTimes() {
        return maxAdjustTimes;
    }

    public void setMaxAdjustTimes(int maxAdjustTimes) {
        this.maxAdjustTimes = maxAdjustTimes;
    }

    public String getSettingPassword() {
        return settingPassword;
    }

    public void setSettingPassword(String settingPassword) {
        this.settingPassword = settingPassword;
    }

    public boolean isEnableISOLog() {
        return isEnableISOLog;
    }

    public void setEnableISOLog(boolean enableISOLog) {
        isEnableISOLog = enableISOLog;
    }

    public int getSettleFailedRetryIntervalMins() {
        return settleFailedRetryIntervalMins;
    }

    public void setSettleFailedRetryIntervalMins(int settleFailedRetryIntervalMins) {
        this.settleFailedRetryIntervalMins = settleFailedRetryIntervalMins;
    }

    public String getDefaultCurrencyCode() {
        return defaultCurrencyCode;
    }

    public void setDefaultCurrencyCode(String defaultCurrencyCode) {
        this.defaultCurrencyCode = defaultCurrencyCode;
    }

    public int getReceiveTimeout() {
        return receiveTimeout;
    }

    public void setReceiveTimeout(int receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }
}
