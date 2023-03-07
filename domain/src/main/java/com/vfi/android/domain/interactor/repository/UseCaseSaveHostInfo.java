package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveHostInfo extends UseCase<Boolean, HostInfo> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSaveHostInfo(ThreadExecutor threadExecutor,
                               PostExecutionThread postExecutionThread,
                               IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(HostInfo hostInfo) {
        return Observable.create(emitter -> {
            iRepository.putHostInfo(hostInfo);
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
