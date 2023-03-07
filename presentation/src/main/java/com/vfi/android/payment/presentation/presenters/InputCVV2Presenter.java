package com.vfi.android.payment.presentation.presenters;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.consts.CVV2ControlType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.InputCVV2UI;

import javax.inject.Inject;

public class InputCVV2Presenter extends BasePresenter<InputCVV2UI> {
    private final String TAG = TAGS.UILevel;
    private final CurrentTranData currentTranData;

    @Inject
    public InputCVV2Presenter(UseCaseGetCurTranData useCaseGetCurTranData
                                        ) {
        this.currentTranData = useCaseGetCurTranData.execute(null);
    }

    @Override
    protected void onFirstUIAttachment() {
        doUICmd_showTitle(currentTranData.getTitle(), "");
    }

    public void submitCVV2(String CVV2) {
        if (!checkIsValidCVV2(CVV2)) {
            int cvvMin = currentTranData.getCardBinInfo().getCvv2Min();
            int cvvMax = currentTranData.getCardBinInfo().getCvv2Max();
            doUICmd_showToastMessage(ResUtil.getString(R.string.please_input_correct_cvv2) + " ,Len [" + cvvMin + " - " + cvvMax + "]");
            doUICmd_clearInputText();
        } else {
            currentTranData.getCardInfo().setCVC2OrCVV2(CVV2);
            currentTranData.getRecordInfo().setCvv2(CVV2);
            doUICmd_navigatorToNext();
        }
    }

    private boolean checkIsValidCVV2(String cvv2) {
        int cvvControlType = currentTranData.getCardBinInfo().getCvv2Control();
        LogUtil.d(TAG, "cvvControlType=" + cvvControlType);
        int cvvMin = currentTranData.getCardBinInfo().getCvv2Min();
        int cvvMax = currentTranData.getCardBinInfo().getCvv2Max();
        LogUtil.d(TAG, "cvvMin=" + cvvMin + " cvvMax=" + cvvMax + " cvv2=[" + cvv2 + "]");
        if (cvv2 == null || cvv2.length() == 0) {
            if (cvvControlType == CVV2ControlType.ALLOW_BYPASS) {
                return true;
            } else {
                LogUtil.d(TAG, "Cvv2 not input when cvv2 not allow bypass");
                return false;
            }
        } else if (cvvMin == cvvMax) {
            if (cvvMin == cvv2.length()) {
                return true;
            } else {
                LogUtil.d(TAG, "cvv2 len no equal " + cvvMin);
                return false;
            }
        } else if (cvv2.length() < cvvMin || cvv2.length() > cvvMax){
            LogUtil.d(TAG, "Cvv2 len=" + cvv2.length() + " min=" + cvvMin + " max=" + cvvMax);
            return false;
        }

        return true;
    }

    private void doUICmd_clearInputText() {
        execute(ui -> ui.clearInputText());
    }
}
