package com.vfi.android.payment.presentation.view.contracts;

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.payment.presentation.view.contracts.base.UI;

import java.util.List;

public interface ShowTransInfoUI extends UI {
    void showAmount(String amount, String currencySymbol);
    void showTipAdjustAmount(String baseAmount, String tipAJD, String totalAmount, RecordInfo recordInfo);
    void showCardInfo(String acount, String cardHolder);
    void showThaqiQrInfo(String ref1, String ref2, String ref3);
    void showQrcsInfo();
    void showAlipayWechatInfo(String partnerTransactionId, String paymentId, String title);
    void showPrintFailedDialog(String errMsg, boolean isNeedBackToMainMenu);
    void showChangeAmountdDialog(String errMsg, boolean isNeedBackToMainMenu);
    void enableBackKey(boolean isEnabled);
}
