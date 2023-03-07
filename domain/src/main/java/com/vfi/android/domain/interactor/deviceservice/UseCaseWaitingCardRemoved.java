package com.vfi.android.domain.interactor.deviceservice;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Observable;


public class UseCaseWaitingCardRemoved extends UseCase<Boolean, Boolean> {
    private static final String TAG = TAGS.CHECK_CARD;
    private IPosService posService;
    private boolean isFirstTimeFoundCardNotRemoved;

    @Inject
    public UseCaseWaitingCardRemoved(IPosService posService,
                                     ThreadExecutor threadExecutor,
                                     PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Boolean needDelay) {
        isFirstTimeFoundCardNotRemoved = false;
        return Observable.create(emitter -> {
            while (true) {
                boolean isCardRemoved = false;
                try {
                    isCardRemoved = posService.checkIsCardRemoved(100).blockingSingle();
                } catch (Exception e) {
                    isCardRemoved = false;
                }

                if (isCardRemoved) {
                    if (needDelay) {
                        try {
                            Thread.sleep(1500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    LogUtil.d(TAG, "Card removed.");
                    emitter.onNext(true);
                    emitter.onComplete();
                    break;
                } else {
                    emitter.onNext(false);
                }
            }
            LogUtil.d(TAG, "Exit waiting.");
        });
    }
}

