package com.vfi.android.payment.presentation.view.contracts;

import com.vfi.android.payment.presentation.view.contracts.base.UI;

import java.util.List;

/**
 * Created by RuihaoS on 2019/9/4.
 */
public interface SelectHostMerchantUI extends UI {
    void showSelectHosts(List<String> hostNameList);
    void showSelectMerchants(List<String> merchantNameList);
}
