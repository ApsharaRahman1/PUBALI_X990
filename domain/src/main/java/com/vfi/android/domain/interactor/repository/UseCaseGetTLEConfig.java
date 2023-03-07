package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.TLEConfig;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetTLEConfig extends UseCase<TLEConfig, Integer> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetTLEConfig(ThreadExecutor threadExecutor,
                               PostExecutionThread postExecutionThread,
                               IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<TLEConfig> buildUseCaseObservable(Integer tleIndex) {
        return Observable.create(emitter -> {
            emitter.onNext(iRepository.getTleConfig(tleIndex));
            emitter.onComplete();
        });
    }
}
