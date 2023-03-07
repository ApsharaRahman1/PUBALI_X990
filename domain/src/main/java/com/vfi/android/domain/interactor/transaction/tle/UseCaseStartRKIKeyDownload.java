package com.vfi.android.domain.interactor.transaction.tle;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.tle.RKIDownloadStatus;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interactor.transaction.UseCaseStartTransCommunication;
import com.vfi.android.domain.interactor.transaction.tle.trans.DownloadRKIKeyTransaction;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Emitter;
import io.reactivex.Observable;

/**
 * Return next step, please refer {@link com.vfi.android.domain.entities.tle.RKIDownloadStatus}
 */
public class UseCaseStartRKIKeyDownload extends UseCase<Integer, Void> {
    private String TAG = TAGS.TLE;
    private final IPosService iPosService;
    private final IRepository iRepository;
    private final UseCaseStartTransCommunication useCaseStartTransCommunication;
    private final RKIKeyDownloadManager rkiKeyDownloadManager;

    @Inject
    public UseCaseStartRKIKeyDownload(RKIKeyDownloadManager rkiKeyDownloadManager,
                                      UseCaseStartTransCommunication useCaseStartTransCommunication,
                                      IPosService iPosService,
                                      IRepository iRepository,
                                      ThreadExecutor threadExecutor,
                                      PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.rkiKeyDownloadManager = rkiKeyDownloadManager;
        this.useCaseStartTransCommunication = useCaseStartTransCommunication;
        this.iPosService = iPosService;
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Integer> buildUseCaseObservable(Void aVoid) {
        return Observable.create(emitter -> {
            // do mutual authentication
            rkiKeyDownloadManager.doPrepareForKeyDownload(new RKIKeyDownloadManager.KeyDownloadListener() {
                @Override
                public void onStatusChanged(int status) {
                    emitter.onNext(status);
                    if (status == RKIDownloadStatus.DOWNLOAD_SUCCESS
                            || status == RKIDownloadStatus.DOWNLOAD_FAILED) {
                        emitter.onComplete();
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    emitter.onError(throwable);
                }
            });

            // download rki key
            downloadRKIKey(emitter);
        });
    }


    private void downloadRKIKey(Emitter emitter) {
        LogUtil.d(TAG, "start downloadRKIKey from host");
        DownloadRKIKeyTransaction transaction = new DownloadRKIKeyTransaction(iPosService, iRepository, rkiKeyDownloadManager);
        useCaseStartTransCommunication.asyncExecute(transaction).doOnNext(commStatus -> {

        }).doOnError(throwable -> {
            LogUtil.e(TAG, "Do download RKI key failed.");
            throwable.printStackTrace();
            emitter.onError(throwable);
        }).doOnComplete(() -> {
            LogUtil.d(TAG, "Do download RKI key success.");
            emitter.onNext(RKIDownloadStatus.DOWNLOAD_SUCCESS);
            emitter.onComplete();
        }).subscribe();
    }
}
