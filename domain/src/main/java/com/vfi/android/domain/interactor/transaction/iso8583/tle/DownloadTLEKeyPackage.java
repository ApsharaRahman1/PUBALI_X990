package com.vfi.android.domain.interactor.transaction.iso8583.tle;

import com.vfi.android.domain.entities.businessbeans.TLEConfig;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.entities.tle.TLEPackageInfo;
import com.vfi.android.domain.interactor.transaction.iso8583.base.AbsTransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.ITransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.TransPackageData;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

public class DownloadTLEKeyPackage extends AbsTransPackage implements ITransPackage {
    private TLEPackageInfo tlePackageInfo;
    private TLEConfig tleConfig;

    public DownloadTLEKeyPackage(TransPackageData transPackageData) {
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

        LogUtil.d(TAG, "====================Field 57 end=============");
        return super.f57_tleData();
    }
}
