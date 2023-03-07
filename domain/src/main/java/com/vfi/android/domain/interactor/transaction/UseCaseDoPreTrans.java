package com.vfi.android.domain.interactor.transaction;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.entities.comm.CommStatus;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interactor.transaction.base.ITransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.OfflineSaleUploadTransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.ReversalTransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.TCUploadTransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.TipAdjustUploadTransaction;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseDoPreTrans extends UseCase<Boolean, Void> {
    private final String TAG = TAGS.Transaction;

    private final IRepository iRepository;
    private final IPosService iPosService;
    private final UseCaseStartTransCommunication useCaseStartTransCommunication;

    private List<ITransaction> preTransList;
    private boolean isFirstCheck;

    @Inject
    public UseCaseDoPreTrans(ThreadExecutor threadExecutor,
                             PostExecutionThread postExecutionThread,
                             UseCaseStartTransCommunication useCaseStartTransCommunication,
                             IPosService iPosService,
                             IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
        this.iPosService = iPosService;
        this.useCaseStartTransCommunication = useCaseStartTransCommunication;
        preTransList = new ArrayList<>();
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Void aVoid) {
        LogUtil.d(TAG, "UseCaseDoPreTrans");

        return Observable.create(emitter -> {
            CurrentTranData currentTranData = iRepository.getCurrentTranData();
            int transType = currentTranData.getTransType();

            TerminalCfg terminalCfg = iRepository.getTerminalCfg();
            LogUtil.d(TAG, "Training mode enable status=[" + terminalCfg.isEnableTrainingMode() + "]");
            if (terminalCfg.isEnableTrainingMode()) {
                emitter.onNext(true);
                emitter.onComplete();
                return;
            }

            if (transType == TransType.SETTLEMENT) {
                isFirstCheck = true;
                addSettlementPreTrans();
            } else {
                addCommonPreTrans();
            }

            LogUtil.d(TAG, "preTransList size=" + preTransList.size());
            if (preTransList.size() > 0) {
                Observable.fromIterable(preTransList).concatMap(iTransaction -> {
                    return useCaseStartTransCommunication.buildUseCaseObservable(iTransaction);
                }).doOnNext(commStatus -> {
                    if (transType == TransType.SETTLEMENT && isFirstCheck && commStatus == CommStatus.SENDING) {
                        isFirstCheck = false;
                        markForceSettlementFlag();
                    }
                }).doOnError(throwable -> {
                    LogUtil.d(TAG, "PreTrans error.");
                    emitter.onError(throwable);
                }).doOnComplete(() -> {
                    emitter.onNext(true);
                    emitter.onComplete();
                }).subscribe();
            } else {
                emitter.onNext(true);
                emitter.onComplete();
            }
        });
    }

    private void markForceSettlementFlag() {
        TerminalStatus terminalStatus = iRepository.getTerminalStatus();
        terminalStatus.setNeedForceSettlement(true);
        LogUtil.d(TAG, "Mark force settlement flag true");
        iRepository.putTerminalStatus(terminalStatus);
    }

    private void addSettlementPreTrans() {
        addReversalTrans();
        addOfflineUploadTrans();
        addTipAdjustUploadTrans();
        addTCUploadTrans();
    }

    private void addCommonPreTrans() {
        addReversalTrans();
    }

    private void addReversalTrans() {
        List<RecordInfo> recordInfos = iRepository.getReversalRecords();
        if (recordInfos != null) {
            LogUtil.d(TAG, "Reversal record size=" + recordInfos.size());
            for (RecordInfo record: recordInfos) {
                preTransList.add(new ReversalTransaction(iRepository, iPosService, record));
            }
        }
    }

    private void addTCUploadTrans() {
        List<RecordInfo> recordInfos = iRepository.getUnUploadTCRecords();
        if (recordInfos != null) {
            for (RecordInfo record: recordInfos) {
                preTransList.add(new TCUploadTransaction(iRepository, iPosService, record));
            }
        }
    }

    private void addOfflineUploadTrans() {
        List<RecordInfo> recordInfos = iRepository.getUnUploadOfflineRecords();
        if (recordInfos != null) {
            for (RecordInfo record: recordInfos) {
                preTransList.add(new OfflineSaleUploadTransaction(iRepository, iPosService, record));
            }
        }
    }

    private void addTipAdjustUploadTrans() {
        List<RecordInfo> recordInfos = iRepository.getUnUploadTipAdjustRecords();
        if (recordInfos != null) {
            for (RecordInfo record: recordInfos) {
                preTransList.add(new TipAdjustUploadTransaction(iRepository, iPosService, record));
            }
        }
    }
}
