package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by huan.lu on 2018/10/26.
 */

public class UseCaseIsBatchEmptyByHostType extends UseCase<Boolean,Integer> {
    private final String TAG = TAGS.Setting;
    private final IRepository iRepository;

    @Inject
    public UseCaseIsBatchEmptyByHostType(ThreadExecutor threadExecutor,
                                         PostExecutionThread postExecutionThread,
                                         IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Integer hostType) {
        LogUtil.d(TAG, "IsBatchEmptyByHostIndex hostType=" + hostType);
        return Observable.create(emitter -> {
            emitter.onNext(iRepository.isEmptyBatch(hostType));
            emitter.onComplete();
        });
    }
}
