package com.vfi.android.domain.utils;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtils {
    public static final String TAG = "RSA";
    public static RSAData rsaData = null;

    public static RSAData getRSAData() throws Exception {
        if (rsaData != null)
            return rsaData;

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        //初始化密钥生成器
        keyPairGenerator.initialize(2048);
        //生成密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        BigInteger publicExponent = publicKey.getPublicExponent();
        BigInteger publicModulus = publicKey.getModulus();
        //私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        LogUtil.d(TAG, "=====================================================================");
        LogUtil.d(TAG, "公钥长度: " + publicKey.getEncoded().length);
        //LogUtil.d(TAG, "公钥:\n" + Base64.encodeBase64String(publicKey.getEncoded()));

        LogUtil.d(TAG, "=====================================================================");
        LogUtil.d(TAG, "私钥长度: " + privateKey.getEncoded().length);
        //LogUtil.d(TAG, "私钥:\n" + Base64.encodeBase64String(privateKey.getEncoded()));

        LogUtil.d(TAG, "=====================================================================");
        LogUtil.d(TAG, "公钥指数: " + publicExponent.toString(16));

        LogUtil.d(TAG, "=====================================================================");
        LogUtil.d(TAG, "公钥modulus长度: " + publicModulus.toString(16).length());
        LogUtil.d(TAG, "公钥modulus:\n" + publicModulus.toString(16));
        LogUtil.d(TAG, "=====================================================================");

        rsaData = new RSAData();
        rsaData.setPublicKeyExponent(publicExponent.toString(16));
        rsaData.setPublicKeyModulus(publicModulus.toString(16));
        rsaData.setPrivateKey(privateKey);

        return rsaData;
    }

    /**
     * 私钥解密
     *
     * @param encryptedBytes 待解密数据
     * @param key            密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedBytes, byte[] key) throws Exception {
        int keyByteSize = 2048 / 8;
        int decryptBlockSize = keyByteSize - 11;
        int nBlock = encryptedBytes.length / keyByteSize;
        ByteArrayOutputStream outbuf = null;
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        //生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            outbuf = new ByteArrayOutputStream(nBlock * decryptBlockSize);
            for (int offset = 0; offset < encryptedBytes.length; offset += keyByteSize) {
                int inputLen = encryptedBytes.length - offset;
                if (inputLen > keyByteSize) {
                    inputLen = keyByteSize;
                }
                byte[] decryptedBlock = cipher.doFinal(encryptedBytes, offset, inputLen);
                outbuf.write(decryptedBlock);
            }
            outbuf.flush();
            return outbuf.toByteArray();
        } catch (Exception e) {
            throw new Exception("DEENCRYPT ERROR:", e);
        } finally {
            try {
                if (outbuf != null) {
                    outbuf.close();
                }
            } catch (Exception e) {
                outbuf = null;
                throw new Exception("CLOSE ByteArrayOutputStream ERROR:", e);
            }
        }
    }


    /**
     * https://blog.csdn.net/chaiqunxing51/article/details/52116433
     */
    public static byte[] encrypt(byte[] plainBytes, PublicKey publicKey, int keyLength, int reserveSize, String cipherAlgorithm) throws Exception {
        int keyByteSize = keyLength / 8;
        int encryptBlockSize = keyByteSize - reserveSize;
        int nBlock = plainBytes.length / encryptBlockSize;
        if ((plainBytes.length % encryptBlockSize) != 0) {
            nBlock += 1;
        }
        ByteArrayOutputStream outbuf = null;
        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            outbuf = new ByteArrayOutputStream(nBlock * keyByteSize);
            for (int offset = 0; offset < plainBytes.length; offset += encryptBlockSize) {
                int inputLen = plainBytes.length - offset;
                if (inputLen > encryptBlockSize) {
                    inputLen = encryptBlockSize;
                }
                byte[] encryptedBlock = cipher.doFinal(plainBytes, offset, inputLen);
                outbuf.write(encryptedBlock);
            }
            outbuf.flush();
            return outbuf.toByteArray();
        } catch (Exception e) {
            throw new Exception("ENCRYPT ERROR:", e);
        } finally {
            try {
                if (outbuf != null) {
                    outbuf.close();
                }
            } catch (Exception e) {
                outbuf = null;
                throw new Exception("CLOSE ByteArrayOutputStream ERROR:", e);
            }
        }
    }

}
