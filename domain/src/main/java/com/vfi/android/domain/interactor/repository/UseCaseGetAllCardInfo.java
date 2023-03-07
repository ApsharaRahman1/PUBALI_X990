package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetAllCardInfo extends UseCase<List<CardBinInfo>, Void> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetAllCardInfo(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<List<CardBinInfo>> buildUseCaseObservable(Void v) {
        return Observable.just(iRepository.getAllCardInfo());
    }
}
