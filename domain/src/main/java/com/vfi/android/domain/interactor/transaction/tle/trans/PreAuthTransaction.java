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

public class PreAuthTransaction extends BaseOnlineTransaction {
    private final IRepository iRepository;

    @Inject
    public PreAuthTransaction(IRepository iRepository, IPosService posService) {
        super(iRepository, posService, TransType.PREAUTH);
        this.iRepository = iRepository;
    }

    @Override
    protected void processTransResult(boolean isApproved) throws CommonException {
        if (isApproved) {
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
