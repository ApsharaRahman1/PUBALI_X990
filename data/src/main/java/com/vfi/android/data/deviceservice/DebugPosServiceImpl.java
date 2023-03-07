package com.vfi.android.data.deviceservice;

import android.content.Context;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.businessbeans.TLEConfig;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.interactor.transaction.tle.apdu.ApduCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.AuthSessionCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.EstablishSecureChannelCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.GetAppKeyCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.InitializeMutualAuthenticationCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.MutualAuthenticationCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.RequestSessionCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.SW12;
import com.vfi.android.domain.interactor.transaction.tle.apdu.SelectAidCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.VerifyPINCmd;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.EncryptionUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;


import javax.inject.Inject;

public class DebugPosServiceImpl extends PosServiceImpl {
    private final String TAG = TAGS.TLE;
    private IRepository iRepository;
    private CurrentTranData currentTranData;
    private int pinRemainedRetryTimes = 3;
    private String currentPlainTsk;
    private String currentAppKey;
    private String currentSessionKey;
    private String sendB1B2;
    private String messageKeyHex;

    @Inject
    DebugPosServiceImpl(Context context, IRepository iRepository) {
        super(context);
        this.iRepository = iRepository;
    }

    @Override
    public byte[] executeAPDU(ApduCmd apduCmd) {
        currentTranData = iRepository.getCurrentTranData();
        String TAG = TAGS.TLE;
        String responseHex = "";
        if (apduCmd instanceof SelectAidCmd) {
            responseHex = doSelectAidProcess((SelectAidCmd) apduCmd);
        } else if (apduCmd instanceof EstablishSecureChannelCmd) {
            responseHex = doEstablishSecureChannelProcess((EstablishSecureChannelCmd) apduCmd);
        } else if (apduCmd instanceof GetAppKeyCmd) {
            responseHex = doGetAppKeyProcess((GetAppKeyCmd) apduCmd);
        } else if (apduCmd instanceof InitializeMutualAuthenticationCmd) {
            responseHex = doInitializeMutualAuthenticationProcess((InitializeMutualAuthenticationCmd) apduCmd);
        } else if (apduCmd instanceof MutualAuthenticationCmd) {
            responseHex = doMutualAuthenticationProcess((MutualAuthenticationCmd) apduCmd);
        } else if (apduCmd instanceof VerifyPINCmd) {
            responseHex = doVerifyPinProcess((VerifyPINCmd) apduCmd);
        } else if (apduCmd instanceof RequestSessionCmd) {
            responseHex = doRequestSessionProcess((RequestSessionCmd) apduCmd);
        } else if (apduCmd instanceof AuthSessionCmd) {
            responseHex = doAuthSessionProcess((AuthSessionCmd) apduCmd);
        }

        LogUtil.d(TAG, "responseHex=" + responseHex);
        return StringUtil.hexStr2Bytes(responseHex);
    }

    private String doSelectAidProcess(SelectAidCmd apduCmd) {
        return SW12.OK;
    }

    private String doEstablishSecureChannelProcess(EstablishSecureChannelCmd apduCmd) {
        LogUtil.d(TAG, "doEstablishSecureChannelProcess");
        String dataHex = StringUtil.byte2HexStr(apduCmd.getData());
        String randomHex = dataHex.substring(0, 16);
        LogUtil.d(TAG, "randomHex=" + randomHex);
        String cryptogramXHex = dataHex.substring(16, 32);
        LogUtil.d(TAG, "cryptogramXHex=" + cryptogramXHex);

        MerchantInfo merchantInfo = currentTranData.getMerchantInfo();
        TLEConfig tleConfig = iRepository.getTleConfig(merchantInfo.getTleIndex());
        String IAK = generateIAK(randomHex, tleConfig.getRkiVendorId(), tleConfig.getRkiAcquirerId());
        LogUtil.d(TAG, "IAK=" + IAK);

        String tmp = generateCrvptogramX(randomHex, IAK);
        LogUtil.d(TAG, "DebugPosServiceImpl tmp CryptogramX=" + tmp);
        if (!tmp.equals(cryptogramXHex)) {
            LogUtil.e(TAG, "Establish secure channel failed.");
            return SW12.OPFAIL_WRONG_DATA;
        }

        String plainTSK = EncryptionUtil.getRandomBytesAndBreakDown(16);
        LogUtil.d(TAG, "plainTSK=" + plainTSK);
        this.currentPlainTsk = plainTSK;

        String kcv = EncryptionUtil.caculateKcv(plainTSK);
        LogUtil.d(TAG, "kcv=" + kcv);

        String eTSKHex = EncryptionUtil.decCBC3DesNoPadding(IAK, plainTSK);

        String responseHex = eTSKHex + kcv.substring(0, 4);

        responseHex += SW12.OK;

        return responseHex;
    }

