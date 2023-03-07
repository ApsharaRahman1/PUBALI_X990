package com.vfi.android.domain.interactor.transaction;

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interactor.transaction.base.ITransaction;
import com.vfi.android.domain.interactor.transaction.tle.trans.ReversalTransaction;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseDoPostTrans extends UseCase<Boolean, Void> {
    private final String TAG = TAGS.Transaction;
    private IRepository iRepository;
    private IPosService iPosService;
    private List<ITransaction> postTransList;
    private UseCaseStartTransCommunication useCaseStartTransCommunication;

    @Inject
    public UseCaseDoPostTrans(ThreadExecutor threadExecutor,
                              PostExecutionThread postExecutionThread,
                              UseCaseStartTransCommunication useCaseStartTransCommunication,
                              IRepository iRepository,
                              IPosService iPosService) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
        this.iPosService = iPosService;
        this.useCaseStartTransCommunication = useCaseStartTransCommunication;
        postTransList = new ArrayList<>();
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Void aVoid) {
        return Observable.create(emitter -> {
            addReversalTrans();

            TerminalCfg terminalCfg = iRepository.getTerminalCfg();
            LogUtil.d(TAG, "Training mode enable status=[" + terminalCfg.isEnableTrainingMode() + "]");
            if (terminalCfg.isEnableTrainingMode()) {
                emitter.onNext(true);
                emitter.onComplete();
                return;
            }

            LogUtil.d(TAG, "postTransList size=" + postTransList.size());
            if (postTransList.size() > 0) {
                Observable.fromIterable(postTransList).concatMap(iTransaction -> {
                    return useCaseStartTransCommunication.buildUseCaseObservable(iTransaction);
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

    private void addReversalTrans() {
        List<RecordInfo> recordInfos = iRepository.getReversalRecords();
        if (recordInfos != null) {
            LogUtil.d(TAG, "Reversal record size=" + recordInfos.size());
            for (RecordInfo record: recordInfos) {
                postTransList.add(new ReversalTransaction(iRepository, iPosService, record));
            }
        }
    }
}
