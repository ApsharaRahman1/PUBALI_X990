package com.vfi.android.domain.interactor.transaction.tle.trans;

import com.vfi.android.domain.entities.businessbeans.DeviceInformation;
import com.vfi.android.domain.entities.businessbeans.TLEConfig;
import com.vfi.android.domain.entities.comm.CommParam;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TLEErrorCode;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.entities.tle.TLEPackageInfo;
import com.vfi.android.domain.interactor.transaction.base.BaseTransaction;
import com.vfi.android.domain.interactor.transaction.base.ITransaction;
import com.vfi.android.domain.interactor.transaction.tle.RKIKeyDownloadManager;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.EncryptionUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

public class DownloadRKIKeyTransaction extends BaseTransaction implements ITransaction {
    private String TAG = TAGS.TLE;
    RKIKeyDownloadManager rkiKeyDownloadManager;

    public DownloadRKIKeyTransaction(IPosService iPosService,
                                     IRepository iRepository,
                                     RKIKeyDownloadManager rkiKeyDownloadManager) {
        super(iRepository, iPosService, TransType.RKI_KEY_DOWNLOAD);

        this.rkiKeyDownloadManager = rkiKeyDownloadManager;
    }

    @Override
    public CommParam getCommParam() throws CommonException {
        return getSocketCommParam();
    }

    @Override
    public byte[] getTransData() throws CommonException {
        TLEPackageInfo tlePackageInfo = new TLEPackageInfo();
        tlePackageInfo.setTerminalSN(getTerminalSN());
        TLEConfig tleConfig = getiRepository().getTleConfig(getMerchantInfo().getTleIndex());
        tlePackageInfo.setTleConfig(tleConfig);
        tlePackageInfo.setSmartCardSNHex(rkiKeyDownloadManager.getSmartCardSNHex());
        tlePackageInfo.setEncSSKeyHex(rkiKeyDownloadManager.getEncSSKeyHex());
        tlePackageInfo.setKcvHex(rkiKeyDownloadManager.getKcvHex());

        return packISO8583Message(tlePackageInfo);
    }

    @Override
    public void processTransResult(byte[] transResult) throws CommonException {
        unPackISO8583Message(transResult);
        String filed57 = getResponseIso8583Field(57);
        if (isHostApproved() && filed57 != null) {
            try {
                LogUtil.d(TAG, "filed57 hex=[" + filed57 + "]");
                String base64 = StringUtil.hexStr2Str(filed57);
                LogUtil.d(TAG, "base64=[" + base64 + "]");
                String dataHex = EncryptionUtil.decodeBase64(base64);
                LogUtil.d(TAG, "data hex=[" + dataHex + "]");

                int index = 0;
                String encryptAlgo = new String(StringUtil.hexStr2Bytes(dataHex.substring(index, index + 4)));
                LogUtil.d(TAG, "encryptAlgo=" + encryptAlgo);
                index += 4;
                String keyManagement = new String(StringUtil.hexStr2Bytes(dataHex.substring(index, index + 2)));
                LogUtil.d(TAG, "keyManagement=" + keyManagement);
                index += 2;
                String macAlgo = new String(StringUtil.hexStr2Bytes(dataHex.substring(index, index + 2)));
                LogUtil.d(TAG, "macAlgo=" + macAlgo);
                index += 2;
                String commandCode = new String(StringUtil.hexStr2Bytes(dataHex.substring(index, index + 4)));
                LogUtil.d(TAG, "commandCode=" + commandCode);
                index += 4;
                dataHex = dataHex.substring(index);
                int firstDelimiter = dataHex.indexOf("01");
                dataHex = dataHex.substring(firstDelimiter + 2);
                LogUtil.d(TAG, "dataHex=" + dataHex);
                int secondDelimiter = dataHex.indexOf("01");
                if (secondDelimiter == -1) {
                    throw new CommonException(ExceptionType.RKI_KEY_DOANLOAD_FAILED, TLEErrorCode.HOST_RESP_FORMAT_NOT_CORRECT);
                }
                String appIdWithCode250 = dataHex.substring(0, secondDelimiter);
                LogUtil.d(TAG, "appIdWithCode250=[" + appIdWithCode250 + "]");
                String appId = StringUtil.hexStr2Str(EncryptionUtil.removeEncode250(appIdWithCode250));
                LogUtil.d(TAG, "appId=[" + appId + "]");
                firstDelimiter = dataHex.indexOf("01");
                dataHex = dataHex.substring(firstDelimiter + 2);
                LogUtil.d(TAG, "dataHex=" + dataHex);
                secondDelimiter = dataHex.indexOf("01");
                String encryptedPayloadWithCode250;
                if (secondDelimiter == -1) {
                    encryptedPayloadWithCode250 = dataHex;
                } else {
                    encryptedPayloadWithCode250 = dataHex.substring(0, secondDelimiter);
                }
                LogUtil.d(TAG, "encryptedPayloadWithCode250=[" + encryptedPayloadWithCode250 + "]");
                String encryptedPayloadHex = EncryptionUtil.removeEncode250(encryptedPayloadWithCode250);
                LogUtil.d(TAG, "encryptedPayloadHex=[" + encryptedPayloadHex + "]");

                doAuthSessionProcess(encryptedPayloadHex);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CommonException(ExceptionType.RKI_KEY_DOANLOAD_FAILED, TLEErrorCode.DOWNLOAD_RKI_KEY_FROM_HOST_FAILED);
            }
        } else {
            throw new CommonException(ExceptionType.RKI_KEY_DOANLOAD_FAILED, TLEErrorCode.DOWNLOAD_RKI_KEY_FROM_HOST_FAILED);
        }
    }

    @Override
    public boolean processCommError(int commErrorType) throws CommonException {
        return false;
    }

    @Override
    public void transStatusHook(int commStatus) throws CommonException {
        doCommStatusProcess(commStatus);
    }

    private String getTerminalSN() {
        DeviceInformation deviceInfo = getiRepository().getDeviceInformation();
        if (deviceInfo == null) {
            deviceInfo = getPosService().getDeviceInfo().blockingSingle();
            if (deviceInfo == null) {
                return "0000000";
            }
        }

        return deviceInfo.getSerialNo();
    }

    private void doAuthSessionProcess(String encryptedPayloadHex) {
        rkiKeyDownloadManager.authSessionAndSaveKeys(encryptedPayloadHex);
    }
}
