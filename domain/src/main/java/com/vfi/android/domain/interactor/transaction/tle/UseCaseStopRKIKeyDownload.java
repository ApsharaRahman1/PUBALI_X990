package com.vfi.android.domain.interactor.transaction.tle;

import com.vfi.android.domain.entities.tle.RKIDownloadStatus;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Return next step, please refer {@link RKIDownloadStatus}
 */
public class UseCaseStopRKIKeyDownload extends UseCase<Boolean, Void> {
    private RKIKeyDownloadManager rkiKeyDownloadManager;

    @Inject
    public UseCaseStopRKIKeyDownload(RKIKeyDownloadManager rkiKeyDownloadManager,
                                     ThreadExecutor threadExecutor,
                                     PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.rkiKeyDownloadManager = rkiKeyDownloadManager;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Void aVoid) {
        return Observable.create(emitter -> {
            rkiKeyDownloadManager.stopKeyDownload();
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
