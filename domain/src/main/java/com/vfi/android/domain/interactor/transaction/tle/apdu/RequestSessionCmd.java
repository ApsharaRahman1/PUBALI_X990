package com.vfi.android.domain.interactor.transaction.tle.apdu;

import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

public class RequestSessionCmd extends ApduCmd {
    public RequestSessionCmd(byte[] acqId, byte[] vendorId) {
        if (acqId == null) {
            acqId = new byte[0];
        }
        if (vendorId == null) {
            vendorId = new byte[0];
        }

        byte[] data = new byte[acqId.length + vendorId.length];
        int index = 0;
        System.arraycopy(acqId, 0, data, index, acqId.length);
        index += acqId.length;
        System.arraycopy(vendorId, 0, data, index, vendorId.length);

        LogUtil.d(TAG, "dataHex=[" + StringUtil.byte2HexStr(data) + "]");

        setCla((byte) 0x00);
        setIns((byte) 0x41);
        setP1((byte) 0x00);
        setP2((byte) 0x00);
        setLc((byte) (data.length));
        setData(data);
    }
}
