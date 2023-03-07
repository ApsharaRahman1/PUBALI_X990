/**
 * Created by yunlongg1 on 05/09/2017.
 */
package com.vfi.android.domain.interactor.deviceservice;



import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.comm.IComm;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;

public class UseCaseBindDeviceService extends UseCase<Boolean, Void> {

    private final IPosService posService;
    private final IRepository iRepository;

    @Inject
    UseCaseBindDeviceService(IPosService posService,
                             IRepository iRepository,
                             ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Void unused) {
        return posService.bind()
                .flatMap(result -> posService.getDeviceInfo().map(deviceInformation -> {
            iRepository.putDeviceInfomation(deviceInformation);
            return true;
        }));
    }
}
