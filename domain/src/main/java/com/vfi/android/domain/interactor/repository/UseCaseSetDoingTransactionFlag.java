package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.memory.GlobalMemoryCache;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by huan.lu on 2018/9/18.
 */

public class UseCaseSetDoingTransactionFlag extends UseCase<Object,Boolean> {

    private final String TAG = "UseCaseSetDoingTransactionFlag";
    private GlobalMemoryCache globalMemoryCache;

    @Inject
    public UseCaseSetDoingTransactionFlag(ThreadExecutor threadExecutor,
                                          PostExecutionThread postExecutionThread,
                                          GlobalMemoryCache globalMemoryCache) {
        super(threadExecutor, postExecutionThread);
        this.globalMemoryCache = globalMemoryCache;
    }

    @Override
    public Observable<Object> buildUseCaseObservable(Boolean isDoingTransaction) {
        LogUtil.i(TAG,"Is Doing Trans : " + isDoingTransaction);
        globalMemoryCache.setDoingTransaction(isDoingTransaction);
        return Observable.just(true);
    }
}
