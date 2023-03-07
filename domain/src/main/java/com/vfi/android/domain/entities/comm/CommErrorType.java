package com.vfi.android.domain.entities.comm;

public class CommErrorType {
    // connect error
    public static final int COMM_PARAM_ERROR    = 1000;
    public static final int HOST_NOT_FOUND      = 1001;
    public static final int CONNECT_TIMEOUT     = 1002;
    public static final int CONNECT_FAILED      = 1003;
    // send error
    public static final int SEND_DATA_FAILED    = 1004;
    // recv error
    public static final int READ_DATA_FAILED    = 1005;
    public static final int READ_DATA_TIMEOUT   = 1006;
}
