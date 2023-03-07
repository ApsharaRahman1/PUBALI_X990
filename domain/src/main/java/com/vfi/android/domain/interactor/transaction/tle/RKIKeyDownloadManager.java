package com.vfi.android.domain.interactor.transaction.tle;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.businessbeans.TLEConfig;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TLEErrorCode;
import com.vfi.android.domain.entities.databeans.InputTLEPinResult;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.entities.tle.RKIDownloadStatus;
import com.vfi.android.domain.interactor.transaction.UseCaseStartTransCommunication;
import com.vfi.android.domain.interactor.transaction.tle.apdu.ApduCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.AuthSessionCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.AuthSessionResponse;
import com.vfi.android.domain.interactor.transaction.tle.apdu.EstablishSecureChannelCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.EstablishSecureChannelResponse;
import com.vfi.android.domain.interactor.transaction.tle.apdu.GetAppKeyCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.GetAppKeyResponse;
import com.vfi.android.domain.interactor.transaction.tle.apdu.InitializeMutualAuthenticationCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.InitianlizeMutualAuthenticationResponse;
import com.vfi.android.domain.interactor.transaction.tle.apdu.MutualAuthenticationCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.MutualAuthenticationResponse;
import com.vfi.android.domain.interactor.transaction.tle.apdu.RequestSessionCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.RequestSessionResponse;
import com.vfi.android.domain.interactor.transaction.tle.apdu.SelectAidCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.SelectAidResponse;
import com.vfi.android.domain.interactor.transaction.tle.apdu.VerifyPINCmd;
import com.vfi.android.domain.interactor.transaction.tle.apdu.VerifyPINResponse;
import com.vfi.android.domain.interactor.transaction.tle.trans.DownloadRKIKeyTransaction;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.EncryptionUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RKIKeyDownloadManager {
    private final String TAG = TAGS.TLE;
    private IRepository iRepository;
    private IPosService iPosService;
    private CurrentTranData currentTranData;
    private KeyDownloadListener listener;
    private HostInfo hostInfo;
    private MerchantInfo merchantInfo;
    private TLEConfig tleConfig;

    private boolean isRun;
    private boolean isCorrectPin;
    private String currentIAK;
    private String currentCrvptogramX;
    private String currentTSK;
    private String currentAppKey;
    private String currentSessionKey;
    private String smartCardSNHex;
    private String encSSKeyHex;
    private String kcvHex;

    @Inject
    public RKIKeyDownloadManager(IRepository iRepository,
                                 IPosService iPosService) {
        this.iPosService = iPosService;
        this.iRepository = iRepository;
    }

    public void doPrepareForKeyDownload(KeyDownloadListener listener) {
        this.currentTranData = iRepository.getCurrentTranData();
        this.hostInfo = currentTranData.getHostInfo();
        this.merchantInfo = currentTranData.getMerchantInfo();
        this.tleConfig = iRepository.getTleConfig(merchantInfo.getTleIndex());

        LogUtil.d(TAG, "doPrepareForKeyDownload");
        this.listener = listener;
        // 1. power on
        powerOnSmartCardReader();
        listener.onStatusChanged(RKIDownloadStatus.REQ_INSERT_CARD);

        // 2. check card
        boolean isCardPresent = loopCheckIsCardInserted();
        if (isCardPresent) {
            // 3. select aid
            if (!selectAID()) {
                listener.onError(new CommonException(ExceptionType.RKI_KEY_DOANLOAD_FAILED, TLEErrorCode.SELECT_AID_FAILED));
                return;
            }
            // 3. establish secure channel
            if (!establishSecureChannel()) {
                listener.onError(new CommonException(ExceptionType.RKI_KEY_DOANLOAD_FAILED, TLEErrorCode.ESTABLISH_SECURE_CHANNEL_FAILED));
                return;
            }
            // 4. request app key
            if (!requestApplicationKey()) {
                listener.onError(new CommonException(ExceptionType.RKI_KEY_DOANLOAD_FAILED, TLEErrorCode.REQUEST_APP_KEY_FAILED));
                return;
            }
            // 5. performMutualAuthentication
            if (!performMutualAuthentication()) {
                listener.onError(new CommonException(ExceptionType.RKI_KEY_DOANLOAD_FAILED, TLEErrorCode.PERFORM_MUTUAL_AUTHENTICATION_FAILED));
                return;
            }
            listener.onStatusChanged(RKIDownloadStatus.REQ_INPUT_PIN);
        } else {
            listener.onStatusChanged(RKIDownloadStatus.DOWNLOAD_FAILED);
        }

        // 6. waiting input pin
        boolean isCorrectPin = waitingInputPin();
        if (isCorrectPin) {
            listener.onStatusChanged(RKIDownloadStatus.START_DOWNLOADING);
        } else {
            listener.onStatusChanged(RKIDownloadStatus.DOWNLOAD_FAILED);
        }

        // 7. request session
        if (!requestSession()) {
            listener.onError(new CommonException(ExceptionType.RKI_KEY_DOANLOAD_FAILED, TLEErrorCode.REQUEST_SESSION_FAILED));
            return;
        }
    }

    public void stopKeyDownload() {
        LogUtil.d(TAG, "stopKeyDownload isRun=" + isRun);
        if (isRun == true) {
            isRun = false;
        } else {
            listener.onStatusChanged(RKIDownloadStatus.DOWNLOAD_FAILED);
        }
    }

    private boolean selectAID() {
        ApduCmd apduCmd = new SelectAidCmd();
        SelectAidResponse response = new SelectAidResponse(executeAPDUCmd(apduCmd));
        if (response.isSuccess()) {
            LogUtil.d(TAG, "selectAID success");
            return true;
        } else {
            LogUtil.d(TAG, "selectAID failed");
            return false;
        }
    }

    private boolean establishSecureChannel() {
        String randomDataHex = EncryptionUtil.getRandomBytesAndBreakDown(8);
        this.currentIAK = generateIAK(randomDataHex, tleConfig.getRkiVendorId(), tleConfig.getRkiAcquirerId());
        this.currentCrvptogramX = generateCrvptogramX(randomDataHex, currentIAK);
        ApduCmd apduCmd = new EstablishSecureChannelCmd(true, StringUtil.hexStr2Bytes(randomDataHex+currentCrvptogramX));
        byte[] response = executeAPDUCmd(apduCmd);
        EstablishSecureChannelResponse escResponse = new EstablishSecureChannelResponse(response);
        escResponse.decryptionETsk(currentIAK);
        if (escResponse.isSuccess() && escResponse.isCorrectKcv()) {
            currentTSK = escResponse.getPlainTSKHexStr();
            LogUtil.d(TAG, "establishSecureChannel success");
            return true;
        } else {
            LogUtil.e(TAG, "establishSecureChannel failed");
            return false;
        }
    }

    private boolean requestApplicationKey() {
        LogUtil.d(TAG, "start requestApplicationKey");
        ApduCmd apduCmd = new GetAppKeyCmd();
        GetAppKeyResponse response = new GetAppKeyResponse(executeAPDUCmd(apduCmd));
        response.decryptionETsk(currentTSK);
        if (response.isSuccess() && response.isCorrectKcv()) {
            currentAppKey = response.getPlainAppKeyHexStr();
            return true;
        } else {
            return false;
        }
    }

    private boolean performMutualAuthentication() {
        String A1 = EncryptionUtil.getRandomBytesAndBreakDown(4);
        String A2 = EncryptionUtil.getRandomBytesAndBreakDown(4);
        String A1A2 = A1 + A2;
        LogUtil.d(TAG, "A1A2=[" + A1A2 + "]");
        String encryptedA1A2 = EncryptionUtil.encCBC3DesNoPadding(currentAppKey, A1A2);
        LogUtil.d(TAG, "encryptedA1A2=[" + encryptedA1A2 + "]");

        ApduCmd apduCmd = new InitializeMutualAuthenticationCmd(StringUtil.hexStr2Bytes(encryptedA1A2));
        InitianlizeMutualAuthenticationResponse response = new InitianlizeMutualAuthenticationResponse(executeAPDUCmd(apduCmd));
        response.decryptERndA1B2B1A2(currentAppKey);
        if (!response.isSuccess() && !response.getA1A2().equals(A1A2)) {
            LogUtil.d(TAG, "Check failed");
            return false;
        }

        String B1B2 = response.getB1B2();
        String eB1B2 = EncryptionUtil.encCBC3DesNoPadding(currentAppKey, B1B2);
        apduCmd = new MutualAuthenticationCmd(StringUtil.hexStr2Bytes(eB1B2));
        MutualAuthenticationResponse maResponse = new MutualAuthenticationResponse(executeAPDUCmd(apduCmd));
        if (!maResponse.isSuccess()) {
            return false;
        }

        LogUtil.d(TAG, "performMutualAuthentication success");
//        currentSessionKey = response.geteRndA1B2B1A2();
        currentSessionKey = response.getPlainA1B2B1A2();
        LogUtil.d(TAG, "current session key=[" + currentSessionKey + "]");
        return true;
    }

    public InputTLEPinResult inputPin(String pin) {
        LogUtil.d(TAG, "inputPin plain pin=" + pin);
        if (currentSessionKey == null || currentSessionKey.length() == 0) {
            return new InputTLEPinResult(false, 3);
        }

        String pinHexStr = EncryptionUtil.paddingWithISO9797Method2(StringUtil.byte2HexStr(pin.getBytes()));
        LogUtil.d(TAG, "pinHexStr=" + pinHexStr);
        String encryptedPin = EncryptionUtil.encCBC3DesNoPadding(currentSessionKey, pinHexStr);
        LogUtil.d(TAG, "encryptedPin=" + encryptedPin);

        ApduCmd apduCmd = new VerifyPINCmd(StringUtil.hexStr2Bytes(encryptedPin));
        VerifyPINResponse response = new VerifyPINResponse(executeAPDUCmd(apduCmd));
        this.isCorrectPin = response.isCorrectPin();
        return new InputTLEPinResult(response.isCorrectPin(), response.getRemainRetrys());
    }

    private boolean requestSession() {
        String acqIdHex = StringUtil.byte2HexStr(tleConfig.getRkiAcquirerId().getBytes());
        String acqId = StringUtil.getNonNullStringRightPadding(acqIdHex, 40);
        LogUtil.d(TAG, "acqId=[" + acqId + "]");

        String vendorIdHex = StringUtil.byte2HexStr(tleConfig.getRkiVendorId().getBytes());
        String vendorId = StringUtil.getNonNullStringRightPadding(vendorIdHex, 40);
        LogUtil.d(TAG, "vendorId=[" + vendorId + "]");

        ApduCmd apduCmd = new RequestSessionCmd(StringUtil.hexStr2Bytes(acqId), StringUtil.hexStr2Bytes(vendorId));
        RequestSessionResponse response = new RequestSessionResponse(executeAPDUCmd(apduCmd));
        if (response.isSuccess()) {
            smartCardSNHex = response.getSamIdHex();
            encSSKeyHex = response.getEncSSKeyHex();
            kcvHex = response.getKcvHex();
            return true;
        } else {
            return false;
        }
    }

    public boolean authSessionAndSaveKeys(String encryptedPayloadHex) {
        AuthSessionCmd apduCmd = new AuthSessionCmd(StringUtil.hexStr2Bytes(encryptedPayloadHex));
        AuthSessionResponse response = new AuthSessionResponse(executeAPDUCmd(apduCmd));
        response.decryptionByTsk(currentSessionKey);
        if (response.isSuccess()) {
            LogUtil.d(TAG, "Do auth session success");
        } else {
            listener.onError(new CommonException(ExceptionType.RKI_KEY_DOANLOAD_FAILED, TLEErrorCode.AUTH_SESSION_FAILED));
        }

        return true;
    }

    private void powerOnSmartCardReader() {
        iPosService.powerOnSmartCardReader().blockingSingle();
    }

    private boolean loopCheckIsCardInserted() {
        isRun = true;
        while (isRun) {
            boolean isCardPresent = iPosService.isIcCardPresent().blockingSingle();
            if (isCardPresent) {
                isRun = false;
                return true;
            } else {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }
            }
        }

        return false;
    }

    private boolean waitingInputPin() {
        isRun = true;
        isCorrectPin = false;
        while (isRun && !isCorrectPin) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
        }

        return isCorrectPin;
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

    private byte[] executeAPDUCmd(ApduCmd apduCmd) {
        return iPosService.executeAPDU(apduCmd);
    }

    public interface KeyDownloadListener {
        void onStatusChanged(int status);
        void onError(Throwable throwable);
    }

    public String getSmartCardSNHex() {
        return smartCardSNHex;
    }

    public String getEncSSKeyHex() {
        return encSSKeyHex;
    }

    public String getKcvHex() {
        return kcvHex;
    }
}
