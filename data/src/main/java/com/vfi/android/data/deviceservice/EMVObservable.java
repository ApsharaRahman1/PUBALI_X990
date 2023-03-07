package com.vfi.android.data.deviceservice;

import android.os.Bundle;
import android.os.RemoteException;

import com.vfi.android.domain.entities.businessbeans.CardInformation;
import com.vfi.android.domain.entities.businessbeans.EMVCallback;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.entities.consts.EMVConstant;
import com.vfi.android.domain.entities.consts.EMVResult;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.AIDParams;
import com.vfi.android.domain.entities.databeans.APIDParam;
import com.vfi.android.domain.entities.databeans.CAPKParams;
import com.vfi.android.domain.entities.databeans.EMVParamIn;
import com.vfi.android.domain.entities.databeans.EmvOnlineResultParamIn;
import com.vfi.android.domain.entities.databeans.EmvProcessOnlineResult;
import com.vfi.android.domain.entities.databeans.EmvTagListParam;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.smartpos.deviceservice.aidl.DRLData;
import com.vfi.smartpos.deviceservice.aidl.EMVHandler;
import com.vfi.smartpos.deviceservice.aidl.OnlineResultHandler;
import com.vfi.smartpos.deviceservice.constdefine.CTLSKernelID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

import static com.vfi.android.domain.entities.businessbeans.EMVCallback.CallbackType.ON_CONFIRM_CARDINFO;
import static com.vfi.android.domain.entities.businessbeans.EMVCallback.CallbackType.ON_CONFIRM_CERTINFO;
import static com.vfi.android.domain.entities.businessbeans.EMVCallback.CallbackType.ON_REQUEST_AMOUNT;
import static com.vfi.android.domain.entities.businessbeans.EMVCallback.CallbackType.ON_REQUEST_INPUTOFFLINEPIN;
import static com.vfi.android.domain.entities.businessbeans.EMVCallback.CallbackType.ON_REQUEST_INPUTPIN;
import static com.vfi.android.domain.entities.businessbeans.EMVCallback.CallbackType.ON_REQUEST_ONLINE;
import static com.vfi.android.domain.entities.businessbeans.EMVCallback.CallbackType.ON_SELECT_APP;
import static com.vfi.android.domain.entities.businessbeans.EMVCallback.CallbackType.ON_TRADING_RES;
import static com.vfi.android.domain.entities.consts.EMVConstant.ARQC_DATA;
import static com.vfi.android.domain.entities.consts.EMVConstant.RESULT;
import static com.vfi.android.domain.entities.consts.EMVConstant.REVERSAL_DATA;
import static com.vfi.android.domain.entities.consts.EMVConstant.SCRIPT_DATA;
import static com.vfi.android.domain.entities.consts.EMVConstant.TC_DATA;

/**
 * Created by yunlongg1 on 16/10/2017.
 */

public class EMVObservable {
    private static final String TAG = TAGS.EmvFlow;
    private PosServiceImpl posService;

    private static class SingletonHolder {
        private static final EMVObservable INSTANCE = new EMVObservable();
    }

    private EMVObservable() {
    }

    public static final EMVObservable getInstance() {
        return EMVObservable.SingletonHolder.INSTANCE;
    }

    public EMVObservable build(PosServiceImpl posService) {
        this.posService = posService;
        return this;
    }

    Observable<Object> stop() {
        LogUtil.i(TAG, "abortEMV executed.");
        return Observable.create(eventEmitter -> {
            EMVObservable.this.posService.getHandler().getEMV().abortEMV();
            eventEmitter.onNext(0);
            eventEmitter.onComplete();
        });
    }

    Observable<Object> importAmount(long amount) {
        return Observable.create(eventEmitter -> {
            EMVObservable.this.posService.getHandler().getEMV().importAmount(amount);
            eventEmitter.onNext(0);
            eventEmitter.onComplete();
        });
    }

    Observable<Object> importAppSelected(int index) {
        return Observable.create(eventEmitter -> {
            EMVObservable.this.posService.getHandler().getEMV().importAppSelection(index);
            eventEmitter.onNext(0);
            eventEmitter.onComplete();
        });
    }

