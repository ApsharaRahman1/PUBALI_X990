package com.vfi.android.domain;

import android.util.Base64;

import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TLEErrorCode;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.utils.EncryptionUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void generateIAK() {
        String IAK = null;

        String TAG = TAGS.Encryption;

        String vendorId = "56656E646F723030310000000000000000000000";
        vendorId = StringUtil.getNonNullStringRightPadding(vendorId, 40);
        LogUtil.d(TAG, "vendorId=" + vendorId);

        String acquirerId = "4163713030310000000000000000000000000000";
        acquirerId = StringUtil.getNonNullStringRightPadding(acquirerId, 40);
        LogUtil.d(TAG, "acquirerId=" + acquirerId);

        String randomDataHex = "0102030405060708"; //EncryptionUtil.getRandomBytesAndBreakDown(8);
        LogUtil.d(TAG, "randomDataHex=" + randomDataHex);

        String blockA = acquirerId.substring(0, 20);
        LogUtil.d(TAG, "blockA=" + blockA);
        String blockB = acquirerId.substring(20, 40);
        LogUtil.d(TAG, "blockB=" + blockB);
        String blockC = vendorId.substring(0, 20);
        LogUtil.d(TAG, "blockC=" + blockC);
        String blockD = vendorId.substring(20, 40);
        LogUtil.d(TAG, "blockD=" + blockD);

        String blockAD = EncryptionUtil.doXOR(blockA, blockD, 10);
        LogUtil.d(TAG, "blockAD=" + blockAD);

        String blockBC = EncryptionUtil.doXOR(blockB, blockC, 10);
        LogUtil.d(TAG, "blockBC=" + blockBC);

        String blockADBC = blockAD + blockBC;
        LogUtil.d(TAG, "blockADBC=" + blockADBC);

        String blockF = EncryptionUtil.doXOR(blockADBC, randomDataHex, blockADBC.length() / 2);
        LogUtil.d(TAG, "blockF=" + blockF);

        String blockG = EncryptionUtil.sha1(blockF);
        LogUtil.d(TAG, "blockG=" + blockG);

        IAK = StringUtil.getNonNullStringRightPadding(blockG, 32);
        LogUtil.d(TAG, "IAK=" + IAK);

        generateCrvptogramX(randomDataHex, IAK);
        /**
         * The SAM provided to terminal Vendor has the
         * following preloaded information (Sample)
         * Vendor ID: 56656E646F723030310000000000000000000000
         * Acquirer ID: 4163713030310000000000000000000000000000
         * Terminal random generate 8 bytes data – RndX
         * - Rndx= 0102030405060708
         * Terminal calculates IAK (Initial Authentication Key)
         * by using Algorithm 2.
         * Developer Sample Also 2:
         * Set BlockA = First 10 bytes of Acquirer ID
         * = 41637130303100000000
         * Set BlockB = Second 10 bytes of Acquirer ID
         * = 00000000000000000000
         * Set BlockC = First 10 bytes of Vendor ID
         * = 56656E646F7230303100
         * Set BlockD = Second 10 bytes of Vendor ID
         * = 00000000000000000000
         * BlockAD = XOR BlockA & BlockD
         * = 41637130303100000000
         * BlockBC = XOR BlockB & BlockC
         * = 56656E646F7230303100
         * BlockADBC = Joining BlockAD & BlockBC
         * = 4163713030310000000056656E646F7230303100
         * Block F = XOR BlockADBC & RndX
         * = 4061723435370708000056656E646F7230303100
         * BlockG = Shal(BlockF)
         * = BA23D0422BB9EF70D4A3B22918507D169BAB36A1
         * IAK = First 16 bytes of Block G
         * = BA23D0422BB9EF70D4A3B22918507D16
         */
    }

    private String generateCrvptogramX(String randomData8BytesHex, String IAK) {
        LogUtil.d(TAGS.Encryption, "randomData8BytesHex=" + randomData8BytesHex + " IAK=" + IAK);
        String crvptogramX = EncryptionUtil.encCBC3DesNoPadding(IAK, randomData8BytesHex);
        LogUtil.d(TAGS.Encryption, "crvptogramX=" + crvptogramX);

        return crvptogramX;
    }

    @Test
    public void cbcTest() {
        String key = "1ADFCB75ADE93D572F2AC1796E795804";
        String data = "EA9E86DFA70B08E904A7986D85F162C2";

        String encryptedResult = EncryptionUtil.encCBC3DesNoPadding(key, data);
        LogUtil.d(TAGS.Encryption, "encryptedResult=" + encryptedResult);
        String decryptedResult = EncryptionUtil.decCBC3DesNoPadding(key, data);
        LogUtil.d(TAGS.Encryption, "decryptedResult=" + decryptedResult);
    }

    @Test
    public void encode250Test() {
        String data = "5658353230000000000000000000000000000000";
        String result = EncryptionUtil.addEncode250(data);
        LogUtil.d(TAGS.Encryption, "result=[" + result + "]");

        if (EncryptionUtil.removeEncode250(result).equals(data)) {
            LogUtil.d("You do a good job");
        } else {
            LogUtil.d("failed.");
        }

        data = "b96df000331e835784d551b05798eaab64dc00323c2b6c8d684d1cf63b385bcab59e97942ae400334ad7b73f2564d4ab7c196aa554912554481d530cd3860fba47092e33079f46c6578e6ad82793653e964c67da9a3f0cd2481ca8e4a7833bcf27e7565b766049a51d50d6a5fa3565f0f52b44";
        LogUtil.d(TAGS.Encryption, "result=[" + EncryptionUtil.removeEncode250(data) + "]");
    }

    @Test
    public void packField57() {
        String encAlgo = "03";
        String keyManagement = "4";
        String macAlgo = "1";
        String cmdCode = "01";
        String deviceModelCodeHex = "5658353230000000000000000000000000000000";
        String terminalSNHex = "3238302d3939372d383537000000000000000000";
        String appId = "01";
        String smartCardSNHex = "3030343336000000000000000000000000000000";
        String encryptedRequestKeyHex = "084eb92735461e5d919b277f3b178da4";
        String kcvHex = "42334133";
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(StringUtil.byte2HexStr(encAlgo.getBytes()));
        stringBuffer.append(StringUtil.byte2HexStr(keyManagement.getBytes()));
        stringBuffer.append(StringUtil.byte2HexStr(macAlgo.getBytes()));
        stringBuffer.append(StringUtil.byte2HexStr(cmdCode.getBytes()));
        stringBuffer.append("01");
        stringBuffer.append(EncryptionUtil.addEncode250(deviceModelCodeHex));
        stringBuffer.append("01");
        stringBuffer.append(EncryptionUtil.addEncode250(terminalSNHex));
        stringBuffer.append("01");
        stringBuffer.append(EncryptionUtil.addEncode250(StringUtil.byte2HexStr(appId.getBytes())));
        stringBuffer.append("01");
        stringBuffer.append(EncryptionUtil.addEncode250(smartCardSNHex));
        stringBuffer.append("01");
        stringBuffer.append(EncryptionUtil.addEncode250(encryptedRequestKeyHex));
        stringBuffer.append("01");
        stringBuffer.append(EncryptionUtil.addEncode250(kcvHex));
        LogUtil.d(TAGS.Encryption, "data=[" + stringBuffer.toString() + "]");

        String base64 = EncryptionUtil.encodeBase64(stringBuffer.toString());
        LogUtil.d(TAGS.Encryption, "base64=[" + base64 + "]");
    }

//    @Test

}