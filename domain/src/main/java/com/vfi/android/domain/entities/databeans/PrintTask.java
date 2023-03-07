package com.vfi.android.domain.entities.databeans;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.BitmapUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.vfi.android.domain.entities.databeans.PrinterParamIn.ALIGN_LEFT;
import static com.vfi.android.domain.entities.databeans.PrinterParamIn.FONT_NORMAL;
import static com.vfi.android.domain.entities.databeans.PrinterParamIn.FONT_SMALL;
import static com.vfi.android.domain.entities.databeans.PrinterParamIn.PRINT_BARCODE;
import static com.vfi.android.domain.entities.databeans.PrinterParamIn.PRINT_IMAGE;
import static com.vfi.android.domain.entities.databeans.PrinterParamIn.PRINT_PAPERFEED;
import static com.vfi.android.domain.entities.databeans.PrinterParamIn.PRINT_START;
import static com.vfi.android.domain.entities.databeans.PrinterParamIn.PRINT_TEXT;

public class PrintTask {
    private final String TAG = TAGS.PRINT;
    private List<PrinterParamIn> printerParamIns;
    private int printGary;
    private int taskId;
    /**
     * isOnlyNotifyPrintError - only a print is divided into several prints will use this attribute.
     * except last print task, others need set this flag to ignore print success notify.
     */
    private boolean isOnlyNotifyPrintError;

    /**
     * max chars a line in LARGE mode.
     */
    public static final int FONT_LARGE_MAXLENGTH = 32;
    /**
     * max chars a line in NORMAL mode.
     */
    public static final int FONT_NORMAL_MAXLENGTH = 32;
    /**
     * max chars a line in SMALL mode.
     */
    public static final int FONT_SMALL_MAXLENGTH = 48;

    public PrintTask() {
        printerParamIns = new ArrayList<>();
        this.setPrintGary(7);
    }

    public PrintTask(int taskId) {
        this.taskId = taskId;
        printerParamIns = new ArrayList<>();
        this.setPrintGary(7);
    }

    public List<PrinterParamIn> getPrinterParamIns() {
        LogUtil.d(TAG, "taskId=" + taskId + " print param size=" + printerParamIns.size());
        return printerParamIns;
    }

    public void checkStartPrintFlag() {
        PrinterParamIn paramLast = printerParamIns.get(printerParamIns.size() - 1);
        if (paramLast.getType() != PrinterParamIn.PRINT_START) {
            addStartPrintFlag();
        }
    }

    public PrinterParamIn getPrintGary() {
        PrinterParamIn paramIn = new PrinterParamIn();
        paramIn.setGray(printGary);

        return paramIn;
    }

    public void setPrintGary(int printGary) {
        this.printGary = printGary;
    }

    public void addPrintLine(int font, int align, boolean bold, String text) {
        if (text == null || text.isEmpty() || text.trim().equals("null")) {
            return;
        }

        PrinterParamIn printerParamIn = new PrinterParamIn();
        printerParamIn.setType(PRINT_TEXT);
        printerParamIn.setFontSize(font);
        printerParamIn.setBold(bold);
        printerParamIn.setAlign(align);
        printerParamIn.setGray(printGary);
        printerParamIn.setContent(text);
        printerParamIn.setNewline(true);

        printerParamIns.add(printerParamIn);
    }

    public void addPrintLine(int font, String text) {
        if (text == null) {
            return;
        }

        PrinterParamIn printerParamIn = new PrinterParamIn();
        printerParamIn.setType(PRINT_TEXT);
        printerParamIn.setFontSize(font);
        printerParamIn.setAlign(ALIGN_LEFT);
        printerParamIn.setGray(printGary);
        printerParamIn.setContent(text);
        printerParamIn.setNewline(true);

        printerParamIns.add(printerParamIn);
    }

    public void addPrintLine(int font, String arg0, String arg1) {
        addPrintLine(font, false, arg0, arg1);
    }