    Observable<Object> importCardConfirmResult(boolean pass) {
        LogUtil.i(TAG, "importCardConfirmResult pass: " + pass);
        return Observable.create(eventEmitter -> {
            EMVObservable.this.posService.getHandler().getEMV().importCardConfirmResult(pass);
            eventEmitter.onNext(0);
            eventEmitter.onComplete();
        });
    }

    Observable<Object> exchangeEmvData(List<String> emvDataList) {
        LogUtil.i(TAG, "exchange emv data : " + emvDataList.get(0));
        return Observable.create(e -> {
            EMVObservable.this.posService.getHandler().getEMV().setEMVData(emvDataList);
            e.onNext(0);
            e.onComplete();
        });
    }

    Observable<Object> importCertConfirmResult(int option) {
        return Observable.create(eventEmitter -> {
            EMVObservable.this.posService.getHandler().getEMV().importCertConfirmResult(option);
            eventEmitter.onNext(0);
            eventEmitter.onComplete();
        });
    }

    Observable<EmvProcessOnlineResult> inputOnlineResult(EmvOnlineResultParamIn emvOnlineResultParamIn) {
        return Observable.create(eventEmitter -> {

            EmvProcessOnlineResult EMVProcessOnlineResult = new EmvProcessOnlineResult();
            OnlineResultHandler onlineResultHandler = new OnlineResultHandler.Stub() {
                @Override
                public void onProccessResult(final int result, final Bundle data) throws RemoteException {
                    EMVProcessOnlineResult.setResult(result);
                    EMVProcessOnlineResult.setScriptResult(data.getString(SCRIPT_DATA));
                    EMVProcessOnlineResult.setTcData(data.getString(TC_DATA));
                    EMVProcessOnlineResult.setReversalData(data.getString(REVERSAL_DATA));
                    LogUtil.d(TAG, "input online result=" + result);
                    eventEmitter.onNext(EMVProcessOnlineResult);
                    eventEmitter.onComplete();
                }
            };

            Bundle pbocOnlineResultBundle = new Bundle();

            String respCode;
            respCode = emvOnlineResultParamIn.getRespCode();

            pbocOnlineResultBundle.putBoolean("isOnline", emvOnlineResultParamIn.isOnline());
            pbocOnlineResultBundle.putString("respCode", respCode);
            pbocOnlineResultBundle.putString("authCode", emvOnlineResultParamIn.getAuthCode());
            pbocOnlineResultBundle.putString("field55", emvOnlineResultParamIn.getField55());
            LogUtil.i(TAG, "respCode: " + respCode);
            EMVObservable.this.posService.getHandler().getEMV().inputOnlineResult(pbocOnlineResultBundle, onlineResultHandler);
        });
    }

