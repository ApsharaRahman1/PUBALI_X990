package com.vfi.android.domain.interactor.transaction.tle.apdu;

public class GetAppletInfoCmd extends ApduCmd {
    public GetAppletInfoCmd() {
        setCla((byte) 0x00);
        setIns((byte) 0x12);
        setP1((byte) 0x00);
        setP2((byte) 0x00);
        setData(null);
        setLe((byte) 0x00);
    }
}
