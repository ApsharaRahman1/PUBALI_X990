package com.vfi.android.domain.interactor.transaction;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.SwitchParameter;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.entities.businessbeans.TransAttribute;
import com.vfi.android.domain.entities.consts.InterceptorResult;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interactor.print.UseCaseClearPrintBuffer;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.memory.GlobalMemoryCache;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseCheckAndInitTrans extends UseCase<Integer, Integer> {
    private final String TAG = TAGS.Transaction;

    private final IRepository iRepository;
    private final IPosService posService;
    private final GlobalMemoryCache globalMemoryCache;
    private final UseCaseClearPrintBuffer useCaseClearPrintBuffer;

    @Inject
    public UseCaseCheckAndInitTrans(ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread,
                                    IRepository iRepository,
                                    GlobalMemoryCache globalMemoryCache,
                                    UseCaseClearPrintBuffer useCaseClearPrintBuffer,
                                    IPosService iPosService) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
        this.posService = iPosService;
        this.globalMemoryCache = globalMemoryCache;
        this.useCaseClearPrintBuffer = useCaseClearPrintBuffer;
    }

    @Override
    public Observable<Integer> buildUseCaseObservable(Integer transType) {
        return Observable.create(emitter -> {
            int interceptorResult = checkTransInterceptor(transType);
            LogUtil.d(TAG, "interceptorResult=" + interceptorResult);

            if (interceptorResult == InterceptorResult.NORMAL) {
                initTransaction(transType);
            } else {
                LogUtil.d(TAG, "Terminal transaction by interceptor[" + interceptorResult + "] failed.");
            }

            emitter.onNext(interceptorResult);
            emitter.onComplete();
        });
    }

    private void initTransaction(int transType) {
        // Reset CurrentTransData
        CurrentTranData currentTranData = new CurrentTranData();

        useCaseClearPrintBuffer.asyncExecuteWithoutResult(null);

        RecordInfo recordInfo = currentTranData.getRecordInfo();
        TransAttribute transAttribute = TransAttribute.findTypeByType(transType);
        recordInfo.setTransType(transType);
        recordInfo.setProcessCode(transAttribute.getProcessCode());

        SwitchParameter switchParameter = iRepository.getSwitchParameter(transType);
        if (switchParameter == null) {
            LogUtil.d(TAG, "Didn't find transaction switch parameter");
            throw new RuntimeException("No switch parameter found");
        }

        currentTranData.setSwitchParameter(switchParameter);

        // init record invoice
        TerminalCfg terminalCfg = iRepository.getTerminalCfg();
        recordInfo.setInvoiceNum(terminalCfg.getSysInvoiceNum());

        iRepository.putCurrentTranData(currentTranData);
    }

    private int checkTransInterceptor(int transType) {
        if (isLowPower()) {
            LogUtil.d(TAG, "Low power.");
            return InterceptorResult.LOW_POWER;
        }

        if (isTransactionDisabled(transType)) {
            LogUtil.d(TAG, "Transaction disabled.");
            return InterceptorResult.TRANS_NOT_SUPPORT;
        }

        if (isForceSettlement(transType)) {
            LogUtil.d(TAG, "Force settlement");
            return InterceptorResult.NEED_SETTLEMENT;
        }

        if (isOverMaximumSettlementCount(transType)) {
            LogUtil.d(TAG, "Over max settlement count");
            return InterceptorResult.OVER_MAX_SETTLEMENT_COUNT;
        }

        if (isOverMaximumSettlementAmount(transType)) {
            LogUtil.d(TAG, "Over max settlement amount");
            return InterceptorResult.OVER_MAX_SETTLEMENT_AMOUNT;
        }

        return InterceptorResult.NORMAL;
    }

    private boolean isLowPower() {
        LogUtil.d(TAG, "isLowPowerStatus=" + globalMemoryCache.isLowPowerStatus());
        LogUtil.d(TAG, "isPowerConnected=" + globalMemoryCache.isPowerConnected());

        if (globalMemoryCache.isLowPowerStatus()
                && !globalMemoryCache.isPowerConnected()) {
            return true;
        }

        return false;
    }

    private boolean isTransactionDisabled(int transType) {
        SwitchParameter switchParameter = iRepository.getSwitchParameter(transType);
        if (switchParameter == null || !switchParameter.isEnableTrans()) {
            LogUtil.e(TAG, "Trans switch disabled.");
            return true;
        }

        if (TransAttribute.findTypeByType(transType) == null) {
            LogUtil.e(TAG, "Trans attribute not found.");
            return true;
        }

        return false;
    }

    private boolean isForceSettlement(int transType) {
        if (transType == TransType.SETTLEMENT) {
            return false;
        }

        TerminalStatus terminalStatus = iRepository.getTerminalStatus();
        if (terminalStatus.isNeedForceSettlement()) {
            if (iRepository.isAllHostEmptyBatch()) {
                terminalStatus.setNeedForceSettlement(false);
                iRepository.putTerminalStatus(terminalStatus);
                return false;
            }

            return true;
        }

        return false;
    }

    private boolean isOverMaximumSettlementCount(int transType) {
        if (transType == TransType.SETTLEMENT) {
            return false;
        }

        // TODO

        return false;
    }

    private boolean isOverMaximumSettlementAmount(int transType) {
        if (transType == TransType.SETTLEMENT) {
            return false;
        }

        // TODO

        return false;
    }
}
