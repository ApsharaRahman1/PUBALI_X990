package com.vfi.android.domain.interactor.deviceservice;

import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by yunlongg1 on 15/11/2017.*/

public class UseCaseCheckCardStop extends UseCase {

    private final IPosService posService;
    private final IRepository iRepository;

    @Inject
    UseCaseCheckCardStop(ThreadExecutor threadExecutor,
                         IPosService posService,
                         IRepository iRepository,
                         PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.posService = posService;
        this.iRepository = iRepository;
    }

    @Override
    public Observable buildUseCaseObservable(Object unused) {
        iRepository.lockCheckCardLock();
        return Observable.create(emitter -> {
            try {
                posService.stopCheckCard().blockingSingle();
            } finally {
                iRepository.unlockCheckCardLock();
            }
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}