    Observable<EMVCallback> start(EMVParamIn emvParamIn) {
        LogUtil.i(TAG, "start" + emvParamIn.isForceOnline());
        return Observable.create((ObservableEmitter<EMVCallback> eventEmitter) -> {
            EMVHandler emvHandler = new EMVHandler.Stub() {
                @Override
                public void onRequestAmount() throws RemoteException {
                    LogUtil.i(TAG, "EMVHandler onRequestAmount");
                    eventEmitter.onNext(EMVCallback.newInstance(ON_REQUEST_AMOUNT));
                }

                @Override
                public void onSelectApplication(List<Bundle> appList) throws RemoteException {
                    LogUtil.i(TAG, "EMVHandler onSelectApplication");
                    List<String> appStrList = new ArrayList<>();

                    EMVCallback callback = EMVCallback.newInstance(ON_SELECT_APP);
                    Iterator<Bundle> iterator = appList.iterator();
                    while (iterator.hasNext()) {
                        Bundle bundle = iterator.next();

                        String aid = bundle.getString("aid", "");
                        String aidName = bundle.getString("aidName", "");
                        String app = aidName + ":" + aid;
                        LogUtil.d(TAG, "app=[" + app + "]");
                        appStrList.add(app);
                    }

                    callback.setAppList(appStrList);
                    eventEmitter.onNext(callback);
                }

                @Override
                public void onConfirmCardInfo(Bundle info) throws RemoteException {
                    LogUtil.i(TAG, "EMVHandler onConfirmCardInfo");
                    final CardInformation cardInformation = new CardInformation();
                    cardInformation.setTrack1(info.getString("TRACK1"));
                    cardInformation.setTrack2(info.getString("TRACK2"));
                    cardInformation.setTrack3(info.getString("TRACK3"));
                    cardInformation.setServiceCode(info.getString("SERVICE_CODE"));
                    cardInformation.setExpiredDate(info.getString("EXPIRED_DATE"));
                    cardInformation.setPan(info.getString("PAN"));
                    cardInformation.setCardSequenceNum(info.getString("CARD_SN"));

                    final int CARD_TYPE_MSD = 1;
                    int cardType = info.getInt("CARD_TYPE", -1);
                    LogUtil.i(TAG, "cardType=" + cardType);
                    if (cardType == CARD_TYPE_MSD) {
                        cardInformation.setMsdCard(true);
                    }

                    LogUtil.i(TAG, "PAN  " + info.getString("PAN"));
                    LogUtil.i(TAG, "CARD_SN  " + info.getString("CARD_SN"));
                    LogUtil.i(TAG, "EXPIRED_DATE  " + info.getString("EXPIRED_DATE"));
                    LogUtil.i(TAG, "SERVICE_CODE  " + info.getString("SERVICE_CODE"));
                    LogUtil.i(TAG, "TRACK1  " + info.getString("TRACK1"));
                    LogUtil.i(TAG, "TRACK2  " + info.getString("TRACK2"));
                    LogUtil.i(TAG, "TRACK3  " + info.getString("TRACK3"));

                    eventEmitter.onNext(
                            EMVCallback.newInstance(ON_CONFIRM_CARDINFO)
                                    .setCardInformation(cardInformation));
                }

                @Override
                public void onRequestInputPIN(boolean isOnlinePin, int retryTimes) throws RemoteException {
                    LogUtil.i(TAG, "EMVHandler onRequestInputPIN isOnlinePin: " + isOnlinePin + " retryTimes: " + retryTimes);
                    EMVCallback.CallbackType callbackType;
                    if (isOnlinePin) {
                        callbackType = ON_REQUEST_INPUTPIN;
                    } else {
                        callbackType = ON_REQUEST_INPUTOFFLINEPIN;
                    }
                    eventEmitter.onNext(EMVCallback.newInstance(callbackType).setOnlinePIN(isOnlinePin).setRetryTimes(retryTimes));
                }

                @Override
                public void onConfirmCertInfo(String certType, String certInfo) throws RemoteException {
                    LogUtil.i(TAG, "EMVHandler onConfirmCertInfo");
                    eventEmitter.onNext(EMVCallback.newInstance(ON_CONFIRM_CERTINFO));
                }

                @Override
                public void onRequestOnlineProcess(Bundle aaResult) throws RemoteException {
                    LogUtil.i(TAG, "EMVHandler onRequestOnlineProcess " + aaResult.getInt(RESULT));
                    EMVCallback callback = EMVCallback.newInstance(ON_REQUEST_ONLINE);
                    callback.setOnlineResult(aaResult.getInt(RESULT));
                    EMVResult emvResult = EMVResult.findPbocResultById(aaResult.getInt(RESULT));
                    switch (emvResult) {
                        case AARESULT_ARQC://EMV request online
                        case CTLS_ARQC: //CTLS_ARQC
                            callback.setArqcData(aaResult.getString(ARQC_DATA));
                            callback.setReversalData(aaResult.getString(REVERSAL_DATA));
                            break;
                        default:
                            break;
                    }

                    boolean isNeedSignature = aaResult.getBoolean(EMVConstant.SIGNATURE, false);
                    int cvmType = aaResult.getInt(EMVConstant.CTLS_CVMR, -1);
                    LogUtil.i(TAG, "Is need to sign : " + isNeedSignature);
                    LogUtil.i(TAG, "Contactless CVM Result : " + cvmType);
                    callback.setNeedSignature(isNeedSignature);
                    callback.setContactlessCvmResult(cvmType);

                    eventEmitter.onNext(callback);
                    eventEmitter.onComplete();
                }

                @Override
                public void onTransactionResult(int result, Bundle data) throws RemoteException {
                    LogUtil.i(TAG, "EMVHandler onTransactionResult " + result);
                    EMVCallback callback = EMVCallback.newInstance(ON_TRADING_RES);
                    callback.setOnlineResult(result);
                    if (data != null) {
                        callback.setTcData(data.getString(TC_DATA, ""));

                        boolean isNeedSignature = data.getBoolean(EMVConstant.SIGNATURE, false);
                        int cvmType = data.getInt(EMVConstant.CTLS_CVMR, -1);
                        LogUtil.i(TAG, "Is need to sign ： " + isNeedSignature);
                        LogUtil.i(TAG, "CVM Result Type ： " + cvmType);
                        callback.setNeedSignature(isNeedSignature);
                        callback.setContactlessCvmResult(cvmType);
                    }

                    if (callback.getOnlineResult() == EMVResult.AARESULT_AAC.getId() ||
                            callback.getOnlineResult() == EMVResult.EMV_NO_APP.getId() ||
                            (callback.getOnlineResult() > EMVResult.EMV_COMPLETE.getId() &&
                                    callback.getOnlineResult() != EMVResult.CTLS_TC.getId() &&
                                    callback.getOnlineResult() != EMVResult.CTLS_SEE_PHONE.getId() &&
                                    callback.getOnlineResult() != EMVResult.EMV_COMM_TIMEOUT.getId())) {
                        eventEmitter.onError(new CommonException(ExceptionType.EMV_FAILED, callback.getOnlineResult()));
                        LogUtil.i(TAG, "PBOCHandler onTransactionResult onError " + result);
                        return;
                    }
                    eventEmitter.onNext(callback);
                    eventEmitter.onComplete();
                }
            };

            Bundle emvBundle = new Bundle();
            emvBundle.putLong("authAmount", emvParamIn.getAuthAmount());
            emvBundle.putBoolean("isSupportSM", emvParamIn.isSupportSM());
            emvBundle.putBoolean("isForceOnline", emvParamIn.isForceOnline());
            emvBundle.putInt("panConfirmTimeOut", 999);
            emvBundle.putString("merchantName", emvParamIn.getMerchantName());
            emvBundle.putString("merchantId", emvParamIn.getMerchantId());
            emvBundle.putString("terminalId", emvParamIn.getTerminalId());
            final int TYPE_RETURN_NAME = 0;
            final int TYPE_RETURN_NAME_AND_AID = 1;
            emvBundle.putInt("multiAppSelectReturnType", TYPE_RETURN_NAME_AND_AID);

            HashMap<String, Integer> map = new HashMap<>();
            map.put("A000000605", CTLSKernelID.CTLS_KERNEL_ID_02_MASTER);
            EMVObservable.this.posService.getHandler().getEMV().registerKernelAID(map);

            int slotType = emvParamIn.getSlotType();
            if (slotType == CardEntryMode.IC) {
                emvBundle.putInt("cardType", 0);
            } else if (emvParamIn.getSlotType() == CardEntryMode.RF) {
                emvBundle.putInt("cardType", 1);
            } else {
                emvBundle.putInt("cardType", 0);
            }

            EMVObservable.this.posService.getHandler().getEMV().startEMV(emvParamIn.getEmvProcesstype(), emvBundle, emvHandler);
        });
    }

