package com.vfi.android.domain.entities.businessbeans;


import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

/**
 * DeviceInformation created on 2/6/18 12:32 PM
 *
 * @author yunlongg1@verifone.com
 * @version 1.0
 * description:
 * TODO
 */

public class DeviceInformation {

    private String serialNo;
    private String IMSI;
    private String IMEI;
    private String ICCID;
    private String manufacture;
    private String model;
    private String androidOSVersion;
    private String androidKernelVersion;
    private String romVersion;
    private String firmwareVersion;
    private String hardwareVersion;
    private String netAccessNo = "9613";

    public String getSerialNo() {
        serialNo = StringUtil.getNonNullString(serialNo);
//        byte[] sn = serialNo.getBytes();
//        for (int i = 0; i < serialNo.length(); i++) {
//            switch (sn[i]) {
//                case '0':
//                case '1':
//                case '2':
//                case '3':
//                case '4':
//                case '5':
//                case '6':
//                case '7':
//                case '8':
//                case '9':
//                    break;
//                default:
//                    sn[i] = '0';
//                    break;
//            }
//        }
//
//        serialNo = new String(sn);
        LogUtil.d(TAGS.Bean, "serialNo=[" + serialNo + "]");
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getIMSI() {
        return IMSI;
    }

    public void setIMSI(String IMSI) {
        this.IMSI = IMSI;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getICCID() {
        return ICCID;
    }

    public void setICCID(String ICCID) {
        this.ICCID = ICCID;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAndroidOSVersion() {
        return androidOSVersion;
    }

    public void setAndroidOSVersion(String androidOSVersion) {
        this.androidOSVersion = androidOSVersion;
    }

    public String getAndroidKernelVersion() {
        return androidKernelVersion;
    }

    public void setAndroidKernelVersion(String androidKernelVersion) {
        this.androidKernelVersion = androidKernelVersion;
    }

    public String getRomVersion() {
        return romVersion;
    }

    public void setRomVersion(String romVersion) {
        this.romVersion = romVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getNetAccessNo() {
        return netAccessNo;
    }

    public void setNetAccessNo(String netAccessNo) {
        this.netAccessNo = netAccessNo;
    }
}
