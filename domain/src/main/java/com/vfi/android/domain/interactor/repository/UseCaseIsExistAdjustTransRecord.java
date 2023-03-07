package com.vfi.android.domain.interactor.repository;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.HostType;
import com.vfi.android.domain.entities.consts.NotAllowError;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.LogUtil;

import javax.inject.Inject;

import io.reactivex.Observable;


/**
 * Created by fusheng.z on 2018/04/08.
 */

public class UseCaseIsExistAdjustTransRecord extends UseCase<Boolean, String> {
    private final String TAG = TAGS.Transaction;

    private final IRepository iRepository;

    @Inject
    UseCaseIsExistAdjustTransRecord(IRepository iRepository,
                                    ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread,
                                    UseCaseGetCardBinInfoByIndex useCaseGetCardBinInfoByIndex,
                                    UseCaseGetHostInfo useCaseGetHostInfo,
                                    UseCaseGetMerchantInfo useCaseGetMerchantInfo) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;

    }


    @Override
    public Observable<Boolean> buildUseCaseObservable(String invoiceNum) {

        return Observable.create(emitter -> {
            CurrentTranData currentTranData = iRepository.getCurrentTranData();
            RecordInfo recordInfo = iRepository.getRecordInfoByInvoice(invoiceNum);
            if (recordInfo == null) {
                LogUtil.e(TAG, "InvoiceNum=" + invoiceNum + " record not found");
                emitter.onNext(false);
                emitter.onComplete();
                return;
            }

            // check org trans type
            int recordTransType = recordInfo.getTransType();
            boolean isSaleRelatedTrans = (recordTransType == TransType.SALE || (recordTransType == TransType.TIP_ADJUST && recordInfo.getTipAdjOrgTransType() == TransType.SALE));
            boolean isOfflineRelatedTrans = (recordTransType == TransType.OFFLINE || (recordTransType == TransType.TIP_ADJUST && recordInfo.getTipAdjOrgTransType() == TransType.OFFLINE));
            if (!isSaleRelatedTrans
                    && !isOfflineRelatedTrans) {
                LogUtil.e(TAG, "TransType[" + recordTransType + "] not allow do tip adjust transaction.");
                emitter.onError(new CommonException(ExceptionType.TRANS_NOT_ALLOW, NotAllowError.ORIGIN_TRANS_NOT_ALLOW_ADJUST));
                return;
            }

            CardBinInfo cardBinInfo = iRepository.getCardBinInfoByIndex(recordInfo.getCardBinIndex());
            if (cardBinInfo == null) {
                LogUtil.e(TAG, "Do tipAdjust transaction, card bin not found.");
                emitter.onError(new CommonException(ExceptionType.TRANS_NOT_ALLOW, NotAllowError.ORIGIN_TRANS_HOST_NOT_FOUND));
                return;
            }
            currentTranData.setCardBinInfo(cardBinInfo);

            // check host
            HostInfo hostInfo = iRepository.getHostInfoByHostType(recordInfo.getHostType());
            //Note: Only CUP and LOCAL Host are allow to have TIPS Adjust
            if(hostInfo.getHostType() != HostType.CUP && hostInfo.getHostType() != HostType.LOCAL){
                LogUtil.e(TAG, "HostType[" + hostInfo.getHostType() + "] not allow do tip adjust transaction.");
                emitter.onError(new CommonException(ExceptionType.TRANS_NOT_ALLOW, NotAllowError.ORIGIN_TRANS_NOT_ALLOW_ADJUST));
                return;
            }
            currentTranData.setHostInfo(hostInfo);

            TerminalCfg terminalCfg = iRepository.getTerminalCfg();
            // check if tipAdjust times > max adjust times.
            int maxTipAdjTimes = terminalCfg.getMaxAdjustTimes();
            LogUtil.d(TAG, "current tip times=" + recordInfo.getTipAdjustTimes() + " maxTipAdjTimes=" + maxTipAdjTimes);
            if (maxTipAdjTimes > 0 && recordInfo.getTipAdjustTimes() >= maxTipAdjTimes) {
                //Exceed Adjust max
                LogUtil.e("Exceed Adjust max times");
                emitter.onError(new CommonException(ExceptionType.TRANS_NOT_ALLOW, NotAllowError.EXCEED_MAX_ADJUST_TIME));
                return;
            }

            MerchantInfo merchantInfo = iRepository.getMerchantInfo(recordInfo.getMerchantIndex());
            if (merchantInfo == null) {
                emitter.onError(new CommonException(ExceptionType.TRANS_NOT_ALLOW, NotAllowError.ORIGIN_TRANS_MERCHANT_NOT_FOUND));
                return;
            }
            currentTranData.setMerchantInfo(merchantInfo);

            // Save and reset record transType.
            if (recordInfo.getTransType() != TransType.TIP_ADJUST) {
                recordInfo.setTipAdjOrgTransType(recordInfo.getTransType());
            }
            recordInfo.setTransType(TransType.TIP_ADJUST);
            recordInfo.setTipAdjOrgTraceNum(recordInfo.getTraceNum());
            recordInfo.setTraceNum(merchantInfo.getTraceNum());
            currentTranData.setRecordInfo(recordInfo);

            emitter.onNext(true);
            emitter.onComplete();
        });
    }
}
