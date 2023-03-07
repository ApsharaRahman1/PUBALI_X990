package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveCardBinInfo extends UseCase<Boolean,CardBinInfo> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSaveCardBinInfo(ThreadExecutor threadExecutor,
                                  PostExecutionThread postExecutionThread,
                                  IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(CardBinInfo cardBinInfo) {
        return Observable.create(emitter -> {
            iRepository.putCardBinInfo(cardBinInfo);
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
