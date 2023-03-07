package com.vfi.android.payment.presentation.models;

import com.vfi.android.domain.utils.StringUtil;

import java.util.List;

public class SettingItemViewModel{

    private int id;
    private int viewType; // Distinguish how to display
    private String label; // show on the left
    private String text;  // show on the right, use for ViewType: LABEL_AND_TEXT/LABEL_AND_EDIT/LABEL_AND_NUM/LABEL_AND_NUM_PASSWD
    private boolean checked; // show on the right, only use for ViewType:LABEL_AND_SWITCH
    private List<String> spinnerDropDownResource; // show on the right, only use for ViewType:LABEL_AND_SPINNER
    private int currentSpinnerIndex; // only use for ViewType:LABEL_AND_SPINNER
    private int arrayIndex;    // Distinguish array arrayIndex;
    private ParamItemModel paramItemModel; // save parameter
    private HostItemModel hostItemModel; // only use for ViewType:HOST_INFO

    public static class ViewType{
        public static final int LABEL_AND_TEXT = 0;
        public static final int LABEL_AND_EDIT = 1;
        public static final int LABEL_AND_SWITCH = 2;
        public static final int LABEL_AND_NUM = 3;
        public static final int LABEL_AND_NUM_PASSWD = 4;
        public static final int HOST_INFO = 5;
        public static final int LABEL_AND_SPINNER = 8;
    }

    public boolean isNeedSaveItem(){
        switch (viewType){
            // LABEL_AND_TEXT,LABEL_AND_EDIT,LABEL_AND_SWITCH,LABEL_AND_NUM,LABEL_AND_NUM_PASSWD will auto save changed value, other cannot save changed type need be add here.
        }
        return false;
    }

    public SettingItemViewModel(int id,int viewType,String label,String text ){
        this.id = id;
        this.viewType = viewType;
        this.label = label;
        this.text = text;
    }

    public SettingItemViewModel(int id,int viewType,String label,String text,int arrayIndex){
        this.id = id;
        this.viewType = viewType;
        this.label = label;
        this.text = text;
        this.arrayIndex = arrayIndex;
    }

    public SettingItemViewModel(int id,int viewType,String label,boolean checked){
        this.id = id;
        this.viewType = viewType;
        this.label = label;
        this.checked = checked;
    }

    public SettingItemViewModel(int id,int viewType,String label,boolean checked,int arrayIndex){
        this.id = id;
        this.viewType = viewType;
        this.label = label;
        this.checked = checked;
        this.arrayIndex = arrayIndex;
        this.text = "";
    }

    public SettingItemViewModel(int id, int viewType, HostItemModel hostItemModel, int arrayIndex) {
        this.id = id;
        this.viewType = viewType;
        this.hostItemModel = hostItemModel;
        this.arrayIndex = arrayIndex;
    }

    public SettingItemViewModel(ParamItemModel paramItemModel) {
        this.paramItemModel = paramItemModel;
        this.id = paramItemModel.getParamType();
        this.label = paramItemModel.getParamName();
        switch (paramItemModel.getParamFormatType()) {
            case ParamItemModel.FMT_CONST:
                this.viewType = ViewType.LABEL_AND_TEXT;
                this.text = paramItemModel.getParamValue();
                break;
            case ParamItemModel.FMT_BOOLEAN:
                this.viewType = ViewType.LABEL_AND_SWITCH;
                this.checked = StringUtil.parseBoolean(paramItemModel.getParamValue(), false);
                break;
            case ParamItemModel.FMT_SELECT_LIST:
                this.viewType = ViewType.LABEL_AND_SPINNER;
                this.spinnerDropDownResource = paramItemModel.getParamList();
                break;
            case ParamItemModel.FMT_AMOUNT:
            case ParamItemModel.FMT_IP_ADDR:
                this.viewType = ViewType.LABEL_AND_NUM;
                this.text = paramItemModel.getParamValue();
                break;
            case ParamItemModel.FMT_NUMBER_PASSWD_MAX_6:
                this.viewType = ViewType.LABEL_AND_NUM_PASSWD;
                this.text = paramItemModel.getParamValue();
                break;
            default:
                this.viewType = ViewType.LABEL_AND_EDIT;
                this.text = paramItemModel.getParamValue();
                break;
        }

        this.arrayIndex = paramItemModel.getBelongListIndex();
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public int getArrayIndex() {
        return arrayIndex;
    }

    public void setArrayIndex(int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ParamItemModel getParamItemModel() {
        return paramItemModel;
    }

    public int getCurrentSpinnerIndex() {
        return currentSpinnerIndex;
    }

    public HostItemModel getHostItemModel() {
        return hostItemModel;
    }
}
