package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseIsAllowPinBypass extends UseCase<Boolean, Void> {
    private final String TAG = TAGS.INPUT_PIN;
    private final IRepository iRepository;

    @Inject
    public UseCaseIsAllowPinBypass(ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread,
                                   IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Void aVoid) {
        return Observable.create(emitter -> {
            CurrentTranData currentTranData = iRepository.getCurrentTranData();
            CardBinInfo cardBinInfo = currentTranData.getCardBinInfo();
            boolean isAllowPinBypass = false;
            if (cardBinInfo != null && cardBinInfo.isAllowPinBypass()) {
                isAllowPinBypass = true;
            }

            LogUtil.d(TAG, "isAllowPinBypass=" + isAllowPinBypass);
            emitter.onNext(isAllowPinBypass);
            emitter.onComplete();
        });
    }
}
