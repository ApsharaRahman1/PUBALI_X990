package com.vfi.android.domain.interactor.repository;


import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetRecordInfoByInvoice extends UseCase<RecordInfo, String> {
    private IRepository iRepository;

    @Inject
    public UseCaseGetRecordInfoByInvoice(ThreadExecutor threadExecutor,
                                         PostExecutionThread postExecutionThread,
                                         IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<RecordInfo> buildUseCaseObservable(String invoiceNo) {
        return Observable.create(emitter -> {
            RecordInfo recordInfo = iRepository.getRecordInfoByInvoice(invoiceNo);
            if (recordInfo == null) {
                List<RecordInfo> reversalList = iRepository.getReversalRecords();
                Iterator<RecordInfo> iterator = reversalList.iterator();
                while (iterator.hasNext()) {
                    RecordInfo reversal = iterator.next();
                    if (reversal.getInvoiceNum().endsWith(invoiceNo)) {
                        emitter.onNext(reversal);
                        emitter.onComplete();
                        return;
                    }
                }

                emitter.onError(new CommonException(ExceptionType.TRANS_RECORD_NOT_FOUND));
            } else {
                emitter.onNext(iRepository.getRecordInfoByInvoice(invoiceNo));
                emitter.onComplete();
            }
        });
    }
}
