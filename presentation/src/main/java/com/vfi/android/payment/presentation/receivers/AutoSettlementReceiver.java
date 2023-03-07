package com.vfi.android.payment.presentation.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.entities.consts.InterceptorResult;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalCfg;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalStatus;
import com.vfi.android.domain.interactor.repository.UseCaseIsAllHostsEmptyBatch;
import com.vfi.android.domain.interactor.repository.UseCaseIsDoingTransaction;
import com.vfi.android.domain.interactor.repository.UseCaseSaveAutoSettlementResult;
import com.vfi.android.domain.interactor.repository.UseCaseSaveTerminalStatus;
import com.vfi.android.domain.interactor.transaction.UseCaseCheckAndInitTrans;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.AndroidApplication;
import com.vfi.android.payment.presentation.internal.di.components.CommonComponent;
import com.vfi.android.payment.presentation.internal.di.components.DaggerCommonComponent;
import com.vfi.android.payment.presentation.mappers.TransInterceptorResultMapper;
import com.vfi.android.payment.presentation.utils.AndroidUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.ToastUtil;
import com.vfi.android.payment.presentation.view.activities.AutoSettlementActivity;

import javax.inject.Inject;

public class AutoSettlementReceiver extends BroadcastReceiver {
    public static final String TAG = TAGS.AUTO_SETTLEMENT;

    public static final String ACTION_SET_AUTO_SETTLEMENT_TIMER = "com.verifone.payment.action.SET_AUTO_SETTLEMENT_TIMER";
    public static final String ACTION_AUTO_SETTLEMENT = "com.verifone.payment.action.AUTO_SETTLEMENT";
    public static final String ACTION_SET_AUTO_SETTLEMENT_FAILED_TIMER = "com.verifone.payment.action.SET_AUTO_SETTLEMENT_FAILED_TIMER";
    public static final String ACTION_FAILED_RE_SETTLEMENT = "com.verifone.payment.action.FAILED_RE_SETTLEMENT";
    public static final String ACTION_CLEAR_AUTO_SETTLEMENT_FAILED_TIMER = "com.verifone.payment.action.CLEAR_AUTO_SETTLEMENT_FAILED_TIMER";
    public static final String ACTION_CLEAR_AUTO_SETTLEMENT_TIMER = "com.verifone.payment.action.CLEAR_AUTO_SETTLEMENT_TIMER";

    @Inject
    UseCaseIsAllHostsEmptyBatch useCaseIsAllHostsEmptyBatch;
    @Inject
    UseCaseGetTerminalCfg useCaseGetTerminalCfg;
    @Inject
    UseCaseGetTerminalStatus useCaseGetTerminalStatus;
    @Inject
    UseCaseCheckAndInitTrans useCaseCheckAndInitTrans;
    @Inject
    UseCaseSaveAutoSettlementResult useCaseSaveAutoSettlementResult;
    @Inject
    UseCaseIsDoingTransaction useCaseIsDoingTransaction;
    @Inject
    UseCaseSaveTerminalStatus useCaseSaveTerminalStatus;
    @Inject
    Context context;

    public AutoSettlementReceiver() {
        CommonComponent commonComponent;
        commonComponent = DaggerCommonComponent.builder()
                .applicationComponent(AndroidApplication.getInstance().getApplicationComponent())
                .build();

        commonComponent.inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d(TAG, "action=[" + intent.getAction() + "]");
        TerminalCfg terminalCfg = useCaseGetTerminalCfg.execute(null);
        LogUtil.d(TAG, "isEnableAutoSettlement=" + terminalCfg.isEnableAutoSettlement());
        if (!terminalCfg.isEnableAutoSettlement()) {
            return;
        }

        switch (intent.getAction()) {
            case ACTION_AUTO_SETTLEMENT:
                doAutoSettlementProcess();
                break;
            case ACTION_SET_AUTO_SETTLEMENT_FAILED_TIMER:
                setFailedReSettlementTimer();
                break;
            case ACTION_SET_AUTO_SETTLEMENT_TIMER:
                setAutoSettlementTimer(false);
                break;
            case ACTION_FAILED_RE_SETTLEMENT:
                doFailedReSettlementProcess();
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                doBootProcess();
                break;
            case ACTION_CLEAR_AUTO_SETTLEMENT_FAILED_TIMER:
                cancelFailedReSettlementTimer();
                break;
            case ACTION_CLEAR_AUTO_SETTLEMENT_TIMER:
                cancelAutoSettlementTimer();
                break;
            default:
                break;
        }
    }

