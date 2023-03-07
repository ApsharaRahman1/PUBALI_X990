package com.vfi.android.domain.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class TDESUtils {
    private static final String TAG = "TDESUtils";

    /**
     * 对8bytes 0 进行加密
     * @param key 16 bytes key
     * @return 加密后的数据
     */
    public static byte[] calculate3DESKcv(byte[] key) {
        byte[] key24 = new byte[24];
        byte[] iv = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

        if (key == null) {
            LogUtil.e(TAG, "encrypt3DES key is null!");
            return null;
        }

        //LogUtil.d(TAG, "encrypt3DES Key: " + StringUtil.byte2HexStr(key));

        if (key.length != 16) {
            LogUtil.e(TAG, "encrypt3DES length of key is not 16 bytes!!");
            return null;
        }

        System.arraycopy(key, 0, key24, 0, 16);
        System.arraycopy(key, 0, key24, 16, 8);

        //生成密钥
        SecretKey desKey = new SecretKeySpec(key24, "DESede");

        try {
            Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, desKey, new IvParameterSpec(iv));
            byte[] ret = cipher.doFinal(new byte[8]);

            //LogUtil.d(TAG, "encrypt3DES Out: " + StringUtil.byte2HexStr(ret));
            return ret;
        } catch (Exception ex) {
            LogUtil.e(TAG, "3DES加密失败");
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * DES加密
     * @param key 16 bytes key
     * @param data 要加密的数据, 长度是8的倍数
     * @return 加密后的数据
     */
    public static byte[] encrypt3DES(byte[] key, byte[] data) {
        byte[] key24 = new byte[24];
        byte[] iv = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

        if (key == null) {
            LogUtil.e(TAG, "encrypt3DES key is null!");
            return null;
        }

        LogUtil.d(TAG, "encrypt3DES Key: " + StringUtil.byte2HexStr(key));

        if (key.length != 16) {
            LogUtil.e(TAG, "encrypt3DES length of key is not 16 bytes!!");
            return null;
        }

        System.arraycopy(key, 0, key24, 0, 16);
        System.arraycopy(key, 0, key24, 16, 8);

        //生成密钥
        SecretKey desKey = new SecretKeySpec(key24, "DESede");

        try {
            Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, desKey, new IvParameterSpec(iv));
            byte[] ret = cipher.doFinal(data);

            LogUtil.d(TAG, "decrypt3DES Out len: " + ret.length);
            LogUtil.d(TAG, "encrypt3DES Out: " + StringUtil.byte2HexStr(ret));
            return ret;
        } catch (Exception ex) {
            LogUtil.e(TAG, "3DES加密失败");
            ex.printStackTrace();
        }
        return null;
    }

    public static byte[] calculate3DESKcvByECBMode(byte[] key) {
        byte[] key24 = new byte[24];

        if (key == null) {
            LogUtil.e(TAG, "encrypt3DES key is null!");
            return null;
        }

        //LogUtil.d(TAG, "encrypt3DES Key: " + StringUtil.byte2HexStr(key));

        if (key.length != 16) {
            LogUtil.e(TAG, "encrypt3DES length of key is not 16 bytes!!");
            return null;
        }

        System.arraycopy(key, 0, key24, 0, 16);
        System.arraycopy(key, 0, key24, 16, 8);

        //生成密钥
        SecretKey desKey = new SecretKeySpec(key24, "DESede");

        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
            byte[] ret = cipher.doFinal(new byte[8]);

            //LogUtil.d(TAG, "encrypt3DES Out: " + StringUtil.byte2HexStr(ret));
            return ret;
        } catch (Exception ex) {
            LogUtil.e(TAG, "3DES加密失败");
            ex.printStackTrace();
        }
        return null;
    }

    public static byte[] encrypt3DESByECBMode(byte[] key, byte[] data) {
        byte[] key24 = new byte[24];

        if (key == null) {
            LogUtil.e(TAG, "encrypt3DES key is null!");
            return null;
        }

        LogUtil.d(TAG, "encrypt3DES Key: " + StringUtil.byte2HexStr(key));

        if (key.length != 16) {
            LogUtil.e(TAG, "encrypt3DES length of key is not 16 bytes!!");
            return null;
        }

        System.arraycopy(key, 0, key24, 0, 16);
        System.arraycopy(key, 0, key24, 16, 8);

        //生成密钥
        SecretKey desKey = new SecretKeySpec(key24, "DESede");

        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
            byte[] ret = cipher.doFinal(data);

            LogUtil.d(TAG, "decrypt3DES Out len: " + ret.length);
            LogUtil.d(TAG, "encrypt3DES Out: " + StringUtil.byte2HexStr(ret));
            return ret;
        } catch (Exception ex) {
            LogUtil.e(TAG, "3DES加密失败");
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 3DES CBC 双倍长解密
     * @param key 16 bytes key
     * @param data 加密数据
     * @return 解密后数据
     */
    public static byte[] decrypt3DES(byte[] key, byte[] data){
        byte[] key24 = new byte[24];
        byte[] iv = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

        if (key == null || data == null) {
            LogUtil.e(TAG, "encrypt3DES key/data is null!");
            return null;
        }

        if (key.length != 16) {
            LogUtil.e(TAG, "encrypt3DES length of key/data is not 16 bytes!!");
            return null;
        }

        LogUtil.d(TAG, "decrypt3DES  Key: " + StringUtil.byte2HexStr(key));
        LogUtil.d(TAG, "decrypt3DES data: " + StringUtil.byte2HexStr(data));

        System.arraycopy(key, 0, key24, 0, 16);
        System.arraycopy(key, 0, key24, 16, 8);
        //生成密钥
        SecretKey desKey = new SecretKeySpec(key24, "DESede");

        try {
            Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, desKey, new IvParameterSpec(iv));
            byte[] ret = cipher.doFinal(data);

            LogUtil.d(TAG, "decrypt3DES Out len: " + ret.length);
            LogUtil.d(TAG, "decrypt3DES Out: " + StringUtil.byte2HexStr(ret));
            return ret;
        } catch (Exception ex) {
            //解密失败，打日志
            LogUtil.e(TAG, "3DES解密失败");
            ex.printStackTrace();
        }
        return null;
    }
}
