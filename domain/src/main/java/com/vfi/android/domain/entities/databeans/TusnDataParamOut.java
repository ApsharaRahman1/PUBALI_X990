package com.vfi.android.domain.entities.databeans;

/**
 * Created by fusheng.z on 2018/3/19.
 */

public class TusnDataParamOut {
    /**
     * 终端类型: 1为旧终端 2为新终端
     */
    private int terminalType;
    /**
     * 使用终端TUSN_AUK对传入随机数与TUSN进行MAC运算的结果，对于旧终端，为8个空格
     */
    private String mac;
    /**
     * 唯一终端序列号
     */
    private String tusn;

    public int getTerminalType() {
        return terminalType;
    }

    public TusnDataParamOut(int terminalType, String tusn, String mac ){
        this.terminalType = terminalType;
        this.tusn = tusn;
        this.mac = mac;
    }

    public TusnDataParamOut setTerminalType(int terminalType) {
        this.terminalType = terminalType;
        return this;
    }

    public String getMac() {
        return mac;
    }

    public TusnDataParamOut setMac(String mac) {
        this.mac = mac;
        return this;
    }

    public String getTusn() {
        return tusn;
    }

    public TusnDataParamOut setTusn(String tusn) {
        this.tusn = tusn;
        return this;
    }
}
