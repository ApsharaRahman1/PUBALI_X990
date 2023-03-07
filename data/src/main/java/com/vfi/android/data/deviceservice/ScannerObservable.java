package com.vfi.android.data.deviceservice;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.ScannerParamIn;
import com.vfi.android.domain.entities.databeans.ScannerParamOut;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.smartpos.deviceservice.aidl.ScannerListener;

import io.reactivex.Observable;

/**
 * Created by chong.z on 22/01/2018.*/

public class ScannerObservable {
    private static final String TAG = TAGS.SCANNER;
    private PosServiceImpl posService;

    private static class SingletonHolder {
        private static final ScannerObservable INSTANCE = new ScannerObservable();
    }

    private ScannerObservable() {
    }

    public static final ScannerObservable getInstance() {
        return ScannerObservable.SingletonHolder.INSTANCE;
    }

    public ScannerObservable build(PosServiceImpl posService) {
        this.posService = posService;
        return this;
    }

    /**
     * 启动扫码 | start scan
     * @param配置参数 | the parameter
     *  topTitleString(String)：扫描框最上方提示信息，最大12汉字,默认中间对齐，| the title string on the top, max length 24, align center.
     *  upPromptString(String)：扫描框上方提示信息，最大20汉字，默认中间对齐， | the prompt string upside of the scan box, max length 40, align center.
     *  downPromptString(String)：扫描框下方提示信息，最大20汉字，默认中间对齐 | the prompt string downside of the scan box , max length 40, align center.
     *  timeout - 超时时间，单位ms | the timeout, millsecond.
     *  listener - 扫码结果监听 | the call back listerner
     */
    Observable<ScannerParamOut> startScan(final ScannerParamIn param) {
        Bundle bundle = new Bundle();
        bundle.putString("topTitleString", param.getTopTitleString());
        bundle.putString("upPromptString", param.getUpPromptString());
        bundle.putString("downPromptString", param.getDownPromptString());

        //1: 后置摄像头
        return Observable.create(e -> posService.getHandler()
                .getScanner(0).startScan(bundle, param.getTimeout(), new ScannerListener.Stub() {
                    ScannerParamOut scannerParamOut = new ScannerParamOut();

                    @Override
                    public void onSuccess(String barcode) throws RemoteException {
                        Log.d(TAG, "onSuccess: " + barcode);
                        scannerParamOut.setScanResult(ScannerParamOut.SCAN_SUCCESS);
                        scannerParamOut.setScanCode(barcode);
                        e.onNext(scannerParamOut);
                        e.onComplete();
                    }

                    @Override
                    public void onError(int error, String message) throws RemoteException {
                        Log.d(TAG, "scan failed error=" + error + " message=" + message);
                        e.onError(new CommonException(ExceptionType.SCAN_FAILED));
                    }

                    @Override
                    public void onTimeout() throws RemoteException {
                        Log.d(TAG, "scan timeout");
                        scannerParamOut.setScanResult(ScannerParamOut.SCAN_TIMEOUT);
                        e.onNext(scannerParamOut);
                        e.onComplete();
                    }

                    @Override
                    public void onCancel() throws RemoteException {
                        Log.d(TAG, "scan canceled");
                        scannerParamOut.setScanResult(ScannerParamOut.SCAN_CANCEL);
                        e.onNext(scannerParamOut);
                        e.onComplete();
                    }
                }));
    }
}
