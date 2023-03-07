package com.vfi.android.domain.entities.databeans;

public class PinKeyCoordinate {
    public static final int KEY_TYPE_NUM = 0;
    public static final int KEY_TYPE_CONFIRM = 1;
    public static final int KEY_TYPE_CANCEL = 2;
    public static final int KEY_TYPE_DELETE = 3;

    private String keyName;
    private int leftTopX;
    private int leftTopY;
    private int rightBottomX;
    private int rightBottomY;
    private int keyType;

    public PinKeyCoordinate(String keyName, int leftTopX, int leftTopY, int rightBottomX, int rightBottomY, int keyType) {
        this.keyName = keyName;
        this.leftTopX = leftTopX;
        this.leftTopY = leftTopY;
        this.rightBottomX = rightBottomX;
        this.rightBottomY = rightBottomY;
        this.keyType = keyType;
    }


    public String getKeyName() {
        return keyName;
    }

    public int getKeyType() {
        return keyType;
    }

    public int getLeftTopX() {
        return leftTopX;
    }

    public int getLeftTopY() {
        return leftTopY;
    }

    public int getRightBottomX() {
        return rightBottomX;
    }

    public int getRightBottomY() {
        return rightBottomY;
    }
}
