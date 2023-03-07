package com.vfi.android.domain.entities.databeans;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;

import java.util.List;

public class HostMerchantItem {
    private CardBinInfo cardBinInfo;
    private HostInfo hostInfo;
    private List<MerchantInfo> merchantInfoList;

    public HostMerchantItem(CardBinInfo cardBinInfo, HostInfo hostInfo, List<MerchantInfo> merchantInfoList) {
        this.cardBinInfo = cardBinInfo;
        this.hostInfo = hostInfo;
        this.merchantInfoList = merchantInfoList;
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    public List<MerchantInfo> getMerchantInfoList() {
        return merchantInfoList;
    }

    public CardBinInfo getCardBinInfo() {
        return cardBinInfo;
    }
}
