package com.vfi.android.domain.interactor.transaction.tle.trans;

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.base.BaseOfflineTransaction;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.StringUtil;

public class SimulateNormalTransacion extends BaseOfflineTransaction {
    
    public SimulateNormalTransacion(IRepository iRepository, IPosService posService, int transType) {
        super(iRepository, posService, transType);
    }

    @Override
    protected void saveTransInfo() throws CommonException {
        RecordInfo recordInfo = getCurrentRecordInfo();
        long time = System.currentTimeMillis();
        recordInfo.setTransDate(StringUtil.formatDate(time));
        recordInfo.setTransTime(StringUtil.formatTime(time));
        recordInfo.setTerminalId(getMerchantInfo().getTerminalId());
        recordInfo.setMerchantId(getMerchantInfo().getMerchantId());
        recordInfo.setNii(getHostInfo().getNII());
        recordInfo.setAuthCode("123456");
        recordInfo.setRefNo("123456781234");
        saveTransactionRecord();
        if (getTransAttribute().isNeedIncreaseInvoice()) {
            increaseInvoice();
        }
        if (getTransAttribute().isNeedIncreaseTrace()) {
            increaseTrace();
        }
    }

    @Override
    public void transStatusHook(int commStatus) throws CommonException {
    }
}
