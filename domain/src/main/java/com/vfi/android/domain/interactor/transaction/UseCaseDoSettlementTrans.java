package com.vfi.android.domain.interactor.transaction;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.entities.comm.CommStatus;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.HostType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.SettlementRecord;
import com.vfi.android.domain.entities.databeans.SettlementRecordItem;
import com.vfi.android.domain.entities.databeans.SettlementStatus;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interactor.transaction.base.ITransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.BatchUploadTransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.SettlementTrailerTransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.SettlementTransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.SimulateSettlementTransaction;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class UseCaseDoSettlementTrans extends UseCase<SettlementStatus, Void> {
    private final String TAG = TAGS.Settlement;
    private final IRepository iRepository;
    private final IPosService iPosService;
    private final CurrentTranData currentTranData;
    private final UseCaseStartTransCommunication useCaseStartTransCommunication;

    private boolean isFirstCheck;

    @Inject
    public UseCaseDoSettlementTrans(IRepository iRepository,
                                    IPosService iPosService,
                                    UseCaseStartTransCommunication useCaseStartTransCommunication,
                                    ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        currentTranData = iRepository.getCurrentTranData();
        this.iRepository = iRepository;
        this.iPosService = iPosService;
        this.useCaseStartTransCommunication = useCaseStartTransCommunication;
    }

    @Override
    public Observable<SettlementStatus> buildUseCaseObservable(Void aVoid) {
        return Observable.create(emitter -> {
            isFirstCheck = true;
            SettlementRecord settlementRecord = currentTranData.getCurrentSettlementRecord();
            List<ITransaction> settlementTransList = new ArrayList<>();
            Iterator<SettlementRecordItem> iterator = settlementRecord.getSettlementRecordItems().iterator();
            while (iterator.hasNext()) {
                SettlementRecordItem recordItem = iterator.next();
                if (!recordItem.isNeedSettlement()) {
                    continue;
                }

                switch (recordItem.getHostType()) {
                    case HostType.CUP:
                    case HostType.LOCAL:
                    case HostType.DEBIT:
                    case HostType.INSTALLMENT:
                    case HostType.LOYALTY:
                        LogUtil.d(TAG, "Add settlement trans, host[" + HostType.toDebugString(recordItem.getHostType()) + "]");
                        if (isEnabledTrainingMode()) {
                            settlementTransList.add(new SimulateSettlementTransaction(iRepository, iPosService, recordItem));
                        } else {
                            settlementTransList.add(new SettlementTransaction(iRepository, iPosService, recordItem));
                        }
                        break;
                }
            }

            if (settlementTransList.size() <= 0) {
                emitter.onComplete();
                return;
            }

            // start do settlement process
            Observable.fromIterable(settlementTransList).concatMap(iTransaction -> {
                if (iTransaction instanceof SettlementTransaction) {
                    SettlementTransaction transaction = (SettlementTransaction) iTransaction;
                    return doSettlementTransaction(transaction);
                } else if (iTransaction instanceof SimulateSettlementTransaction) {
                    SimulateSettlementTransaction transaction = (SimulateSettlementTransaction) iTransaction;
                    return doSimulateSettleTransaction(transaction);
                } else {
                    // other settlement need change here
                    return Observable.just(new SettlementStatus(-1, -1, -1));
                }
            }).doOnNext(settlementStatus -> {
                LogUtil.d(TAG, "===>HostType[" + settlementStatus.toDebugHostString() + "] status is " + settlementStatus.toDebugStatusString());
                emitter.onNext(settlementStatus);
            }).doOnError(throwable -> {
                LogUtil.d(TAG, "Settlement error.");
                emitter.onError(throwable);
            }).doOnComplete(() -> {
                if (settlementRecord.isAllSettlementSuccess()) {
                    markForceSettlementFlag(false);
                }
                emitter.onComplete();
            }).subscribe();
        });
    }

    private boolean isEnabledTrainingMode() {
        TerminalCfg terminalCfg = iRepository.getTerminalCfg();
        LogUtil.d(TAG, "Training mode enable status=[" + terminalCfg.isEnableTrainingMode() + "]");
        return terminalCfg.isEnableTrainingMode();
    }

    private Observable<SettlementStatus> doSettlementTransaction(SettlementTransaction settlementTransaction) {
        final SettlementRecordItem settlementRecordItem = settlementTransaction.getSettlementRecordItem();
        return Observable.create(emitter -> {
            notifySettleStatus(emitter, settlementRecordItem, SettlementStatus.START);
            useCaseStartTransCommunication.buildUseCaseObservable(settlementTransaction).doOnError(throwable -> {
                if (throwable instanceof CommonException) {
                    CommonException commonException = (CommonException) throwable;
                    int exceptionType = commonException.getExceptionType();
                    LogUtil.d(TAG, "exceptionType=" + exceptionType);
                    if (exceptionType == ExceptionType.SETTLEMENT_NEED_BATCH_UPLOAD) {
                        notifySettleStatus(emitter, settlementRecordItem, SettlementStatus.BATCH_UPLOAD);
                        doBatchUploadProcess(emitter, settlementTransaction.getSettlementRecordItem());
                        emitter.onComplete();
                        return;
                    }
                } else {
                    throwable.printStackTrace();
                }
                LogUtil.d(TAG, "Settlement host[" + HostType.toDebugString(settlementTransaction.getHostType()) + "] failed.");
                notifySettleStatus(emitter, settlementRecordItem, SettlementStatus.FAILED);
                emitter.onComplete();
            }).doOnNext(commStatus -> {
                if (isFirstCheck && commStatus == CommStatus.SENDING) {
                    isFirstCheck = false;
                    markForceSettlementFlag(true);
                }
            }).doOnComplete(() -> {
                LogUtil.d(TAG, "Settlement host[" + HostType.toDebugString(settlementTransaction.getHostType()) + "] success.");
                if (settlementRecordItem.isSettlementSuccess()) {
                    notifySettleStatus(emitter, settlementRecordItem, SettlementStatus.SUCCESS);
                } else {
                    notifySettleStatus(emitter, settlementRecordItem, SettlementStatus.FAILED);
                }
                emitter.onComplete();
            }).subscribe();
        });
    }

    private void markForceSettlementFlag(boolean isNeedForceSettlement) {
        TerminalStatus terminalStatus = iRepository.getTerminalStatus();
        terminalStatus.setNeedForceSettlement(isNeedForceSettlement);
        LogUtil.d(TAG, "Mark force settlement flag " + isNeedForceSettlement);
        if (!isNeedForceSettlement) {
            terminalStatus.setAutoSettlementFailed(false);
        }
        iRepository.putTerminalStatus(terminalStatus);
    }

    private Observable<SettlementStatus> doSimulateSettleTransaction(SimulateSettlementTransaction settlementTransaction) {
        final SettlementRecordItem settlementRecordItem = settlementTransaction.getSettlementRecordItem();
        return Observable.create(emitter -> {
            notifySettleStatus(emitter, settlementRecordItem, SettlementStatus.START);
            useCaseStartTransCommunication.buildUseCaseObservable(settlementTransaction).doOnError(throwable -> {
                if (throwable instanceof CommonException) {
                    CommonException commonException = (CommonException) throwable;
                    int exceptionType = commonException.getExceptionType();
                    LogUtil.d(TAG, "exceptionType=" + exceptionType);
                    if (exceptionType == ExceptionType.SETTLEMENT_NEED_BATCH_UPLOAD) {
                        notifySettleStatus(emitter, settlementRecordItem, SettlementStatus.BATCH_UPLOAD);
                        doBatchUploadProcess(emitter, settlementTransaction.getSettlementRecordItem());
                        emitter.onComplete();
                        return;
                    }
                } else {
                    throwable.printStackTrace();
                }
                LogUtil.d(TAG, "Settlement host[" + HostType.toDebugString(settlementTransaction.getHostType()) + "] failed.");
                notifySettleStatus(emitter, settlementRecordItem, SettlementStatus.FAILED);
                emitter.onComplete();
            }).doOnComplete(() -> {
                LogUtil.d(TAG, "Settlement host[" + HostType.toDebugString(settlementTransaction.getHostType()) + "] success.");
                if (settlementRecordItem.isSettlementSuccess()) {
                    notifySettleStatus(emitter, settlementRecordItem, SettlementStatus.SUCCESS);
                } else {
                    notifySettleStatus(emitter, settlementRecordItem, SettlementStatus.FAILED);
                }
                emitter.onComplete();
            }).subscribe();
        });
    }

    private void doBatchUploadProcess(ObservableEmitter<SettlementStatus> emitter, SettlementRecordItem settlementRecordItem) {
        int hostType = settlementRecordItem.getHostType();
        int merchantIndex = settlementRecordItem.getMerchantIndex();
        List<RecordInfo> batchUpRecords = iRepository.getBatchUploadRecords(hostType, merchantIndex);
        List<ITransaction> batchUploadTransList = new ArrayList<>();
        Iterator<RecordInfo> iterator = batchUpRecords.iterator();
        while (iterator.hasNext()) {
            RecordInfo recordInfo = iterator.next();
            boolean isLastUploadRecord = !iterator.hasNext();
            batchUploadTransList.add(new BatchUploadTransaction(iRepository, iPosService, recordInfo, isLastUploadRecord));
        }

        batchUploadTransList.add(new SettlementTrailerTransaction(iRepository, iPosService, settlementRecordItem));

        Observable.fromIterable(batchUploadTransList).concatMap(iTransaction -> {
            return useCaseStartTransCommunication.buildUseCaseObservable(iTransaction);
        }).doOnError(throwable1 -> {
            LogUtil.d(TAG, "Settlement batch upload failed.");
            notifySettleStatus(emitter, settlementRecordItem, SettlementStatus.FAILED);
            emitter.onComplete();
        }).doOnComplete(() -> {
            LogUtil.d(TAG, "Settlement batch upload success.");
            if (settlementRecordItem.isSettlementSuccess()) {
                notifySettleStatus(emitter, settlementRecordItem, SettlementStatus.SUCCESS);
            } else {
                notifySettleStatus(emitter, settlementRecordItem, SettlementStatus.FAILED);
            }
            emitter.onComplete();
        }).subscribe();
    }

    private void notifySettleStatus(ObservableEmitter<SettlementStatus> emitter, SettlementRecordItem settlementRecordItem, int status) {
        emitter.onNext(new SettlementStatus(settlementRecordItem.getHostType(), settlementRecordItem.getMerchantIndex(), status));
    }
}
