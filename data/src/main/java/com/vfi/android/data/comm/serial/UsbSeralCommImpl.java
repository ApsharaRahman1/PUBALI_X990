package com.vfi.android.data.comm.serial;

import com.vfi.android.domain.entities.comm.CommParam;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interfaces.comm.IComm;

import javax.inject.Inject;

public class UsbSeralCommImpl implements IComm {
    @Inject
    public UsbSeralCommImpl() {

    }

    @Override
    public void initCommParam(CommParam commParam) throws CommonException {

    }

    @Override
    public boolean connect() throws CommonException {
        return false;
    }

    @Override
    public int send(byte[] data) throws CommonException {
        return 0;
    }

    @Override
    public int receive(byte[] buf, int readLen, int timeoutMillisecond) throws CommonException {
        return 0;
    }

    @Override
    public void close() {

    }
}
