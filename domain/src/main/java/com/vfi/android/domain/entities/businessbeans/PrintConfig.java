package com.vfi.android.domain.entities.businessbeans;

public class PrintConfig {
    private int index;
    private boolean isPrintCustomerCopy;
    private boolean isPrintMerchantCopy;
    private boolean isPrintBankCopy;
    private String header1;
    private String header2;
    private String header3;
    private String header4;
    private String header5;
    private String header6;
    private String footer1;
    private String footer2;
    private String footer3;
    private String footer4;
    private String footer5;
    private String footer6;

    public PrintConfig() {

    }

    public String getHeader1() {
        return header1;
    }

    public void setHeader1(String header1) {
        this.header1 = header1;
    }

    public String getHeader2() {
        return header2;
    }

    public void setHeader2(String header2) {
        this.header2 = header2;
    }

    public String getHeader3() {
        return header3;
    }

    public void setHeader3(String header3) {
        this.header3 = header3;
    }

    public String getHeader4() {
        return header4;
    }

    public void setHeader4(String header4) {
        this.header4 = header4;
    }

    public String getHeader5() {
        return header5;
    }

    public void setHeader5(String header5) {
        this.header5 = header5;
    }

    public String getHeader6() {
        return header6;
    }

    public void setHeader6(String header6) {
        this.header6 = header6;
    }

    public String getFooter1() {
        return footer1;
    }

    public void setFooter1(String footer1) {
        this.footer1 = footer1;
    }

    public String getFooter2() {
        return footer2;
    }

    public void setFooter2(String footer2) {
        this.footer2 = footer2;
    }

    public String getFooter3() {
        return footer3;
    }

    public void setFooter3(String footer3) {
        this.footer3 = footer3;
    }

    public String getFooter4() {
        return footer4;
    }

    public void setFooter4(String footer4) {
        this.footer4 = footer4;
    }

    public String getFooter5() {
        return footer5;
    }

    public void setFooter5(String footer5) {
        this.footer5 = footer5;
    }

    public String getFooter6() {
        return footer6;
    }

    public void setFooter6(String footer6) {
        this.footer6 = footer6;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isPrintCustomerCopy() {
        return isPrintCustomerCopy;
    }

    public void setPrintCustomerCopy(boolean printCustomerCopy) {
        isPrintCustomerCopy = printCustomerCopy;
    }

    public boolean isPrintMerchantCopy() {
        return isPrintMerchantCopy;
    }

    public void setPrintMerchantCopy(boolean printMerchantCopy) {
        isPrintMerchantCopy = printMerchantCopy;
    }

    public boolean isPrintBankCopy() {
        return isPrintBankCopy;
    }

    public void setPrintBankCopy(boolean printBankCopy) {
        isPrintBankCopy = printBankCopy;
    }
}
