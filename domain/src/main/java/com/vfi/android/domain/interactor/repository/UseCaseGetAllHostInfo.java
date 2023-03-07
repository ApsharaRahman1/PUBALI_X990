package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetAllHostInfo extends UseCase<List<HostInfo>, Void> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetAllHostInfo(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<List<HostInfo>> buildUseCaseObservable(Void v) {
        return Observable.just(iRepository.getAllHostInfos());
    }
}
