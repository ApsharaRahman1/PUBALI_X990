package com.vfi.android.data.deviceservice;


import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.LedParam;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.Observable;

/**
 * Created by yunlongg1 on 17/11/2017.
 */

public class LedObservable {
    private final String TAG = TAGS.Led;
    private PosServiceImpl posService;
    private Lock lock = new ReentrantLock();

    private final int LED_BLUE = 1;
    private final int LED_YELLOW = 2;
    private final int LED_GREEN = 3;
    private final int LED_RED = 4;

    private static class SingletonHolder {
        private static final LedObservable INSTANCE = new LedObservable();
    }

    private LedObservable(){
    }

    public static final LedObservable getInstance() {
        return LedObservable.SingletonHolder.INSTANCE;
    }

    public LedObservable build(PosServiceImpl posService){
        this.posService = posService;
        return this;
    }

    Observable<Boolean> controlLedLight(LedParam param){
        return Observable.create(eventEmitter -> {
//            LogUtil.d(TAG, "isBlueLightOn=" + param.isBlueLightOn());
//            LogUtil.d(TAG, "isYellowLightOn=" + param.isYellowLightOn());
//            LogUtil.d(TAG, "isGreenLightOn=" + param.isGreenLightOn());
//            LogUtil.d(TAG, "isRedLightOn=" + param.isRedLightOn());
            lock.lock();

            try {
                if (param.isBlueLightOn()) {
                    posService.getHandler().getLed().turnOn(LED_BLUE);
                } else {
                    posService.getHandler().getLed().turnOff(LED_BLUE);
                }

                if (param.isYellowLightOn()) {
                    posService.getHandler().getLed().turnOn(LED_YELLOW);
                } else {
                    posService.getHandler().getLed().turnOff(LED_YELLOW);
                }

                if (param.isGreenLightOn()) {
                    posService.getHandler().getLed().turnOn(LED_GREEN);
                } else {
                    posService.getHandler().getLed().turnOff(LED_GREEN);
                }

                if (param.isRedLightOn()) {
                    posService.getHandler().getLed().turnOn(LED_RED);
                } else {
                    posService.getHandler().getLed().turnOff(LED_RED);
                }
            } finally {
                lock.unlock();
            }

            eventEmitter.onNext(true);
            eventEmitter.onComplete();
        });
    }
}
