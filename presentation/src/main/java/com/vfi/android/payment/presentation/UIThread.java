package com.vfi.android.payment.presentation;


import com.vfi.android.domain.executor.PostExecutionThread;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * MainThread (UiCmd Thread) implementation based on a {@link Scheduler}
 * which will execute actions on the Android UiCmd thread
 */
@Singleton
public class UIThread implements PostExecutionThread {

  @Inject
  UIThread() {}

  @Override
  public Scheduler getScheduler() {
    return AndroidSchedulers.mainThread();
  }
}
