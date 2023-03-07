package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetTerminalCfg extends UseCase<TerminalCfg, Void> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetTerminalCfg(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<TerminalCfg> buildUseCaseObservable(Void aVoid) {
        return Observable.create(emitter -> {
           emitter.onNext(iRepository.getTerminalCfg());
           emitter.onComplete();
        });
    }
}
