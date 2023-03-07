package com.vfi.android.domain.entities.comm;

public class CommType {
    public static final int SOCKET      = 0;
    public static final int HTTP        = 1;
    public static final int HTTPS       = 2;
    public static final int SSL_SOCKET  = 3;
    public static final int USB_SERIAL  = 4;
    /**
     * Communication module skip connect host and call processTransResult directly.
     */
    public static final int SIMULATE_COMM  = 5;
}
