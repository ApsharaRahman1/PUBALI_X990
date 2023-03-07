package com.vfi.android.domain.utils;


import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by huan.lu on 2018/12/4.
 */

public class AESUtil {
    private final static byte[] iv = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    public static SecretKeySpec createAES_Key(String password) {
        try {
            //密钥生成器
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG","Crypto");
            random.setSeed(password.getBytes());
            kg.init(128,random);

            //产生原始对称密钥
            SecretKey secretKey = kg.generateKey();
            //获得对称密钥数组
            byte[] encodeFormat = secretKey.getEncoded();
            //生成AES 密钥
            SecretKeySpec aesKey = new SecretKeySpec(encodeFormat,"AES");

            return aesKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encrypt(String content, String password) {
        try {

            byte[] byteContent = content.getBytes("UTF-8");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivp = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE,createAES_Key(password),ivp);

            byte[] result = cipher.doFinal(byteContent);

            return result;
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
