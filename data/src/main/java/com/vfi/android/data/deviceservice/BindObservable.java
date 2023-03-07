package com.vfi.android.data.deviceservice;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.smartpos.deviceservice.aidl.IDeviceService;

import io.reactivex.Observable;

/**
 * Created by yunlongg1 on 16/10/2017.
 */

public class BindObservable {
    private static final String TAG = "BindObservable";
    public final static String PACKAGE_NAME = "com.vfi.smartpos.deviceservice";
    private final static String ACTION_NAME = "com.vfi.smartpos.device_service";
    private PosServiceImpl posService;

    /* Singleton pattern*/
    private static class SingletonHolder {
        private static final BindObservable INSTANCE = new BindObservable();
    }

    private BindObservable() {
    }

    public static final BindObservable getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public BindObservable build(PosServiceImpl posService) {
        this.posService = posService;
        return this;
    }

    public Observable<Boolean> create() {
        return Observable.create(eventEmitter -> {
            /*Return success if this handler had been init.*/
            if (null != this.posService.getHandler()) {
                eventEmitter.onNext(true);
                eventEmitter.onComplete();
            } else {/*Create a new connection and keep the handler in global scope.*/
                LogUtil.i(TAG, "=====Start bind device service");

                ServiceConnection serviceConnection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        LogUtil.i(TAG, "=====Bind device service success,thread:" + Thread.currentThread().getName());
                        posService.setHandler(IDeviceService.Stub.asInterface(iBinder));
                        new Thread(() -> {
                            eventEmitter.onNext(true);
                            eventEmitter.onComplete();
                        }).start();
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {
                        LogUtil.i(TAG, "=====Disconnected device service");
                        posService.setHandler(null);
                        Intent intent = new Intent().setAction(ACTION_NAME).setPackage(PACKAGE_NAME);
                        Bundle servicePreferences = new Bundle();
                        servicePreferences.putString("Project", "SCB");
                        intent.putExtras(servicePreferences);
                        posService.getContext().bindService(intent,
                                this, Context.BIND_AUTO_CREATE);
                    }
                };
                Intent intent = new Intent().setAction(ACTION_NAME).setPackage(PACKAGE_NAME);
                Bundle servicePreferences = new Bundle();
                servicePreferences.putString("Project", "SCB");
                intent.putExtras(servicePreferences);
                boolean res = posService.getContext().bindService(
                        intent,
                        serviceConnection,
                        Context.BIND_AUTO_CREATE);
                if (res) {
                    LogUtil.i(TAG, "=====Device service exist");
                } else {
                    LogUtil.i(TAG, "=====Device service not exist");
                    eventEmitter.onError(new CommonException(ExceptionType.DEVICE_SERVICE_NOT_EXIST));
                }
            }
        });
    }
}
