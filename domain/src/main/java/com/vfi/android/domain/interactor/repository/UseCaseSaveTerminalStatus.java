package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveTerminalStatus extends UseCase<Boolean, TerminalStatus> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSaveTerminalStatus(ThreadExecutor threadExecutor,
                                     PostExecutionThread postExecutionThread,
                                     IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(TerminalStatus terminalStatus) {
        return Observable.create(emitter -> {
            iRepository.putTerminalStatus(terminalStatus);
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
