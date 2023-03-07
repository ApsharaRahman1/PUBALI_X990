package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.PrintConfig;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetAllPrintConfig extends UseCase<List<PrintConfig>, Void> {
    private final IRepository iRepository;

    @Inject
    public UseCaseGetAllPrintConfig(ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread,
                                    IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<List<PrintConfig>> buildUseCaseObservable(Void avoid) {
        return Observable.create(emitter -> {
            emitter.onNext(iRepository.getAllPrintConfigs());
            emitter.onComplete();
        });
    }
}
