package com.vfi.android.domain.interactor.deviceservice;



import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by fusheng.z.t on 2018/4/2.
 */

public class UseCaseImportPin extends UseCase<Object, byte[]> {

    private IPosService posService;

    @Inject
    UseCaseImportPin(IPosService posService,
                     ThreadExecutor threadExecutor,
                     PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
    }


    @Override
    public Observable<Object> buildUseCaseObservable(byte[] bytes) {
        return posService.importPin(1, bytes);
    }
}
