package com.vfi.android.domain.interactor.deviceservice;


import com.vfi.android.domain.entities.databeans.CAPKParams;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

import io.reactivex.Observable;

/*Created by Yaping on 29/11/2017.*/

public class UseCaseConfigCAPKs extends UseCase<Boolean, CAPKParams> {

    private IPosService posService;

    @Inject
    UseCaseConfigCAPKs(IPosService posService,
                       ThreadExecutor threadExecutor,
                       PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
    }


    @Override
    public Observable<Boolean> buildUseCaseObservable(CAPKParams capkIn) {
        return posService.updateRID(capkIn);
    }
}
