package com.vfi.android.domain.interactor.transaction.tle;

import com.vfi.android.domain.entities.databeans.InputTLEPinResult;
import com.vfi.android.domain.entities.tle.RKIDownloadStatus;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Return next step, please refer {@link RKIDownloadStatus}
 */
public class UseCaseInputCardPin extends UseCase<InputTLEPinResult, String> {
    private RKIKeyDownloadManager rkiKeyDownloadManager;

    @Inject
    public UseCaseInputCardPin(RKIKeyDownloadManager rkiKeyDownloadManager,
                               ThreadExecutor threadExecutor,
                               PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.rkiKeyDownloadManager = rkiKeyDownloadManager;
    }

    @Override
    public Observable<InputTLEPinResult> buildUseCaseObservable(String pin) {
        return Observable.create(emitter -> {
            emitter.onNext(rkiKeyDownloadManager.inputPin(pin));
            emitter.onComplete();
        });
    }
}
