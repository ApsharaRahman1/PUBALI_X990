package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetCurTranData extends UseCase<CurrentTranData, Void> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetCurTranData(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<CurrentTranData> buildUseCaseObservable(Void aVoid) {
        return Observable.create(emitter -> {
           emitter.onNext(iRepository.getCurrentTranData());
           emitter.onComplete();
        });
    }
}
