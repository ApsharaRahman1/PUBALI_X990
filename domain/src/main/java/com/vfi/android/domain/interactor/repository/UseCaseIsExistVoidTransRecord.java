package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TransAttribute;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.NotAllowError;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Observable;


/**
 * Created by fusheng.z on 2018/04/08.
 */

public class UseCaseIsExistVoidTransRecord extends UseCase<Boolean, String> {
    private final String TAG = TAGS.Transaction;

    private final IRepository iRepository;

    @Inject
    UseCaseIsExistVoidTransRecord(IRepository iRepository,
                                  ThreadExecutor threadExecutor,
                                  PostExecutionThread postExecutionThread,
                                  UseCaseGetCardBinInfoByIndex useCaseGetCardBinInfoByIndex,
                                  UseCaseGetHostInfo useCaseGetHostInfo,
                                  UseCaseGetMerchantInfo useCaseGetMerchantInfo) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;

    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(String invoiceNum) {

        return Observable.create(emitter -> {
            CurrentTranData currentTranData = iRepository.getCurrentTranData();
            RecordInfo recordInfo = iRepository.getRecordInfoByInvoice(invoiceNum);
            if (recordInfo == null) {
                LogUtil.e(TAG, "tranceNum=" + invoiceNum + " record not found");
                emitter.onError(new CommonException(ExceptionType.TRANS_NOT_ALLOW, NotAllowError.TRANS_RECORD_NOT_FOUND));
                return;
            }
            int recordTransType = recordInfo.getTransType();

            if (recordTransType != TransType.SALE
                    && recordTransType != TransType.OFFLINE
                    && recordTransType != TransType.PREAUTH_COMP
                    && recordTransType != TransType.INSTALLMENT
                    && recordTransType != TransType.TIP_ADJUST) {
                LogUtil.e(TAG, "TransType[" + recordTransType + "] not allow do void transaction.");
                emitter.onError(new CommonException(ExceptionType.TRANS_NOT_ALLOW, NotAllowError.ORIGIN_TRANS_NOT_ALLOW_VOID));
                return;
            }

            CardBinInfo cardBinInfo = iRepository.getCardBinInfoByIndex(recordInfo.getCardBinIndex());
            if (cardBinInfo == null) {
                LogUtil.e(TAG, "Do void transaction, card bin not found.");
                emitter.onError(new CommonException(ExceptionType.TRANS_NOT_ALLOW, NotAllowError.ORIGIN_TRANS_HOST_NOT_FOUND));
                return;
            }
            currentTranData.setCardBinInfo(cardBinInfo);

            HostInfo hostInfo = iRepository.getHostInfoByHostType(recordInfo.getHostType());
            if (hostInfo == null) {
                LogUtil.e(TAG, "Do void transaction, host not found.");
                emitter.onError(new CommonException(ExceptionType.TRANS_NOT_ALLOW, NotAllowError.ORIGIN_TRANS_HOST_NOT_FOUND));
                return;
            }
            currentTranData.setHostInfo(hostInfo);

            MerchantInfo merchantInfo = iRepository.getMerchantInfo(recordInfo.getMerchantIndex());
            if (merchantInfo == null) {
                LogUtil.e(TAG, "Do void transaction, merchant not found.");
            }
            currentTranData.setMerchantInfo(merchantInfo);

            recordInfo.setVoidOrgTransType(recordInfo.getTransType());
            recordInfo.setVoidOrgTraceNum(recordInfo.getTraceNum());
            recordInfo.setOrgInvoiceNum(recordInfo.getInvoiceNum());
            recordInfo.setOrgBatchNo(recordInfo.getBatchNo());
            recordInfo.setOrgAuthCode(recordInfo.getAuthCode());
            recordInfo.setOrgRefNo(recordInfo.getRefNo());
            recordInfo.setCardOrgCode(recordInfo.getCardOrgCode());
            recordInfo.setTransType(TransType.VOID);
            if (recordInfo.getVoidOrgTransType() == TransType.INSTALLMENT) {
                recordInfo.setProcessCode("029700");
            } else {
                recordInfo.setProcessCode(TransAttribute.findTypeByType(TransType.VOID).getProcessCode());
            }
            recordInfo.setTraceNum(merchantInfo.getTraceNum());
            recordInfo.setInvoiceNum(recordInfo.getInvoiceNum()); // void transaction not change invoice number
            recordInfo.setBatchNo(merchantInfo.getBatchNum());
            if (recordTransType != TransType.TIP_ADJUST
                    && recordTransType != TransType.OFFLINE) {
                recordInfo.setAuthCode(null);
                recordInfo.setRefNo(null); // ISO 8583 need use orgRefNo
                recordInfo.setCardOrgCode(null);
            }
            currentTranData.setRecordInfo(recordInfo);

            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
