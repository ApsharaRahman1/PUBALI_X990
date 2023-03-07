package com.vfi.android.domain.interactor.transaction.tle.apdu;

public class ChangePINCmd extends ApduCmd {
    public ChangePINCmd(String newPin) {
        setCla((byte) 0x80);
        setIns((byte) 0x30);
        setP1((byte) 0x00);
        setP2((byte) 0x00);
        setLc((byte) newPin.length());
        setData(newPin.getBytes());
    }
}
