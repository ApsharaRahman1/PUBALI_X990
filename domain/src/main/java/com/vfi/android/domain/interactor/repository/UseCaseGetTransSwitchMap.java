package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.SwitchParameter;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetTransSwitchMap extends UseCase<Map<Integer, SwitchParameter>, List<Integer>> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetTransSwitchMap(ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread,
                                    IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Map<Integer, SwitchParameter>> buildUseCaseObservable(List<Integer> transTypeList) {
        return Observable.create(emitter -> {
            Map<Integer, SwitchParameter> map = new HashMap<>();

            for (int transType : transTypeList) {
                map.put(transType, iRepository.getSwitchParameter(transType));
            }

            emitter.onNext(map);
            emitter.onComplete();
        });
    }
}
