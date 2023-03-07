package com.vfi.android.domain.interactor.deviceservice;



import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by fusheng.z on 2018/02/02.
 */

public class UseCaseImportAppSelected extends UseCase<Object, Integer> {

    private IPosService posService;

    @Inject
    UseCaseImportAppSelected(IPosService posService,
                             ThreadExecutor threadExecutor,
                             PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
    }


    @Override
    public Observable<Object> buildUseCaseObservable(Integer index) {
        return posService.importAppSelected(index);
    }
}
