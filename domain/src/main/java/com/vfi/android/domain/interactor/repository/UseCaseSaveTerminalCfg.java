package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveTerminalCfg extends UseCase<Boolean, TerminalCfg> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSaveTerminalCfg(ThreadExecutor threadExecutor,
                                  PostExecutionThread postExecutionThread,
                                  IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(TerminalCfg terminalCfg) {
        return Observable.create(emitter -> {
           iRepository.putTerminalCfg(terminalCfg);
           emitter.onNext(true);
           emitter.onComplete();
        });
    }
}
