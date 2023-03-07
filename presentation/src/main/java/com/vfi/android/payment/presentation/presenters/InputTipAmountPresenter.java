package com.vfi.android.payment.presentation.presenters;


import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.interactor.deviceservice.UseCaseImportCardConfirmResult;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.interactor.repository.UseCaseSaveTransTipAmount;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TvShowUtil;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.InputTipAmountUI;

import javax.inject.Inject;

/**
 * Created by yunlongg1 on 03/11/2017.
 */

public class InputTipAmountPresenter extends BasePresenter<InputTipAmountUI> {
    private final String TAG = TAGS.UILevel;

    private final UseCaseSaveTransTipAmount useCaseSaveTransTipAmount;
    private final CurrentTranData currentTranData;

    @Inject
    InputTipAmountPresenter(UseCaseSaveTransTipAmount useCaseSaveTransTipAmount,
                            UseCaseGetCurTranData useCaseGetCurTranData) {
        this.useCaseSaveTransTipAmount = useCaseSaveTransTipAmount;
        this.currentTranData = useCaseGetCurTranData.execute(null);
    }

    @Override
    protected void onFirstUIAttachment() {
        showTitle();
        doUICmd_ShowCurrencyHint("");
        if (currentTranData.getTransType() == TransType.TIP_ADJUST) {
            String hint = ResUtil.getString(R.string.tv_hint_old_tip) + " " + StringUtil.formatAmount(currentTranData.getTransTipAmount());
            doUICmd_showOldTipAmount(hint);
        } else {
            checkAndShowRecommendedTipAmount();
        }
    }

    private void checkAndShowRecommendedTipAmount() {
        CardBinInfo cardBinInfo = currentTranData.getCardBinInfo();
        if (cardBinInfo != null) {
            int tipPercent = cardBinInfo.getTipPercent();
            LogUtil.d(TAG, "Recommend tipPercent=[" + tipPercent + "]");
            if (tipPercent >= 100 && tipPercent <= 10000) {
                long amountLong = StringUtil.parseLong(currentTranData.getTransAmount(), 0);
                long recommendedTipAmount = amountLong * tipPercent / 10000;
                doUICmd_showRecommendedTipAmount(TvShowUtil.formatAmount("" + recommendedTipAmount));
            }
        }
    }

    private void showTitle() {
        String title = currentTranData.getTitle();
        if (currentTranData.getTransType() == TransType.TIP_ADJUST) {
            doUICmd_showTitle(title, "");
        } else {
            doUICmd_showTitle(ResUtil.getString(R.string.title_tips), "");
        }
    }

    public void saveTipAmount(String tipAmount) {
        LogUtil.d(TAG, "Save tip amount=" + tipAmount);
        tipAmount = tipAmount.replace(",", "");
        tipAmount = tipAmount.replace(".", "");

        long amountLong = StringUtil.parseLong(currentTranData.getTransAmount(), 0);
        long tipAmountLong = Long.parseLong(tipAmount);

        if (tipAmountLong > amountLong) {
            String toastHint = ResUtil.getString(R.string.toast_hint_amount_range);
            toastHint += "[" + TvShowUtil.formatAmount("" + 0);
            toastHint += " - " + TvShowUtil.formatAmount("" + amountLong) + "]";
            doUICmd_clearInputText();
            doUICmd_showToastMessage(toastHint);
            return;
        }

        if (currentTranData.getTransType() == TransType.TIP_ADJUST
                && tipAmountLong == StringUtil.parseLong(currentTranData.getTransTipAmount(), 0)) {
            doUICmd_clearInputText();
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_same_tip_not_allow));
            return;
        }

        if (amountLong + tipAmountLong > 999999999999L) {
            doUICmd_clearInputText();
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_total_amount_exceeds_limit));
            return;
        }

        tipAmount = String.format("%012d", tipAmountLong);
        LogUtil.d(TAG, "Tip amount=" + tipAmount);
        useCaseSaveTransTipAmount.execute(tipAmount);
        doUICmd_navigatorToNext();
    }

    private void doUICmd_ShowCurrencyHint(String currency) {
        execute(ui-> ui.showCurrencyHint(currency));
    }

    private void doUICmd_clearInputText() {
        execute(ui-> ui.clearInputText());
    }

    private void doUICmd_showRecommendedTipAmount(String tipAmount) {
        execute(ui -> ui.showRecommendedTipAmount(tipAmount));
    }

    private void doUICmd_showOldTipAmount(String oldTipAmount) {
        execute(ui -> ui.showOldTipAmount(oldTipAmount));
    }
}
