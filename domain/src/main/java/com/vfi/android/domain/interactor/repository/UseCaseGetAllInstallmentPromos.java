package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.InstallmentPromo;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetAllInstallmentPromos extends UseCase<List<InstallmentPromo>, Boolean> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetAllInstallmentPromos(ThreadExecutor threadExecutor,
                                          PostExecutionThread postExecutionThread,
                                          IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<List<InstallmentPromo>> buildUseCaseObservable(Boolean isOnlyEnabled) {
        return Observable.create(emitter -> {
            emitter.onNext(iRepository.getAllEnabledInstalmentPromo(isOnlyEnabled));
            emitter.onComplete();
        });
    }
}
