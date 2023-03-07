package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveMerchantInfoList extends UseCase<Boolean, List<MerchantInfo>> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSaveMerchantInfoList(ThreadExecutor threadExecutor,
                                       PostExecutionThread postExecutionThread,
                                       IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(List<MerchantInfo> merchantInfoList) {
        return Observable.create(emitter -> {
            if (merchantInfoList != null && merchantInfoList.size() > 0) {
                iRepository.clearMerchantDataTable();
                Iterator<MerchantInfo> iterator = merchantInfoList.iterator();
                while (iterator.hasNext()) {
                    MerchantInfo merchantInfo = iterator.next();
                    iRepository.putMerchantInfo(merchantInfo);
                }
            }
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
