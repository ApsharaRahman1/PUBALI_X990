package com.vfi.android.data.deviceservice;

import android.os.Bundle;
import android.os.RemoteException;

import com.vfi.android.domain.entities.businessbeans.CardInformation;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.entities.consts.CheckCardErrorConst;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.CheckCardParamIn;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.smartpos.deviceservice.aidl.CheckCardListener;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.Observable;

/**
 * Created by yunlongg1 on 20/10/2017.
 */

public class CheckCardObservable {
    private static final String TAG = TAGS.CHECK_CARD;
    private PosServiceImpl posService;
    private Lock lock = new ReentrantLock();
    private boolean isSimulatorCardRmoved;
    private long count = 0;

    private static class SingletonHolder {
        private static final CheckCardObservable INSTANCE = new CheckCardObservable();
    }

    private CheckCardObservable() {
    }

    public static final CheckCardObservable getInstance() {
        return CheckCardObservable.SingletonHolder.INSTANCE;
    }

    public CheckCardObservable build(PosServiceImpl posService) {
        this.posService = posService;
        return this;
    }

    Observable<Object> stop() {
        lock.lock();
        LogUtil.d(TAG, "check card stopped");
        return Observable.create(eventEmitter -> {
            try {
                posService.getHandler().getEMV().stopCheckCard();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            eventEmitter.onNext(0);
            eventEmitter.onComplete();
        });
    }

    Observable<CardInformation> start(CheckCardParamIn checkCardParamIn) {
        lock.lock();
        LogUtil.d(TAG, "check card start");
        final CardInformation cardInformation = new CardInformation();
        return Observable.create(eventEmitter -> {
            LogUtil.i(TAG, "current thread: " + Thread.currentThread());
            CheckCardListener listener = new CheckCardListener.Stub() {
                @Override
                public void onCardSwiped(Bundle track) throws RemoteException {
                    cardInformation.setTrack1(track.getString("TRACK1"));
                    cardInformation.setTrack2(track.getString("TRACK2"));
                    cardInformation.setTrack3(track.getString("TRACK3"));
                    cardInformation.setServiceCode(track.getString("SERVICE_CODE"));
                    cardInformation.setExpiredDate(track.getString("EXPIRED_DATE"));
                    cardInformation.setPan(track.getString("PAN"));
                    cardInformation.setCardEntryMode(CardEntryMode.MAG);

                    LogUtil.i(TAG, "SERVICE_CODE  " + track.getString("SERVICE_CODE"));
                    LogUtil.i(TAG, "TRACK1  " + track.getString("TRACK1"));
                    LogUtil.i(TAG, "TRACK2  " + track.getString("TRACK2"));
                    LogUtil.i(TAG, "TRACK3  " + track.getString("TRACK3"));
                    LogUtil.i(TAG, "PAN  " + track.getString("PAN"));
                    LogUtil.i(TAG, "EXPIRED_DATE  " + track.getString("EXPIRED_DATE"));

                    eventEmitter.onNext(cardInformation);
                    eventEmitter.onComplete();
                }

                @Override
                public void onCardPowerUp() throws RemoteException {
                    LogUtil.i(TAG, "onCardPowerUp: CardInformation.CardEntryMode.IC");
                    cardInformation.setCardEntryMode(CardEntryMode.IC);
                    eventEmitter.onNext(cardInformation);
                    eventEmitter.onComplete();
                }

                @Override
                public void onCardActivate() throws RemoteException {
                    LogUtil.i(TAG, "onCardPowerUp: CardInformation.CardEntryMode.RF");
                    cardInformation.setCardEntryMode(CardEntryMode.RF);
                    eventEmitter.onNext(cardInformation);
                    eventEmitter.onComplete();
                }

                @Override
                public void onTimeout() throws RemoteException {
                    LogUtil.i(TAG, "onTimeout");
                    if (!eventEmitter.isDisposed()) {
                        eventEmitter.onError(new CommonException(ExceptionType.CHECK_CARD_TIMEOUT));
                    }
                }

                @Override
                public void onError(int error, String message) throws RemoteException {
                    LogUtil.i(TAG, error + " : " + message);
                    int exceptionType = ExceptionType.CHECK_CARD_FAILED;
                    int subErrorType = CheckCardErrorConst.UNKOWN_ERROR;
                    switch (error) {
                        case 1:
                            subErrorType = CheckCardErrorConst.SWIPE_CARD_FAILED;
                            break;
                        case 2:
                            subErrorType = CheckCardErrorConst.INSERT_CARD_FAILED;
                            break;
                        case 3:  //error code 3 is "IC card power up failed" just display "Read card failed."
                            subErrorType = CheckCardErrorConst.READ_CHIP_FAILED;
                            break;
                        case 4:
                            subErrorType = CheckCardErrorConst.TAP_CARD_FAILED;
                            break;
                        case 5:
                            subErrorType = CheckCardErrorConst.ACTIVATE_CONTACTLESS_CARD_FAILED;
                            break;
                        case 6:
                            subErrorType = CheckCardErrorConst.CARD_CONFLICT;
                            break;
                    }
                    LogUtil.d(TAG, "exceptionType=" + exceptionType);
                    eventEmitter.onError(new CommonException(exceptionType, subErrorType));
                }
            };
            Bundle bundle = new Bundle();
            bundle.putBoolean("supportMagCard", checkCardParamIn.isSupportMagCard());
            bundle.putBoolean("supportICCard", checkCardParamIn.isSupportICCard());
            bundle.putBoolean("supportRFCard", checkCardParamIn.isSupportRFCard());

            LogUtil.i(TAG, "supportRFCard  " + checkCardParamIn.isSupportRFCard());
            LogUtil.i(TAG, "supportICCard  " + checkCardParamIn.isSupportICCard());
            LogUtil.i(TAG, "supportMagCard  " + checkCardParamIn.isSupportMagCard());
            LogUtil.i(TAG, "timeout  " + checkCardParamIn.getTimeout());
            try {
                posService.getHandler().getEMV().checkCard(bundle, checkCardParamIn.getTimeout(), listener);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });
    }

    Observable<Boolean> checkIsCardRemoved(long timeout) {
        lock.lock();
        LogUtil.d(TAG, "check Is card Removed");
        return Observable.create(eventEmitter -> {
            CheckCardListener checkCardRemovedListener = new CheckCardListener.Stub() {
                @Override
                public void onCardSwiped(Bundle track) throws RemoteException {
                    if (count++ % 10 == 0)
                        LogUtil.d(TAG, "onCardSwiped");
                    posService.getHandler().getEMV().stopCheckCard();
                    if (isSimulatorCardRmoved) {
                        isSimulatorCardRmoved = false;
                        eventEmitter.onNext(true);
                    } else {
                        eventEmitter.onNext(false);
                    }
                    eventEmitter.onComplete();
                }

                @Override
                public void onCardPowerUp() throws RemoteException {
                    if (count++ % 10 == 0)
                        LogUtil.i(TAG, "onCardPowerUp: CardInformation.CardType.IC");
                    posService.getHandler().getEMV().stopCheckCard();
                    if (isSimulatorCardRmoved) {
                        isSimulatorCardRmoved = false;
                        eventEmitter.onNext(true);
                    } else {
                        eventEmitter.onNext(false);
                    }
                    eventEmitter.onComplete();
                }

                @Override
                public void onCardActivate() throws RemoteException {
                    if (count++ % 10 == 0)
                        LogUtil.i(TAG, "onCardPowerUp: CardInformation.CardType.RF");
                    posService.getHandler().getEMV().stopCheckCard();
                    if (isSimulatorCardRmoved) {
                        isSimulatorCardRmoved = false;
                        eventEmitter.onNext(true);
                    } else {
                        eventEmitter.onNext(false);
                    }
                    eventEmitter.onComplete();
                }

                @Override
                public void onTimeout() throws RemoteException {
                    LogUtil.i(TAG, "onTimeout");
                    posService.getHandler().getEMV().stopCheckCard();
                    if (isSimulatorCardRmoved) {
                        isSimulatorCardRmoved = false;
                        eventEmitter.onNext(true);
                    } else {
                        eventEmitter.onNext(true);
                    }
                    eventEmitter.onComplete();
                }

                @Override
                public void onError(int error, String message) throws RemoteException {
                    LogUtil.i(TAG, error + " : " + message);
                    posService.getHandler().getEMV().stopCheckCard();
                    if (isSimulatorCardRmoved) {
                        isSimulatorCardRmoved = false;
                        eventEmitter.onNext(true);
                    } else {
                        eventEmitter.onNext(false);
                    }
                    eventEmitter.onComplete();
                }
            };
            Bundle bundle = new Bundle();
            bundle.putBoolean("supportMagCard", true);
            bundle.putBoolean("supportICCard", true);
            bundle.putBoolean("supportRFCard", true);

            LogUtil.i(TAG, "timeout  " + timeout + " ms");
            try {
                posService.getHandler().getEMV().checkCardMs(bundle, timeout, checkCardRemovedListener);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });
    }

    Observable<Boolean> simulatorCheckCardRemoved() {
        lock.lock();
        return Observable.create(emitter -> {
            try {
                isSimulatorCardRmoved = true;
            } finally {
                lock.unlock();
            }
            emitter.onNext(true);
            emitter.onComplete();
        });
    }

    Observable<Boolean> closeRfField() {
        return Observable.create(emitter -> {
            LogUtil.i(TAG, "close rf field");
            posService.getHandler().getRFCardReader().CloseRfField();
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}

