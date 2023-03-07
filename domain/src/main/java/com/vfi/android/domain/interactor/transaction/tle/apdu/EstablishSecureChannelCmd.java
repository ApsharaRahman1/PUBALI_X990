package com.vfi.android.domain.interactor.transaction.tle.apdu;

/**
 * Most of the admin functions such as verify PIN, change PIN, Get App Key and e.t.c. needed this function to
 * be completed successfully with a session key that generated base on this establishment.
 */
public class EstablishSecureChannelCmd extends ApduCmd {
    private final int AlgorithmID_personalization = 0x01;
    private final int AlgorithmID_operational     = 0x02;

    public EstablishSecureChannelCmd(boolean isOperational, byte[] data) {
        if (data == null) {
            data = new byte[0];
        }

        setCla((byte) 0x00);
        setIns((byte) 0x45);
        setP1((byte) 0x00);
        if (isOperational) {
            setP2((byte) AlgorithmID_operational);
        } else {
            setP2((byte) AlgorithmID_personalization);
        }
        setLc((byte) data.length);
        setData(data);
    }
}
