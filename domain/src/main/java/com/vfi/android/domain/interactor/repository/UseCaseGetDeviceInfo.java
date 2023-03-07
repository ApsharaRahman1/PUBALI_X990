package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.DeviceInformation;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetDeviceInfo extends UseCase<DeviceInformation, Void> {
    private final IRepository iRepository;
    private final IPosService iPosService;

    @Inject
    public UseCaseGetDeviceInfo(ThreadExecutor threadExecutor,
                                PostExecutionThread postExecutionThread,
                                IPosService iPosService,
                                IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
        this.iPosService = iPosService;
    }

    @Override
    public Observable<DeviceInformation> buildUseCaseObservable(Void aVoid) {
        return Observable.create(emitter -> {
            DeviceInformation deviceInformation = iRepository.getDeviceInformation();
            if (deviceInformation == null) {
                deviceInformation = iPosService.getDeviceInfo().blockingSingle();
                if (deviceInformation == null) {
                    emitter.onError(new CommonException(ExceptionType.GET_DEVICE_INFO_FAILED));
                } else {
                    iRepository.putDeviceInfomation(deviceInformation);
                }
            }

            emitter.onNext(deviceInformation);
            emitter.onComplete();
        });
    }
}
