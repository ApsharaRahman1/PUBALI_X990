package com.vfi.android.data.database.nmx;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.vfi.android.data.database.DBFlowDatabase;

/**
 * Created by RuihaoS on 2019/8/13.
 */
@Table(database = DBFlowDatabase.class)
public class DBModelTLE extends BaseModel {

    /**
     * Index of DBModelTLE
     */
    @PrimaryKey
    @Column
    public int index;

    /**
     * appId
     */
    @Column
    public String appId;

    /**
     * keyId
     */
    @Column
    public String keyId;

    /**
     * encryptionAlgo
     */
    @Column
    public String encryptionAlgo;

    /**
     * macAlgo
     */
    @Column
    public String macAlgo;

    /**
     * keyManagement
     */
    @Column
    public String keyManagement;

    /**
     * Device model code, max 20 bytes
     */
    @Column
    private String deviceModelCode;

    /**
     * version
     */
    @Column
    public String version;

    /**
     * flag
     */
    @Column
    public String flag;

    @Column
    public String cmdCode;

    @Column
    public String rkiKeyId;


    @Column
    public String rkiTerminalType;


    @Column
    public String rkiAcquirerId;


    @Column
    public String rkiVendorId;


    @Column
    public String rkiTPDU;


    @Column
    public String rkiNII;


    @Column
    public String derivedKeys;


    @Column
    public String encryptionType;


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getEncryptionAlgo() {
        return encryptionAlgo;
    }

    public void setEncryptionAlgo(String encryptionAlgo) {
        this.encryptionAlgo = encryptionAlgo;
    }

    public String getMacAlgo() {
        return macAlgo;
    }

    public void setMacAlgo(String macAlgo) {
        this.macAlgo = macAlgo;
    }

    public String getKeyManagement() {
        return keyManagement;
    }

    public void setKeyManagement(String keyManagement) {
        this.keyManagement = keyManagement;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(String cmdCode) {
        this.cmdCode = cmdCode;
    }

    public String getRkiKeyId() {
        return rkiKeyId;
    }

    public void setRkiKeyId(String rkiKeyId) {
        this.rkiKeyId = rkiKeyId;
    }

    public String getRkiTerminalType() {
        return rkiTerminalType;
    }

    public void setRkiTerminalType(String rkiTerminalType) {
        this.rkiTerminalType = rkiTerminalType;
    }

    public String getRkiAcquirerId() {
        return rkiAcquirerId;
    }

    public void setRkiAcquirerId(String rkiAcquirerId) {
        this.rkiAcquirerId = rkiAcquirerId;
    }

    public String getRkiVendorId() {
        return rkiVendorId;
    }

    public void setRkiVendorId(String rkiVendorId) {
        this.rkiVendorId = rkiVendorId;
    }

    public String getRkiTPDU() {
        return rkiTPDU;
    }

    public void setRkiTPDU(String rkiTPDU) {
        this.rkiTPDU = rkiTPDU;
    }

    public String getRkiNII() {
        return rkiNII;
    }

    public void setRkiNII(String rkiNII) {
        this.rkiNII = rkiNII;
    }

    public String getDerivedKeys() {
        return derivedKeys;
    }

    public void setDerivedKeys(String derivedKeys) {
        this.derivedKeys = derivedKeys;
    }

    public String getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(String encryptionType) {
        this.encryptionType = encryptionType;
    }

    public String getDeviceModelCode() {
        return deviceModelCode;
    }

    public void setDeviceModelCode(String deviceModelCode) {
        this.deviceModelCode = deviceModelCode;
    }
}
