package com.vfi.android.domain.entities.comm;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;

public class CommParam {
    /**
     * Communication type, such as socket, http,....
     * {@link CommType}
     */
    private int commType;

    /**
     * Socket communication parameters
     */
    private SocketParam socketParam;

    /**
     * SSL socket communication parameters
     */
    private SSLSocketParam sslSocketParam;

    public CommParam(int commType) {
        this.commType = commType;
        if (commType != CommType.SIMULATE_COMM) {
            LogUtil.e(TAGS.COMM, "Only CommType[SIMULATE_COMM] can use.");
            throw new RuntimeException("Only CommType[SIMULATE_COMM] can use.");
        }
    }

    public CommParam(int commType, SocketParam param) {
        this.commType = commType;
        this.socketParam = param;
    }

    public CommParam(int commType, SSLSocketParam param) {
        this.commType = commType;
        this.sslSocketParam = param;
    }

    public int getCommType() {
        return commType;
    }

    public SocketParam getSocketParam() {
        return socketParam;
    }

    public SSLSocketParam getSslSocketParam() {
        return sslSocketParam;
    }
}
