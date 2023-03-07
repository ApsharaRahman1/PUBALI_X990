package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.consts.HostType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.LogUtil;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveHostInfoList extends UseCase<Boolean, List<HostInfo>> {
    private final IRepository iRepository;

    @Inject
    public UseCaseSaveHostInfoList(ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread,
                                   IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(List<HostInfo> hostInfoList) {
        return Observable.create(emitter -> {
            if (hostInfoList != null && hostInfoList.size() > 0) {
                iRepository.clearHostDataTable();
                Iterator<HostInfo> iterator = hostInfoList.iterator();
                while (iterator.hasNext()) {
                    HostInfo hostInfo = iterator.next();
                    if (hostInfo.getHostType() == HostType.UNKNOWN) {
                        LogUtil.d(TAGS.Setting, "Skip save unknown host[" + hostInfo.getHostName() + "]");
                        continue;
                    }
                    iRepository.putHostInfo(hostInfo);
                }
            }
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
