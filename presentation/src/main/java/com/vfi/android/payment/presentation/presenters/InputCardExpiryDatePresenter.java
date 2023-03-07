package com.vfi.android.payment.presentation.presenters;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.consts.CVV2ControlType;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.InputCardExpiryDateUI;

import javax.inject.Inject;

public class InputCardExpiryDatePresenter extends BasePresenter<InputCardExpiryDateUI> {
    private final CurrentTranData currentTranData;
    private final UINavigator uiNavigator;

    @Inject
    public InputCardExpiryDatePresenter(UINavigator uiNavigator,
                                        UseCaseGetCurTranData useCaseGetCurTranData
    ) {
        this.currentTranData = useCaseGetCurTranData.execute(null);
        this.uiNavigator = uiNavigator;
    }

    @Override
    protected void onFirstUIAttachment() {
        doUICmd_showTitle(currentTranData.getTitle(), "");
    }

    public void submitCardExpiryDate(String expiryDate) {
        String[] dateTemp = expiryDate.split("/");
        expiryDate = dateTemp[1] + dateTemp[0];
        if (!checkIsValidDate(expiryDate)) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_invalid_date));
            doUICmd_clearInputText();
        } else if (isExpiredCard(expiryDate)) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_expired_card));
            doUICmd_clearInputText();
        } else {
            currentTranData.getCardInfo().setExpiredDate(expiryDate);
            if (currentTranData.getCardInfo().getCardEntryMode() == CardEntryMode.MANUAL
                    && currentTranData.getCardBinInfo().getCvv2Control() != CVV2ControlType.NO_NEED) {
                uiNavigator.getUiFlowControlData().setNeedInputCVV2(true);
            }
            doUICmd_navigatorToNext();
        }
    }

    private boolean checkIsValidDate(String dateYYMM) {
        final int MIN_EXPIRY_DATE_LEN = 4;
        if (dateYYMM == null || dateYYMM.length() < MIN_EXPIRY_DATE_LEN
                || dateYYMM.equals("MMYY")) {
            return false;
        } else {
            try {
                int month = Integer.parseInt(dateYYMM.substring(2, 4));
                if (month < 1 || month > 12) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    private boolean isExpiredCard(String dateYYMM) {
        String currentDate = StringUtil.formatDate(System.currentTimeMillis());
        if (currentDate != null && currentDate.length() > 6) {
            String currentYYMM = currentDate.substring(2, 6);
            int inputDate = StringUtil.parseInt(dateYYMM, -1);
            int currentDateYYMM = StringUtil.parseInt(currentYYMM, -1);
            if (inputDate < currentDateYYMM) {
                LogUtil.d(TAGS.UILevel, "Expiration date[YYMM:" + dateYYMM + "] currentDate=[YYMM:" + currentYYMM + "]");
                return true;
            }
        }

        return false;
    }

    private void doUICmd_clearInputText() {
        execute(ui -> ui.clearInputText());
    }
}
