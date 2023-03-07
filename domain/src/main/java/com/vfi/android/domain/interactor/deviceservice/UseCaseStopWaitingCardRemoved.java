package com.vfi.android.domain.interactor.deviceservice;


import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Observable;


public class UseCaseStopWaitingCardRemoved extends UseCase<Boolean, Void> {
    private static final String TAG = TAGS.CHECK_CARD;
    private IPosService posService;

    @Inject
    public UseCaseStopWaitingCardRemoved(IPosService posService,
                                         ThreadExecutor threadExecutor,
                                         PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Void v) {
        LogUtil.d(TAG, "stop waiting remove card.");
        return posService.simulatorCardRemoved();
    }
}

