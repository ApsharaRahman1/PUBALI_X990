package com.vfi.android.payment.presentation.internal.di.modules;

import android.content.Context;


import com.vfi.android.data.comm.network.HttpCommImpl;
import com.vfi.android.data.comm.network.HttpsCommImpl;
import com.vfi.android.data.comm.network.SocketCommImpl;
import com.vfi.android.data.comm.serial.UsbSeralCommImpl;
import com.vfi.android.data.deviceservice.DebugPosServiceImpl;
import com.vfi.android.data.deviceservice.PosServiceImpl;
import com.vfi.android.data.excutor.JobExecutor;
import com.vfi.android.data.repository.RepositoryImpl;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interfaces.comm.IComm;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.payment.BuildConfig;
import com.vfi.android.payment.presentation.AndroidApplication;
import com.vfi.android.payment.presentation.UIThread;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides objects which will live during the application lifecycle.
 */
@Module
public class ApplicationModule {
    private final AndroidApplication application;

    public ApplicationModule(AndroidApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return this.application;
    }

//    @Provides
//    @Singleton
//    SharedPreferencesHelper provideSharedPreferencesHelper() { return new SharedPreferencesHelper(application); }
//
    @Provides
    @Singleton
    ThreadExecutor provideThreadExecutor(JobExecutor jobExecutor) {
        return jobExecutor;
    }

    @Provides
    @Singleton
    PostExecutionThread providePostExecutionThread(UIThread uiThread) {
        return uiThread;
    }

    @Provides
    @Singleton
    IPosService provideDeviceService(PosServiceImpl iDeviceService, DebugPosServiceImpl debugPosService) {
        if (BuildConfig.DEBUG) {
            return debugPosService;
        } else {
            return iDeviceService;
        }
    }

    @Provides
    @Singleton
    IRepository provideParametersRepository(RepositoryImpl parametersRepository) {
        return parametersRepository;
    }
}
