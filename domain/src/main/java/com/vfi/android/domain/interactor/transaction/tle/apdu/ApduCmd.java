package com.vfi.android.domain.interactor.transaction.tle.apdu;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;


public class ApduCmd {
    protected String TAG = TAGS.TLE;

    /**
     * CLA Description
     * 0x00 Normal Operation Request
     * 0x20 Admin Operation Request
     */
    private byte cla;
    /**
     * INS Description
     * 0xA4 Select Application
     * 0x12 Get Applet Info
     * 0x20 Verify PIN
     * 0x30 Change PIN
     * 0x45 Establish Secure Channel
     * 0x46 Get App Key
     * 0x43 Initialize Mutaul Auth
     * 0x44 Mutual Auth
     * 0x41 Request Session
     * 0x42 Authenticate Session
     */
    private byte ins;
    private byte p1;
    private byte p2;
    private Byte lc;
    private byte[] data;
    private Byte le;

    public ApduCmd() {
        lc = null;
        le = null;
    }

    public byte[] getApduCmd() {
        LogUtil.d(TAG, "=============cmd start==========================");
        int cmdLen = 4;
        if (lc != null) {
            cmdLen ++;
        }
        if (data != null) {
            cmdLen += data.length;
        }
        if (le != null) {
            cmdLen ++;
        }

        byte[] cmd = new byte[cmdLen];
        int index = 0;
        cmd[index++] = cla;
        LogUtil.d(TAG, "CLA=[" + String.format("%02X", cla) + "]");
        cmd[index++] = ins;
        LogUtil.d(TAG, "INS=[" + String.format("%02X", ins) + "]");
        cmd[index++] = p1;
        LogUtil.d(TAG, "P1=[" + String.format("%02X", p1) + "]");
        cmd[index++] = p2;
        LogUtil.d(TAG, "P2=[" + String.format("%02X", p2) + "]");
        if (lc != null) {
            cmd[index++] = lc;
            LogUtil.d(TAG, "LC=[" + String.format("%02X", lc) + "]");
        } else {
            LogUtil.d(TAG, "LC=[null]");
        }
        if (data != null && data.length > 0) {
            System.arraycopy(data, 0, cmd, index, data.length);
            index += data.length;
            LogUtil.d(TAG, "Data=[" + StringUtil.byte2HexStr(data) + "]");
        } else {
            LogUtil.d(TAG, "Data=[null]");
        }
        if (le != null) {
            cmd[index++] = le;
            LogUtil.d(TAG, "LE=[" + String.format("%02X", le) + "]");
        } else {
            LogUtil.d(TAG, "LE=[null]");
        }

        LogUtil.d(TAG, "Cmd len=[" + cmdLen + "] data=[" + StringUtil.byte2HexStr(data) + "]");
        LogUtil.d(TAG, "=============cmd end============================");
        return cmd;
    }

    public byte getCla() {
        return cla;
    }

    public void setCla(byte cla) {
        this.cla = cla;
    }

    public byte getIns() {
        return ins;
    }

    public void setIns(byte ins) {
        this.ins = ins;
    }

    public byte getP1() {
        return p1;
    }

    public void setP1(byte p1) {
        this.p1 = p1;
    }

    public byte getP2() {
        return p2;
    }

    public void setP2(byte p2) {
        this.p2 = p2;
    }

    public Byte getLc() {
        return lc;
    }

    public void setLc(Byte lc) {
        this.lc = lc;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Byte getLe() {
        return le;
    }

    public void setLe(Byte le) {
        this.le = le;
    }
}
