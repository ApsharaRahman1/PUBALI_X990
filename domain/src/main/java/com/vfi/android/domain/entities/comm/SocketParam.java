package com.vfi.android.domain.entities.comm;

public class SocketParam {
    /**
     * host ip address
     */
    private String hostIp;
    /**
     * host port
     */
    private int port;
    /**
     * Connect timeout time, default 30 secs.
     */
    private int connectTimeout = 30; // seconds

    public SocketParam(String hostIp, int port) {
        this.hostIp = hostIp;
        this.port = port;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}