    public void addPrintLine(int font, boolean bold, String arg0, String arg1) {
        if (arg0 == null || arg1 == null) {
            return;
        }


        byte[] lineBuffer;
        int arg0_chineseNum = StringUtil.getChineseCharNum(arg0);
        int arg1_chineseNum = StringUtil.getChineseCharNum(arg1);

        String content;
        int contentLen = (arg0.length() - arg0_chineseNum) + arg0_chineseNum * 2 + (arg1.length() - arg1_chineseNum) + arg1_chineseNum * 2;
        LogUtil.e("TAG, contentLen=" + contentLen);
        if (font == FONT_NORMAL) {
            if (contentLen > FONT_NORMAL_MAXLENGTH) {
                content = arg0 + " " + arg1;
            } else {
                lineBuffer = new byte[FONT_NORMAL_MAXLENGTH + printChineseLengthExt(arg0_chineseNum) + printChineseLengthExt(arg1_chineseNum)];
                Arrays.fill(lineBuffer, (byte) 0x20);
                System.arraycopy(arg0.getBytes(), 0, lineBuffer, 0, arg0.length() + arrayCopyChineseByteLengthExt(arg0_chineseNum));
                System.arraycopy(arg1.getBytes(), 0, lineBuffer, lineBuffer.length - arg1.length() - arrayCopyChineseByteLengthExt(arg1_chineseNum), arg1.length() + arrayCopyChineseByteLengthExt(arg1_chineseNum));
                content = new String(lineBuffer);
            }
        } else if (font == FONT_SMALL) {
            if (contentLen > FONT_SMALL_MAXLENGTH) {
                content = arg0 + " " + arg1;
            } else {
                lineBuffer = new byte[FONT_SMALL_MAXLENGTH + printChineseLengthExt(arg0_chineseNum) + printChineseLengthExt(arg1_chineseNum)];
                Arrays.fill(lineBuffer, (byte) 0x20);
                System.arraycopy(arg0.getBytes(), 0, lineBuffer, 0, arg0.length() + arrayCopyChineseByteLengthExt(arg0_chineseNum));
                LogUtil.d("lineBuffer.length=" + lineBuffer.length + " arg1.len=" + arg1.length() + " arrlen=" + arrayCopyChineseByteLengthExt(arg1_chineseNum) + " arg1_chineseNum=" + arg1_chineseNum);
                System.arraycopy(arg1.getBytes(), 0, lineBuffer, lineBuffer.length - arg1.length() - arrayCopyChineseByteLengthExt(arg1_chineseNum), arg1.length() + arrayCopyChineseByteLengthExt(arg1_chineseNum));
                content = new String(lineBuffer);
            }
        } else {
            return;
        }

        PrinterParamIn printerParamIn = new PrinterParamIn();
        printerParamIn.setType(PRINT_TEXT);
        printerParamIn.setFontSize(font);
        printerParamIn.setAlign(ALIGN_LEFT);
        printerParamIn.setBold(bold);
        printerParamIn.setGray(printGary);
        printerParamIn.setContent(content);
        printerParamIn.setNewline(true);

        printerParamIns.add(printerParamIn);
    }

    public void addTwoTextPrintLine(String leftText, String rightText) {
        int leftMaxLen = 18;

        if (leftText == null) {
            leftText = "";
        }

        if (rightText == null) {
            rightText = "";
        }

        if (leftText.length() < 18) {
            leftText = leftText + "                    ".substring(0, leftMaxLen - leftText.length());
        }

        String text = leftText + rightText;

        PrinterParamIn printerParamIn = new PrinterParamIn();
        printerParamIn.setType(PRINT_TEXT);
        printerParamIn.setFontSize(FONT_SMALL);
        printerParamIn.setBold(false);
        printerParamIn.setAlign(ALIGN_LEFT);
        printerParamIn.setGray(printGary);
        printerParamIn.setContent(text);
        printerParamIn.setNewline(true);

        printerParamIns.add(printerParamIn);
    }

    private int printChineseLengthExt(int chinestLength) {
        if (chinestLength == 0) {
            return 0;
        } else {
            return chinestLength;
        }
    }

    private int arrayCopyChineseByteLengthExt(int chinestLength) {
        if (chinestLength == 0) {
            return 0;
        } else {
            return 2 * chinestLength;
        }
    }

    public void addPrintImage(int offset, int height, int width, byte[] imageData) {
        if (imageData == null || imageData.length == 0) {
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        float scale = ((float) width) / bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        bitmap = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix,true);

        ByteArrayOutputStream bos = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        PrinterParamIn printerParamIn = new PrinterParamIn();
        printerParamIn.setType(PRINT_IMAGE);
        printerParamIn.setOffset(offset);
        printerParamIn.setHeight(bitmap.getHeight());
        printerParamIn.setWidth(bitmap.getWidth());
        printerParamIn.setGray(printGary);
        printerParamIn.setImageData(bos.toByteArray());

        printerParamIns.add(printerParamIn);
    }

    public void addPrintBarCode(int height, int width, int pixelPoint, String content, int align) {
        PrinterParamIn printerParamIn = new PrinterParamIn();
        printerParamIn.setType(PRINT_BARCODE);
        printerParamIn.setHeight(height);
        printerParamIn.setWidth(width);
        printerParamIn.setPixelPoint(pixelPoint);
        printerParamIn.setContent(content);
        printerParamIn.setAlign(align);

        printerParamIns.add(printerParamIn);
    }

    public void addPaperfeed(int lines) {
        PrinterParamIn printerParamIn = new PrinterParamIn();
        printerParamIn.setType(PRINT_PAPERFEED);
        printerParamIn.setLines(lines);

        printerParamIns.add(printerParamIn);
    }

    public void addStartPrintFlag() {
        PrinterParamIn printerParamIn = new PrinterParamIn();
        printerParamIn.setType(PRINT_START);
        printerParamIns.add(printerParamIn);
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public boolean isOnlyNotifyPrintError() {
        return isOnlyNotifyPrintError;
    }

    public void setOnlyNotifyPrintError(boolean onlyNotifyPrintError) {
        this.isOnlyNotifyPrintError = onlyNotifyPrintError;
    }
}
