package com.vfi.android.domain.interactor.transaction.tle.trans;

import com.vfi.android.domain.entities.comm.CommType;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TransErrorCode;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.base.BaseOnlineTransaction;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

public class InstallmentSaleTransaction extends BaseOnlineTransaction {

    @Inject
    public InstallmentSaleTransaction(IRepository iRepository, IPosService posService) {
        super(iRepository, posService, TransType.INSTALLMENT);
    }

    @Override
    protected void processTransResult(boolean isApproved) throws CommonException {
        if (isHostApproved()) {
            saveTransactionRecord();
        } else {
            throw new CommonException(ExceptionType.TRANS_FAILED, TransErrorCode.TRANS_REJECT);
        }
    }

    @Override
    protected int getCommType() {
        return CommType.SOCKET;
    }
}
