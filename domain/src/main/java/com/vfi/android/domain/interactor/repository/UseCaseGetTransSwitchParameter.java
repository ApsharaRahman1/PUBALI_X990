package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.SwitchParameter;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetTransSwitchParameter extends UseCase<SwitchParameter, Integer> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetTransSwitchParameter(ThreadExecutor threadExecutor,
                                          PostExecutionThread postExecutionThread,
                                          IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<SwitchParameter> buildUseCaseObservable(Integer transType) {
        return Observable.create(emitter -> {
            emitter.onNext(iRepository.getSwitchParameter(transType));
            emitter.onComplete();
        });
    }
}
