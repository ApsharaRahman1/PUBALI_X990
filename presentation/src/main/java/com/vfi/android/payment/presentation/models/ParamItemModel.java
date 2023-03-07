package com.vfi.android.payment.presentation.models;

import java.util.List;

public class ParamItemModel {
    private int paramType;
    private String paramName;
    private int paramFormatType;
    private String paramValue;

    private int belongListIndex; // If parameter belong in a parameter list, record list index in this field;
    private int currentIndex; // only for FORMAT_LIST
    private List<String> paramList; // only for FORMAT_LIST

    private boolean isNeedCheckBatchEmpty;
    private boolean isNeedCheckSuperPassword;
    private boolean isCurrentEmptyBatch;

    // below are parameter format types
    public static final int FMT_CONST               = 0; // no allow change it
    public static final int FMT_STRING              = 1;
    public static final int FMT_NUMBER              = 2;
    public static final int FMT_BOOLEAN             = 3;
    public static final int FMT_TIME_HH_MM          = 4; // HH:MM
    public static final int FMT_SELECT_LIST         = 5;
    public static final int FMT_NUMBER_PASSWD_MAX_6 = 6;
    public static final int FMT_IP_ADDR             = 7;
    public static final int FMT_AMOUNT              = 8;
    public static final int FMT_NUMBER_MAX_6        = 9;
    public static final int FMT_NUM_L_PADDING_FIX_6 = 10;
    public static final int FMT_PAN_10              = 11;
    public static final int FMT_NUMBER_MAX_2        = 12;
    public static final int FMT_NUMBER_MAX_1        = 13;
    public static final int FMT_NUMBER_MAX_5        = 14;
    public static final int FMT_STR_L_PADDING_FIX_8 = 15;
    public static final int FMT_STR_L_PADDING_FIX_15= 16;
    public static final int FMT_INDEXS_LIST         = 17;
    public static final int FMT_CVV2_MAX            = 18;
    public static final int FMT_CVV2_MIN            = 19;

    public ParamItemModel(int paramType, int paramFormatType, String paramName, String paramValue) {
        this.paramType = paramType;
        this.paramName = paramName;
        this.paramFormatType = paramFormatType;
        this.paramValue = paramValue;
    }

    public ParamItemModel(int paramType, int paramFormatType, String paramName, int paramValue) {
        this.paramType = paramType;
        this.paramName = paramName;
        this.paramFormatType = paramFormatType;
        this.paramValue = "" + paramValue;
    }

    public ParamItemModel(int paramType, int paramFormatType, String paramName, long paramValue) {
        this.paramType = paramType;
        this.paramName = paramName;
        this.paramFormatType = paramFormatType;
        this.paramValue = "" + paramValue;
    }

    public ParamItemModel(int paramType, int paramFormatType, String paramName, boolean paramValue) {
        this.paramType = paramType;
        this.paramName = paramName;
        this.paramFormatType = paramFormatType;
        this.paramValue = "" + paramValue;
    }

    public ParamItemModel(int paramType, int paramFormatType, String paramName, int currentIndex, List<String> paramList) {
        this.paramType = paramType;
        this.paramName = paramName;
        this.paramFormatType = paramFormatType;
        if (currentIndex >= paramList.size()) {
            currentIndex = 0;
        }
        this.paramValue = "" + paramList.get(currentIndex);
        this.currentIndex = currentIndex;
        this.paramList = paramList;
    }

    public ParamItemModel(int paramType, int paramFormatType, String paramName, String paramValue, int belongListIndex) {
        this.paramType = paramType;
        this.paramName = paramName;
        this.paramFormatType = paramFormatType;
        this.paramValue = paramValue;
        this.belongListIndex = belongListIndex;
    }

    public ParamItemModel(int paramType, int paramFormatType, String paramName, int paramValue, int belongListIndex) {
        this.paramType = paramType;
        this.paramName = paramName;
        this.paramFormatType = paramFormatType;
        this.paramValue = "" + paramValue;
        this.belongListIndex = belongListIndex;
    }

    public ParamItemModel(int paramType, int paramFormatType, String paramName, long paramValue, int belongListIndex) {
        this.paramType = paramType;
        this.paramName = paramName;
        this.paramFormatType = paramFormatType;
        this.paramValue = "" + paramValue;
        this.belongListIndex = belongListIndex;
    }

    public ParamItemModel(int paramType, int paramFormatType, String paramName, boolean paramValue, int belongListIndex) {
        this.paramType = paramType;
        this.paramName = paramName;
        this.paramFormatType = paramFormatType;
        this.paramValue = "" + paramValue;
        this.belongListIndex = belongListIndex;
    }

    public ParamItemModel(int paramType, int paramFormatType, String paramName, int currentIndex, List<String> paramList, int belongListIndex) {
        this.paramType = paramType;
        this.paramName = paramName;
        this.paramFormatType = paramFormatType;
        if (currentIndex >= paramList.size()) {
            currentIndex = 0;
        }
        this.paramValue = "" + paramList.get(currentIndex);
        this.currentIndex = currentIndex;
        this.paramList = paramList;
        this.belongListIndex = belongListIndex;
    }

    public int getParamType() {
        return paramType;
    }

    public String getParamName() {
        return paramName;
    }

    public int getParamFormatType() {
        return paramFormatType;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public List<String> getParamList() {
        return paramList;
    }

    public int getBelongListIndex() {
        return belongListIndex;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public boolean isNeedCheckBatchEmpty() {
        return isNeedCheckBatchEmpty;
    }

    public ParamItemModel setNeedCheckBatchEmpty(boolean needCheckBatchEmpty, boolean currentEmptyBatch) {
        isNeedCheckBatchEmpty = needCheckBatchEmpty;
        isCurrentEmptyBatch = currentEmptyBatch;

        return this;
    }

    public boolean isNeedCheckSuperPassword() {
        return isNeedCheckSuperPassword;
    }

    public ParamItemModel setNeedCheckSuperPassword(boolean needCheckSuperPassword) {
        isNeedCheckSuperPassword = needCheckSuperPassword;

        return this;
    }

    public boolean isCurrentEmptyBatch() {
        return isCurrentEmptyBatch;
    }
}
