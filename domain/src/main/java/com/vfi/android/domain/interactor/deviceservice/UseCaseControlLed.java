package com.vfi.android.domain.interactor.deviceservice;


import com.vfi.android.domain.entities.databeans.LedParam;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseControlLed extends UseCase<Boolean, LedParam> {
    private final IPosService iPosService;

    @Inject
    public UseCaseControlLed(ThreadExecutor threadExecutor,
                             PostExecutionThread postExecutionThread,
                             IPosService iPosService) {
        super(threadExecutor, postExecutionThread);
        this.iPosService = iPosService;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(LedParam param) {
        return iPosService.controlLedStatus(param);
    }
}
