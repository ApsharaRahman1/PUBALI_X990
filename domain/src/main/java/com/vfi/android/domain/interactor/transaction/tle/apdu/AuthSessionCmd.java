package com.vfi.android.domain.interactor.transaction.tle.apdu;

public class AuthSessionCmd extends ApduCmd {
    public AuthSessionCmd(byte[] encResp) {
        if (encResp == null) {
            encResp = new byte[0];
        }

        setCla((byte) 0x00);
        setIns((byte) 0x42);
        setP1((byte) 0x00);
        setP2((byte) 0x00);
        setLc((byte) (encResp.length));
        setData(encResp); // encrypted message from Smart RKI host
    }
}
