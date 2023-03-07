package com.vfi.android.data.deviceservice;

import android.os.RemoteException;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.smartpos.deviceservice.aidl.ISerialPort;

import io.reactivex.Observable;

public class UsbSerialObservable {
    private static final String TAG = TAGS.COMM;
    private PosServiceImpl posService;
    private ISerialPort serialPort;

    private static class SingletonHolder {
        private static final UsbSerialObservable INSTANCE = new UsbSerialObservable();
    }

    private UsbSerialObservable() {
    }

    public static final UsbSerialObservable getInstance() {
        return UsbSerialObservable.SingletonHolder.INSTANCE;
    }

    public UsbSerialObservable build(PosServiceImpl posService) {
        this.posService = posService;
        return this;
    }

    private ISerialPort getSerialPort() {
        if (serialPort == null) {
            try {
                serialPort = posService.getHandler().getSerialPort("usb-rs232");
            } catch (RemoteException e) {
                e.printStackTrace();
                serialPort = null;
            }
        }

        return serialPort;
    }

    public Observable<Boolean> connect(int bps, int par, int dbs) {
        return Observable.create(emitter -> {
            ISerialPort iSerialPort = getSerialPort();
            if (iSerialPort == null) {
                emitter.onNext(false);
                emitter.onComplete();
                return;
            }

            boolean bRet = iSerialPort.open();
            if (!bRet) {
                emitter.onNext(bRet);
                emitter.onComplete();
            }

            bRet = iSerialPort.init(bps, par, dbs);
            emitter.onNext(bRet);
            emitter.onComplete();
        });
    }

    public Observable<Boolean> disconnect() {
        return Observable.create(emitter -> {
            ISerialPort iSerialPort = getSerialPort();
            if (iSerialPort == null) {
                emitter.onNext(false);
                emitter.onComplete();
                return;
            }

            iSerialPort.clearInputBuffer();
            boolean bRet = iSerialPort.close();
            emitter.onNext(bRet);
            emitter.onComplete();
        });
    }

    public Observable<Integer> read(byte[] buffer, int readLen, int timeout) {
        return Observable.create(emitter -> {
            ISerialPort iSerialPort = getSerialPort();
            if (iSerialPort == null || buffer == null || buffer.length < readLen || readLen <= 0) {
                emitter.onNext(0);
                emitter.onComplete();
                return;
            }

            int ret = iSerialPort.read(buffer, readLen, timeout);
//            LogUtil.d(TAG, "USB serial read ret=" + ret);
            emitter.onNext(ret);
            emitter.onComplete();
        });
    }

    public Observable<Integer> write(byte[] data, int timeout) {
        return Observable.create(emitter -> {
            ISerialPort iSerialPort = getSerialPort();
            if (iSerialPort == null || data == null || data.length <= 0) {
                emitter.onNext(0);
                emitter.onComplete();
                return;
            }

            int ret = iSerialPort.write(data, timeout);
            LogUtil.d(TAG, "USB serial write ret=" + ret);
            emitter.onNext(ret);
            emitter.onComplete();
        });
    }

    public Observable<Boolean> clearInputBuffer() {
        return Observable.create(emitter -> {
            ISerialPort iSerialPort = getSerialPort();
            if (iSerialPort == null) {
                emitter.onNext(false);
                emitter.onComplete();
                return;
            }

            boolean bRet = iSerialPort.clearInputBuffer();
            emitter.onNext(bRet);
            emitter.onComplete();
        });
    }

    public Observable<Boolean> isBufferEmpty(boolean input) {
        return Observable.create(emitter -> {
            ISerialPort iSerialPort = getSerialPort();
            if (iSerialPort == null) {
                emitter.onNext(false);
                emitter.onComplete();
                return;
            }

            boolean bRet = iSerialPort.isBufferEmpty(input);
            emitter.onNext(bRet);
            emitter.onComplete();
        });
    }
}
