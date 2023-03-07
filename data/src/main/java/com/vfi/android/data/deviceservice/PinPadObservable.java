package com.vfi.android.data.deviceservice;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.PinKeyCoordinate;
import com.vfi.android.domain.entities.databeans.PinPadCalcMacParamIn;
import com.vfi.android.domain.entities.databeans.PinPadEncryptDecryptParamIn;
import com.vfi.android.domain.entities.databeans.PinPadEncryptTrackParamIn;
import com.vfi.android.domain.entities.databeans.PinPadGeneralParamIn;
import com.vfi.android.domain.entities.databeans.PinPadInitPinInputCustomViewParamIn;
import com.vfi.android.domain.entities.databeans.PinPadStartPinInputParamIn;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.smartpos.deviceservice.aidl.PinInputListener;
import com.vfi.smartpos.deviceservice.aidl.PinKeyCoorInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by fusheng.z on 27/11/2017.
 */

public class PinPadObservable {
    private static final String TAG = TAGS.PINPAD;
    public PosServiceImpl posService;
    private static long lastClickTime;

    private static class SingletonHolder {
        private static final PinPadObservable INSTANCE = new PinPadObservable();
    }

    public PinPadObservable() {
    }

    public static final PinPadObservable getInstance() {
        return PinPadObservable.SingletonHolder.INSTANCE;
    }

    public PinPadObservable build(PosServiceImpl posService) {
        this.posService = posService;
        return this;
    }

    /**
     * avoid double click confirm button
     */
    public static boolean Control() {
        long curClickTime = System.currentTimeMillis();
        LogUtil.d(TAG, "current=" + curClickTime);
        LogUtil.d(TAG, "last=" + lastClickTime);
        if (Math.abs(curClickTime - lastClickTime) >= 1000) {
            lastClickTime = curClickTime;
            return true;
        } else {
            return false;
        }
    }

    public boolean ishtiakLoad(byte[] sessionKey, byte[] checkSumValue) {
        try {

            boolean ret = posService.getHandler().getPinpad(0).loadWorkKey(2, 1, 1, sessionKey, checkSumValue);
            if(ret){
                Log.d("keyIshtiak","successfully");
            }else{
                Log.d("keyIshtiak","void");
            }
            return ret;
        } catch (Exception e) {
            Log.d("keyIshtiak",e.toString());
            return false;
        }


    }

    Observable<Boolean> isKeyExist(final PinPadGeneralParamIn param) {
        return Observable.create(e -> {
            boolean ret = posService.getHandler().getPinpad(0).isKeyExist(param.getKeyType(), param.getKeyIdx());
            LogUtil.i(TAG, "isKeyExist ret: " + ret);
            e.onNext(ret);
            e.onComplete();
        });
    }

    Observable<Object> loadTEK(final PinPadGeneralParamIn param) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                LogUtil.i(TAG, "---------------------------------------------------------");
                LogUtil.i(TAG, "loadTEK  KeyIdx: " + param.getKeyIdx());
                LogUtil.i(TAG, "loadTEK  Key: " + StringUtil.bcd2Asc(param.getKey()));
                LogUtil.i(TAG, "loadTEK  KCV: " + StringUtil.bcd2Asc(param.getCheckValue()));

