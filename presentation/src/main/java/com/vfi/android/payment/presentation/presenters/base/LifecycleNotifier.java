package com.vfi.android.payment.presentation.presenters.base;

import android.arch.lifecycle.LifecycleObserver;

public interface LifecycleNotifier extends LifecycleObserver {
    void register(LifecycleObserver observer);
    void unregister(LifecycleObserver observer);
}
