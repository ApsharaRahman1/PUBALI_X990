package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetHostInfo extends UseCase<HostInfo, String> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetHostInfo(ThreadExecutor threadExecutor,
                              PostExecutionThread postExecutionThread,
                              IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<HostInfo> buildUseCaseObservable(String hostIndex) {
        return Observable.create(emitter -> {
            HostInfo hostInfo = iRepository.getHostInfo(Integer.valueOf(hostIndex));

            if (hostInfo == null) {
                emitter.onError(new CommonException(ExceptionType.GET_HOST_INFO_FAILED));
            } else {
                emitter.onNext(hostInfo);
                emitter.onComplete();
            }
        });
    }
}
