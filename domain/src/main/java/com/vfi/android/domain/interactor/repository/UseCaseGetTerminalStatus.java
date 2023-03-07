package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetTerminalStatus extends UseCase<TerminalStatus, Void> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetTerminalStatus(ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread,
                                    IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<TerminalStatus> buildUseCaseObservable(Void aVoid) {
        return Observable.create(emitter -> {
           emitter.onNext(iRepository.getTerminalStatus());
           emitter.onComplete();
        });
    }
}
