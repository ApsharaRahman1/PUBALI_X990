package com.vfi.android.data.database.cst;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.vfi.android.data.database.DBFlowDatabase;

/**
 * Created by RuihaoS on 2019/8/13.
 */
@Table(database = DBFlowDatabase.class)
public class DBModelCurrencySymbol extends BaseModel {


    /**
     * Index of CST
     */
    @Column
    @PrimaryKey
    public int index;

    /**
     * Currency symbol
     */
    @Column
    public String currencySymbol;

    /**
     * Currency code
     */
    @Column
    public int currencyCode;

    /**
     * Product code
     */
    @Column
    public String productCode;


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public int getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(int currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
