package com.vfi.android.payment.presentation.presenters;


import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.consts.CVMResult;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TvShowUtil;
import com.vfi.android.payment.presentation.view.contracts.ElectronicSignUI;

import javax.inject.Inject;

public class ElectronicSignPresenter extends BasePresenter<ElectronicSignUI> {
    private final String TAG = "ElectronicSignPresenter";

    private final CurrentTranData currentTranData;
//    private final UseCaseSaveESign useCaseSaveESign;

    @Inject
    public ElectronicSignPresenter( UseCaseGetCurTranData useCaseGetCurTranData ) {
//        this.useCaseSaveESign = useCaseSaveESign;
        this.currentTranData = useCaseGetCurTranData.execute(null);
    }

    @Override
    protected void onFirstUIAttachment() {
        showTitle();
        loadCardInfo();
    }

    private void showTitle() {
        String title = currentTranData.getTitle();
        String subTitle = "";

        doUICmd_showTitle(title, subTitle);
    }

    private void loadCardInfo() {
        String acount = currentTranData.getRecordInfo().getPan();
        acount = TvShowUtil.formatAcount(acount);

        String amount = StringUtil.formatAmount(currentTranData.getTotalAmount());

        doUICmd_showTransInfo(acount, amount);
    }

    public void saveESginData(byte[] eSignBytesData, boolean isAllowNoSignNext) {
        if (isAllowNoSignNext) {
            currentTranData.getRecordInfo().setCvmResult(CVMResult.REQ_SIGN_BUT_SKIP);
            doUICmd_navigatorToNext();
        } else {
            if (eSignBytesData == null) {
                doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_please_sign));
            } else {
                currentTranData.getRecordInfo().setSignData(eSignBytesData);
                doUICmd_navigatorToNext();
            }
        }
    }

    private void doUICmd_showTransInfo(String acount, String amount) {
        execute(ui -> ui.showTransInfo(acount, amount));
    }

    private void doUICmd_clearESignBoard() {
        execute(ui -> ui.clearESignBoard());
    }
}
