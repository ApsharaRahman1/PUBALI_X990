package com.vfi.android.domain.interactor.repository;

import android.text.TextUtils;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.HostType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.databeans.HostMerchantItem;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.executor.PostExecutionThread;
import com.vfi.android.domain.executor.ThreadExecutor;
import com.vfi.android.domain.interactor.UseCase;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;

public class UseCaseDoCardBinRouting extends UseCase<List<HostMerchantItem>, Void> {
    private final String TAG = TAGS.BIN_ROUTING;
    private final IRepository iRepository;

    @Inject
    public UseCaseDoCardBinRouting(ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread,
                                   IRepository iRepository) {
        super(threadExecutor, postExecutionThread);
        this.iRepository = iRepository;
    }

    @Override
    public Observable<List<HostMerchantItem>> buildUseCaseObservable(Void aVoid) {
        return Observable.create(emitter -> {
            LogUtil.d(TAG, "=============Start do card bin routing logical==================");

            List<HostMerchantItem> hostMerchantItems = new ArrayList<>();

            CurrentTranData currentTranData = iRepository.getCurrentTranData();
            List<CardBinInfo> cardBinInfoList = iRepository.getCardInfos(currentTranData.getCardInfo().getPan());
            LogUtil.d(TAG, "card bin list size=" + cardBinInfoList.size());
            if (cardBinInfoList.size() == 0) {
                addDebugInfo(cardBinInfoList);
                emitter.onError(new CommonException(ExceptionType.GET_CARD_BIN_FAILED));
                return;
            } else if (cardBinInfoList.size() == 1) {
                CardBinInfo cardBinInfo = cardBinInfoList.get(0);
                currentTranData.setCardBinInfo(cardBinInfo);
                LogUtil.d(TAG, "hostIndexs=" + cardBinInfo.getHostIndexs());
                addSelectHostMerchantItem(hostMerchantItems, cardBinInfo);
            } else {
                if (isWrongCardBinConfig(cardBinInfoList)) {
                    addDebugInfo(cardBinInfoList);
                    emitter.onError(new CommonException(ExceptionType.GET_CARD_BIN_FAILED));
                    return;
                }

                for (CardBinInfo cardBinInfo : cardBinInfoList) {
                    addSelectHostMerchantItem(hostMerchantItems, cardBinInfo);
                }
            }

            if (hostMerchantItems.size() == 0) {
                LogUtil.e(TAG, "BIN Routing failed, no host merchant found.");
                emitter.onError(new CommonException(ExceptionType.GET_HOST_INFO_FAILED));
                return;
            }

            LogUtil.d(TAG, "=============End do card bin routing logical==================");

            emitter.onNext(hostMerchantItems);
            emitter.onComplete();
        });
    }

    private boolean isWrongCardBinConfig(List<CardBinInfo> cardBinInfoList) {
        Map<Integer, Integer> map = new HashMap<>(); // hostIndex, cardType

        LogUtil.d(TAG, "   ======Multi Card bin====");
        for (CardBinInfo cardBinInfo : cardBinInfoList) {
            if (StringUtil.getNonNullString(cardBinInfo.getHostIndexs()).length() == 0) {
                continue;
            }

            String[] hostIndexs = cardBinInfo.getHostIndexs().split(",");
            for (int i = 0; i < hostIndexs.length; i++) {
                int hostIndex = StringUtil.parseInt(hostIndexs[i], -1);
                if (hostIndex == -1) {
                    continue;
                }

                if (map.containsKey(hostIndex)) {
                    LogUtil.d(TAG, "Wrong config, First hostIndex=" + hostIndex + " cardType=" + map.get(hostIndex) + " Second cardType=" + cardBinInfo.getCardType());
                    return true;
                } else {
                    map.put(hostIndex, cardBinInfo.getCardType());
                }
            }
        }

        return false;
    }

