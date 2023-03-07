package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.SwitchParameter;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveTransactionSwitch extends UseCase<Boolean, SwitchParameter> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSaveTransactionSwitch(ThreadExecutor threadExecutor,
                                        PostExecutionThread postExecutionThread,
                                        IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(SwitchParameter switchParameter) {
        return Observable.create(emitter -> {
           iRepository.putSwitchParameter(switchParameter);
           emitter.onNext(true);
           emitter.onComplete();
        });
    }
}
