package com.vfi.android.domain.interactor.deviceservice;

import com.vfi.android.domain.entities.consts.CTLSLedStatus;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.LedParam;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.utils.LogUtil;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSetCTLSLedStatus extends UseCase<Boolean, Integer> {
    private final String TAG = TAGS.CHECK_CARD;
    private final UseCaseControlLed useCaseControlLed;
    private Lock lock = new ReentrantLock();

    @Inject
    public UseCaseSetCTLSLedStatus(ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread,
                                   UseCaseControlLed useCaseControlLed) {
        super(threadExecutor, postExecutionThread);
        this.useCaseControlLed = useCaseControlLed;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Integer ctlsLedStatus) {
        return Observable.create(emitter -> {
            lock.lock();
            try {

                LedParam ledParam;
                LogUtil.d(TAG, "ctlsLedStatus=" + ctlsLedStatus);
                switch (ctlsLedStatus) {
                    case CTLSLedStatus.START_CHECK_CARD:
                        ledParam = new LedParam(true, false, false, false);
                        break;
                    case CTLSLedStatus.DETECT_CTLS_CARD:
                        ledParam = new LedParam(true, true, false, false);
                        break;
                    case CTLSLedStatus.START_CTLS_EMV_FLOW:
                        ledParam = new LedParam(true, true, true, false);
                        break;
                    case CTLSLedStatus.CTLS_EMV_FINISHED:
                        ledParam = new LedParam(true, true, true, true);
                        break;
                    default:
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {

                        }
                        ledParam = new LedParam(false, false, false, false);
                        break;
                }

                useCaseControlLed.execute(ledParam);
            } finally {
                lock.unlock();
            }

            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
