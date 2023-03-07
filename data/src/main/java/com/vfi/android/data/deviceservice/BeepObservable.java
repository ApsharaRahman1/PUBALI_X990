package com.vfi.android.data.deviceservice;

import io.reactivex.Observable;

/**
 * Created by yunlongg1 on 17/11/2017.
 */

public class BeepObservable {
    private PosServiceImpl posService;

    private static class SingletonHolder {
        private static final BeepObservable INSTANCE = new BeepObservable();
    }

    private BeepObservable(){
    }

    public static final BeepObservable getInstance() {
        return BeepObservable.SingletonHolder.INSTANCE;
    }

    public BeepObservable build(PosServiceImpl posService){
        this.posService = posService;
        return this;
    }

    Observable<Object> beep(int times){
        int finalTimes = (times <= 0 || times > 3) ? 1 : times;
        return Observable.create(eventEmitter -> {
            for(int i = finalTimes; i > 0; i--){
                int beepTime = 200;
                if (times > 1) {
                    beepTime = 100;
                }
                posService.getHandler().getBeeper().startBeep(beepTime);
                if((i-1) > 0 ){
                    Thread.sleep(150);
                }
            }
            eventEmitter.onNext(0);
            eventEmitter.onComplete();
        });
    }
}
