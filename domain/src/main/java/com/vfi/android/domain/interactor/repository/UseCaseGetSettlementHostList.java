package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.consts.HostType;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseGetSettlementHostList extends UseCase<List<Integer>, Void> {
    private IRepository iRepository;

    @Inject
    public UseCaseGetSettlementHostList(ThreadExecutor threadExecutor,
                                        PostExecutionThread postExecutionThread,
                                        IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<List<Integer>> buildUseCaseObservable(Void aVoid) {
        List<Integer> hostList = iRepository.getHostTypeList();
        if (hostList.size() == 0) {
            hostList.add(HostType.LOCAL);
            hostList.add(HostType.CUP);
            hostList.add(HostType.DEBIT);
            hostList.add(HostType.INSTALLMENT);
            hostList.add(HostType.LOYALTY);
        }

        return Observable.just(hostList);
    }
}
