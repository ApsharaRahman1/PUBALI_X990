package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.TLEConfig;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveTleConfig extends UseCase<Boolean, TLEConfig> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSaveTleConfig(ThreadExecutor threadExecutor,
                                PostExecutionThread postExecutionThread,
                                IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(TLEConfig tleConfig) {
        return Observable.create(emitter -> {
            iRepository.putTleConfig(tleConfig);
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
