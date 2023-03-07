package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetCardBinInfoByIndex extends UseCase<CardBinInfo, String> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetCardBinInfoByIndex(ThreadExecutor threadExecutor,
                                        PostExecutionThread postExecutionThread,
                                        IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<CardBinInfo> buildUseCaseObservable(String cardBinIndex) {
        return Observable.create(emitter -> {
            CardBinInfo cardBinInfo = iRepository.getCardBinInfoByIndex(Integer.valueOf(cardBinIndex));
            if (cardBinInfo == null) {
                emitter.onError(new CommonException(ExceptionType.GET_CARD_BIN_FAILED));
            }else{

                emitter.onNext(cardBinInfo);
                emitter.onComplete();
            }

        });
    }
}
