package com.vfi.android.payment.presentation.presenters;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.InputOrgTransDateUI;

import javax.inject.Inject;

public class InputOrgTransDatePresenter extends BasePresenter<InputOrgTransDateUI> {
    private final CurrentTranData currentTranData;

    @Inject
    public InputOrgTransDatePresenter(UseCaseGetCurTranData useCaseGetCurTranData) {
        this.currentTranData = useCaseGetCurTranData.execute(null);
    }

    @Override
    protected void onFirstUIAttachment() {
        doUICmd_showTitle(currentTranData.getTitle(), "");
    }

    public void submitOrgTransDate(String orgTransDate) {
        orgTransDate = orgTransDate.replace("/", "");
        LogUtil.d("TAG", "orgTransDate=" + orgTransDate);
        if (!checkIsValidDate(orgTransDate)) {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_enter_correct_org_trans_date));
            doUICmd_clearInputText();
        } else {
            currentTranData.getRecordInfo().setOrgTransDate(orgTransDate);
            doUICmd_navigatorToNext();
        }
    }

    private boolean checkIsValidDate(String dateMMDD) {
        final int MIN_ORG_TRANS_DATE_LEN = 4;
        if (dateMMDD == null || dateMMDD.length() < MIN_ORG_TRANS_DATE_LEN
                || dateMMDD.equals("MMDD")) {
            return false;
        } else {
            try {
                int month = Integer.parseInt(dateMMDD.substring(0, 2));
                int day = Integer.parseInt(dateMMDD.substring(2, 4));

                if (month < 1 || month > 12) {
                    return false;
                }

                int maxDays = 30;
                switch (month) {
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                    case 12:
                        maxDays = 31;
                        break;
                    case 2:
                        maxDays = 29;
                        break;
                    default:
                        maxDays = 30;
                        break;
                }
                if (day < 1 || day > maxDays) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    private void doUICmd_clearInputText() {
        execute(new UICommand<InputOrgTransDateUI>() {
            @Override
            public void execute(InputOrgTransDateUI ui) {
                ui.clearInputText();
            }
        });
    }
}
