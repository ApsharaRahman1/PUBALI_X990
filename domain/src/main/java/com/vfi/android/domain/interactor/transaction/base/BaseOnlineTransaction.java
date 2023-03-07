package com.vfi.android.domain.interactor.transaction.base;

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TransAttribute;
import com.vfi.android.domain.entities.comm.CommErrorType;
import com.vfi.android.domain.entities.comm.CommParam;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;

public abstract class BaseOnlineTransaction extends BaseTransaction implements ITransaction {

    public BaseOnlineTransaction(IRepository iRepository, IPosService posService, int transType) {
        super(iRepository, posService, transType);
    }

    public BaseOnlineTransaction(IRepository iRepository, IPosService posService, RecordInfo recordInfo, int transType) {
        super(iRepository, posService, recordInfo, transType);
    }

    protected abstract void processTransResult(boolean isApproved) throws CommonException;
    protected abstract int getCommType();

    @Override
    public CommParam getCommParam() throws CommonException {
        return getCommParam(getCommType());
    }

    @Override
    public byte[] getTransData() throws CommonException {
        return packISO8583Message();
    }

    @Override
    public void processTransResult(byte[] transResult) throws CommonException {
        unPackISO8583Message(transResult);

        TransAttribute transAttribute = getTransAttribute();
        if (transAttribute.isNeedInputOnlineResult() && isInsertCardTransaction() && isFullEmvFlow()) {
            inputOnlineResult();
        } else {
            processTransResult(isHostApproved());
        }
    }

    @Override
    public boolean processCommError(int commErrorType) throws CommonException {
        switch (commErrorType) {
            case CommErrorType.COMM_PARAM_ERROR:
            case CommErrorType.CONNECT_FAILED:
            case CommErrorType.CONNECT_TIMEOUT:
            case CommErrorType.SEND_DATA_FAILED:
            case CommErrorType.READ_DATA_FAILED:
            case CommErrorType.READ_DATA_TIMEOUT:
            case CommErrorType.HOST_NOT_FOUND:
                TransAttribute transAttribute = getTransAttribute();
                if (transAttribute.isNeedOnlineFailedTryOffline() && isInsertCardTransaction() && isFullEmvFlow()) {
                    inputOnlineResult();
                    return true;
                }
                break;
        }

        return false;
    }

    @Override
    public void transStatusHook(int commStatus) throws CommonException {
        doCommStatusProcess(commStatus);
    }
}
