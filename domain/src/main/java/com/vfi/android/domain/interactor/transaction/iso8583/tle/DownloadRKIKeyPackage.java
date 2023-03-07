package com.vfi.android.domain.interactor.transaction.iso8583.tle;

import com.vfi.android.domain.entities.businessbeans.TLEConfig;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.entities.tle.TLEPackageInfo;
import com.vfi.android.domain.interactor.transaction.iso8583.base.AbsTransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.ITransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.TransPackageData;
import com.vfi.android.domain.utils.EncryptionUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

public class DownloadRKIKeyPackage extends AbsTransPackage implements ITransPackage {
    private TLEPackageInfo tlePackageInfo;
    private TLEConfig tleConfig;

    public DownloadRKIKeyPackage(TransPackageData transPackageData) {
        super(transPackageData);
        this.tlePackageInfo = transPackageData.getTlePackageInfo();
        this.tleConfig = tlePackageInfo.getTleConfig();
    }

    @Override
    protected int[] getTransFields() {
        return new int[] {0, 3, 11, 24, 41, 42, 57};
    }

    @Override
    public byte[] packISO8583Message() throws CommonException {
        return packISOMessage();
    }

    @Override
    public void unPackISO8583Message(byte[] message) throws CommonException {
        unPackISOMessage(message);
    }

    @Override
    public String f3_processCode() {
        return "950001";
    }

    @Override
    public String f11_traceNum() {
        return getTransPackageData().getMerchantInfo().getTraceNum();
    }

    @Override
    public String f24_nii() {
        return tleConfig.getRkiNII();
    }

    @Override
    public String f41_terminalId() {
        return getTransPackageData().getMerchantInfo().getTerminalId();
    }

    @Override
    public String f42_cardAcquirerId() {
        return getTransPackageData().getMerchantInfo().getMerchantId();
    }

    @Override
    public String f57_tleData() {
        LogUtil.d(TAG, "====================Field 57 start=============");
        String encAlgo = StringUtil.getNonNullStringLeftPadding(tleConfig.getEncryptionAlgo(), 2);
        LogUtil.d(TAG, "encAlgo=[" + encAlgo + "]");
        String keyManagement = StringUtil.getNonNullStringLeftPadding(tleConfig.getKeyManagement(), 1);
        LogUtil.d(TAG, "keyManagement=[" + keyManagement + "]");
        String macAlgo = StringUtil.getNonNullStringLeftPadding(tleConfig.getMacAlgo(), 1);
        LogUtil.d(TAG, "macAlgo=[" + macAlgo + "]");
        String cmdCode = StringUtil.getNonNullStringLeftPadding(tleConfig.getCmdCode(), 2);
        LogUtil.d(TAG, "cmdCode=[" + cmdCode + "]");
        String deviceModeCode = StringUtil.getNonNullString(tleConfig.getDeviceModelCode());
        String padding = "0000000000000000000000000000000000000000".substring(deviceModeCode.length() * 2);
        String deviceModelCodeHex = StringUtil.byte2HexStr(deviceModeCode.getBytes()) + padding;
        LogUtil.d(TAG, "deviceModeCode=" + deviceModeCode);
        String terminalSN = tlePackageInfo.getTerminalSN();
        LogUtil.d(TAG, "terminalSN=" + terminalSN);
        padding = "0000000000000000000000000000000000000000".substring(terminalSN.length() * 2);
        String terminalSNHex = StringUtil.byte2HexStr(terminalSN.getBytes()) + padding;
        String appId = StringUtil.getNonNullStringLeftPadding(tleConfig.getAppId(), 2);
        LogUtil.d(TAG, "appId=" + appId);
        String smartCardSNHex = tlePackageInfo.getSmartCardSNHex();
        LogUtil.d(TAG, "smartCardSNHex=" + smartCardSNHex);
        String encryptedRequestKeyHex = tlePackageInfo.getEncSSKeyHex();
        LogUtil.d(TAG, "encryptedRequestKeyHex=" + encryptedRequestKeyHex);
        String kcvHex = tlePackageInfo.getKcvHex();
        LogUtil.d(TAG, "kcvHex=" + kcvHex);

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(StringUtil.byte2HexStr(encAlgo.getBytes()));
        stringBuffer.append(StringUtil.byte2HexStr(keyManagement.getBytes()));
        stringBuffer.append(StringUtil.byte2HexStr(macAlgo.getBytes()));
        stringBuffer.append(StringUtil.byte2HexStr(cmdCode.getBytes()));
        stringBuffer.append("01");
        stringBuffer.append(encode250(deviceModelCodeHex));
        stringBuffer.append("01");
        stringBuffer.append(encode250(terminalSNHex));
        stringBuffer.append("01");
        stringBuffer.append(encode250(StringUtil.byte2HexStr(appId.getBytes())));
        stringBuffer.append("01");
        stringBuffer.append(encode250(smartCardSNHex));
        stringBuffer.append("01");
        stringBuffer.append(encode250(encryptedRequestKeyHex));
        stringBuffer.append("01");
        stringBuffer.append(encode250(kcvHex));

        String base64 = EncryptionUtil.encodeBase64(stringBuffer.toString());
        LogUtil.d(TAGS.Encryption, "base64=[" + base64 + "]");

        String base64Hex = StringUtil.byte2HexStr(base64.getBytes());
        LogUtil.d(TAGS.Encryption, "base64Hex=[" + base64Hex + "]");

        LogUtil.d(TAG, "====================Field 57 end=============");
        return base64Hex;
    }

    private String encode250(String dataHex) {
        return EncryptionUtil.addEncode250(dataHex);
    }
}
