package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.CardInformation;
import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseSaveManualCardNum extends UseCase<Boolean, String> {
    private final static String TAG = "UseCaseSaveManualCardNum";

    private final IRepository iRepository;
    private final CurrentTranData currentTranData;

    @Inject
    UseCaseSaveManualCardNum(IRepository iRepository,
                             ThreadExecutor threadExecutor,
                             PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
        this.currentTranData = iRepository.getCurrentTranData();
    }


    @Override
    public Observable<Boolean> buildUseCaseObservable(String cardNum) {
        return Observable.create(emitter -> {
            LogUtil.d(TAG, "Manual input card number[" + cardNum + "]");
            CardInformation cardInfo = currentTranData.getCardInfo();
            cardInfo.setCardEntryMode(CardEntryMode.MANUAL);
            cardInfo.setPan(cardNum);
            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
