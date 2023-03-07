package com.vfi.android.domain.utils;


import android.util.Base64;

import com.vfi.android.domain.entities.consts.TAGS;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class EncryptionUtil {
    private final static String TAG = TAGS.Encryption;
    public static Random globalRandom;

    public static String getNumRandomRound10(int count) {
        if (globalRandom == null) {
            globalRandom = new Random();
        }

        String numStr = "";
        for (int i = 0; i < count; i++) {
            numStr += globalRandom.nextInt(10);
        }

        LogUtil.d(TAG, "Random=[" + numStr + "]");
        return numStr.toUpperCase();
    }

    public static String getRandomBytesAndBreakDown(int count) {
        if (globalRandom == null) {
            globalRandom = new Random();
        }

        byte[] randomBytes = new byte[count];
        globalRandom.nextBytes(randomBytes);

        String randomStr = StringUtil.byte2HexStr(randomBytes);
        LogUtil.d(TAG, "randomBytes=" + randomStr);

        return randomStr.toUpperCase();
    }

    public static String getNumRandomRound10AndBreakDown(int count) {
        String ramdonStr = getNumRandomRound10(count);
        String breakDownStr = "";

        for (int i = 0; i < ramdonStr.length(); i++) {
            byte[] ramdon = ramdonStr.getBytes();
            breakDownStr += (ramdon[i] ^ 0xFF);
        }
        LogUtil.d(TAG, "BreakDownStr=[" + breakDownStr + "]");

        return ramdonStr.toUpperCase() + breakDownStr.toUpperCase();
    }

    public static byte[] generateIV(String randomHexString) {
        byte[] iv = new byte[8];
        if (randomHexString == null || randomHexString.length() < 6) {
            return iv;
        }

        byte[] ramdon = StringUtil.hexStr2Bytes(randomHexString);
        iv[0] = ramdon[0];
        iv[1] = ramdon[1];
        iv[2] = ramdon[2];
        iv[3] = (byte) (ramdon[0] ^ 0xFF);
        iv[4] = (byte) (ramdon[1] ^ 0xFF);
        iv[5] = (byte) (ramdon[2] ^ 0xFF);
        iv[6] = 0x00;
        iv[7] = 0x00;

        LogUtil.d(TAG, "iv=[" + StringUtil.byte2HexStr(iv) + "]");
        return iv;
    }

    public static String getPinpadIdentifier(String manufacturer, String sn) {
        String identifier = manufacturer + sn;
        int len = (identifier.length() + 7) / 8 * 8;
        identifier += "000000000".substring(0, len - identifier.length());
        LogUtil.d(TAG, "identifier=[" + identifier + "]");

        return identifier;
    }

    public static String generateNewKey(String manufacturer, String sn, String keyHexString) {
        String data1 = getPinpadIdentifier(manufacturer, sn);
        byte[] data1bytes = StringUtil.hexStr2Bytes(data1);
        for (int i = 0; i < data1bytes.length; i++) {
            data1bytes[i] = (byte) (data1bytes[i] ^ 0xFF);
        }

        LogUtil.d(TAG, "data2=[" + StringUtil.byte2HexStr(data1bytes) + "]");
        byte[] key = StringUtil.hexStr2Bytes(keyHexString);
        String finalData = data1 + StringUtil.byte2HexStr(data1bytes);
        LogUtil.d(TAG, "final data=[" + finalData + "]");

        String result = encECB3DesNoPadding(keyHexString, finalData);

        LogUtil.d(TAG, "result=[" + result + "]");
        return result.toUpperCase();
    }

    /**
     * @param keyHexString
     * @return hex string data
     */
    public static String caculateKcv(String keyHexString) {
        String dataHexString = "0000000000000000";
        String kcv = encECB3DesNoPadding(keyHexString, dataHexString);
        LogUtil.d(TAG, "KCV=[" + kcv + "]");
        return kcv.toUpperCase();
    }

    /**
     * @param keyHexString
     * @param dataHexString
     * @return hex string data
     */
    public static String encECB3DesNoPadding(String keyHexString, String dataHexString) {
        StringBuffer stringBuffer = new StringBuffer();
        byte[] temp = null;
        byte[] temp1 = null;

        int i = 0;
        while (i + 16 <= dataHexString.length()) {
            byte[] data = StringUtil.hexStr2Bytes(dataHexString.substring(i, i+16));
            temp1 = encryptDes(StringUtil.hexStr2Bytes(keyHexString.substring(0, 16)), data, true);
            temp = decryptDes(StringUtil.hexStr2Bytes(keyHexString.substring(16, 32)), temp1, true);
            temp1 = encryptDes(StringUtil.hexStr2Bytes(keyHexString.substring(0, 16)), temp, true);
            stringBuffer.append(StringUtil.byte2Str(temp1));
            i += 16;
        }

        return stringBuffer.toString().toUpperCase();
    }

    /**
     * @param keyHexString
     * @param dataHexString
     * @return hex string data
     */
    public static String decECB3DesNoPadding(String keyHexString, String dataHexString) {
        StringBuffer stringBuffer = new StringBuffer();
        byte[] temp = null;
        byte[] temp1 = null;

        int i = 0;
        while (i + 16 <= dataHexString.length()) {
            byte[] data = StringUtil.hexStr2Bytes(dataHexString.substring(i, i+16));
            temp1 = decryptDes(StringUtil.hexStr2Bytes(keyHexString.substring(0, 16)), data, true);
            temp = encryptDes(StringUtil.hexStr2Bytes(keyHexString.substring(16, 32)), temp1, true);
            temp1 = decryptDes(StringUtil.hexStr2Bytes(keyHexString.substring(0, 16)), temp, true);
            stringBuffer.append(StringUtil.byte2Str(temp1));
            i += 16;
        }

        return stringBuffer.toString().toUpperCase();
    }

    /**
     * @param keyHexString
     * @param dataHexString
     * @return hex string data
     */
    public static String encCBC3DesNoPadding(String keyHexString, String dataHexString) {
        try{
            if (keyHexString.length() == 32) {
                keyHexString += keyHexString.substring(0, 16);
            }
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(StringUtil.hexStr2Bytes(keyHexString));
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);

            Cipher cipher = Cipher.getInstance("desede/CBC/NoPadding");
            byte[] iv = StringUtil.hexStr2Bytes("0000000000000000");
            IvParameterSpec ips = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
            byte[] encryptData = cipher.doFinal(StringUtil.hexStr2Bytes(dataHexString));

            return StringUtil.byte2HexStr(encryptData);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param keyHexString
     * @param dataHexString
     * @return hex string data
     */
    public static String decCBC3DesNoPadding(String keyHexString, String dataHexString) {
        try{
            if (keyHexString.length() == 32) {
                keyHexString += keyHexString.substring(0, 16);
            }
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(StringUtil.hexStr2Bytes(keyHexString));
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/CBC/NoPadding");
            byte[] iv = StringUtil.hexStr2Bytes("0000000000000000");
            IvParameterSpec ips = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

            byte[] decryptData = cipher.doFinal(StringUtil.hexStr2Bytes(dataHexString));
            return StringUtil.byte2HexStr(decryptData);
        } catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private static byte[] encryptDes(byte[] key, byte[] src, boolean isECBMode) {
        try {
            DESKeySpec desKey = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKey);
            Cipher cipher;
            if (isECBMode) {
                cipher = Cipher.getInstance("DES/ECB/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            } else {
                cipher = Cipher.getInstance("DES/CBC/NoPadding");
                byte[] iv = new byte[8];
                Arrays.fill(iv, (byte) '\0');
                cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            }
            return cipher.doFinal(src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] decryptDes(byte[] key, byte[] src, boolean isECBMode) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKey);
            Cipher cipher;
            if (isECBMode) {
                cipher = Cipher.getInstance("DES/ECB/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, secretKey, random);
            } else {
                cipher = Cipher.getInstance("DES/CBC/NoPadding");
                byte[] iv = new byte[8];
                Arrays.fill(iv, (byte) '\0');
                cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            }
            return cipher.doFinal(src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMd5HexString(byte[] value) {
        if (value == null) {
            value = new byte[0];
        }

        String md5HexString = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(value);
            byte[] md5Bytes = messageDigest.digest();
            md5HexString = StringUtil.byte2HexStr(md5Bytes);
            LogUtil.d(TAG, "md5HexString=" + md5HexString);
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e(TAG, "NoSuchAlgorithmException");
            e.printStackTrace();
        }

        return md5HexString;
    }

    public static byte[] doXOR(byte[] A, byte[] B, int blockLen) {
        byte[] result = new byte[blockLen];
        Arrays.fill(result, (byte) '\0');
        for (int i = 0; i < blockLen; i++) {
            if (i < A.length) {
                result[i] = A[i];
            }

            if (i < B.length) {
                result[i] ^= B[i];
            }
        }

        return result;
    }

    public static String doXOR(String AHexStr, String BHexStr, int blockLen) {
        AHexStr = StringUtil.getNonNullStringRightPadding(AHexStr, blockLen * 2);
        BHexStr = StringUtil.getNonNullStringRightPadding(BHexStr, blockLen * 2);

        byte[] A = StringUtil.hexStr2Bytes(AHexStr);
        byte[] B = StringUtil.hexStr2Bytes(BHexStr);

        return StringUtil.byte2HexStr(doXOR(A, B, blockLen));
    }

    public static String sha1(String dataHex) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(StringUtil.hexStr2Bytes(dataHex));

            StringBuilder buf = new StringBuilder();
            byte[] digest = md.digest();
            buf.append(StringUtil.byte2HexStr(digest));

            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String paddingWithISO9797Method2(String dataHex) {
        int paddingLen = (dataHex.length() + 15) / 16 * 16 - dataHex.length();
        LogUtil.d(TAG, "paddingLen=" + paddingLen);
        String paddingHexStr = "80" + "0000000000000000".substring(0, paddingLen - 2);

        dataHex += paddingHexStr;
        LogUtil.d(TAG, "final dataHex=" + dataHex);
        return dataHex;
    }

    public static String removeISO9797Method2Padding(String dataHex) {
        LogUtil.d(TAG, "removeISO9797Method2Padding dataHex=" + dataHex);
        int index = dataHex.lastIndexOf("80");
        if (index == -1 || index < dataHex.length() - 16) {
            return dataHex;
        } else {
            String originData = dataHex.substring(0, index);
            LogUtil.d(TAG, "removeISO9797Method2Padding final=" + originData);
            return originData;
        }
    }

    public static String addEncode250(String dataHex) {
        StringBuffer stringBuffer = new StringBuffer();
        byte[] data = StringUtil.hexStr2Bytes(dataHex);
        for (int i = 0; i < data.length; i++) {
            if (data[i] <= 0x05 && data[i] >= 0x00) {
                byte value = (byte) (0x30 + data[i]);
                stringBuffer.append(String.format("00%02X", value));
            } else {
                stringBuffer.append(String.format("%02X", data[i]));
            }
        }

        return stringBuffer.toString();
    }

    public static String removeEncode250(String dataHex) {
        StringBuffer stringBuffer = new StringBuffer();
        byte[] data = StringUtil.hexStr2Bytes(dataHex);
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0x00) {
                i++;
                byte value = (byte) (data[i] - 0x30);
                stringBuffer.append(String.format("%02X", value));
            } else {
                stringBuffer.append(String.format("%02X", data[i]));
            }
        }

        return stringBuffer.toString();
    }

    public static String encodeBase64(String dataHex) {
        byte[] data = StringUtil.hexStr2Bytes(dataHex.toString());
        String base64 = Base64.encodeToString(data, 0, data.length, Base64.URL_SAFE | Base64.NO_WRAP);
        LogUtil.d(TAGS.Encryption, "base64=[" + base64 + "]");
        return base64;
    }

    public static String decodeBase64(String data) {
        byte[] base64Bytes = Base64.decode(data, Base64.DEFAULT);
        return StringUtil.byte2HexStr(base64Bytes);
    }
}
