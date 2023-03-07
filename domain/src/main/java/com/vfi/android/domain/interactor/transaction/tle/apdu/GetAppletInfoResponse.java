package com.vfi.android.domain.interactor.transaction.tle.apdu;

public class GetAppletInfoResponse extends ApduResponse {
    private String appletName;
    private String appletVersion;

    public GetAppletInfoResponse(byte[] response) {
        super(response);
    }

    public String getAppletName() {
        return appletName;
    }

    public String getAppletVersion() {
        return appletVersion;
    }
}
