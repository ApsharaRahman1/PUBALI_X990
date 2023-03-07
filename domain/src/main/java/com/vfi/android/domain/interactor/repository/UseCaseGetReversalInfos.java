package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetReversalInfos extends UseCase<List<RecordInfo>, Void> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetReversalInfos(ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread,
                                   IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<List<RecordInfo>> buildUseCaseObservable(Void v) {
        return Observable.create(emitter -> {
            emitter.onNext(iRepository.getReversalRecords());
            emitter.onComplete();
        });
    }
}
