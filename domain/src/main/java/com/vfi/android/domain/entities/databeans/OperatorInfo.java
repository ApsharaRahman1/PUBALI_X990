package com.vfi.android.domain.entities.databeans;


/**
 * Created by fusheng.z on 18/12/2017.
 */

public class OperatorInfo {
    public final static int TYPE_OPERATOR       = 0;       //Operator
    public final static int TYPE_MANAGER        = 1;       //Manager
    public final static int TYPE_SETTING        = 2;
    public final static int TYPE_SUPER_MANAGER  = 3;       //Super Manager
    public final static int TYPE_EXPORT_LOG     = 4;       //Only used for export Log

    public final static int MAX_PASSWD_LEN  = 6;

    private int operatorType;
    private String passwdMd5Val;

    public OperatorInfo(int operatorType, String passwdMd5Val) {
        this.operatorType = operatorType;
        this.passwdMd5Val = passwdMd5Val;
    }

    public int getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(int operatorType) {
        this.operatorType = operatorType;
    }

    public String getPasswdMd5Val() {
        return passwdMd5Val;
    }

    public void setPasswdMd5Val(String passwdMd5Val) {
        this.passwdMd5Val = passwdMd5Val;
    }
}
