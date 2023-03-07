package com.vfi.android.domain.interactor.deviceservice;


import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by Yaping on 29/11/2017.
 */

public class UseCaseImportCardConfirmResult extends UseCase<Object, Boolean> {

    private IPosService posService;

    @Inject
    UseCaseImportCardConfirmResult(IPosService posService,
                                   ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
    }


    @Override
    public Observable<Object> buildUseCaseObservable(Boolean pass) {
        return posService.importCardConfirmResult(pass);
    }
}