    private String doGetAppKeyProcess(GetAppKeyCmd apduCmd) {
        LogUtil.d(TAG, "doGetAppKeyProcess");
        String responseHex = "";
        if (currentPlainTsk == null) {
            responseHex = SW12.OPFAIL_STATUS_NOT_SATISFIED;
        } else {
            currentAppKey = EncryptionUtil.getRandomBytesAndBreakDown(16);
            String kcv = EncryptionUtil.caculateKcv(currentAppKey);
            LogUtil.d(TAG, "currentAppKey=" + currentAppKey + " kcv=" + kcv);
            String encryptedAppkey = EncryptionUtil.encCBC3DesNoPadding(currentPlainTsk, currentAppKey + "8000000000000000");
            responseHex = encryptedAppkey + kcv.substring(0, 4) + SW12.OK;
        }

        return responseHex;
    }

    private String doInitializeMutualAuthenticationProcess(InitializeMutualAuthenticationCmd apduCmd) {
        LogUtil.d(TAG, "doInitializeMutualAuthenticationProcess");
        String responseHex = "";

        if (currentAppKey == null) {
            responseHex = SW12.OPFAIL_STATUS_NOT_SATISFIED;
        } else {
            String encryptedA1A2 = StringUtil.byte2HexStr(apduCmd.getData());
            String A1A2 = EncryptionUtil.decCBC3DesNoPadding(currentAppKey, encryptedA1A2);
            LogUtil.d(TAG, "DebugPosServiceImpl A1A2=" + A1A2);

            String B1 = EncryptionUtil.getRandomBytesAndBreakDown(4);
            String B2 = EncryptionUtil.getRandomBytesAndBreakDown(4);
            LogUtil.d(TAG, "B1=[" + B1 + "]");
            LogUtil.d(TAG, "B2=[" + B2 + "]");
            String A1B2B1A2 = A1A2.substring(0, 8) + B2 + B1 + A1A2.substring(8, 16);
            LogUtil.d(TAG, "A1B2B1A2=[" + A1B2B1A2 + "]");
            this.sendB1B2 = B1 + B2;

            currentSessionKey = A1B2B1A2; //
            LogUtil.d(TAG, "currentSessionKey=" + currentSessionKey);
            String encryptedA1B2B1A2 = EncryptionUtil.decCBC3DesNoPadding(currentAppKey, A1B2B1A2);
            LogUtil.d(TAG, "encryptedA1B2B1A2=" + encryptedA1B2B1A2);
            responseHex = encryptedA1B2B1A2 + SW12.OK;
        }

        return responseHex;
    }

    private String doMutualAuthenticationProcess(MutualAuthenticationCmd apduCmd) {
        LogUtil.d(TAG, "doMutualAuthenticationProcess");
        String responseHex = "";
        if (currentAppKey == null) {
            responseHex = SW12.OPFAIL_STATUS_NOT_SATISFIED;
        } else {
            String encryptedB1B2 = StringUtil.byte2HexStr(apduCmd.getData());
            String B1B2 = EncryptionUtil.decCBC3DesNoPadding(currentAppKey, encryptedB1B2);
            LogUtil.d(TAG, "B1B2=[" + B1B2 + "]");
            LogUtil.d(TAG, "sendB1B2=[" + sendB1B2 + "]");
            if (B1B2.equals(sendB1B2)) {
                responseHex = SW12.OK;
            } else {
                responseHex = SW12.OPFAIL_MUTUAL_AUTH_FAILED;
            }
        }

        return responseHex;
    }

