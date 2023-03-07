package com.vfi.android.domain.interactor.deviceservice;


import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by yunlongg1 on 12/10/2017.*/

public class UseCaseEmvStop extends UseCase {
    private IPosService posService;

    @Inject
    UseCaseEmvStop(ThreadExecutor threadExecutor,
                   PostExecutionThread postExecutionThread,
                   IPosService posService) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
    }

    @Override
    public Observable buildUseCaseObservable(Object o) {
         return posService.stopEmvFlow();
    }
}
