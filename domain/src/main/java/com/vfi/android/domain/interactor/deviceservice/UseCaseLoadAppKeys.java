/**
 * Created by yunlongg1 on 05/09/2017.
 */
package com.vfi.android.domain.interactor.deviceservice;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.AppKeys;
import com.vfi.android.domain.entities.databeans.PinPadGeneralParamIn;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseLoadAppKeys extends UseCase<Boolean, AppKeys> {
    private final String TAG = TAGS.KeyLoad;
    private final IPosService posService;

    @Inject
    UseCaseLoadAppKeys(IPosService posService,
                       ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
    }
//ishtiak
    @Override
    public Observable<Boolean> buildUseCaseObservable(AppKeys appKeys) {
        return Observable.create(emitter -> {
            if (appKeys.getTek() != null) {
                posService.loadTEK(appKeys.getTek()).subscribe();
            }

            if (appKeys.getTmkList() != null) {
                for (PinPadGeneralParamIn masterKeyParamIn : appKeys.getTmkList()) {
                    posService.loadEncryptMainKey(masterKeyParamIn).subscribe();
                }
            }

            if (appKeys.getDataKeyList() != null) {
                posService.loadWorkKey(appKeys.getDataKeyList()).subscribe();
            }

            if (appKeys.getMacKeyList() != null) {
                posService.loadWorkKey(appKeys.getMacKeyList()).subscribe();
            }

            if (appKeys.getPinKeyList() != null) {
                posService.loadWorkKey(appKeys.getPinKeyList()).subscribe();
            }

            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
