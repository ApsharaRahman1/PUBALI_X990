package com.vfi.android.domain.interfaces.comm;

import com.vfi.android.domain.entities.comm.CommParam;
import com.vfi.android.domain.entities.exceptions.CommonException;

/**
 * Exception description: this interface only throw CommonException with below type and subErrType
 * exceptionType COMM_EXCEPTION {@link com.vfi.android.domain.entities.consts.ExceptionType}
 * subErrType {@link com.vfi.android.domain.entities.comm.CommErrorType}
 */
public interface IComm {
    void initCommParam(CommParam commParam) throws CommonException;
    boolean connect() throws CommonException;
    int send(byte[] data) throws CommonException;
    int receive(byte[] buf, int readLen, int timeoutMillisecond) throws CommonException;
    void close();
}
