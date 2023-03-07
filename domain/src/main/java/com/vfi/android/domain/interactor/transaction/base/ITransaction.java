package com.vfi.android.domain.interactor.transaction.base;

import com.vfi.android.domain.entities.comm.CommParam;
import com.vfi.android.domain.entities.exceptions.CommonException;

public interface ITransaction {
    /**
     * return transaction communication parameter
     * @return communication parameter
     */
    CommParam getCommParam() throws CommonException;

    /**
     * Get transaction data, send it to bank host.
     * @return normally is ISO message data.
     */
    byte[] getTransData() throws CommonException;

    /**
     * Process bank host response data.
     * @param transResult normally is bank response ISO message data.
     */
    void processTransResult(byte[] transResult) throws CommonException;

    /**
     * Process communication error, such as timeout of send/receive message
     * @param commErrorType {@link com.vfi.android.domain.entities.comm.CommErrorType}
     * @return true - transaction process error by itself.  false - communication module throw exception
     */
    boolean processCommError(int commErrorType) throws CommonException;

    /**
     * Provide hooks for communication
     * @param commStatus {@link com.vfi.android.domain.entities.comm.CommStatus}
     */
    void transStatusHook(int commStatus) throws CommonException;
}
