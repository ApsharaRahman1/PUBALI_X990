package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetAllMerchantInfo extends UseCase<List<MerchantInfo>, Void> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetAllMerchantInfo(ThreadExecutor threadExecutor,
                                     PostExecutionThread postExecutionThread,
                                     IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<List<MerchantInfo>> buildUseCaseObservable(Void v) {
        return Observable.just(iRepository.getAllMerchants());
    }
}
