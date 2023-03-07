package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetMerchantInfo extends UseCase<MerchantInfo, String> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetMerchantInfo(ThreadExecutor threadExecutor,
                                  PostExecutionThread postExecutionThread,
                                  IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<MerchantInfo> buildUseCaseObservable(String merchantIndex) {
        return Observable.create(emitter -> {

            MerchantInfo merchantInfo = iRepository.getMerchantInfo(Integer.valueOf(merchantIndex));
            if (merchantInfo == null) {
                emitter.onError(new CommonException(ExceptionType.GET_MERCHANT_INFO_FAILED));
            } else {
                emitter.onNext(merchantInfo);
                emitter.onComplete();
            }

        });
    }
}
