package com.vfi.android.domain.interactor.transaction.base;

import com.vfi.android.domain.entities.comm.CommParam;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;

public abstract class BaseOfflineTransaction extends BaseTransaction implements ITransaction {
    private final IRepository iRepository;

    public BaseOfflineTransaction(IRepository iRepository,
                                  IPosService posService, int transType) {
        super(iRepository, posService, transType);
        this.iRepository = iRepository;
    }

    protected abstract void saveTransInfo() throws CommonException;

    @Override
    public CommParam getCommParam() throws CommonException {
        return getOfflineTransCommParam();
    }

    @Override
    public byte[] getTransData() throws CommonException {
        return new byte[0];
    }

    @Override
    public void processTransResult(byte[] transResult) throws CommonException {
        saveTransInfo();
    }

    @Override
    public boolean processCommError(int commErrorType) throws CommonException {
        return false;
    }

    @Override
    public void transStatusHook(int commStatus) throws CommonException {
        doCommStatusProcess(commStatus);
    }
}
