package com.vfi.android.domain.entities.databeans;

import com.vfi.android.domain.entities.businessbeans.PrintConfig;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.interfaces.repository.IRepository;

public class PrintInfo {
    /**
     * please refer to {@link com.vfi.android.domain.entities.consts.PrintType}
     */
    private int printType;

    /**
     * print slip type, merchant/customer/bank
     */
    private int printSlipType;
    public static final int SLIP_TYPE_MERCHANT = 0;
    public static final int SLIP_TYPE_CUSTOMER = 1;
    public static final int SLIP_TYPE_BANK = 2;

    /**
     * Presentation can add print task independent
     */
    private PrintTask printTask;

    /**
     * It is used for Reprint
     */
    private RecordInfo recordInfo;

    /**
     * If it is reprint slip, mark it true.
     */
    private boolean isDuplicateSlip;

    /**
     * As a notify to printer continue print.
     */
    private boolean isContinueFromPrintError;

    /**
     * Logo image data.
     */
    private byte[] printLogoData;

    /**
     * Print config info
     */
    private PrintConfig printConfig;

    private IRepository iRepository;

    public PrintInfo(int printType, int printSlipType) {
        this.printType = printType;
        this.printSlipType = printSlipType;
    }

    public PrintInfo(PrintTask printTask) {
        this.printTask = printTask;
    }

    public int getPrintType() {
        return printType;
    }

    public int getPrintSlipType() {
        return printSlipType;
    }

    public PrintTask getPrintTask() {
        return printTask;
    }

    public boolean isDuplicateSlip() {
        return isDuplicateSlip;
    }

    public void setDuplicateSlip(boolean duplicateSlip) {
        isDuplicateSlip = duplicateSlip;
    }

    public RecordInfo getRecordInfo() {
        return recordInfo;
    }

    public void setRecordInfo(RecordInfo recordInfo) {
        this.recordInfo = recordInfo;
    }

    public boolean isContinueFromPrintError() {
        return isContinueFromPrintError;
    }

    public void setContinueFromPrintError(boolean continueFromPrintError) {
        isContinueFromPrintError = continueFromPrintError;
    }

    public byte[] getPrintLogoData() {
        return printLogoData;
    }

    public void setPrintLogoData(byte[] printLogoData) {
        this.printLogoData = printLogoData;
    }

    public PrintConfig getPrintConfig() {
        return printConfig;
    }

    public void setPrintConfig(PrintConfig printConfig) {
        this.printConfig = printConfig;
    }

    public IRepository getiRepository() {
        return iRepository;
    }

    public void setiRepository(IRepository iRepository) {
        this.iRepository = iRepository;
    }
}
