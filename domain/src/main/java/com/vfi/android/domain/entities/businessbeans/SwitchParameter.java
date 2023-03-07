package com.vfi.android.domain.entities.businessbeans;

import com.vfi.android.domain.entities.annotations.Param;

import java.lang.reflect.Field;

/**
 * Created by yunlongg1 on 26/10/2017.
 */

public class SwitchParameter {
    private static final String TAG = "SwitchParameter";
    /**
     * Transaction type, prefer to {@link com.vfi.android.domain.entities.consts.TransType}
     */
    private int transType;

    /**
     * Enable/Disable this transaction
     */
    @Param.Default(Boolean = true)
    private boolean isEnableTrans;

    /**
     * Enable/Disable enter tip amount
     */
    @Param.Default(Boolean = true)
    private boolean isEnableEnterTip;

    /**
     * Enable/Disable manual input
     */
    @Param.Default(Boolean = true)
    private boolean isEnableManual;

    /**
     * Enable/Disable swipe card
     */
    @Param.Default(Boolean = true)
    private boolean isEnableSwipeCard;

    /**
     * Enable/Disable insert card
     */
    @Param.Default(Boolean = true)
    private boolean isEnableInsertCard;

    /**
     * Enable/Disable tap card
     */
    @Param.Default(Boolean = true)
    private boolean isEnableTapCard;

    /**
     * Enable/Disable emv force online
     */
    @Param.Default(Boolean = false)
    private boolean isEMVForceOnline;

    /**
     * Enable/Disable do check reversal before this transaction
     */
    @Param.Default(Boolean = true)
    private boolean isEnableCheckReversal;

    /**
     * Enable/Disable Enter manager password
     **/
    @Param.Default(Boolean = false)
    private boolean isEnableInputManagerPwd;

    public boolean isEnableTrans() {
        return isEnableTrans;
    }

    public void setEnableTrans(boolean enableTrans) {
        isEnableTrans = enableTrans;
    }

    public boolean isEnableManual() {
        return isEnableManual;
    }

    public void setEnableManual(boolean enableManual) {
        isEnableManual = enableManual;
    }

    public boolean isEnableSwipeCard() {
        return isEnableSwipeCard;
    }

    public void setEnableSwipeCard(boolean enableSwipeCard) {
        isEnableSwipeCard = enableSwipeCard;
    }

    public boolean isEnableInsertCard() {
        return isEnableInsertCard;
    }

    public void setEnableInsertCard(boolean enableInsertCard) {
        isEnableInsertCard = enableInsertCard;
    }

    public boolean isEnableTapCard() {
        return isEnableTapCard;
    }

    public void setEnableTapCard(boolean enableTapCard) {
        isEnableTapCard = enableTapCard;
    }

    public boolean isEMVForceOnline() {
        return isEMVForceOnline;
    }

    public void setEMVForceOnline(boolean EMVForceOnline) {
        isEMVForceOnline = EMVForceOnline;
    }

    public boolean isEnableCheckReversal() {
        return isEnableCheckReversal;
    }

    public void setEnableCheckReversal(boolean enableCheckReversal) {
        isEnableCheckReversal = enableCheckReversal;
    }

    public boolean isEnableInputManagerPwd() {
        return isEnableInputManagerPwd;
    }

    public void setEnableInputManagerPwd(boolean enableInputManagerPwd) {
        isEnableInputManagerPwd = enableInputManagerPwd;
    }

    public SwitchParameter() {
    }

    public void initDefaultSwitchParameter() {
        Class<SwitchParameter> clazz = SwitchParameter.class;
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.isAnnotationPresent(Param.Default.class)) {
                try {
                    Boolean value = f.getAnnotation(Param.Default.class).Boolean();
                    f.set(this, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getTransType() {
        return transType;
    }

    public void setTransType(int transType) {
        this.transType = transType;
    }

    public boolean isEnableEnterTip() {
        return isEnableEnterTip;
    }

    public void setEnableEnterTip(boolean enableEnterTip) {
        isEnableEnterTip = enableEnterTip;
    }
}
