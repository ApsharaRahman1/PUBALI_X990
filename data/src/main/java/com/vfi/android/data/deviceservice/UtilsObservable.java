package com.vfi.android.data.deviceservice;

import android.graphics.Bitmap;

import io.reactivex.Observable;

/**
 * Created by yunlongg1 on 17/11/2017.
 */

public class UtilsObservable {
    private PosServiceImpl posService;

    private static class SingletonHolder {
        private static final UtilsObservable INSTANCE = new UtilsObservable();
    }

    private UtilsObservable(){
    }

    public static final UtilsObservable getInstance() {
        return UtilsObservable.SingletonHolder.INSTANCE;
    }

    public UtilsObservable build(PosServiceImpl posService){
        this.posService = posService;
        return this;
    }

    Observable<byte[]> getCompressedImageData(Bitmap bitmap){
        return Observable.create(eventEmitter -> {
            byte[] imageData = posService.getHandler().getUtils().getImage().bitmap2Jbig(bitmap);
            eventEmitter.onNext(imageData);
            eventEmitter.onComplete();
        });
    }
}
