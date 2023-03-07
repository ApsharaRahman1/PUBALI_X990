package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.InstallmentPromo;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveInstallmentPromoList extends UseCase<Boolean, List<InstallmentPromo>> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSaveInstallmentPromoList(ThreadExecutor threadExecutor,
                                           PostExecutionThread postExecutionThread,
                                           IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(List<InstallmentPromo> installmentPromoList) {
        return Observable.create(emitter -> {
            if (installmentPromoList != null && installmentPromoList.size() > 0) {
                iRepository.clearInstallmentPromoTable();

                for (InstallmentPromo installmentPromo : installmentPromoList) {
                    iRepository.putInstallmentPromo(installmentPromo);
                }
            }
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
