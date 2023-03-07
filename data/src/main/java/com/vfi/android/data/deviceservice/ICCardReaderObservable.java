package com.vfi.android.data.deviceservice;


import com.vfi.android.domain.utils.LogUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by yichao.t on 2018/1/8.
 */

public class ICCardReaderObservable {
    public final static int CARDREADER_TYPE = 0;
    private static final String TAG = "ICCardReaderObservable";
    private PosServiceImpl posService;

    private static class SingletonHolder {
        private static final ICCardReaderObservable INSTANCE = new ICCardReaderObservable();
    }

    private ICCardReaderObservable() {
    }

    public static final ICCardReaderObservable getInstance() {
        return ICCardReaderObservable.SingletonHolder.INSTANCE;
    }

    public ICCardReaderObservable build(PosServiceImpl posService) {
        this.posService = posService;
        return this;
    }

    Observable<Boolean> icCardPowerOn() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                posService.getHandler().getInsertCardReader(CARDREADER_TYPE).powerUp();
                e.onNext(true);
                e.onComplete();
            }
        });
    }

    Observable<Boolean> icCardPowerOff() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                posService.getHandler().getInsertCardReader(CARDREADER_TYPE).powerDown();
                e.onNext(true);
                e.onComplete();
            }
        });
    }

    Observable<Boolean> isIcCardPresent() {
        return Observable.create(e -> {
            Boolean isCardExist = posService.getHandler().getInsertCardReader(CARDREADER_TYPE).isCardIn();
            LogUtil.i(TAG, "isCardIn isExist: " + isCardExist);
            e.onNext(isCardExist);
            e.onComplete();

        });
    }

    public Observable<byte[]> exchangeApdu(byte[] data) {
        return Observable.create(emitter -> {
            emitter.onNext(posService.getHandler().getInsertCardReader(CARDREADER_TYPE).exchangeApdu(data));
            emitter.onComplete();
        });
    }
}
