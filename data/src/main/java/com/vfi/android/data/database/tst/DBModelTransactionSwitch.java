package com.vfi.android.data.database.tst;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.vfi.android.data.database.DBFlowDatabase;

/**
 * Created by RuihaoS on 2019/8/14.
 */
@Table(database = DBFlowDatabase.class)
public class DBModelTransactionSwitch extends BaseModel {
    /**
     * transaction type
     */
    @PrimaryKey
    @Column
    private int transType;

    /**
     * Enable/Disable transaction
     */
    @Column
    private boolean isEnableTrans;

    /**
     * Enable/Disable enter tip amount
     */
    @Column
    private boolean isEnableEnterTip;

    /**
     * Enable/Disable manual input
     */
    @Column
    private boolean isEnableManual;

    /**
     * Enable/Disable swipe card
     */
    @Column
    private boolean isEnableSwipeCard;

    /**
     * Enable/Disable insert card
     */
    @Column
    private boolean isEnableInsertCard;

    /**
     * Enable/Disable tap card
     */
    @Column
    private boolean isEnableTapCard;

    /**
     * Enable/Disable emv force online
     */
    @Column
    private boolean isEMVForceOnline;

    /**
     * Enable/Disable check reversal before transaction
     */
    @Column
    private boolean isEnableCheckReversal;

    /**
     * Enable/Disable input manager password
     */
    @Column
    private boolean isEnableInputManagerPwd;

    public int getTransType() {
        return transType;
    }

    public void setTransType(int transType) {
        this.transType = transType;
    }

    public boolean isEnableTrans() {
        return isEnableTrans;
    }

    public void setEnableTrans(boolean enableTrans) {
        isEnableTrans = enableTrans;
    }

    public boolean isEnableEnterTip() {
        return isEnableEnterTip;
    }

    public void setEnableEnterTip(boolean enableEnterTip) {
        isEnableEnterTip = enableEnterTip;
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
}
