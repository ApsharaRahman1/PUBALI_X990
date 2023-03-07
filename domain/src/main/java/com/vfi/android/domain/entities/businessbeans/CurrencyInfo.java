package com.vfi.android.domain.entities.businessbeans;

/**
 * Created by RuihaoS on 2019/8/14.
 */
public class CurrencyInfo {

    /**
     * Index of CST
     */
    public int index;

    /**
     * Currency symbol
     */
    public String currencySymbol;

    /**
     * Currency code
     */
    public int currencyCode;

    /**
     * Product code
     */
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
        //this.currencyCode = currencyCode;

        this.currencyCode = currencyCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
