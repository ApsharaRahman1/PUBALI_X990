package com.vfi.android.domain.interactor.deviceservice;



import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by fusheng.z on 2017/11/30.
 */

public class UseCaseIsNeedRemoveCard extends UseCase<Boolean, Void> {
    private static final String TAG = "UseCaseIsNeedRemoveCard";
    private IPosService posService;

    @Inject
    public UseCaseIsNeedRemoveCard(IPosService posService,
                                   ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Void v) {
        return posService.isIcCardPresent();
    }
}

