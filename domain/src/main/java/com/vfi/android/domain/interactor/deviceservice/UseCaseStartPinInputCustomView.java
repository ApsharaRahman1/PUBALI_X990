/**
 * Created by yunlongg1 on 05/09/2017.
 */
package com.vfi.android.domain.interactor.deviceservice;

import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseStartPinInputCustomView extends UseCase<Object, Void> {

    private final IPosService posService;

    @Inject
    UseCaseStartPinInputCustomView(IPosService posService, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
    }

    @Override
    public Observable<Object> buildUseCaseObservable(Void unused) {
        return posService.startPinInputCustomView();
    }
}
