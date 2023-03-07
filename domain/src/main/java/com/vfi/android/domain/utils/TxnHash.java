package com.vfi.android.domain.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TxnHash {
    private static final String TAG = "TxnHash";

    public static String calcTxnHash(String TE_ID, String TE_PIN, String TerminalID, String TraceNo) {
        String txn_hash = null;
        try {
            String hash_data = calcTLEHash(TE_ID, TE_PIN, "1234");
            txn_hash = calcTLEHash(hash_data, TerminalID, TraceNo);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "calc txn_hash field");
        }

        LogUtil.i(TAG, "calc txn_hash " + txn_hash);
        return txn_hash;
    }

    private static String calcTLEHash(String string1, String string2, String string3) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string1);
        stringBuilder.append(string2);
        stringBuilder.append(string3.substring(string3.length() - 4));
        String PIN_hash = sha1(stringBuilder.toString());
        return PIN_hash.substring(0, 8).toUpperCase();
    }

    public static String sha1(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        try {
            md.update(data.getBytes("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder buf = new StringBuilder();
        byte[] bits = md.digest();
        for(int i=0;i<bits.length;i++){
            int a = bits[i];
            if(a<0) a+=256;
            if(a<16) buf.append("0");
            buf.append(Integer.toHexString(a));
        }
        return buf.toString();
    }
}
