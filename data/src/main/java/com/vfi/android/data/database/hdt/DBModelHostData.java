package com.vfi.android.data.database.hdt;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.vfi.android.data.database.DBFlowDatabase;

/**
 * Created by RuihaoS on 2019/8/13.
 */
@Table(database = DBFlowDatabase.class)
public class DBModelHostData extends BaseModel {

    /**
     * Index of this host
     */
    @Column
    @PrimaryKey
    public int hostIndex;

    /**
     * Host type:
     * 1 – POSLINE
     * 2 - DCC
     */
    @Column
    public int hostType;

    /**
     * Host name
     */
    @Column
    public String hostName;

    /**
     * nii
     */
    @Column
    public String NII;

    /**
     * tpdu
     */
    @Column
    public String TPDU;

    /**
     * Primary IP address
     */
    @Column
    public String primaryIp;

    /**
     * Primary port
     */
    @Column
    public int primaryPort;

    /**
     * Secondary IP address
     */
    @Column
    public String secondaryIp;

    /**
     * Secondary port
     */
    @Column
    public int secondaryPort;

    /**
     * Link to merchants
     */
    @Column
    public String merchantIndexs;


    public int getHostIndex() {
        return hostIndex;
    }

    public void setHostIndex(int hostIndex) {
        this.hostIndex = hostIndex;
    }

    public int getHostType() {
        return hostType;
    }

    public void setHostType(int hostType) {
        this.hostType = hostType;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPrimaryIp() {
        return primaryIp;
    }

    public void setPrimaryIp(String primaryIp) {
        this.primaryIp = primaryIp;
    }

    public int getPrimaryPort() {
        return primaryPort;
    }

    public void setPrimaryPort(int primaryPort) {
        this.primaryPort = primaryPort;
    }

    public String getSecondaryIp() {
        return secondaryIp;
    }

    public void setSecondaryIp(String secondaryIp) {
        this.secondaryIp = secondaryIp;
    }

    public int getSecondaryPort() {
        return secondaryPort;
    }

    public void setSecondaryPort(int secondaryPort) {
        this.secondaryPort = secondaryPort;
    }

    public String getMerchantIndexs() {
        return merchantIndexs;
    }

    public void setMerchantIndexs(String merchantIndexs) {
        this.merchantIndexs = merchantIndexs;
    }

    public String getNII() {
        return NII;
    }

    public void setNII(String NII) {
        this.NII = NII;
    }

    public String getTPDU() {
        return TPDU;
    }

    public void setTPDU(String TPDU) {
        this.TPDU = TPDU;
    }
}
