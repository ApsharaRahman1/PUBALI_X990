package com.vfi.android.domain.interactor.transaction.tle.apdu;

public class InitializeMutualAuthenticationCmd extends ApduCmd {
    public InitializeMutualAuthenticationCmd(byte[] eRndA1A2) {
        setCla((byte) 0x00);
        setIns((byte) 0x43);
        setP1((byte) 0x00);
        setP2((byte) 0x00);
        setLc((byte) eRndA1A2.length);
        setData(eRndA1A2);
    }
}
