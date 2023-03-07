package com.vfi.android.domain.interactor.transaction.tle.apdu;

public class VerifyPINCmd extends ApduCmd {
    public VerifyPINCmd(byte[] encryptedPin) {
        setCla((byte) 0x00);
        setIns((byte) 0x20);
        setP1((byte) 0x00);
        setP2((byte) 0x00);
        setLc((byte) encryptedPin.length);
        setData(encryptedPin);
    }
}
