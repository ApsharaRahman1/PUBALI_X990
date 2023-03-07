package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.PrintConfig;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSavePrintConfig extends UseCase<Boolean, PrintConfig> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSavePrintConfig(ThreadExecutor threadExecutor,
                                  PostExecutionThread postExecutionThread,
                                  IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(PrintConfig printConfig) {
        return Observable.create(emitter -> {
           iRepository.putPrintConfig(printConfig);
           emitter.onNext(true);
           emitter.onComplete();
        });
    }
}
