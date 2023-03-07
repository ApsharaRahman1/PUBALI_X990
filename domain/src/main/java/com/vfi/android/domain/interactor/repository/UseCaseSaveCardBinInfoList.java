package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveCardBinInfoList extends UseCase<Boolean, List<CardBinInfo>> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSaveCardBinInfoList(ThreadExecutor threadExecutor,
                                      PostExecutionThread postExecutionThread,
                                      IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(List<CardBinInfo> cardBinInfoList) {
        return Observable.create(emitter -> {
            if (cardBinInfoList != null && cardBinInfoList.size() > 0) {
                iRepository.clearCardBinTable();

                for (CardBinInfo cardBinInfo : cardBinInfoList) {
                    iRepository.putCardBinInfo(cardBinInfo);
                }
            }
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
