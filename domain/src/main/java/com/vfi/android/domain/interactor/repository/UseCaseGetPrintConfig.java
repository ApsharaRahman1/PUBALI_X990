package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.PrintConfig;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetPrintConfig extends UseCase<PrintConfig, Integer> {
    private final String TAG = TAGS.PRINT;
    private final IRepository iRepository;

    @Inject
    public UseCaseGetPrintConfig(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<PrintConfig> buildUseCaseObservable(Integer merchantIndex) {
        return Observable.create(emitter -> {
            PrintConfig printConfig = null;
            LogUtil.d(TAG, "UseCaseGetPrintConfig merchantIndex=" + merchantIndex);
            printConfig = iRepository.getPrintConfig(merchantIndex);

            if (printConfig == null) {
                LogUtil.d(TAG, "No print config found, set all copy to true.");
                printConfig = new PrintConfig();
                printConfig.setPrintBankCopy(true);
                printConfig.setPrintCustomerCopy(true);
                printConfig.setPrintMerchantCopy(true);
            }
            emitter.onNext(printConfig);
            emitter.onComplete();
        });
    }
}
