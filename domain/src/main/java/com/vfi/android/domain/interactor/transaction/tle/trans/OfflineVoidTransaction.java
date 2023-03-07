package com.vfi.android.domain.interactor.transaction.tle.trans;

import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.base.BaseOfflineTransaction;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

public class OfflineVoidTransaction extends BaseOfflineTransaction {

    @Inject
    public OfflineVoidTransaction(IRepository iRepository, IPosService posService) {
        super(iRepository, posService, TransType.VOID);
    }

    @Override
    protected void saveTransInfo() throws CommonException {
        saveTransactionRecord();
        clearOrigTransRecord();
    }

    @Override
    public void transStatusHook(int commStatus) throws CommonException {
        // Offline void no need increase trace&invoice number.
    }
}
