package com.vfi.android.domain.entities.businessbeans;


/**
 * Created by Tony
 * 2018/12/6
 * email: chong.z@verifone.cn
 */
public class HostInfo {
    /**
     * index
     */
    private int hostIndex;

    /**
     * host type
     */
    private int hostType;

    /**
     * display host name
     */
    private String hostName;

    /**
     * tpdu
     */
    private String TPDU;

    /**
     * network international id (NII)<br></>
     */
    private String NII;

    /**
     *Primary IP address
     */
    private String primaryIp;

    /**
     * Primary port
     */
    private int primaryPort;

    /**
     * Secondary IP address
     */
    private String secondaryIp;

    /**
     * Secondary port
     */
    private int secondaryPort;

    /**
     * host support merchants, for example:  1,2,4
     */
    private String merchantIndexs;

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

    public String getTPDU() {
        return TPDU;
    }

    public void setTPDU(String TPDU) {
        this.TPDU = TPDU;
    }

    public String getNII() {
        return NII;
    }

    public void setNII(String NII) {
        this.NII = NII;
    }
}
