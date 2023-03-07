package com.vfi.android.domain.interactor.transaction.tle.apdu;

public class MutualAuthenticationCmd extends ApduCmd {
    public MutualAuthenticationCmd(byte[] eRndB1B2) {
        setCla((byte) 0x00);
        setIns((byte) 0x44);
        setP1((byte) 0x00);
        setP2((byte) 0x00);
        setLc((byte) eRndB1B2.length);
        setData(eRndB1B2);
    }
}
