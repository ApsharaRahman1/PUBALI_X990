package com.vfi.android.domain.interactor.deviceservice;

import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseStartBeep extends UseCase<Object, Integer> {
    IPosService iPosService;

    @Inject
    public UseCaseStartBeep(IPosService iPosService,
                            ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.iPosService = iPosService;
    }

    @Override
    public Observable<Object> buildUseCaseObservable(Integer times) {
        return iPosService.beep(times);
    }
}
