package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveMenuId extends UseCase<Boolean, Integer> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSaveMenuId(ThreadExecutor threadExecutor,
                             PostExecutionThread postExecutionThread,
                             IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Integer menuId) {
        return Observable.create(emitter -> {
            CurrentTranData currentTranData = iRepository.getCurrentTranData();
            currentTranData.setMenuId(menuId);
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
