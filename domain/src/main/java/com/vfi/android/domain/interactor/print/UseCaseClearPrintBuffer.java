package com.vfi.android.domain.interactor.print;

import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseClearPrintBuffer extends UseCase<Boolean, Void> {
    private final PrintTaskManager printTaskManager;

    @Inject
    public UseCaseClearPrintBuffer(ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread,
                                   PrintTaskManager printTaskManager) {
        super(threadExecutor, postExecutionThread);
        this.printTaskManager = printTaskManager;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Void aVoid) {
        return Observable.create(emitter -> {
            printTaskManager.clearPrintTasks();
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
