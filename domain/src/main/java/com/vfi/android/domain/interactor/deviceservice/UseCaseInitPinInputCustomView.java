package com.vfi.android.domain.interactor.deviceservice;

import android.content.Context;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.PinInformation;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.PinPadInitPinInputCustomViewParamIn;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interactor.repository.UseCaseIsAllowPinBypass;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static com.vfi.android.domain.entities.databeans.PinPadInitPinInputCustomViewParamIn.SM4_TYPE;
import static com.vfi.android.domain.entities.databeans.PinPadInitPinInputCustomViewParamIn.TDES_TYPE;


/**
 * Created by fusheng.z on 2017/11/30.
 */

public class UseCaseInitPinInputCustomView extends UseCase<Map<String, String>, PinPadInitPinInputCustomViewParamIn> {
    private static final String TAG = TAGS.INPUT_PIN;
    private IPosService posService;
    private IRepository transactionRepository;
    private CurrentTranData currentTranData;
    private PostExecutionThread androidThread;
    private UseCaseIsAllowPinBypass useCaseIsAllowPinBypass;
    private Context context;

    @Inject
    UseCaseInitPinInputCustomView(IPosService posService,
                                  ThreadExecutor threadExecutor,
                                  PostExecutionThread postExecutionThread,
                                  IRepository transactionRepository,
                                  UseCaseIsAllowPinBypass useCaseIsAllowPinBypass,
                                  Context context) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
        this.transactionRepository = transactionRepository;
        this.androidThread = postExecutionThread;
        this.context = context;
        this.useCaseIsAllowPinBypass = useCaseIsAllowPinBypass;
    }

    @Override
    public Observable<Map<String, String>> buildUseCaseObservable(PinPadInitPinInputCustomViewParamIn paramIn) {
        PinPadInitPinInputCustomViewParamIn.PinpadListener pinPadListener = paramIn.getPinpadListener();
        return Observable.zip(Observable.just(transactionRepository.getTerminalCfg()),
                Observable.just(transactionRepository.getCurrentTranData()),
                (terminalCfg, c) -> {
                    this.currentTranData = c;
                    PinPadInitPinInputCustomViewParamIn initPinInputParamIn = new PinPadInitPinInputCustomViewParamIn();

                    byte[] pinLimit;
                    boolean isAllowPinBypass = useCaseIsAllowPinBypass.execute(null);
                    LogUtil.d(TAG, "isAllowPinBypass=" + isAllowPinBypass);
                    if (isAllowPinBypass) {
                        pinLimit = new byte[] {0, 4, 5, 6, 7, 8, 9, 10, 11, 12};
                    } else {
                        pinLimit = new byte[] {4, 5, 6, 7, 8, 9, 10, 11, 12};
                    }
                    int pinKeyIdx = 0;
//                    pinKeyIdx = currentTranData.getCurrentSlotKeyInfo().getCPINKeyIndex();
                    //TODO
                    LogUtil.i(TAG, "Pin Key idx: " + pinKeyIdx);
                    initPinInputParamIn.setKeyId(pinKeyIdx);
                    initPinInputParamIn.setPinLimit(pinLimit);
                    LogUtil.d(TAG, "isEmvRequireOnlinePin=" + currentTranData.isEmvRequireOnlinePin());
                    if (currentTranData.isEmvRequireOnlinePin()) {
                        boolean isPinKeyExist = posService.isKeyExist(2, initPinInputParamIn.getKeyId());
                        LogUtil.d(TAG, "isPinKeyExist=" + isPinKeyExist);
                        if (!isPinKeyExist) {
                            throw new CommonException(ExceptionType.PIN_KEY_NOT_FOUND);
                        }
                    }
                    initPinInputParamIn.setOnlineState(currentTranData.isEmvRequireOnlinePin());
                    initPinInputParamIn.setTimeout(terminalCfg.getOperationTimeout());
                    String pan = currentTranData.getCardInfo().getPan();
                    if (pan == null && pan.length() == 0) {
                        LogUtil.d(TAG, "Calc pinblock wrong pan");
                        pan = "00000000000000";
                    }

                    if (currentTranData.getPinInfo().getPanFromMsg0050().length() == 12) {
                        pan = "000" + currentTranData.getPinInfo().getPanFromMsg0050() + "F";
                        initPinInputParamIn.setPan(pan);
                    } else {
                        initPinInputParamIn.setPan(pan);
                    }
                    LogUtil.i(TAG, "Pan for pinblock=[" + initPinInputParamIn.getPan() + "]");

//                    byte[] random = EncryptionUtil.generateIV(getTrackOrPinEncRandom());
//                    LogUtil.d(TAG, "random=" + StringUtil.byte2HexStr(random));
//                    initPinInputParamIn.setRandom(random);

                    if (terminalCfg.getPinAlgType() == TDES_TYPE) {
                        initPinInputParamIn.setKeyType(0x00);
                        initPinInputParamIn.setDesType(0x01);
                    } else if (terminalCfg.getPinAlgType() == SM4_TYPE) {
                        initPinInputParamIn.setKeyType(0x03);
                        initPinInputParamIn.setDesType(0x02);
                    } else {
                        initPinInputParamIn.setKeyType(0x00);
                        initPinInputParamIn.setDesType(0x01);
                    }

                    initPinInputParamIn.setPinKeyCoordinates(paramIn.getPinKeyCoordinates());

                    if (!terminalCfg.isRandomPinpadKeyboard()) {
                        byte[] keyboardNumberPosition = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
                        initPinInputParamIn.setKeyboardNumberPosition(keyboardNumberPosition);
                    }

                    initPinInputParamIn.setPinpadListener(new PinPadInitPinInputCustomViewParamIn.PinpadListener() {
                        @Override
                        public void onInput(int len, int key) {
                            if (pinPadListener != null) {
                                Observable.just(0)
                                        .subscribeOn(androidThread.getScheduler())
                                        .subscribe(unused -> pinPadListener.onInput(len, key));
                            }
                        }

                        @Override
                        public void onConfirm(byte[] pinBlock, boolean isNonePin) {
                            currentTranData.getPinInfo().setInputPinRequested(true);
                            if (isNonePin) {
                                currentTranData.getPinInfo().setPinBypassed(true);
                            }
                            if (pinBlock != null && pinBlock.length > 0) {
                                LogUtil.i(TAG, "pinBlock.length  " + pinBlock.length + "  setPinInMode true, pinBlock hex=" + StringUtil.byte2HexStr(pinBlock));
                                if (currentTranData.isEmvRequireOnlinePin()) {
                                    PinInformation pinInformation = currentTranData.getPinInfo();
                                    pinInformation.setEncryptedPinblock(pinBlock);
                                }
                            } else {
                                LogUtil.i(TAG, "setPinInMode false");
                            }

                            if (pinPadListener != null) {
                                Observable.just(0)
                                        .subscribeOn(androidThread.getScheduler())
                                        .subscribe(unused -> pinPadListener.onConfirm(pinBlock, isNonePin));
                            }

                            // fix bug - offline input pin can not show again, root cause pboc set inputpin flag changed by pin -> pboc UI flow.
                            if (pinBlock != null && pinBlock.length > 0) {
                                posService.importPin(1, pinBlock).subscribe();
                            } else {
                                posService.importPin(1, null).subscribe();
                            }
                        }

                        @Override
                        public void onCancel() {
                            posService.importPin(0, null).subscribe();
                            if (pinPadListener != null) {
                                Observable.just(0)
                                        .subscribeOn(androidThread.getScheduler())
                                        .subscribe(unused -> pinPadListener.onCancel());
                            }
                        }

                        @Override
                        public void onError(int errorCode, String errorMsg) {
                            LogUtil.i(TAG, "errorMsg: " + errorMsg);
                            if (CardEntryMode.MAG != currentTranData.getCardInfo().getCardEntryMode() &&
                                    errorCode == -2) {
                                posService.importPin(0, null).subscribe();
                            }

                            if (pinPadListener != null) {
                                Observable.just(0)
                                        .subscribeOn(androidThread.getScheduler())
                                        .subscribe(unused -> pinPadListener.onError(errorCode, errorMsg));
                            }
                        }
                    });

                    return initPinInputParamIn;
                })
                .flatMap((Function<PinPadInitPinInputCustomViewParamIn, Observable<Map<String, String>>>) posService::initPinInputCustomView)
                .doOnError(throwable -> {
//                    posService.importPin(0, null).subscribe();
                });
    }

    private String getTrackOrPinEncRandom() {
        String random = "000000";
//        String random;
//        String debugRandom = transactionRepository.getDebugRandom();
//        if (BuildConfig.DEBUG && debugRandom != null && debugRandom.length() > 0) {
//            random = debugRandom;
//        } else {
//            random = currentTranData.getTrackOrPinEncRandom();
//        }

        LogUtil.d(TAG, "random=[" + random + "]");
        return random;
    }
}
