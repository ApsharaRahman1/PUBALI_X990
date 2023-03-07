package com.vfi.android.domain.comm;

import com.vfi.android.domain.entities.comm.CommType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.interfaces.comm.IComm;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;
import javax.inject.Named;

public class CommInterfaceFactory {
    private final String TAG = TAGS.COMM;

    @Inject
    @Named("SOCKET")
    IComm socketComm;
    @Inject
    @Named("USB_SERIAL")
    IComm usbSeralComm;
    @Inject
    @Named("HTTP")
    IComm httpComm;
    @Inject
    @Named("HTTPS")
    IComm httpsComm;

    @Inject
    public CommInterfaceFactory() {

    }

    public IComm createCommInterface(int commType) {
        LogUtil.d(TAG, "commType=" + commType);
        switch (commType) {
            case CommType.HTTP:
                return httpComm;
            case CommType.HTTPS:
                return httpsComm;
            case CommType.SOCKET:
                return socketComm;
            case CommType.USB_SERIAL:
                return usbSeralComm;
            default:
                throw new RuntimeException("CommType[" + commType + "] not support.");
        }
    }
}
