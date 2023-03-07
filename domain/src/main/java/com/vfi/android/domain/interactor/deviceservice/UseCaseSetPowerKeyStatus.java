package com.vfi.android.domain.interactor.deviceservice;

import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSetPowerKeyStatus extends UseCase<Boolean, Boolean> {

    private IPosService posService;

    @Inject
    public UseCaseSetPowerKeyStatus(IPosService posService,
                                    ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Boolean isPowerKeyEnable) {
        return Observable.create(emitter -> {
            posService.setPowerKeyStatus(isPowerKeyEnable);
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
