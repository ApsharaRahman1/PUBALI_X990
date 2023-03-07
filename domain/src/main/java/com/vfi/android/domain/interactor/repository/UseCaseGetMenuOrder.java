package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.databeans.MenuOrder;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.List;

import io.reactivex.Observable;

public class UseCaseGetMenuOrder extends UseCase<List<MenuOrder>, Void> {
    private final IRepository iRepository;

    public UseCaseGetMenuOrder(ThreadExecutor threadExecutor,
                               PostExecutionThread postExecutionThread,
                               IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<List<MenuOrder>> buildUseCaseObservable(Void aVoid) {
        return Observable.create(emitter -> {
           emitter.onNext(iRepository.getMenuOrder());
           emitter.onComplete();
        });
    }
}
