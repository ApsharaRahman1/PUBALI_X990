package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;


import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetClearAllBatches extends UseCase<Boolean, Void> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetClearAllBatches(ThreadExecutor threadExecutor,
                                     PostExecutionThread postExecutionThread,
                                     IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Void aVoid) {
        return Observable.create(emitter -> {
            iRepository.clearAllBatches();
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
