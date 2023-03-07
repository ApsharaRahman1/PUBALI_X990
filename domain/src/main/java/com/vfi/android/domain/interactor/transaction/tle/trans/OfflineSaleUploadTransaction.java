package com.vfi.android.domain.interactor.transaction.tle.trans;

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.comm.CommType;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.base.BaseOnlineTransaction;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;

public class OfflineSaleUploadTransaction extends BaseOnlineTransaction {

    public OfflineSaleUploadTransaction(IRepository iRepository,
                                        IPosService posService,
                                        RecordInfo recordInfo) {
        super(iRepository, posService, recordInfo, TransType.OFFLINE_UPLOAD);
    }

    @Override
    protected void processTransResult(boolean isApproved) throws CommonException {
        if (isApproved) {
            markOfflineTransUploaded();
        }
    }

    @Override
    protected int getCommType() {
        return CommType.SOCKET;
    }
}
