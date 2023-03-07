package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.DeviceInformation;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetTerminalSN extends UseCase<String, Void> {
    private final String TAG = TAGS.Dialog;
    private final IPosService iPosService;
    private final IRepository iRepository;

    @Inject
    public UseCaseGetTerminalSN(IPosService iPosService,
                                IRepository iRepository,
                                ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.iPosService = iPosService;
        this.iRepository = iRepository;
    }

    @Override
    public Observable<String> buildUseCaseObservable(Void avoid) {
        return Observable.create(emitter -> {
            DeviceInformation deviceInformation = iRepository.getDeviceInformation();
            if (deviceInformation == null) {
                deviceInformation = iPosService.getDeviceInfo().blockingSingle();
                if (deviceInformation == null) {
                    deviceInformation = new DeviceInformation();
                }
            }

            String sn = deviceInformation.getSerialNo();
            if (sn == null) {
                sn = "Unknown SN";
            }

            LogUtil.d(TAG, "SN=" + sn);
            emitter.onNext(sn);
            emitter.onComplete();
        });
    }
}
