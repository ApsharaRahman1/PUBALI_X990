package com.vfi.android.domain.interactor.transaction.tle.trans;

import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.base.BaseOfflineTransaction;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

public class TipAdjustTransaction extends BaseOfflineTransaction {

    @Inject
    public TipAdjustTransaction(IRepository iRepository, IPosService posService) {
        super(iRepository, posService, TransType.TIP_ADJUST);
    }

    @Override
    protected void saveTransInfo() throws CommonException {
        saveTipAdjustTransRecord();
    }
}
