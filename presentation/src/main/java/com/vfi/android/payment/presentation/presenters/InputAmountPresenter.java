package com.vfi.android.payment.presentation.presenters;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.businessbeans.SwitchParameter;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.consts.CVV2ControlType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.interactor.repository.UseCaseGetTerminalCfg;
import com.vfi.android.domain.interactor.repository.UseCaseGetTransSwitchParameter;
import com.vfi.android.domain.interactor.repository.UseCaseSaveTransAmount;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TvShowUtil;
import com.vfi.android.payment.presentation.view.contracts.InputAmountUI;

import javax.inject.Inject;

/**
 * Created by yunlongg1 on 03/11/2017.
 */

public class InputAmountPresenter extends BasePresenter<InputAmountUI> {
    private final String TAG = TAGS.UILevel;

    private final UseCaseSaveTransAmount useCaseSaveTransAmount;
    private final UseCaseGetTransSwitchParameter useCaseGetTransSwitchParameter;
    private final CurrentTranData currentTranData;
    private final UINavigator uiNavigator;
    private final TerminalCfg terminalCfg;

    @Inject
    InputAmountPresenter(UseCaseSaveTransAmount useCaseSaveTransAmount,
                         UINavigator uiNavigator,
                         UseCaseGetTerminalCfg useCaseGetTerminalCfg,
                         UseCaseGetTransSwitchParameter useCaseGetTransSwitchParameter,
                         UseCaseGetCurTranData useCaseGetCurTranData) {
        this.useCaseSaveTransAmount = useCaseSaveTransAmount;
        this.currentTranData = useCaseGetCurTranData.execute(null);
        this.uiNavigator = uiNavigator;
        this.terminalCfg = useCaseGetTerminalCfg.execute(null);
        this.useCaseGetTransSwitchParameter = useCaseGetTransSwitchParameter;
    }

    @Override
    protected void onFirstUIAttachment() {
        String title = currentTranData.getTitle();
        doUICmd_showTitle(title, "");

        MerchantInfo merchantInfo = currentTranData.getMerchantInfo();
        if (merchantInfo != null && merchantInfo.getAmountDigits() != 0) {
            LogUtil.d(TAG, "Amount digits=" + merchantInfo.getAmountDigits());
            doUICmd_setMaxAmountDigit(merchantInfo.getAmountDigits());
        }
    }

    public void saveAmount(String amount) {
        LogUtil.d(TAG, "Save amount=" + amount);
        amount = amount.replace(",", "");
        amount = amount.replace(".", "");

        long amountLong = Long.parseLong(amount);
        long maxAmount = 0;
        long minAmount = 0;

        MerchantInfo merchantInfo = currentTranData.getMerchantInfo();
        if (merchantInfo != null) {
            maxAmount = merchantInfo.getMaxAmount();
            minAmount = merchantInfo.getMinAmount();
        }

        if (currentTranData.getTransType() == TransType.PREAUTH
                && maxAmount > 0
                && terminalCfg.getMaxPreAuthAmount() < maxAmount) {
            maxAmount = terminalCfg.getMaxPreAuthAmount();
        }

        LogUtil.d(TAG, "minAmount=" + minAmount + " maxAmount=" + maxAmount);
        if (maxAmount > 0 && maxAmount > minAmount) {
            if (amountLong < minAmount || amountLong > maxAmount) {
                String toastHint = ResUtil.getString(R.string.toast_hint_amount_range);
                toastHint += "[" + TvShowUtil.formatAmount("" + minAmount);
                toastHint += " - " + TvShowUtil.formatAmount("" + maxAmount) + "]";
                doUICmd_showToastMessage(toastHint);
                doUICmd_clearAmountText();
                return;
            }
        }

        if (amountLong == 0) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_invalid_amount));
        } else {
            amount = String.format("%012d", amountLong);
            LogUtil.d(TAG, "amount=" + amount);
            useCaseSaveTransAmount.execute(amount);

            LogUtil.d(TAG, "Terminal isEnableTip=" + terminalCfg.isEnableTip());
            SwitchParameter switchParameter = useCaseGetTransSwitchParameter.execute(currentTranData.getTransType());
            LogUtil.d(TAG, "Transaction isEnableTip=" + switchParameter.isEnableEnterTip());
            if (switchParameter.isEnableEnterTip() && terminalCfg.isEnableTip()) {
                uiNavigator.getUiFlowControlData().setNeedInputTipAmount(true);
            } else {
                uiNavigator.getUiFlowControlData().setNeedInputTipAmount(false);
            }

            doUICmd_navigatorToNext();
        }
    }

    private void doUICmd_setMaxAmountDigit(int digit) {
        execute(ui -> ui.setMaxAmountDigit(digit));
    }

    private void doUICmd_clearAmountText() {
        execute(ui -> ui.clearAmountText());
    }
}
