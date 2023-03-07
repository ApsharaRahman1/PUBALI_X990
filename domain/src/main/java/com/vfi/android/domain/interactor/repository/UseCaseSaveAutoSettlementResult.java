package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveAutoSettlementResult extends UseCase<Boolean, Boolean> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSaveAutoSettlementResult(ThreadExecutor threadExecutor,
                                           PostExecutionThread postExecutionThread,
                                           IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Boolean isAllSettlementSuccess) {
        return Observable.create(emitter -> {
            TerminalStatus terminalStatus = iRepository.getTerminalStatus();
            if (isAllSettlementSuccess) {
                terminalStatus.setNeedForceSettlement(false);
                terminalStatus.setAutoSettlementFailed(false);
            } else {
                terminalStatus.setNeedForceSettlement(true);
                terminalStatus.setAutoSettlementFailed(true);
            }
            iRepository.putTerminalStatus(terminalStatus);
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
