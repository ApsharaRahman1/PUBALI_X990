package com.vfi.android.domain.interactor.other;


import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseTimer extends UseCase<Long, Long> {

    @Inject
    public UseCaseTimer(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
    }

    @Override
    public Observable<Long> buildUseCaseObservable(Long aLong) {
        return Observable.timer(aLong, TimeUnit.MILLISECONDS);
    }
}
