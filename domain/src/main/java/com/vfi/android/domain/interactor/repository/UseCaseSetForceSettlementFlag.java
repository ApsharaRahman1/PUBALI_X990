package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSetForceSettlementFlag extends UseCase<Boolean, Boolean> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSetForceSettlementFlag(ThreadExecutor threadExecutor,
                                         PostExecutionThread postExecutionThread,
                                         IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Boolean isForceSettlement) {
        return Observable.create(emitter -> {
            TerminalStatus terminalStatus = iRepository.getTerminalStatus();
            LogUtil.d(TAGS.FORCE_SETTLEMENT, "SetForceSettlementFlag=" + isForceSettlement);
            terminalStatus.setNeedForceSettlement(isForceSettlement);
            iRepository.putTerminalStatus(terminalStatus);
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
