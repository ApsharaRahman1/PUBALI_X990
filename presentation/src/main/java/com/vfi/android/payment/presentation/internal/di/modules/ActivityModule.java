package com.vfi.android.payment.presentation.internal.di.modules;

import android.app.Activity;


import com.vfi.android.data.comm.network.HttpCommImpl;
import com.vfi.android.data.comm.network.HttpsCommImpl;
import com.vfi.android.data.comm.network.SocketCommImpl;
import com.vfi.android.data.comm.serial.UsbSeralCommImpl;
import com.vfi.android.domain.interfaces.comm.IComm;
import com.vfi.android.payment.presentation.internal.di.PerActivity;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * A module to wrap the Activity state and expose it to the graph.
 */
@Module
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    /**
     * Expose the activity to dependents in the graph.
     */
    @Provides
    @PerActivity
    Activity activity() {
        return this.activity;
    }


    @Provides
    @Named("SOCKET")
    IComm provideSocketCommunicationInterface(SocketCommImpl socketComm) {
        return socketComm;
    }

    @Provides
    @Named("HTTPS")
    IComm provideHTTPSCommunicationInterface(HttpsCommImpl httpsComm) {
        return httpsComm;
    }

    @Provides
    @Named("HTTP")
    IComm provideHTTPCommunicationInterface(HttpCommImpl httpComm) {
        return httpComm;
    }

    @Provides
    @Named("USB_SERIAL")
    IComm provideUsbSerialCommunicationInterface(UsbSeralCommImpl usbSeralComm) {
        return usbSeralComm;
    }
}
