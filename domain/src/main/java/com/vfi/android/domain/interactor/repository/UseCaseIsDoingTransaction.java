package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.memory.GlobalMemoryCache;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by huan.lu on 2018/10/26.
 */

public class UseCaseIsDoingTransaction extends UseCase<Boolean,Void> {

    GlobalMemoryCache globalMemoryCache;

    @Inject
    public UseCaseIsDoingTransaction(ThreadExecutor threadExecutor,
                                     PostExecutionThread postExecutionThread,
                                     GlobalMemoryCache globalMemoryCache) {
        super(threadExecutor, postExecutionThread);
        this.globalMemoryCache = globalMemoryCache;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Void aVoid) {
        return Observable.just(globalMemoryCache.isDoingTransaction());
    }
}
