package com.vfi.android.domain.interactor.transaction.tle.trans;

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.comm.CommErrorType;
import com.vfi.android.domain.entities.comm.CommParam;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.base.BaseTransaction;
import com.vfi.android.domain.interactor.transaction.base.ITransaction;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;


public class ReversalTransaction extends BaseTransaction implements ITransaction {

    public ReversalTransaction(IRepository iRepository,
                               IPosService posService,
                               RecordInfo recordInfo) {
        super(iRepository, posService, recordInfo, TransType.REVERSAL);
    }

    @Override
    public CommParam getCommParam() {
        return getSocketCommParam();
    }

    @Override
    public byte[] getTransData() {
        return packISO8583Message();
    }

    @Override
    public void processTransResult(byte[] transResult) throws CommonException {
        try {
            unPackISO8583Message(transResult);
        } catch (Exception e) {
            throw new CommonException(ExceptionType.REVERSAL_FAILED);
        }

        if (isHostApproved()) {
            clearReversal();
        } else {
            throw new CommonException(ExceptionType.REVERSAL_FAILED);
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
                throw new CommonException(ExceptionType.REVERSAL_FAILED);
        }

        return false;
    }

    @Override
    public void transStatusHook(int commStatus) {
        doCommStatusProcess(commStatus);
    }
}
