package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.databeans.SettlementRecord;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;


/**
 * Created by fusheng.z on 2018/04/08.
 */

public class UseCaseGetSettlementRecords extends UseCase<SettlementRecord, Void> {

    private final IRepository iRepository;

    @Inject
    UseCaseGetSettlementRecords(IRepository iRepository,
                                ThreadExecutor threadExecutor,
                                PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }
    /**
     *
     * @return
     */
    @Override
    public Observable<SettlementRecord> buildUseCaseObservable(Void v) {
        return Observable.create(emitter -> {
            emitter.onNext(iRepository.getSettlementInformation());
            emitter.onComplete();
        });
    }
}