    private String doVerifyPinProcess(VerifyPINCmd apduCmd) {
        LogUtil.d(TAG, "doVerifyPinProcess");
        String responseHex = "";
        if (currentSessionKey == null) {
            responseHex = SW12.OPFAIL_MUTUAL_AUTH_NEEDED;
        } else {
            VerifyPINCmd cmd = apduCmd;
            String encryptedPin = StringUtil.byte2HexStr(cmd.getData());
            LogUtil.d(TAG, "encryptedPin=[" + encryptedPin + "]");

            String plainPin = EncryptionUtil.decCBC3DesNoPadding(currentSessionKey, encryptedPin);
            plainPin = EncryptionUtil.removeISO9797Method2Padding(plainPin);
            plainPin = new String(StringUtil.hexStr2Bytes(plainPin));
            LogUtil.d(TAG, "DebugPosServiceImpl plainPin=" + plainPin);

            if (plainPin.equals("123456")) {
                responseHex = SW12.OK;
            } else {
                LogUtil.d(TAG, "VerifyPIN failed");
                responseHex = "65" + String.format("%02d", pinRemainedRetryTimes);
                pinRemainedRetryTimes--;
            }
        }

        return responseHex;
    }

    private String doRequestSessionProcess(RequestSessionCmd apduCmd) {
        String smartCardSNHex = StringUtil.byte2HexStr("1234567890".getBytes());
        String smartCardSN = StringUtil.getNonNullStringRightPadding(smartCardSNHex, 40);
        LogUtil.d(TAG, "smartCardSN=[" + smartCardSN + "]");

        messageKeyHex = "084EB92735461E5D919B277F3B178DA4";
        LogUtil.d(TAG, "messageKeyHex=[" + messageKeyHex + "]");
        String kcvHex = EncryptionUtil.caculateKcv(messageKeyHex);
        LogUtil.d(TAG, "kcvHex=[" + kcvHex + "]");

        String responseHex = smartCardSN + messageKeyHex + kcvHex.substring(0, 4) + SW12.OK;
        return responseHex;
    }

    private String doAuthSessionProcess(AuthSessionCmd apduCmd) {
        String dataHex = StringUtil.byte2HexStr(apduCmd.getData());
        LogUtil.d(TAG, "dataHex=[" + dataHex + "]");

        String rkiKeyIdHex = "001122334455";
        String rkiDekHex = "C132C754EC913BE65BE029D3D32F1908";
        String rkiDekKcvHex = "8E7A381A";
        String rkiMekHex = "753EA1EC8520347AC80BF726830EC7BF";
        String rkiMekKcvHex = "6DABC257";

        String keyIdHex = "001122334455";
        String dekHex = "C132C754EC913BE65BE029D3D32F1908";
        String dekKcvHex = "8E7A381A";
        String mekHex = "753EA1EC8520347AC80BF726830EC7BF";
        String mekKcvHex = "6DABC257";

        String response = rkiKeyIdHex + "01" + rkiDekHex + "01" + rkiDekKcvHex + "01" + rkiMekHex + "01" + rkiMekKcvHex + "01";
        response += keyIdHex + "01" + dekHex + "01" + dekKcvHex + "01" + mekHex + "01" + mekKcvHex;

        String responseWithPadding = EncryptionUtil.paddingWithISO9797Method2(response);
        String encryptedResponse = EncryptionUtil.encCBC3DesNoPadding(currentSessionKey, responseWithPadding);

        return encryptedResponse + SW12.OK;
    }

    private String generateIAK(String randomData8BytesHex, String vendorId, String acquirerId) {
        String IAK = null;

        String vendorIdHex = StringUtil.byte2HexStr(vendorId.getBytes());
        vendorId = StringUtil.getNonNullStringRightPadding(vendorIdHex, 40);
        LogUtil.d(TAG, "vendorId=" + vendorId);

        String acquirerIdHex = StringUtil.byte2HexStr(acquirerId.getBytes());
        acquirerId = StringUtil.getNonNullStringRightPadding(acquirerIdHex, 40);
        LogUtil.d(TAG, "acquirerId=" + acquirerId);

        String randomDataHex = randomData8BytesHex;
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

        return IAK;
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
        String crvptogramX = EncryptionUtil.encCBC3DesNoPadding(IAK, randomData8BytesHex);
        LogUtil.d(TAG, "crvptogramX=" + crvptogramX);

        return crvptogramX;
    }
}
