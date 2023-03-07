package com.vfi.android.domain.interactor.transaction.tle.apdu;

import com.vfi.android.domain.utils.StringUtil;

public class SelectAidCmd extends ApduCmd {
    public SelectAidCmd() {
        setCla((byte) 0x00);
        setIns((byte) 0xA4);
        setP1((byte) 0x04);
        setP2((byte) 0x0C);
        setLc((byte) 0x07);
        setData(StringUtil.hexStr2Bytes("A0000047530501"));
    }
}
