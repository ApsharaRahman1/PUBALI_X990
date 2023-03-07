package com.vfi.android.domain.entities.databeans;

/**
 * Created by hongchao.w on 2018/2/5.
 */

public class EmvTagListParam {
    private String[] tagList;


    public String[] getTagList(){
        return this.tagList;
    }
    public void setTagList(String[]tagList){
        this.tagList = tagList;
    }
}
