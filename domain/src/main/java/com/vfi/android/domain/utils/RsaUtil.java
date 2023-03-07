package com.vfi.android.domain.utils;


import android.util.Base64;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RsaUtil {
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1PADDING";

    public static KeyPair generateKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            LogUtil.d("captain", "exception:" + e.toString());
        }
        return null;
    }

    /**
     * 使用公钥加密
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) {
        try {
            byte[] decodeKey = Base64.decode(publicKey, Base64.DEFAULT);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodeKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            return encryptByPublicKey(data, pubKey);
        } catch (Exception ex) {
            ex.printStackTrace();
            LogUtil.d(LogUtil.TAG, "encryptByPublicKey exception:" + ex.toString());
            return null;
        }
    }

    private static byte[] encryptByPublicKey(byte[] data, PublicKey pubKey) {
        // 得到公钥对象
        try {
            // 加密数据
            Cipher cp = Cipher.getInstance(TRANSFORMATION);
            cp.init(Cipher.ENCRYPT_MODE, pubKey);
            return cp.doFinal(data);
        } catch (Exception ex) {
            LogUtil.d(LogUtil.TAG, "encryptByPublicKey exception:" + ex.toString());
        }
        return null;
    }

    /**
     * 使用私钥解密
     */
    public static byte[] decryptByPrivateKey(byte[] data, byte[] privateKey) {
        // 得到私钥对象
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey keyPrivate = kf.generatePrivate(keySpec);
            return decryptByPrivateKey(data, keyPrivate);
        } catch (Exception ex) {
            LogUtil.d(LogUtil.TAG, "exception:" + ex.toString());
        }
        return null;
    }

    public static byte[] decryptByPrivateKey(byte[] data, PrivateKey keyPrivate) {
        // 得到私钥对象
        try {
            Cipher cp = Cipher.getInstance(TRANSFORMATION);
            cp.init(Cipher.DECRYPT_MODE, keyPrivate);
            return cp.doFinal(data);
        } catch (Exception ex) {
            LogUtil.d(LogUtil.TAG, "exception:" + ex.toString());
        }
        return null;
    }
}
