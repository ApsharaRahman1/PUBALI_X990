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

public class UseCaseSaveMerchantInfo extends UseCase<Boolean, MerchantInfo> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSaveMerchantInfo(ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread,
                                   IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(MerchantInfo merchantInfo) {
        return Observable.create(emitter -> {
            iRepository.putMerchantInfo(merchantInfo);
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
