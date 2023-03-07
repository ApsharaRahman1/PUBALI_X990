package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.InstallmentPromo;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveInstallmentPromo extends UseCase<Boolean, InstallmentPromo> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSaveInstallmentPromo(ThreadExecutor threadExecutor,
                                       PostExecutionThread postExecutionThread,
                                       IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(InstallmentPromo installmentPromo) {
        return Observable.create(emitter -> {
            iRepository.putInstallmentPromo(installmentPromo);
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
