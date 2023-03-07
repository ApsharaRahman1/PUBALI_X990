package com.vfi.android.data.database.itp;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.vfi.android.data.database.DBFlowDatabase;

/**
 * Created by RuihaoS on 2019/8/13.
 */
@Table(database = DBFlowDatabase.class)
public class DBModelInstallmentPromo extends BaseModel {

    /**
     * Index of ITP
     */
    @PrimaryKey
    @Column
    public int index;

    /**
     * Enable/Disable this ITP
     */
    @Column
    public boolean isEnablePromo;

    /**
     * Type of Installment
     */
    @Column
    public String promoLabel;

    /**
     * Term of installment
     */
    @Column
    public String termList;

    /**
     *
     */
    @Column
    public String promoCode;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isEnablePromo() {
        return isEnablePromo;
    }

    public void setEnablePromo(boolean enablePromo) {
        isEnablePromo = enablePromo;
    }

    public String getPromoLabel() {
        return promoLabel;
    }

    public void setPromoLabel(String promoLabel) {
        this.promoLabel = promoLabel;
    }

    public String getTermList() {
        return termList;
    }

    public void setTermList(String termList) {
        this.termList = termList;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }
}