    private void addSelectHostMerchantItem(List<HostMerchantItem> hostMerchantItems, CardBinInfo cardBinInfo) {
        if (hostMerchantItems == null || cardBinInfo == null) {
            return;
        }
        if (TextUtils.isEmpty(cardBinInfo.getHostIndexs())) {
            LogUtil.e(TAG, "No host info found");
            LogUtil.d(TAG, "CardBinInfo[" + cardBinInfo.getCardName() + "] panLow=" + cardBinInfo.getPanLow() + " panHigh=" + cardBinInfo.getPanHigh());
            return;
        }

        String[] hostIndexs = cardBinInfo.getHostIndexs().split(",");
        for (int i = 0; i < hostIndexs.length; i++) {
            int hostIndex = StringUtil.parseInt(hostIndexs[i], -1);
            if (hostIndex == -1) {
                LogUtil.d(TAG, "Wrong host index config[" + hostIndexs[i] + "]");
                continue;
            }
            HostInfo hostInfo = iRepository.getHostInfo(hostIndex);
            if (hostInfo == null) {
                LogUtil.d(TAG, "Host Index[" + hostIndex + "] not found.");
                continue;
            }

            if (!checkIsTransRelatedHost(hostInfo)) {
                LogUtil.d(TAG, "Remove host type[" + HostType.toDebugString(hostInfo.getHostType()) + "]");
                continue;
            }

            LogUtil.d(TAG, "merchantIndexs=" + hostInfo.getMerchantIndexs());
            if (TextUtils.isEmpty(hostInfo.getMerchantIndexs())) {
                LogUtil.d(TAG, "No merchant info found");
                continue;
            }

            List<MerchantInfo> merchantInfoList = new ArrayList<>();
            String[] merchantIndexs = hostInfo.getMerchantIndexs().split(",");
            for (int m = 0; m < merchantIndexs.length; m++) {
                int merchantIndex = StringUtil.parseInt(merchantIndexs[m], -1);
                if (merchantIndex == -1) {
                    LogUtil.d(TAG, "Wrong merchant index config[" + merchantIndexs[m] + "]");
                    continue;
                }
                MerchantInfo merchantInfo = iRepository.getMerchantInfo(merchantIndex);
                if (merchantInfo == null) {
                    LogUtil.d(TAG, "Merchant Index[" + merchantIndex + "] not found");
                    continue;
                }

                LogUtil.d(TAG, "===Add Host[" + hostIndex + "]=" + hostInfo.getHostName() + " Merchant[" + merchantIndex + "]=" + merchantInfo.getMerchantName());
                merchantInfoList.add(merchantInfo);
            }

            if (merchantInfoList.size() == 0) {
                LogUtil.d(TAG, "Host[" + hostIndex + "] no merchant found");
                continue;
            } else {
                hostMerchantItems.add(new HostMerchantItem(cardBinInfo, hostInfo, merchantInfoList));
            }
        }
    }

    private boolean checkIsTransRelatedHost(HostInfo hostInfo) {
        int currentTransType = iRepository.getCurrentTranData().getTransType();
        LogUtil.d(TAG, "currentTransType=" + currentTransType);

        if (hostInfo.getHostType() == HostType.INSTALLMENT && currentTransType != TransType.INSTALLMENT) {
            return false;
        }

        if (currentTransType == TransType.INSTALLMENT && hostInfo.getHostType() != HostType.INSTALLMENT) {
            return false;
        }

        if ((currentTransType == TransType.REDEMPTION || currentTransType == TransType.PTS_INQUIRY) && hostInfo.getHostType() != HostType.LOYALTY) {
            return false;
        }

        if (hostInfo.getHostType() == HostType.LOYALTY && (currentTransType != TransType.REDEMPTION && currentTransType != TransType.PTS_INQUIRY)) {
            return false;
        }

        return true;
    }

    private void addDebugInfo(List<CardBinInfo> cardBinInfoList) {
        LogUtil.d(TAG, "-------------CARD BIN ERROR----------");
        if (cardBinInfoList.size() == 0) {
            LogUtil.d(TAG, "No Card Bin Found");
        } else {
            LogUtil.d(TAG, "Found Multi Card Bins");
            for (int i = 0; i < cardBinInfoList.size(); i++) {
                CardBinInfo cardBinInfo = cardBinInfoList.get(i);
                LogUtil.d(TAG, "CardBinInfo[" + i + "] panLow=" + cardBinInfo.getPanLow() + " panHigh=" + cardBinInfo.getPanHigh());
            }
        }
        LogUtil.d(TAG, "--------------------------------------");
    }
}