                boolean ret = posService.getHandler().getPinpad(0).loadMainKey(param.getKeyIdx(), param.getKey(), param.getCheckValue());
                //ishtiak
                //  boolean ret = posService.getHandler().getPinpad(0).loadTEK(param.getKeyIdx(), param.getKey(), param.getCheckValue());
                if (ret) {
                    LogUtil.i(TAG, "loadTEK success");
                    e.onNext(ret);
                    e.onComplete();
                } else {
                    LogUtil.i(TAG, "loadTEK failed");
                    String strError = posService.getHandler().getPinpad(0).getLastError();
                    e.onError(new RemoteException(strError));
                }
            }
        });
    }

    Observable<Boolean> loadEncryptMainKey(final PinPadGeneralParamIn param) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                Bundle extend = new Bundle();
                extend.putBoolean("isCBCType", false);
                extend.putBoolean("isMasterEncMasterMode", param.isMasterEncMasterMode());
                extend.putInt("decryptKeyIndex", param.getEncryptMasterKeyIdex());

                LogUtil.i(TAG, "---------------------------------------------------------");
                LogUtil.i(TAG, "loadEncryptedMainKey  KeyIdx: " + param.getKeyIdx());
                LogUtil.i(TAG, "loadEncryptedMainKey  Key: " + StringUtil.bcd2Asc(param.getKey()));
                LogUtil.i(TAG, "loadEncryptedMainKey  KCV: " + StringUtil.bcd2Asc(param.getCheckValue()));
                LogUtil.i(TAG, "loadEncryptedMainKey  isMasterEncMaster: " + param.isMasterEncMasterMode());
                LogUtil.i(TAG, "loadEncryptedMainKey  isMasterEncMasterKeyIdx: " + param.getEncryptMasterKeyIdex());
                boolean ret = posService.getHandler().getPinpad(0).loadEncryptMainKeyEX(param.getKeyIdx(), param.getKey(), param.getAlgorithmType(), param.getCheckValue(), extend);
                if (ret) {
                    LogUtil.i(TAG, "loadEncryptedMainKey success");
                    LogUtil.i(TAG, "---------------------------------------------------------");
                    e.onNext(ret);
                    e.onComplete();
                } else {
                    LogUtil.i(TAG, "loadEncryptedMainKey failed");
                    LogUtil.i(TAG, "---------------------------------------------------------");
                    String strError = posService.getHandler().getPinpad(0).getLastError();
                    e.onError(new RemoteException(strError));
                }
            }
        });
    }


    Observable<Object> loadMainKey(final PinPadGeneralParamIn param) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                LogUtil.d("load pin key，Idx=[" + param.getKeyIdx() + "]" + " Key=[" + StringUtil.byte2HexStr(param.getKey()) + "]");
                boolean ret = posService.getHandler().getPinpad(0).loadMainKey(param.getKeyIdx(), param.getKey(), param.getCheckValue());
                if (ret) {
                    LogUtil.d("load pin key success");
                    e.onNext(ret);
                    e.onComplete();
                } else {
                    LogUtil.d("load pin key failed");
                    String strError = posService.getHandler().getPinpad(0).getLastError();
                    e.onError(new RemoteException(strError));
                }
            }
        });
    }

    public Observable<Boolean> loadWorkKey(final List<PinPadGeneralParamIn> paramIns) {
        return Observable.create(e -> {
            for (PinPadGeneralParamIn param : paramIns) {
                LogUtil.i(TAG, "---------------------------------------------------------");
                switch (param.getKeyType()) {
                    case PinPadGeneralParamIn.MASTER_KEY:
                        LogUtil.i(TAG, "loadWorkKey  Type: Master key");
                        break;
                    case PinPadGeneralParamIn.MAC_KEY:
                        LogUtil.i(TAG, "loadWorkKey  Type: MAC key");
                        break;
                    case PinPadGeneralParamIn.PIN_KEY:
                        LogUtil.i(TAG, "loadWorkKey  Type: PIN key");
                        break;
                    case PinPadGeneralParamIn.TD_KEY:
                        LogUtil.i(TAG, "loadWorkKey  Type: TRACK key");
                        break;
                    default:
                        LogUtil.i(TAG, "loadWorkKey  Type: Unknow key type");
                        return;
                }
                LogUtil.i(TAG, "loadWorkKey  MkIdx: " + param.getMkIdx());
                LogUtil.i(TAG, "loadWorkKey  KeyIdx: " + param.getKeyIdx());
                LogUtil.i(TAG, "loadWorkKey  Key: " + StringUtil.bcd2Asc(param.getKey()));
                LogUtil.i(TAG, "loadWorkKey  KCV: " + StringUtil.bcd2Asc(param.getCheckValue()));
                boolean ret = posService.getHandler().getPinpad(0).loadWorkKey(param.getKeyType(), param.getMkIdx(), param.getKeyIdx(), param.getKey(), param.getCheckValue());
                //ishtiak

                //   boolean ret = posService.getHandler().getPinpad(0).savePlainKey(param.getKeyType(), param.getKeyIdx(), param.getKey());

                if (!ret) {
                    String strError = posService.getHandler().getPinpad(0).getLastError();
                    LogUtil.i(TAG, "onError:" + strError);
                    if (param.getKeyType() == PinPadGeneralParamIn.MAC_KEY) {
                        e.onError(new CommonException(ExceptionType.LOAD_MAC_KEY_FAILED));
                    } else if (param.getKeyType() == PinPadGeneralParamIn.PIN_KEY) {
                        e.onError(new CommonException(ExceptionType.LOAD_PIN_KEY_FAILED));
                    } else if (param.getKeyType() == PinPadGeneralParamIn.TD_KEY) {
                        e.onError(new CommonException(ExceptionType.LOAD_TRACK_KEY_FAILED));
                    } else {
                        e.onError(new CommonException(ExceptionType.LOAD_WORK_KEY_FAILED));
                    }
                    return;
                } else {
                    LogUtil.i(TAG, "loadWorkKey  load success");
                }
            }
            LogUtil.i(TAG, "---------------------------------------------------------");
            e.onNext(true);
            e.onComplete();
        });
    }

    Observable<byte[]> calcMAC(final PinPadCalcMacParamIn param) {
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> e) throws Exception {
                byte[] Mac = posService.getHandler().getPinpad(0).calcMAC(param.getKeyId(), param.getData());
                if (Mac != null && Mac.length != 0) {
                    e.onNext(Mac);
                    e.onComplete();
                } else {
                    String strError = posService.getHandler().getPinpad(0).getLastError();
                    e.onError(new RemoteException(strError));
                }
            }
        });
    }

    Observable<byte[]> calcMACWithCalType(final PinPadCalcMacParamIn param) {
        return Observable.create(e -> {
            LogUtil.i(TAG, "getKeyId : " + param.getKeyId());
            LogUtil.i(TAG, "getCalcType : " + param.getCalcType());
            LogUtil.i(TAG, "getDesType : " + param.getDesType());

            byte[] Mac = posService.getHandler().getPinpad(0).calcMACWithCalType(param.getKeyId(), param.getCalcType(), param.getCBCInitVec(), param.getData(), param.getDesType(), false);
            if (Mac != null && Mac.length != 0) {
                LogUtil.i(TAG, "calcMACWithCalType " + StringUtil.bcd2Asc(Mac));
                e.onNext(Mac);
                e.onComplete();
            } else {
                LogUtil.i(TAG, "calcMACWithCalType calc MAC exception");
                e.onError(new CommonException(ExceptionType.CALCULATE_MAC_FAILED));
            }
        });
    }

    Observable<byte[]> encryptTrackData(final PinPadEncryptTrackParamIn param) {
        return Observable.create(e -> {
            if (param == null) {
                LogUtil.i(TAG, "Track info not exist");
                e.onNext("0".getBytes());
                e.onComplete();
            } else {
                int encMode = 0x01; // ECB
                if (param.getMode() == 1) {
                    encMode = 0x02; // CBC
                }
                byte[] encryTrackData = posService.getHandler().getPinpad(0).calculateByDataKey(param.getKeyId(), param.getAlgorithmType(), encMode, 0x00, param.getTrkData(), param.getIv());
                if (encryTrackData != null) {
                    e.onNext(encryTrackData);
                    e.onComplete();
                } else {
                    String strError = posService.getHandler().getPinpad(0).getLastError();
                    LogUtil.i(TAG, "encryptTrackData " + strError);
                    e.onError(new CommonException(ExceptionType.ENCRYPT_TRACK_INFO_FAILED));
                }
            }
        });
    }

    Observable<byte[]> encryptTrackDataWithAlgorithmType(final PinPadEncryptTrackParamIn param) {
        return Observable.create(e -> {
            if (param == null) {
                e.onNext("".getBytes());
                e.onComplete();
            }
            byte[] encryTrackData = posService.getHandler().getPinpad(0).encryptTrackDataWithAlgorithmType(param.getMode(), param.getKeyId(), param.getAlgorithmType(), param.getTrkData(), false);
            if (encryTrackData != null) {
                e.onNext(encryTrackData);
                e.onComplete();
            } else {
                String strError = posService.getHandler().getPinpad(0).getLastError();
                e.onError(new RemoteException(strError));
            }
        });
    }

    Observable<byte[]> startPinInput(final PinPadStartPinInputParamIn param) {
        Bundle bundle = new Bundle();
        bundle.putByteArray("pinLimit", param.getPinLimit());
        bundle.putInt("timeout", param.getTimeout());
        bundle.putBoolean("isOnline", param.getOnLineState());
        bundle.putString("promptString", param.getPromptString());
        bundle.putString("pan", param.getPan());
        bundle.putInt("keysType", param.getKeyType());
        bundle.putInt("desType", param.getDesType());
        return Observable.create(e -> {
            LogUtil.i(TAG, "start startPinInput");
            posService.getHandler().getPinpad(0).startPinInput(param.getKeyId(), bundle, null, new PinInputListener.Stub() {

                @Override
                public void onInput(int len, int key) throws RemoteException {
                    LogUtil.i(TAG, "onInput");
                }

                @Override
                public void onConfirm(byte[] data, boolean isNonePin) throws RemoteException {
                    LogUtil.i(TAG, "onConfirm");
                    if (Control()) {
                        if (isNonePin) {
                            e.onNext(new byte[0]);
                        } else {
                            e.onNext(data);
                        }
                        e.onComplete();
                    }
                }

                @Override
                public void onCancel() throws RemoteException {
                    LogUtil.i(TAG, "onCancel");
                    e.onError(new CommonException(ExceptionType.INPUT_PIN_CANCELED));
                }

                @Override
                public void onError(int errorCode) throws RemoteException {
                    String strError = posService.getHandler().getPinpad(0).getLastError();
                    if (errorCode == -2) {
                        LogUtil.i(TAG, "onError:" + errorCode + "  strError: input pin timeout");
                        e.onError(new CommonException(ExceptionType.INPUT_PIN_TIMEOUT));
                    } else {
                        LogUtil.i(TAG, "onError:" + errorCode + "  strError:" + strError);
                        e.onError(new CommonException(ExceptionType.INPUT_PIN_FAILED));
                    }
                }

            });
        });
    }

    Observable<Object> importPin(int option, byte data[]) {
        LogUtil.i(TAG, "importPin option: " + option);
        if (data == null) {
            data = new byte[]{};
        }
        byte[] finalData = data;
        return Observable.create(e -> {
            posService.getHandler().getEMV().importPin(option, finalData);
            e.onNext(0);
            e.onComplete();
        });
    }

    Observable<Object> submitPinInput() {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                posService.getHandler().getPinpad(0).submitPinInput();
                e.onNext(0);
                e.onComplete();
            }
        });
    }

    Observable<Object> stopPinInput() {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                posService.getHandler().getPinpad(0).stopPinInput();
                e.onNext(0);
                e.onComplete();
            }
        });
    }

    Observable<Object> getLastError() {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                String str = posService.getHandler().getPinpad(0).getLastError();
                e.onNext(str);
                e.onComplete();
            }
        });
    }

    Observable<byte[]> desData(final PinPadEncryptDecryptParamIn paramIn) {
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> e) throws Exception {
                LogUtil.d("【IC card import key】 <desData> Mode=" + String.format("%02X", paramIn.getMode()) + " Type=" + String.format("%02X", paramIn.getDesTpye()) + " Key=" + StringUtil.byte2HexStr(paramIn.getInKey()) + " Data=" + StringUtil.byte2HexStr(paramIn.getInData()));
                byte[] resaultData = posService.getHandler().getPinpad(0).colculateData(paramIn.getMode(), paramIn.getDesTpye(), paramIn.getInKey(), paramIn.getInData());
                LogUtil.d("【IC card import key】 <desData> recvData=[" + StringUtil.byte2HexStr(resaultData) + "]");
                if (resaultData != null && resaultData.length != 0) {
                    e.onNext(resaultData);
                    e.onComplete();
                } else {
                    String strError = posService.getHandler().getPinpad(0).getLastError();
                    e.onError(new RemoteException(strError));
                }
            }
        });
    }

    Observable<Map<String, String>> initPinInputCustomView(final PinPadInitPinInputCustomViewParamIn paramIn) {
        return Observable.create(e -> {
            Bundle bundle = new Bundle();
            bundle.putByteArray("pinLimit", paramIn.getPinLimit());
            bundle.putInt("timeout", paramIn.getTimeout());
            bundle.putBoolean("isOnline", paramIn.getOnLineState());
            bundle.putString("promptString", paramIn.getPromptString());
            bundle.putString("pan", paramIn.getPan());
            bundle.putInt("keysType", paramIn.getKeyType());
            bundle.putInt("desType", paramIn.getDesType());
            if (paramIn.getRandom() != null) {
                bundle.putByteArray("random", paramIn.getRandom());
                // bundle.putByteArray("random", paramIn.getRandom());
            }
            if (paramIn.getKeyboardNumberPosition() != null) {
                LogUtil.d(TAG, "displayKeyValue=" + new String(paramIn.getKeyboardNumberPosition()));
                bundle.putByteArray("displayKeyValue", paramIn.getKeyboardNumberPosition());
            }

            PinPadInitPinInputCustomViewParamIn.PinpadListener pinpadListener = paramIn.getPinpadListener();
            List<PinKeyCoordinate> pinKeyCoordinates = paramIn.getPinKeyCoordinates();
            List<PinKeyCoorInfo> pinKeyCoorInfos = new ArrayList<>();
            if (pinKeyCoordinates != null) {
                Iterator<PinKeyCoordinate> iterator = pinKeyCoordinates.iterator();
                while (iterator.hasNext()) {
                    PinKeyCoordinate pinKeyCoordinate = iterator.next();
                    String keyName = pinKeyCoordinate.getKeyName();
                    int x1 = pinKeyCoordinate.getLeftTopX();
                    int y1 = pinKeyCoordinate.getLeftTopY();
                    int x2 = pinKeyCoordinate.getRightBottomX();
                    int y2 = pinKeyCoordinate.getRightBottomY();
                    int keyType = pinKeyCoordinate.getKeyType();

                    LogUtil.d("TAG", "===keyName=" + keyName + " x1=" + x1 + " y1=" + y1 + " x2=" + x2 + " y2=" + y2 + " keyType=" + keyType);
                    pinKeyCoorInfos.add(new PinKeyCoorInfo(keyName, x1, y1, x2, y2, keyType));
                }
            }

            try {
                Map<String, String> map = posService.getHandler().getPinpad(0).initPinInputCustomView(paramIn.getKeyId(), bundle, pinKeyCoorInfos, new PinInputListener.Stub() {
                    @Override
                    public void onInput(int len, int key) throws RemoteException {
                        LogUtil.i(TAG, "onInput len=" + len + " key=" + key);
                        if (pinpadListener != null) {
                            pinpadListener.onInput(len, key);
                        }
                    }

                    @Override
                    public void onConfirm(byte[] data, boolean isNonePin) throws RemoteException {
                        LogUtil.i(TAG, "onConfirm isNonePin=" + isNonePin);
                        posService.getHandler().getPinpad(0).endPinInputCustomView();
                        if (Control()) {
                            if (pinpadListener != null) {
                                if (isNonePin) {
                                    // fix bug #334 - 终端输入密码界面死机
                                    pinpadListener.onConfirm(new byte[0], isNonePin);
                                } else {
                                    pinpadListener.onConfirm(data, isNonePin);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancel() throws RemoteException {
                        LogUtil.i(TAG, "onCancel");
                        posService.getHandler().getPinpad(0).endPinInputCustomView();
                        if (pinpadListener != null) {
                            pinpadListener.onCancel();
                        }
                    }

                    @Override
                    public void onError(int errorCode) throws RemoteException {
                        posService.getHandler().getPinpad(0).endPinInputCustomView();
                        String strError = posService.getHandler().getPinpad(0).getLastError();
                        if (errorCode == -2) {
                            strError = "Input pin timeout";
                            LogUtil.i(TAG, "onError:" + errorCode + "  strError:" + strError);
                        } else {
                            LogUtil.i(TAG, "onError:" + errorCode + "  strError:" + strError);
                        }

                        if (pinpadListener != null) {
                            pinpadListener.onError(errorCode, strError);
                        }
                    }
                });

                e.onNext(map);
                e.onComplete();
            } catch (Exception ex) {
                e.onError(ex);
            }
        });
    }

    Observable<Object> startPinInputCustomView() {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                posService.getHandler().getPinpad(0).startPinInputCustomView();
                e.onNext(0);
                e.onComplete();
            }
        });
    }

    Observable<Object> endPinInputCustomView() {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                posService.getHandler().getPinpad(0).endPinInputCustomView();
                e.onNext(0);
                e.onComplete();
            }
        });
    }

    public boolean isKeyExist(int keyType, int keyId) {
        boolean isKeyExist = false;
        if (posService.getHandler() != null) {
            try {
                isKeyExist = posService.getHandler().getPinpad(0).isKeyExist(keyType, keyId);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        return isKeyExist;
    }

    public Observable<Object> loadPlainWorkKey(final PinPadGeneralParamIn param) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                LogUtil.d("load pin key to pinpad，type=[" + param.getKeyType() + "]" + "Idx=[" + param.getKeyIdx() + "]" + " Key=[" + StringUtil.byte2HexStr(param.getKey()) + "]");
                boolean ret = posService.getHandler().getPinpad(0).savePlainKey(param.getKeyType(), param.getKeyIdx(), param.getKey());
                if (ret) {
                    LogUtil.d("load pin key success");
                    e.onNext(ret);
                    e.onComplete();
                } else {
                    LogUtil.d("load pin key failed");
                    String strError = posService.getHandler().getPinpad(0).getLastError();
                    e.onError(new RemoteException(strError));
                }
            }
        });
    }
}
