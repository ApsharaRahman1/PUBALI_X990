package com.vfi.android.domain.interactor.print.base;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.vfi.android.domain.entities.businessbeans.PrintConfig;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.consts.CVMResult;
import com.vfi.android.domain.entities.consts.CardType;
import com.vfi.android.domain.entities.consts.PrintType;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.databeans.PrintInfo;
import com.vfi.android.domain.entities.databeans.PrintTask;
import com.vfi.android.domain.entities.databeans.PrinterParamIn;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.domain.utils.ZlibUtil;

import java.io.ByteArrayOutputStream;

public abstract class AbstractTransPrintTask extends PrintTask implements ICommPrintItem {
    private RecordInfo recordInfo;
    private PrintInfo printInfo;
    private IRepository iRepository;

    public AbstractTransPrintTask(RecordInfo recordInfo, PrintInfo printInfo) {
        this.recordInfo = recordInfo;
        this.printInfo = printInfo;
        this.iRepository = printInfo.getiRepository();
        setPrintGary(7);
        addPrintContent();
    }

    protected abstract void addPrintContent();

    @Override
    public void addLogoImage() {
        if (printInfo.getPrintLogoData() != null) {
            this.addPrintImage(0, 146, 384, printInfo.getPrintLogoData());
        }
    }

    @Override
    public void addHeader() {
        PrintConfig printConfig = printInfo.getPrintConfig();
        if (printConfig != null) {
            if (StringUtil.getNonNullString(printConfig.getHeader1()).length() > 0) {
                this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, true, printConfig.getHeader1());
            }
            if (StringUtil.getNonNullString(printConfig.getHeader2()).length() > 0) {
                this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getHeader2());
            }
            if (StringUtil.getNonNullString(printConfig.getHeader3()).length() > 0) {
                this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getHeader3());
            }
            if (StringUtil.getNonNullString(printConfig.getHeader4()).length() > 0) {
                this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getHeader4());
            }
            if (StringUtil.getNonNullString(printConfig.getHeader5()).length() > 0) {
                this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getHeader5());
            }
            if (StringUtil.getNonNullString(printConfig.getHeader6()).length() > 0) {
                this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getHeader6());
            }
        }
    }

    @Override
    public void addDuplicateLabel() {
        final int bgHeight = 40;
        final int bgWidth = 380;
        final int fontSize = 30;
        Bitmap bitmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);

        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        paint.setColor(Color.WHITE);
        float textWidth = paint.measureText("DUPLICATE");
        float baseLineY = bgHeight / 2 - (paint.ascent() + paint.descent()) / 2;
        canvas.drawText("DUPLICATE", bgWidth / 2 - textWidth / 2, baseLineY, paint);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, bos); // 40 is ok, I tested it.
        this.addPrintImage(0, bgHeight, bgWidth, bos.toByteArray());
    }

    @Override
    public void addDateTime() {
        String date = recordInfo.getTransDate();
        date = StringUtil.formatDate(date);
        String time = recordInfo.getTransTime();
        time = StringUtil.formatTime(time);

        //PrintTask printTask = new PrintTask(0);
        this.addPrintLine(PrinterParamIn.FONT_SMALL,true, String.format("DATE:%s", date), String.format("TIME:%s", time) );//apshara
        //printTask.addTwoTextPrintLine(String.format("DATE:%s", date),String.format("TIME:%s", time));
    }

    @Override
    public void addTerminalIdMerchantId() {
        String terminalId = recordInfo.getTerminalId();
        String merchantId = recordInfo.getMerchantId();
        this.addPrintLine(PrinterParamIn.FONT_SMALL, true, String.format("TID:%s", terminalId), String.format("MID:%s", merchantId));//apshara
    }

    @Override
    public void addBatchInvoice() {
        String batchNum = recordInfo.getBatchNo();
        String invoiceNum = recordInfo.getInvoiceNum();
        this.addPrintLine(PrinterParamIn.FONT_SMALL, String.format("BAT NUM:%s", batchNum), String.format("INVOICE NO:%s", invoiceNum));//apshara
    }

    @Override
    public void addRRN() {
        this.addPrintLine(PrinterParamIn.FONT_SMALL, getPrintText("RRN: ", recordInfo.getRefNo()));
    }

    @Override
    public void addTransTypeLine() {
        String transType = TransType.getPrintTransTitle(recordInfo.getTransType(), recordInfo.getVoidOrgTransType(), recordInfo.getTipAdjOrgTransType());
        addTransTypeLine(transType);
    }

    private void addTransTypeLine(String transType) {
        final int bgHeight = 40;
        final int bgWidth = 380;
        final int fontSize = 40;
        Bitmap bitmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
       canvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        paint.setColor(Color.BLACK);
        float textWidth = paint.measureText(transType);
        float baseLineY = bgHeight / 2 - (paint.ascent() + paint.descent()) / 2;
        canvas.drawText(transType, bgWidth / 2 - textWidth / 2, baseLineY, paint);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, bos); // 40 is ok, I tested it.
        this.addPrintImage(0, bgHeight, bgWidth, bos.toByteArray());
    }

    @Override
    public void addCardNum() {
        String pan = recordInfo.getPan();
        String panLast4Digit = pan.substring(pan.length() - 4);

        String cardEntryMode = "";
        String posEntryMode = recordInfo.getPosEntryMode();
        if (posEntryMode != null && posEntryMode.length() >= 2) {
            posEntryMode = posEntryMode.substring(0, 2);
            switch (posEntryMode) {
                case "80":
                case "02":
                    cardEntryMode = "Swipe";
                    break;
                case "91": // rf
                case "07": // rf
                case "05": // insert
                    cardEntryMode = "Chip";
                    break;
                case "01":
                    cardEntryMode = "Manual";
                    break;
            }
        }

        this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_LEFT, true, String.format("CARD: XXXX XXXX XXXX %s (%s)", panLast4Digit, cardEntryMode));
    }

    @Override
    public void addCardType() {
        String cardTypeText = CardType.toCardTypeAbbrText(recordInfo.getCardType());
        this.addPrintLine(PrinterParamIn.FONT_SMALL, "EXP DATE:**/**"+"   CARD TYPE:" + cardTypeText);

    }

    @Override
    public void addApprovalCode() {
        this.addPrintLine(PrinterParamIn.FONT_LARGE, PrinterParamIn.ALIGN_LEFT, true,"APR CODE : " + recordInfo.getAuthCode());
    }

    @Override
    public void addAID() {
        if (getNonNullString(recordInfo.getAID()).length() > 0) {
            this.addPrintLine(PrinterParamIn.FONT_SMALL, getPrintText("AID: ", recordInfo.getAID()));
        }
    }

    @Override
    public void addApplicationLabel() {
        if (getNonNullString(recordInfo.getAppLabel()).length() > 0) {
            this.addPrintLine(PrinterParamIn.FONT_SMALL, getPrintText("APP: ", recordInfo.getAppLabel()));
        }
    }

    @Override
    public void addTC() {
//ishtiak
        if (getNonNullString(recordInfo.getTc()).length() > 0) {
            this.addPrintLine(PrinterParamIn.FONT_SMALL, getPrintText("TC : ", recordInfo.getTc()));
        }
//        this.addPrintLine(PrinterParamIn.FONT_SMALL, getPrintText("PREF : ", recordInfo.getRefNo()));
//        this.addPrintLine(PrinterParamIn.FONT_SMALL, getPrintText("TVR : ", recordInfo.getTraceNum()));
//        this.addPrintLine(PrinterParamIn.FONT_SMALL, getPrintText("TSI : ", recordInfo.getTotalAvailablePTS()));
//        this.addPrintLine(PrinterParamIn.FONT_SMALL, getPrintText("TC : ", recordInfo.getTc()));
    }


    @Override
    public void addAmountLine() {
        int printType = printInfo.getPrintType();
        if (printType == PrintType.OFFLINE
                || printType == PrintType.INSTALLMENT
                || printType == PrintType.PREAUTH
                || printType == PrintType.PREAUTH_COMP
                || (printType == PrintType.VOID && recordInfo.getVoidOrgTransType() == TransType.OFFLINE)
                || (printType == PrintType.VOID && recordInfo.getVoidOrgTransType() == TransType.PREAUTH_COMP)
                || (printType == PrintType.VOID && recordInfo.getVoidOrgTransType() == TransType.PREAUTH)
                || (printType == PrintType.VOID && recordInfo.getVoidOrgTransType() == TransType.INSTALLMENT)
                || (printType == PrintType.VOID && recordInfo.getVoidOrgTransType() == TransType.TIP_ADJUST && recordInfo.getTipAdjOrgTransType() == TransType.OFFLINE)
                || (printType == PrintType.VOID && recordInfo.getVoidOrgTransType() == TransType.TIP_ADJUST && recordInfo.getTipAdjOrgTransType() == TransType.PREAUTH_COMP)
                || (printType == PrintType.VOID && recordInfo.getVoidOrgTransType() == TransType.TIP_ADJUST && recordInfo.getTipAdjOrgTransType() == TransType.PREAUTH)
                || (printType == PrintType.TIP_ADJUST && recordInfo.getTipAdjOrgTransType() == TransType.PREAUTH)
                || (printType == PrintType.TIP_ADJUST && recordInfo.getTipAdjOrgTransType() == TransType.PREAUTH_COMP)
                || (printType == PrintType.TIP_ADJUST && recordInfo.getTipAdjOrgTransType() == TransType.OFFLINE)) {
            long baseAmount = StringUtil.parseLong(recordInfo.getAmount(), 0);
            long tipAmount = StringUtil.parseLong(recordInfo.getTipAmount(), 0);
            long totalAmount = baseAmount + tipAmount;
            String totalAmountStr = String.format("%012d", totalAmount);
            if (printType == PrintType.VOID) {
                totalAmountStr = "-" + totalAmountStr;
            }
            totalAmountStr = StringUtil.formatAmount(totalAmountStr);
            this.addPrintLine(PrinterParamIn.FONT_NORMAL, true, "AMT: PHP", totalAmountStr);
        } else {
            addBaseAmount(printType);
            //addTipAmount(printType);
            //addTotalAmount(printType);
        }
    }

    private void addBaseAmount(int printType) {
        String baseAmount = recordInfo.getAmount();
        baseAmount = StringUtil.formatAmount(baseAmount);
        if (printType == PrintType.VOID) {
            this.addPrintLine(PrinterParamIn.FONT_SMALL, true, "AMOUNT:", "-" + baseAmount);
        } else {
            this.addPrintLine(PrinterParamIn.FONT_SMALL, true, "AMOUNT:", baseAmount);
        }
    }
