package com.vfi.android.domain.entities.businessbeans;

/**
 * Created by RuihaoS on 2019/8/14.
 */
public class InstallmentPromo {
    /**
     * Index of ITP
     */
    private int index;

    /**
     * Enable/Disable this ITP
     */
    private boolean isEnablePromo;

    /**
     * Type of Installment
     */
    private String promoLabel;

    /**
     * Term of installment
     */
    private String termList;

    /**
     * Type of Installment Payment Scheme, 3 byte number
     */
    private String promoCode;

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