    Observable<Boolean> updateAID(AIDParams aidParamsIn) {
        return Observable.create(eventEmitter -> {
            LogUtil.i(TAG, " updateAID :" + aidParamsIn.toString());
            boolean res;
            if (aidParamsIn.getAIDPrmType() == 2) {
                res = EMVObservable.this.posService.getHandler().getEMV().updateAID(aidParamsIn.getAIDOperation(), aidParamsIn.getAIDPrmType(), aidParamsIn.getAIDStr());
                LogUtil.i(TAG, "非接 updateAID ret:" + res);
            } else {
                res = EMVObservable.this.posService.getHandler().getEMV().updateAID(aidParamsIn.getAIDOperation(), aidParamsIn.getAIDPrmType(), aidParamsIn.getAIDStr());
                LogUtil.i(TAG, "接触 updateAID ret:" + res);
            }
            eventEmitter.onNext(res);
            eventEmitter.onComplete();
        });

    }

    Observable<Boolean> updateRID(CAPKParams capkParamsIn) {
        return Observable.create(eventEmitter -> {
            boolean res = EMVObservable.this.posService.getHandler().getEMV().updateRID(capkParamsIn.getCAPKOperation(), capkParamsIn.getCAPKStr());
            LogUtil.i(TAG, " updateRID str:" + capkParamsIn.getCAPKStr());
            LogUtil.i(TAG, " updateRID ret:" + res);
            eventEmitter.onNext(res);
            eventEmitter.onComplete();
        });
    }

