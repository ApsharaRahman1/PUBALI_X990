package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;


public class UseCaseSaveApprovalCode extends UseCase<Object, String> {
    private IRepository iRepository;

    @Inject
    public UseCaseSaveApprovalCode(ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread,
                                   IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Object> buildUseCaseObservable(String approvalCode) {
        return Observable.create(emitter -> {
            CurrentTranData currentTranData = iRepository.getCurrentTranData();
            currentTranData.getRecordInfo().setAuthCode(approvalCode);
            iRepository.putCurrentTranData(currentTranData);
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
