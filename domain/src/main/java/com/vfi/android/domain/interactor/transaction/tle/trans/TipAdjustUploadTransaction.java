package com.vfi.android.domain.interactor.transaction.tle.trans;

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.comm.CommType;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TransErrorCode;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.base.BaseOnlineTransaction;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

public class TipAdjustUploadTransaction extends BaseOnlineTransaction {

    @Inject
    public TipAdjustUploadTransaction(IRepository iRepository, IPosService posService, RecordInfo recordInfo) {
        super(iRepository, posService, recordInfo, TransType.TIP_ADJUST_UPLOAD);
    }

    @Override
    protected void processTransResult(boolean isApproved) throws CommonException {
        if (isApproved) {
            markOfflineTransUploaded();
        } else {
            throw new CommonException(ExceptionType.TRANS_FAILED, TransErrorCode.TRANS_REJECT);
        }
    }

    @Override
    protected int getCommType() {
        return CommType.SOCKET;
    }
}
