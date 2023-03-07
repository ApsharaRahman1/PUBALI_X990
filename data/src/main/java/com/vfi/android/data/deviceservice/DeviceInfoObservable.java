package com.vfi.android.data.deviceservice;

import android.os.Bundle;
import android.os.RemoteException;

import com.vfi.android.domain.entities.businessbeans.DeviceInformation;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.databeans.BlockSystemFunctionParamIn;
import com.vfi.android.domain.entities.databeans.TusnDataParamOut;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.smartpos.deviceservice.aidl.TusnData;

import io.reactivex.Observable;

/**
 * Created by fusheng.z on 29/12/2017.
 */

public class DeviceInfoObservable {
    private static final String TAG = "DeviceInfoObservable";
    private PosServiceImpl posService;

    private static class SingletonHolder {
        private static final DeviceInfoObservable INSTANCE = new DeviceInfoObservable();
    }

    private DeviceInfoObservable() {
    }

    public static final DeviceInfoObservable getInstance() {
        return DeviceInfoObservable.SingletonHolder.INSTANCE;
    }

    public DeviceInfoObservable build(PosServiceImpl posService) {
        this.posService = posService;
        return this;
    }

    Observable<DeviceInformation> getDeviceInformation() {
        return Observable.create(e -> {
            DeviceInformation deviceInformation = new DeviceInformation();
            deviceInformation.setSerialNo(posService.getHandler().getDeviceInfo().getSerialNo());
            if (deviceInformation.getSerialNo() == null) {
                e.onError(new CommonException(ExceptionType.TERMINAL_DOES_NOT_HAVE_SN));
            }
            deviceInformation.setIMEI(posService.getHandler().getDeviceInfo().getIMEI());
            deviceInformation.setAndroidKernelVersion(posService.getHandler().getDeviceInfo().getAndroidKernelVersion());
            deviceInformation.setAndroidOSVersion(posService.getHandler().getDeviceInfo().getAndroidOSVersion());
            deviceInformation.setFirmwareVersion(posService.getHandler().getDeviceInfo().getFirmwareVersion());
            deviceInformation.setICCID(posService.getHandler().getDeviceInfo().getICCID());
            deviceInformation.setHardwareVersion(posService.getHandler().getDeviceInfo().getHardwareVersion());
            deviceInformation.setManufacture(posService.getHandler().getDeviceInfo().getManufacture());
            deviceInformation.setModel(posService.getHandler().getDeviceInfo().getModel());
            deviceInformation.setRomVersion(posService.getHandler().getDeviceInfo().getROMVersion());
            e.onNext(deviceInformation);
            e.onComplete();
        });
    }

    Observable<Boolean> updateSystemTime(String date, String time) {
        return Observable.create(e -> {
            boolean res = posService.getHandler().getDeviceInfo().updateSystemTime(date, time);
            e.onNext(res);
            e.onComplete();
        });
    }

    Observable<Boolean> setSystemFunction(BlockSystemFunctionParamIn paramIn) {
        LogUtil.i(TAG, "isHomeEnable:" + paramIn.isHomeEnable() + " isStatusBarEnable:" + paramIn.isStatusBarEnable());
        return Observable.create(e -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("HOMEKEY", paramIn.isHomeEnable());
            bundle.putBoolean("STATUSBARKEY", paramIn.isStatusBarEnable());
            boolean res = posService.getHandler().getDeviceInfo().setSystemFunction(bundle);
            e.onNext(res);
            e.onComplete();
        });
    }

    Observable<TusnDataParamOut> getTusn(byte[] paramIn) {
        LogUtil.i(TAG, "random factor: " + new String(paramIn));
        return Observable.create(e -> {
            // Remove tusn by cunche for scb project
            //TusnData tusnData = posService.getHandler().getDeviceInfo().getTUSN(0, paramIn);
            TusnData tusnData = new TusnData();
            tusnData.setTerminalType(2);
            tusnData.setMac("        ");
            tusnData.setTusn("111111111111");
            if (tusnData != null) {
                TusnDataParamOut
                        tusnDataParamOut = new TusnDataParamOut(
                                tusnData.getTerminalType(),
                                tusnData.getTusn(),
                                tusnData.getMac()
                        );
                LogUtil.i(TAG, "tusnDataParamOut getTusn: " + tusnDataParamOut.getTusn());
                LogUtil.i(TAG, "tusnDataParamOut getMac: " + tusnDataParamOut.getMac());
                e.onNext(tusnDataParamOut);
                e.onComplete();
            } else {
                e.onError(new CommonException(ExceptionType.TERMINAL_DOES_NOT_HAVE_TUSN));
            }

        });
    }

    void setPowerKeyStatus(boolean isPowerKeyEnable) {
        if (posService.getHandler() != null) {
            try {
                boolean isEnable = !isPowerKeyEnable;
                LogUtil.i(TAG, "true - mask the power, false - enable the power key");
                LogUtil.i(TAG, "isEnable=" + isEnable);
                posService.getHandler().getDeviceInfo().setPowerStatus(isEnable);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
