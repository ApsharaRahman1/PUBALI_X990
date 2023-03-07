package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by huan.lu on 2018/10/26.
 */

public class UseCaseIsBatchEmptyByMerchantIndex extends UseCase<Boolean, Integer> {
    private final String TAG = TAGS.Setting;
    private final IRepository iRepository;

    @Inject
    public UseCaseIsBatchEmptyByMerchantIndex(ThreadExecutor threadExecutor,
                                              PostExecutionThread postExecutionThread,
                                              IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Integer merchantIndex) {
        LogUtil.d(TAG, "IsBatchEmptyByMerchantIndex merchantIndex=" + merchantIndex);
        return Observable.create(emitter -> {
            List<HostInfo> hostInfos = iRepository.getAllHostInfos();
            for (HostInfo hostInfo : hostInfos) {
                String[] merchantIndexList = StringUtil.getNonNullString(hostInfo.getMerchantIndexs()).split(",");
                for (int i = 0; i < merchantIndexList.length; i++) {
                    int mIndex = StringUtil.parseInt(merchantIndexList[i], -1);
                    if (mIndex != -1 && mIndex == merchantIndex) {
                        if (!iRepository.isEmptyBatch(hostInfo.getHostType())) {
                            LogUtil.d(TAG, "isEmptyBatch=false");
                            emitter.onNext(false);
                            emitter.onComplete();
                            return;
                        }
                    }
                }
            }

            LogUtil.d(TAG, "isEmptyBatch=true");
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
