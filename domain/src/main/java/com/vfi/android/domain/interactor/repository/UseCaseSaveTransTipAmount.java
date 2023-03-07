package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interactor.deviceservice.UseCaseImportCardConfirmResult;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

import javax.inject.Inject;

import io.reactivex.Observable;


/**
 * Created by nzw on 6/11/18
 */
public class UseCaseSaveTransTipAmount extends UseCase<Boolean, String> {

    private final static String TAG = "UseCasePutTradeTipAmount";
    private IRepository iRepository;
    private IPosService posService;

    @Inject
    public UseCaseSaveTransTipAmount(ThreadExecutor threadExecutor,
                                     PostExecutionThread postExecutionThread,
                                     IPosService posService,
                                     IRepository transactionRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository  = transactionRepository;
        this.posService = posService;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(String tipAmount) {
        return Observable.create(emitter -> {
            CurrentTranData currentTranData = iRepository.getCurrentTranData();

            currentTranData.getRecordInfo().setTipAmount(tipAmount);
            int cardEntryMode = currentTranData.getCardInfo().getCardEntryMode();
            if (cardEntryMode == CardEntryMode.IC || cardEntryMode == CardEntryMode.RF) {
                long amount = StringUtil.parseLong(currentTranData.getTransAmount(), 0);
                long tipAmountLong = StringUtil.parseLong(tipAmount, 0);
                long totalAmount = amount + tipAmountLong;
                String tag = "9F0206" + String.format("%012d", totalAmount);
                LogUtil.d("TAG", "tag=[" + totalAmount + "]");
                posService.setEmvTag(tag);
            }

            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
