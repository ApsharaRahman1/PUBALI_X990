package com.vfi.android.domain.interactor.deviceservice;


import com.vfi.android.domain.entities.databeans.AIDParams;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

import io.reactivex.Observable;

/*Created by Yaping on 29/11/2017.*/

public class UseCaseConfigAIDs extends UseCase<Boolean, AIDParams> {

    private IPosService posService;

    @Inject
    UseCaseConfigAIDs(IPosService posService,
            ThreadExecutor threadExecutor,
            PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
    }


    @Override
    public Observable<Boolean> buildUseCaseObservable(AIDParams aidParamIn) {
        return posService.updateAID(aidParamIn);
    }
}
