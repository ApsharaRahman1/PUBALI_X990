package com.vfi.android.payment.presentation.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalCfg;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalStatus;
import com.vfi.android.domain.interactor.repository.UseCaseIsAllHostsEmptyBatch;
import com.vfi.android.domain.interactor.repository.UseCaseSaveTerminalStatus;
import com.vfi.android.domain.interactor.repository.UseCaseSetForceSettlementFlag;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.presentation.AndroidApplication;
import com.vfi.android.payment.presentation.internal.di.components.CommonComponent;
import com.vfi.android.payment.presentation.internal.di.components.DaggerCommonComponent;

import javax.inject.Inject;

public class ForceSettlementReceiver extends BroadcastReceiver {
    private final static String TAG = TAGS.FORCE_SETTLEMENT;

    public static final String ACTION_FORCE_SETTLEMENT = "com.verifone.payment.action.FORCE_SETTLEMENT";
    public static final String ACTION_SET_FORCE_SETTLEMENT_TIMER = "com.verifone.payment.action.SET_FORCE_SETTLEMENT_TIMER";
    public static final String ACTION_CLEAR_FORCE_SETTLEMENT_TIMER = "com.verifone.payment.action.CLEAR_FORCE_SETTLEMENT_TIMER";

    @Inject
    Context context;
    @Inject
    UseCaseGetTerminalCfg useCaseGetTerminalCfg;
    @Inject
    UseCaseIsAllHostsEmptyBatch useCaseIsAllHostsEmptyBatch;
    @Inject
    UseCaseSaveTerminalStatus useCaseSaveTerminalStatus;
    @Inject
    UseCaseGetTerminalStatus useCaseGetTerminalStatus;
    @Inject
    UseCaseSetForceSettlementFlag useCaseSetForceSettlementFlag;

    public ForceSettlementReceiver() {
        CommonComponent commonComponent =
                DaggerCommonComponent.builder()
                .applicationComponent(AndroidApplication.getInstance().getApplicationComponent())
                .build();
        commonComponent.inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d(TAG, "action=[" + intent.getAction() + "]");

        TerminalCfg terminalCfg = useCaseGetTerminalCfg.execute(null);
        LogUtil.d(TAG, "isEnableForceSettlement=" + terminalCfg.isEnableForceSettlement());
        if (!terminalCfg.isEnableForceSettlement()) {
            return;
        }

        switch (intent.getAction()) {
            case ACTION_FORCE_SETTLEMENT:
                doForceSettlementProcess();
                break;
            case ACTION_SET_FORCE_SETTLEMENT_TIMER:
                setForceSettlementTimer(false);
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                setForceSettlementTimer(true);
                break;
            case ACTION_CLEAR_FORCE_SETTLEMENT_TIMER:
                cancelForceSettlementTimer();
                break;
        }
    }

    private void doForceSettlementProcess() {
        saveForceSettleDateYYYYMMDD();

        setForceSettlementTimer(false);

        boolean isAllHostEmpty = useCaseIsAllHostsEmptyBatch.execute(null);
        if (isAllHostEmpty) {
            LogUtil.d(TAG, "All host empty, no need force settlement");
        } else {
            LogUtil.d(TAG, "Set force settlement flag true.");
            useCaseSetForceSettlementFlag.asyncExecuteWithoutResult(true);
        }
    }

    private void cancelForceSettlementTimer() {
        LogUtil.d(TAG, "cancelForceSettlementTimer");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(getPendingIntent(ACTION_FORCE_SETTLEMENT));
    }

    private void setForceSettlementTimer(boolean isAfterReboot) {
        LogUtil.d(TAG, "setAutoSettlementTimer isAfterReboot=" + isAfterReboot);
        //1. clear auto settlement timer
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(getPendingIntent(ACTION_FORCE_SETTLEMENT));

        //2. set new auto settlement timer
        long currentTimeMillis = System.currentTimeMillis();
        TerminalCfg terminalCfg = useCaseGetTerminalCfg.execute(null);
        long todayForceSettleTimeMillis = StringUtil.formatDate2TimeMillis(terminalCfg.getForceSettlementTime());

        LogUtil.d(TAG, "force settlement time[" + terminalCfg.getForceSettlementTime() + "]");
        LogUtil.d(TAG, "currentTimeMillis=" + currentTimeMillis);
        LogUtil.d(TAG, "todayForceSettleTimeMillis=" + todayForceSettleTimeMillis);

        long triggerAtMillis;
        if (todayForceSettleTimeMillis < currentTimeMillis) {
            if (isAfterReboot && !isAlreadyForceSettledToday()) {
                triggerAtMillis = currentTimeMillis;
            } else {
                LogUtil.d(TAG, "Add one day time millis");
                long oneDaySeconds = 24 * 60 * 60 * 1000; // hour * min * sec * tick
                triggerAtMillis = todayForceSettleTimeMillis + oneDaySeconds;
            }
        } else {
            triggerAtMillis = todayForceSettleTimeMillis;
        }

        LogUtil.d(TAG, "set ACTION_FORCE_SETTLEMENT triggerAtMillis=" + triggerAtMillis);
        am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, getPendingIntent(ACTION_FORCE_SETTLEMENT));
    }

    private PendingIntent getPendingIntent(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private void saveForceSettleDateYYYYMMDD() {
        useCaseGetTerminalStatus.asyncExecute(null).doOnNext(terminalStatus -> {
            String dateYYYYMMDD = StringUtil.formatDate(System.currentTimeMillis());
            LogUtil.d(TAG, "Last Force settle time=" + dateYYYYMMDD);
            terminalStatus.setLastForceSettleDateYYYYMMDD(dateYYYYMMDD);
            useCaseSaveTerminalStatus.asyncExecuteWithoutResult(terminalStatus);
        }).doOnError(throwable -> {
            LogUtil.e(TAG, "Get terminal config failed.");
            throwable.printStackTrace();
        }).subscribe();
    }

    private boolean isAlreadyForceSettledToday() {
        TerminalStatus terminalStatus = useCaseGetTerminalStatus.execute(null);
        String currentDateYYYYMMDD = StringUtil.formatDate(System.currentTimeMillis());
        LogUtil.d(TAG, "currentDateYYYYMMDD=" + currentDateYYYYMMDD);
        String lastForceSettleDateYYYYMMDD = terminalStatus.getLastForceSettleDateYYYYMMDD();
        LogUtil.d(TAG, "lastForceSettleDateYYYYMMDD=" + lastForceSettleDateYYYYMMDD);
        long currentDate = StringUtil.parseLong(currentDateYYYYMMDD, -1);
        long lastForceSettleDate = StringUtil.parseLong(lastForceSettleDateYYYYMMDD, -1);
        boolean isAlreadyForceSettledToday = true;
        if (lastForceSettleDate == -1 || currentDate != lastForceSettleDate) {
            isAlreadyForceSettledToday = false;
        }

        LogUtil.d(TAG, "isAlreadyForceSettledToday=" + isAlreadyForceSettledToday);
        return isAlreadyForceSettledToday;
    }
}
