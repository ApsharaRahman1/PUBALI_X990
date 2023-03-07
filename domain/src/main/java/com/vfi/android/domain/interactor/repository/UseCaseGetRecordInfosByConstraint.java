package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.databeans.HistoryConstraint;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetRecordInfosByConstraint extends UseCase<List<RecordInfo>, HistoryConstraint> {
    private IRepository iRepository;

    @Inject
    public UseCaseGetRecordInfosByConstraint(IRepository iRepository,
                                             ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<List<RecordInfo>> buildUseCaseObservable(HistoryConstraint historyConstraint) {
        return Observable.create(emitter -> {
            emitter.onNext(iRepository.getRecordInfos(historyConstraint));
            emitter.onComplete();
        });
    }
}
