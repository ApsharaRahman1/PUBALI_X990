package com.vfi.android.payment.presentation.internal.di.components;

import android.content.Context;

import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.print.PrintTaskManager;
import com.vfi.android.domain.interactor.transaction.tle.RKIKeyDownloadManager;
import com.vfi.android.domain.interfaces.comm.IComm;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.memory.GlobalMemoryCache;
import com.vfi.android.payment.presentation.AndroidApplication;
import com.vfi.android.payment.presentation.internal.di.modules.ApplicationModule;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.view.menu.MenuManager;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    //Exposed to sub-graphs.
    Context context();

    UINavigator uiNavigator();

    ThreadExecutor threadExecutor();

    PostExecutionThread postExecutionThread();

    IPosService deviceService();

    IRepository parametersRepository();

    GlobalMemoryCache globalMemoryCache();

    MenuManager globalMenuManager();

    PrintTaskManager printTaskManager();

    RKIKeyDownloadManager getRKIKeyDownloadManager();
}
