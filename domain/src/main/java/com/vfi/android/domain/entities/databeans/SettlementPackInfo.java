package com.vfi.android.domain.entities.databeans;

public class SettlementPackInfo {
    private String sysTraceNum;
    private String nii;
    private String terminalId;
    private String merchantId;
    private String batchNum;
    private int countIn; // Credit count/Installment count/Loyalty count/Cup sale count
    private long totalAmountIn; //Credit total amount/Installment total count/
    private int countOut; // Void count
    private long totalAmountOut; // Void total amount

    public SettlementPackInfo() {
    }

    public String getSysTraceNum() {
        return sysTraceNum;
    }

    public void setSysTraceNum(String sysTraceNum) {
        this.sysTraceNum = sysTraceNum;
    }

    public String getNii() {
        return nii;
    }

    public void setNii(String nii) {
        this.nii = nii;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public int getCountIn() {
        return countIn;
    }

    public void setCountIn(int countIn) {
        this.countIn = countIn;
    }

    public long getTotalAmountIn() {
        return totalAmountIn;
    }

    public void setTotalAmountIn(long totalAmountIn) {
        this.totalAmountIn = totalAmountIn;
    }

    public int getCountOut() {
        return countOut;
    }

    public void setCountOut(int countOut) {
        this.countOut = countOut;
    }

    public long getTotalAmountOut() {
        return totalAmountOut;
    }

    public void setTotalAmountOut(long totalAmountOut) {
        this.totalAmountOut = totalAmountOut;
    }
}
