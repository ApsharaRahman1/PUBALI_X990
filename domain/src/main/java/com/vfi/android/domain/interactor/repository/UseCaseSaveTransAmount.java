package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveTransAmount extends UseCase<Boolean, String> {
    private final IRepository iRepository;
    private final IPosService posService;

    @Inject
    public UseCaseSaveTransAmount(ThreadExecutor threadExecutor,
                                  PostExecutionThread postExecutionThread,
                                  IPosService posService,
                                  IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
        this.posService = posService;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(String amount) {
        return Observable.create(emitter -> {
            CurrentTranData currentTranData = iRepository.getCurrentTranData();
            currentTranData.getRecordInfo().setAmount(amount);
            int cardEntryMode = currentTranData.getCardInfo().getCardEntryMode();
            if (cardEntryMode == CardEntryMode.IC || cardEntryMode == CardEntryMode.RF) {
                long totalAmount = StringUtil.parseLong(amount, 0);
                String tag = "9F0206" + String.format("%012d", totalAmount);
                LogUtil.d("TAG", "tag=[" + totalAmount + "]");
                posService.setEmvTag(tag);
            }
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