    Observable<String> getEMVTagList(EmvTagListParam tagList) {
        return Observable.create(eventEmitter -> {
            String res = EMVObservable.this.posService.getHandler().getEMV().getAppTLVList(tagList.getTagList());
            eventEmitter.onNext(res);
            eventEmitter.onComplete();
        });
    }

    Observable<String[]> getAID(int type) {
        return posService.bind().flatMap(unused -> {
            return Observable.create(eventEmitter -> {
                String[] aids = posService.getHandler().getEMV().getAID(type);
                if (aids == null) {
                    LogUtil.d(TAG, "AID type=" + type + " is null");
                    aids = new String[0];
                }
                eventEmitter.onNext(aids);
                eventEmitter.onComplete();
            });
        });
    }

    Observable<String[]> getCapK() {
        return posService.bind().flatMap(unused -> {
            return Observable.create(eventEmitter -> {
                String[] capks = EMVObservable.this.posService.getHandler().getEMV().getRID();
                if (capks == null) {
                    LogUtil.d(TAG, "Capk is null");
                    capks = new String[0];
                }
                eventEmitter.onNext(capks);
                eventEmitter.onComplete();
            });
        });
    }

    public String getEMVTagList(String[] tagList) {
        String tlvTags = "";
        if (posService.getHandler() != null) {
            try {
                tlvTags = posService.getHandler().getEMV().getAppTLVList(tagList);
                if (tlvTags == null) {
                    tlvTags = "";
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        return tlvTags;
    }

    public void setEmvTag(String tag) {
        if (posService.getHandler() != null) {
            try {
                List<String> tagList = new ArrayList<>();
                tagList.add(tag);
                posService.getHandler().getEMV().setEMVData(tagList);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    Observable<Boolean> updateVisaAPID(APIDParam paramIn) {
        return Observable.create(eventEmitter -> {
            byte[] programId = StringUtil.hexStr2Bytes(paramIn.getProgramId());
            byte[] transLimit = StringUtil.hexStr2Bytes(paramIn.getTransLimit());
            byte[] floorLimit = StringUtil.hexStr2Bytes(paramIn.getFloorLimit());
            byte[] cvmLimit = StringUtil.hexStr2Bytes(paramIn.getCvmLimit());
            LogUtil.d(TAG, "updateVisaAPID operationType=" + paramIn.getOperationType());
            LogUtil.d(TAG, "updateVisaAPID programId=" + paramIn.getProgramId());
            LogUtil.d(TAG, "updateVisaAPID transLimit=" + paramIn.getTransLimit());
            LogUtil.d(TAG, "updateVisaAPID floorLimit=" + paramIn.getFloorLimit());
            LogUtil.d(TAG, "updateVisaAPID cvmLimit=" + paramIn.getCvmLimit());
            DRLData drlData;
            if (paramIn.getOperationType() == APIDParam.OP_DELETE_ALL) {
                drlData = new DRLData(null, null, null, null);
            } else {
                drlData = new DRLData(programId, transLimit, floorLimit, cvmLimit);
            }
            boolean res = EMVObservable.this.posService.getHandler().getEMV().updateVisaAPID(paramIn.getOperationType(), drlData);
            LogUtil.i(TAG, " updateVisaAPID ret:" + res);
            LogUtil.i(TAG, "=======================================================");
            eventEmitter.onNext(res);
            eventEmitter.onComplete();
        });
    }
}
