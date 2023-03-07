package com.vfi.android.domain.entities.databeans;

public class EmvOnlineResultParamIn {
    private boolean  isOnline; //是否联机
    private String respCode;//应答码
    private String authCode; // 授权码
    private String field55; //55域应答数据

    public static EmvOnlineResultParamIn getInstance(){
        return new EmvOnlineResultParamIn();
    }

    public boolean isOnline() {
        return isOnline;
    }

    public EmvOnlineResultParamIn setOnline(boolean online) {
        isOnline = online;
        return this;
    }

    public String getRespCode() {
        return respCode;
    }

    public EmvOnlineResultParamIn setRespCode(String respCode) {
        this.respCode = respCode;
        return this;
    }

    public String getAuthCode() {
        return authCode;
    }

    public EmvOnlineResultParamIn setAuthCode(String authCode) {
        this.authCode = authCode;
        return this;
    }

    public String getField55() {
        return field55;
    }

    public EmvOnlineResultParamIn setField55(String field55) {
        this.field55 = field55;
        return this;
    }
}
