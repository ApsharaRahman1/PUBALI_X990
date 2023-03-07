package com.vfi.android.domain.interactor.transaction.tle.apdu;

public class GetAppKeyCmd extends ApduCmd {
    public GetAppKeyCmd() {
        setCla((byte) 0x00);
        setIns((byte) 0x46);
        setP1((byte) 0x00);
        setP2((byte) 0x00); // Key ID: 0x00 – APP KEY
        setLe((byte) 0x1A);
    }
}
