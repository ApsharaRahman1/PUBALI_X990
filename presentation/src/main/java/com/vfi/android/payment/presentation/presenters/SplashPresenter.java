package com.vfi.android.payment.presentation.presenters;

import com.vfi.android.domain.interactor.deviceservice.UseCaseBindDeviceService;
import com.vfi.android.domain.interactor.other.UseCaseTimer;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.SplashUI;

import javax.inject.Inject;

public class SplashPresenter extends BasePresenter<SplashUI> {
    private final UseCaseTimer useCaseTimer;
    private final UseCaseBindDeviceService useCaseBindDeviceService;
    private long onFirstUIAttachmentTime;
    private boolean isAnimationFinished;
    private boolean isInitFinished;

    @Inject
    public SplashPresenter(UseCaseTimer useCaseTimer,
                           UseCaseBindDeviceService useCaseBindDeviceService) {
        this.useCaseTimer = useCaseTimer;
        this.useCaseBindDeviceService = useCaseBindDeviceService;
    }

    @Override
    protected void onFirstUIAttachment() {
        onFirstUIAttachmentTime = System.currentTimeMillis();
        init();
    }

    private void init() {
        useCaseBindDeviceService.asyncExecute(null).doOnError(throwable -> {
            throwable.printStackTrace();
            // bind service failed.
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_bind_service_failed));
            isInitFinished = true;
            navigator2MainUI();
        }).doOnComplete(() -> {
            isInitFinished = true;
            navigator2MainUI();
        }).subscribe();
    }

    public void doAnimationFinishedProcess() {
        isAnimationFinished = true;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        navigator2MainUI();
    }

    private void navigator2MainUI() {
        if (isInitFinished && isAnimationFinished) {
            doUICmd_navigatorToNext();
        }
//        if (!isWaitClickSettingTimeNotEnough()) {
//            useCaseTimer.asyncExecute(2000L).doOnComplete(() -> {
//                doUICmd_navigatorToNext();
//            }).subscribe();
//        } else {
//            doUICmd_navigatorToNext();
//        }
    }

    private boolean isWaitClickSettingTimeNotEnough() {
        if (Math.abs(System.currentTimeMillis() - onFirstUIAttachmentTime) < 2000) {
            return true;
        }

        return false;
    }
}