//apshara
//    private void addTipAmount(int printType) {
//        String tipAmount = recordInfo.getTipAmount();
//        if (tipAmount == null || tipAmount.length() == 0) {
//            addPrintLine(PrinterParamIn.FONT_NORMAL, true, "TIP", "");
//        } else {
//            tipAmount = StringUtil.formatAmount(tipAmount);
//            if (printType == PrintType.VOID) {
//                addPrintLine(PrinterParamIn.FONT_NORMAL, true, "TIP", "-" + tipAmount);
//            } else {
//                addPrintLine(PrinterParamIn.FONT_NORMAL, true, "TIP", tipAmount);
//            }
//        }
//    }

//    private void addTotalAmount(int printType) {
//        long baseAmount = StringUtil.parseLong(recordInfo.getAmount(), 0);
//        long tipAmount = StringUtil.parseLong(recordInfo.getTipAmount(), 0);
//        String totalAmount = String.format("%012d", baseAmount + tipAmount);
//        totalAmount = StringUtil.formatAmount(totalAmount);
//        //addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_RIGHT, false, "- - - - - - - - - -");
//        if (tipAmount == 0) {
//            addPrintLine(PrinterParamIn.FONT_NORMAL, true, "TOTAL", "");
//        } else {
//            if (printType == PrintType.VOID) {
//                addPrintLine(PrinterParamIn.FONT_NORMAL, true, "TOTAL AMOUNT", "-" + totalAmount);
//            } else {
//                addPrintLine(PrinterParamIn.FONT_NORMAL, true, "TOTAL AMOUNT", totalAmount);
//            }
//        }
//    }

    @Override
    public void addTotalAvailablePTS() {
        if (recordInfo.getCardType() == CardType.STA_LUCIA) {
            addOneBlackLine();
            addOneBlackLine();
            this.addPrintLine(PrinterParamIn.FONT_SMALL, "TOTAL AVAILPTS:", "PTS     " + recordInfo.getTotalAvailablePTS());
            addOneBlackLine();
            addOneBlackLine();
        }
    }

    @Override
    public void addCVMResultLine() {
        if (printInfo.getPrintSlipType() == PrintInfo.SLIP_TYPE_CUSTOMER) {
            return;
        }

        switch (recordInfo.getCvmResult()) {
            case CVMResult.INPUT_PIN:
                this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, true, "PIN VERIFIED TRANSACTION");
                break;
            case CVMResult.SIGNATURE:
                if (iRepository != null && iRepository.getTerminalCfg().isEnableESign()
                        && recordInfo.getSignData() != null) {
                    this.addPrintImage(0, 130, 380, ZlibUtil.decompressZipFileData(recordInfo.getSignData()));
                } else {
                    addOneBlackLine();
                    addOneBlackLine();
                    this.addPrintLine(PrinterParamIn.FONT_SMALL, "SIGN:_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _");
                }
                break;
            case CVMResult.REQ_SIGN_BUT_SKIP:
                addOneBlackLine();
                addOneBlackLine();
                this.addPrintLine(PrinterParamIn.FONT_SMALL, "SIGN:_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _");
                break;
            case CVMResult.PIN_BYPASS:
            case CVMResult.NO_CVM_REQUEST:
                this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, "PIN VERIFIED TRANSACTION"); //apshara
                this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, "NO SIGNATURE REQUIRED");
                break;
        }
    }

    @Override
    public void addCardHolderNameLine() {
        this.addPrintLine(PrinterParamIn.FONT_NORMAL, PrinterParamIn.ALIGN_CENTER, false, recordInfo.getCardHolderName());
    }

    @Override
    public void addDisclaimer() {
        String disclaimer = "THANKS FOR USING PUBALI POS " + "\n"
                + "HELPLINE: 16253"; //apshara
        this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, true, disclaimer);
    }

    @Override
    public void addSlipCopy() {
        String slipCopyText;
        int printSlipType = printInfo.getPrintSlipType();
        if (printSlipType == PrintInfo.SLIP_TYPE_BANK) {
            slipCopyText = "BANK COPY";
        } else if (printSlipType == PrintInfo.SLIP_TYPE_CUSTOMER) {
            slipCopyText = "CUSTOMER COPY";
        } else {
            slipCopyText = "MERCHANT COPY";
        }
        this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, true, "*****" + slipCopyText + "*****");
    }

    @Override
    public void addFooter() {
        PrintConfig printConfig = printInfo.getPrintConfig();
        if (printConfig != null) {
            if (StringUtil.getNonNullString(printConfig.getFooter1()).length() > 0) {
                this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getFooter1());
            }
            if (StringUtil.getNonNullString(printConfig.getFooter2()).length() > 0) {
                this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getFooter2());
            }
            if (StringUtil.getNonNullString(printConfig.getFooter3()).length() > 0) {
                this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getFooter3());
            }
            if (StringUtil.getNonNullString(printConfig.getFooter4()).length() > 0) {
                this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getFooter4());
            }
            if (StringUtil.getNonNullString(printConfig.getFooter5()).length() > 0) {
                this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getFooter5());
            }
            if (StringUtil.getNonNullString(printConfig.getFooter6()).length() > 0) {
                this.addPrintLine(PrinterParamIn.FONT_SMALL, PrinterParamIn.ALIGN_CENTER, false, printConfig.getFooter6());
            }
        }
    }

    private String getPrintText(String hint, String value) {
        if (value == null) {
            return null;
        }

        return hint + value;
    }

    public void addOneBlackLine() {
        addPrintLine(PrinterParamIn.FONT_NORMAL, " ");
    }

    public void addOneDividingLine(char dividingChar) {
        this.addPrintLine(PrinterParamIn.FONT_SMALL, "- - - - - - - - - - - - - - - - - - - - - -");
    }

    public PrintInfo getPrintInfo() {
        return printInfo;
    }

    public RecordInfo getRecordInfo() {
        return recordInfo;
    }

    public String getNonNullString(String value) {
        return StringUtil.getNonNullString(value);
    }
}
