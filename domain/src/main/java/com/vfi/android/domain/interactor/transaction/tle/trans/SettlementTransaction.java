package com.vfi.android.domain.interactor.transaction.tle.trans;

import com.vfi.android.domain.entities.comm.CommParam;
import com.vfi.android.domain.entities.comm.CommStatus;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.databeans.SettlementRecordItem;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.base.BaseTransaction;
import com.vfi.android.domain.interactor.transaction.base.ITransaction;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

public class SettlementTransaction extends BaseTransaction implements ITransaction {
    private final String TAG = TAGS.Settlement;
    private final SettlementRecordItem settlementRecordItem;

    @Inject
    public SettlementTransaction(IRepository iRepository, IPosService posService, SettlementRecordItem settlementRecordItem) {
        super(iRepository, posService, TransType.SETTLEMENT);
        this.settlementRecordItem = settlementRecordItem;

        setHostInfo(iRepository.getHostInfoByHostType(settlementRecordItem.getHostType()));
        setMerchantInfo(iRepository.getMerchantInfo(settlementRecordItem.getMerchantIndex()));
    }

    @Override
    public CommParam getCommParam() throws CommonException {
        return getSocketCommParam();
    }

    @Override
    public byte[] getTransData() throws CommonException {
        return packISO8583Message(toSettlementPackInfo(settlementRecordItem));
    }

    @Override
    public void processTransResult(byte[] transResult) throws CommonException {
        unPackISO8583Message(transResult);

        if (isHostApproved()) {
            increaseBatchNum();
            settlementRecordItem.setSettlementSuccess(true);
            clearBatch(settlementRecordItem.getHostType(), settlementRecordItem.getMerchantIndex());
        } else {
            if (isNeedBatchUpload()) {
                throw new CommonException(ExceptionType.SETTLEMENT_NEED_BATCH_UPLOAD);
            } else {
                settlementRecordItem.setSettlementSuccess(false);
                markTerminalStatusForceSettlement();
            }
        }
    }

    @Override
    public boolean processCommError(int commErrorType) throws CommonException {
        return false;
    }

    @Override
    public void transStatusHook(int commStatus) throws CommonException {
        doCommStatusProcess(commStatus);
    }

    public SettlementRecordItem getSettlementRecordItem() {
        return settlementRecordItem;
    }

    public int getHostType() {
        return settlementRecordItem.getHostType();
    }

    public int getMerchantIndex() {
        return settlementRecordItem.getMerchantIndex();
    }
}
