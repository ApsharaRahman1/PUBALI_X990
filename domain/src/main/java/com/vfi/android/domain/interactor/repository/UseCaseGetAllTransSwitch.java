package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.SwitchParameter;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetAllTransSwitch extends UseCase<List<SwitchParameter>, Void> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetAllTransSwitch(ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread,
                                    IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<List<SwitchParameter>> buildUseCaseObservable(Void aVoid) {
        return Observable.create(emitter -> {
            emitter.onNext(iRepository.getAllSwitchParameters());
            emitter.onComplete();
        });
    }
}
