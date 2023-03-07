package com.vfi.android.domain.interactor.transaction.tle.trans;

import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.base.BaseOfflineTransaction;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;

public class OfflineSaleTransaction extends BaseOfflineTransaction {
    private final IRepository iRepository;

    public OfflineSaleTransaction(IRepository iRepository,
                                  IPosService posService) {
        super(iRepository, posService, TransType.OFFLINE);
        this.iRepository = iRepository;
    }

    @Override
    protected void saveTransInfo() throws CommonException {
        saveOfflineTransactionRecord();
    }
}
