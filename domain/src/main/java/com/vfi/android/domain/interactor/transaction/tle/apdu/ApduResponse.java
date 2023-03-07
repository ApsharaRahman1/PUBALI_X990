package com.vfi.android.domain.interactor.transaction.tle.apdu;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

public class ApduResponse {
    protected final String TAG = TAGS.TLE;

    private boolean isSuccess;
    /**
     * SW12 Description
     * ISO7816 Standard SW12
     * 0x9000 OK. Command executed successfully
     * 0x6A86 Operation Failed. Incorrect Parameters P1 P2
     * 0x6A84 Operation Failed. File Full (Insufficient memory)
     * 0x6882 Operation Failed. Secure Messaging Not Supported
     * 0x6986 Operation Failed. Command Not Allowed
     * 0x6982 Operation Failed. Security status not satified
     * 0x6983 Operation Failed. Invalid File ID (or Key ID)
     * 0x6E00 Operation Failed. CLA (Class) not supported
     * 0x6A81 Operation Failed. Function not supported
     * 0x6B00 Operation Failed. Incorrect Parameters P1 P2
     * 0x6A83 Operation Failed. Record Not Found
     * 0x6A82 Operation Failed. File not found (or Key not found)
     * 0x6984 Operation Failed. Invalid Data
     * 0x6985 Operation Failed. Condition of use not satisfied
     * 0x6D00 Operation Failed. INS value not supported
     * 0x6700 Operation Failed. Wrong length
     * 0x6A80 Operation Failed. Wrong data
     * 0x65XX Response byte remaining XX
     * Application Defined SW12
     * 0x69XX Authentication Failed. XX indicates remaining tries
     * 0x6500 PIN Verification Failed
     * 0x6801 Mutual Auth Failed: Cryptograms (Random Number) Comparison Failed
     * 0x6802 KCV Check Failed
     * SAM Specification
     * 0x6803 Security Status Not Satisfied: Mutual Auth Needed
     * 0x6804 Status Not Satisfied: Command Not Allowed
     */
    private String status; // hex string of status
    private byte[] data;

    public ApduResponse(byte[] response) {
        LogUtil.d(TAG, "response=[" + StringUtil.byte2HexStr(response) + "]");
        if (response == null || response.length < 2) {
            isSuccess = false;
            status = "";
        } else {
            String respHex = StringUtil.byte2HexStr(response);
            status = respHex.substring(respHex.length() - 4);
            LogUtil.d(TAG, "Apdu response status=[" + status + "]");
            if (status.equals("9000")) {
                isSuccess = true;
            }

            if (respHex.length() > 4) {
                String dataHex = respHex.substring(0, respHex.length() - 4);
                LogUtil.d(TAG, "Apdu response data=[" + dataHex + "]");
                data = StringUtil.hexStr2Bytes(dataHex);
            }
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getStatus() {
        return status;
    }

    public byte[] getData() {
        return data;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
