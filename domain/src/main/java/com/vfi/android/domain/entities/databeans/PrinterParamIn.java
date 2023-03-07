package com.vfi.android.domain.entities.databeans;

/**
 * Created by fusheng.z on 2017/12/28.
 */

public class PrinterParamIn {
    public static final int PRINT_TEXT = 0;
    public static final int PRINT_QRCODE = 1;
    public static final int PRINT_BARCODE = 2;
    public static final int PRINT_IMAGE = 3;
    public static final int PRINT_PAPERFEED = 4;
    public static final int PRINT_TEXT_IN_LINE = 5;
    public static final int PRINT_DIY_ALIGN = 6;

    public static final int PRINT_START = 100;
    public static final int PRINT_FINISH = 101;

    public static final int FONT_SMALL = 1; //apshara
    public static final int FONT_NORMAL = 1;
    public static final int FONT_LARGE = 2;
    public static final int FONT_XLARGE = 3;

    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;

    /**
     * type(int) - 0:content, 1:qrCode, 2:barCode 3:image 4: Paper feed (走纸不打印)
     **/
    private int type;

    /**
     * 设置行间距
     **/
    private int lineSpace;

    /**
     * fontSize(int) - 0:small(默认), 1:normal, 2:large
     **/
    private int fontSize;

    /**
     *
     */
    private String fontStyle;

    /**
     * 0:left, 1:center, 2:right
     **/
    private int align;

    /**
     * 打印灰度 灰度0~7
     **/
    private int gray;

    /**
     * true:换行, false:不换行
     **/
    private boolean newline;

    /**
     * 像素点大小 (默认：1)
     **/
    private int pixelPoint;

    /**
     * 打印起始位置
     **/
    private int offset;

    /**
     * 宽度
     **/
    private int width;

    /**
     * 高度
     **/
    private int height;

    /**
     * 走纸行数
     **/
    private int lines;

    /**
     * 打印文本/条码/二维码
     **/
    private String content;

    /**
     * 打印条码
     **/
    private String barCode;

    /**
     * 打印二维码
     **/
    private String qrCode;

    /**
     * 打印图片
     **/
    private byte[] imageData;

    /**
     * true粗体 - false常规
     */
    private boolean bold = false;

    /**
     * aidl接口有提供对齐的接口addTextInLine，leftContent,middleContent,rightContent,mode这几个参数
     */
    private String leftContent;
    private String middleContent;
    private String rightContent;
    private int mode;

    private boolean isPrintInEmvFlow;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLineSpace() {
        return lineSpace;
    }

    public void setLineSpace(int lineSpace) {
        this.lineSpace = lineSpace;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public int getGray() {
        return gray;
    }

    public void setGray(int gray) {
        this.gray = gray;
    }

    public boolean isNewline() {
        return newline;
    }

    public void setNewline(boolean newline) {
        this.newline = newline;
    }

    public int getPixelPoint() {
        return pixelPoint;
    }

    public void setPixelPoint(int pixelPoint) {
        this.pixelPoint = pixelPoint;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public String getLeftContent() {
        return leftContent;
    }

    public void setLeftContent(String leftContent) {
        this.leftContent = leftContent;
    }

    public String getMiddleContent() {
        return middleContent;
    }

    public void setMiddleContent(String middleContent) {
        this.middleContent = middleContent;
    }

    public String getRightContent() {
        return rightContent;
    }

    public void setRightContent(String rightContent) {
        this.rightContent = rightContent;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isPrintInEmvFlow() {
        return isPrintInEmvFlow;
    }

    public void setPrintInEmvFlow(boolean printInEmvFlow) {
        isPrintInEmvFlow = printInEmvFlow;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }
}
