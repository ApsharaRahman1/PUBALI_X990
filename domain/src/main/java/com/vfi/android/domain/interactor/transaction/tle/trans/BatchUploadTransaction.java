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

public class BatchUploadTransaction extends BaseOnlineTransaction {
    private boolean isLastUploadRecord;

    @Inject
    public BatchUploadTransaction(IRepository iRepository, IPosService posService, RecordInfo recordInfo, boolean isLastUploadRecord) {
        super(iRepository, posService, recordInfo, TransType.BATCH_UPLOAD);
        this.isLastUploadRecord = isLastUploadRecord;
    }

    @Override
    protected void processTransResult(boolean isApproved) throws CommonException {
        if (!isApproved) {
            markTerminalStatusForceSettlement();
            throw new CommonException(ExceptionType.TRANS_FAILED, TransErrorCode.TRANS_REJECT);
        }
    }

    @Override
    protected int getCommType() {
        return CommType.SOCKET;
    }

    @Override
    public byte[] getTransData() throws CommonException {
        return packISO8583Message(isLastUploadRecord);
    }
}
