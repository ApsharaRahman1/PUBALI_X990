package com.vfi.android.domain.entities.databeans;

/**
 * Created by chong.z on 2018/01/22.
 */

public class ScannerParamIn {
    /**
     * 启动扫码 | start scan
     * @param配置参数 | the parameter
     *  topTitleString(String)：扫描框最上方提示信息，最大12汉字,默认中间对齐，| the title string on the top, max length 24, align center.
     *  upPromptString(String)：扫描框上方提示信息，最大20汉字，默认中间对齐， | the prompt string upside of the scan box, max length 40, align center.
     *  downPromptString(String)：扫描框下方提示信息，最大20汉字，默认中间对齐 | the prompt string downside of the scan box , max length 40, align center.
     *  timeout - 超时时间，单位ms | the timeout, millsecond.
     *  listener - 扫码结果监听 | the call back listerner
     */

    /**
     * 扫描框最上方提示信息，最大12汉字,默认中间对齐
     **/
    private String topTitleString;

    /**
     * 扫描框上方提示信息，最大20汉字，默认中间对齐
     **/
    private String upPromptString;

    /**
     * 扫描框下方提示信息，最大20汉字，默认中间对齐
     **/
    private String downPromptString;

    /**
     * 超时时间，单位ms
     **/
    private long timeout;

    public String getTopTitleString() {
        return topTitleString;
    }

    public void setTopTitleString(String topTitleString) {
        this.topTitleString = topTitleString;
    }

    public String getUpPromptString() {
        return upPromptString;
    }

    public void setUpPromptString(String upPromptString) {
        this.upPromptString = upPromptString;
    }

    public String getDownPromptString() {
        return downPromptString;
    }

    public void setDownPromptString(String downPromptString) {
        this.downPromptString = downPromptString;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