    private void doAutoSettlementProcess() {
        LogUtil.d(TAG, "doAutoSettlementProcess");
        saveAutoSettleDateYYYYMMDD();

        setAutoSettlementTimer(false);

        checkAndStartSettlement();
    }

    private void doBootProcess() {
        LogUtil.d(TAG, "doBootProcess");
        setAutoSettlementTimer(true);

        TerminalStatus terminalStatus = useCaseGetTerminalStatus.execute(null);
        if (terminalStatus.isAutoSettlementFailed()) {
            setFailedReSettlementTimer();
        }
    }

    private void setFailedReSettlementTimer() {
        long currentTimeMillis = System.currentTimeMillis();
        TerminalCfg terminalCfg = useCaseGetTerminalCfg.execute(null);
        LogUtil.d(TAG, "settleFailedRetryIntervalMins=" + terminalCfg.getSettleFailedRetryIntervalMins());
        long retryIntervalTimeMillis = terminalCfg.getSettleFailedRetryIntervalMins() * 60 * 1000;
        long todayAutoSettleTimeMillis = StringUtil.formatDate2TimeMillis(terminalCfg.getAutoSettlementTime());

        LogUtil.d(TAG, "currentTimeMillis=" + currentTimeMillis);
        LogUtil.d(TAG, "retryIntervalTimeMillis=" + retryIntervalTimeMillis);
        LogUtil.d(TAG, "todayAutoSettleTimeMillis=" + todayAutoSettleTimeMillis);

        long nextRetryTimeMillis = currentTimeMillis + retryIntervalTimeMillis;
        LogUtil.d(TAG, "Next retry time millis=" + nextRetryTimeMillis);

        long triggerAtMillis = nextRetryTimeMillis;
        LogUtil.d(TAG, "set ACTION_FAILED_RE_SETTLEMENT triggerAtMillis=" + triggerAtMillis);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(getPendingIntent(ACTION_FAILED_RE_SETTLEMENT));
        am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, getPendingIntent(ACTION_FAILED_RE_SETTLEMENT));
    }

    private void cancelFailedReSettlementTimer() {
        LogUtil.d(TAG, "cancelFailedReSettlementTimer");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(getPendingIntent(ACTION_FAILED_RE_SETTLEMENT));
    }

    private void cancelAutoSettlementTimer() {
        LogUtil.d(TAG, "cancelAutoSettlementTimer");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(getPendingIntent(ACTION_FAILED_RE_SETTLEMENT));
        am.cancel(getPendingIntent(ACTION_AUTO_SETTLEMENT));
    }

    private void doFailedReSettlementProcess() {
        LogUtil.d(TAG, "doFailedReSettlementProcess");
        checkAndStartSettlement();
    }

    private void checkAndStartSettlement() {
        boolean isAllHostsEmptyBatch = useCaseIsAllHostsEmptyBatch.execute(null);
        boolean isDoingTransaction = useCaseIsDoingTransaction.execute(null);

        if (isAllHostsEmptyBatch) {
            LogUtil.i(TAG, "All Batches are Empty");
            ToastUtil.showToastLong(ResUtil.getString(R.string.toast_hint_auto_settle_no_data));
            useCaseSaveAutoSettlementResult.asyncExecuteWithoutResult(true);
        } else if (isDoingTransaction) {
            LogUtil.d(TAG, "isDoingTransaction now, retry later.");
            setFailedReSettlementTimer();
            useCaseSaveAutoSettlementResult.asyncExecuteWithoutResult(false);
        } else {
            if (doTransCheckAndInit(TransType.SETTLEMENT)) {
                LogUtil.i(TAG, "start auto settlement");
                AndroidUtil.wakeupScreen();
                Intent intentStartActivity = new Intent(context, AutoSettlementActivity.class);
                intentStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentStartActivity);
            } else {
                LogUtil.d(TAG, "Initial settlement transaction failed.");
                setFailedReSettlementTimer();
                useCaseSaveAutoSettlementResult.asyncExecuteWithoutResult(false);
            }
        }
    }

    private void setAutoSettlementTimer(boolean isAfterReboot) {
        LogUtil.d(TAG, "setAutoSettlementTimer isAfterReboot=" + isAfterReboot);
        //1. clear auto settlement timer
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(getPendingIntent(ACTION_AUTO_SETTLEMENT));
        am.cancel(getPendingIntent(ACTION_FAILED_RE_SETTLEMENT));

        //2. set new auto settlement timer
        long currentTimeMillis = System.currentTimeMillis();
        TerminalCfg terminalCfg = useCaseGetTerminalCfg.execute(null);
        long todayAutoSettleTimeMillis = StringUtil.formatDate2TimeMillis(terminalCfg.getAutoSettlementTime());

        LogUtil.d(TAG, "auto settlement time[" + terminalCfg.getAutoSettlementTime() + "]");
        LogUtil.d(TAG, "currentTimeMillis=" + currentTimeMillis);
        LogUtil.d(TAG, "todayAutoSettleTimeMillis=" + todayAutoSettleTimeMillis);

        long triggerAtMillis;
        if (todayAutoSettleTimeMillis < currentTimeMillis) {
            if (isAfterReboot && !isAlreadyAutoSettledToday()) {
                // settlement now
                LogUtil.d(TAG, "Settlement now");
                triggerAtMillis = currentTimeMillis;
            } else {
                LogUtil.d(TAG, "Add one day time millis");
                long oneDaySeconds = 24 * 60 * 60 * 1000; // hour * min * sec * tick
                triggerAtMillis = todayAutoSettleTimeMillis + oneDaySeconds;
            }
        } else {
            triggerAtMillis = todayAutoSettleTimeMillis;
        }

        LogUtil.d(TAG, "set ACTION_AUTO_SETTLEMENT triggerAtMillis=" + triggerAtMillis);
        am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, getPendingIntent(ACTION_AUTO_SETTLEMENT));
    }

    private PendingIntent getPendingIntent(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private boolean doTransCheckAndInit(int transType) {
        boolean bRet = true;

        try {
            int ret = useCaseCheckAndInitTrans.execute(transType);
            if (ret != InterceptorResult.NORMAL) {
                ToastUtil.showToastLong(TransInterceptorResultMapper.toString(ret));
                bRet = false;
            }
        } catch (Exception e) {
            ToastUtil.showToastLong(ResUtil.getString(R.string.toast_hint_init_trans_exception));
            e.printStackTrace();
            bRet = false;
        }

        return bRet;
    }

    private void saveAutoSettleDateYYYYMMDD() {
        useCaseGetTerminalStatus.asyncExecute(null).doOnNext(terminalStatus -> {
            String dateYYYYMMDD = StringUtil.formatDate(System.currentTimeMillis());
            LogUtil.d(TAG, "Last Auto settle time=" + dateYYYYMMDD);
            terminalStatus.setLastAutoSettleDateYYYYMMDD(dateYYYYMMDD);
            useCaseSaveTerminalStatus.asyncExecuteWithoutResult(terminalStatus);
        }).doOnError(throwable -> {
            LogUtil.e(TAG, "Get terminal config failed.");
            throwable.printStackTrace();
        }).subscribe();
    }

    private boolean isAlreadyAutoSettledToday() {
        TerminalStatus terminalStatus = useCaseGetTerminalStatus.execute(null);
        String currentDateYYYYMMDD = StringUtil.formatDate(System.currentTimeMillis());
        LogUtil.d(TAG, "currentDateYYYYMMDD=" + currentDateYYYYMMDD);
        String lastSettleDateYYYYMMDD = terminalStatus.getLastAutoSettleDateYYYYMMDD();
        LogUtil.d(TAG, "lastAutoSettleDateYYYYMMDD=" + lastSettleDateYYYYMMDD);
        long currentDate = StringUtil.parseLong(currentDateYYYYMMDD, -1);
        long lastSettleDate = StringUtil.parseLong(lastSettleDateYYYYMMDD, -1);
        boolean isAlreadyAutoSettledToday = true;
        if (lastSettleDate == -1 || currentDate != lastSettleDate) {
            isAlreadyAutoSettledToday = false;
        }

        LogUtil.d(TAG, "isAlreadyAutoSettledToday=" + isAlreadyAutoSettledToday);
        return isAlreadyAutoSettledToday;
    }

    private void doTest() {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long currentTimeMillis = System.currentTimeMillis();
        long autoTimer = currentTimeMillis + 1 * 60 * 1000;
        long failedTimer = currentTimeMillis + 2 * 60 * 1000;
        am.setExact(AlarmManager.RTC_WAKEUP, autoTimer, getPendingIntent(ACTION_AUTO_SETTLEMENT));
        am.setExact(AlarmManager.RTC_WAKEUP, failedTimer, getPendingIntent(ACTION_FAILED_RE_SETTLEMENT));

        am.cancel(getPendingIntent(ACTION_AUTO_SETTLEMENT));
        am.cancel(getPendingIntent(ACTION_FAILED_RE_SETTLEMENT));

        currentTimeMillis = System.currentTimeMillis();
        autoTimer = currentTimeMillis + 3 * 60 * 1000;
        am.setExact(AlarmManager.RTC_WAKEUP, autoTimer, getPendingIntent(ACTION_AUTO_SETTLEMENT));
    }
}
