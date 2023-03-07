package com.vfi.android.payment.presentation.presenters;

import android.annotation.SuppressLint;


import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.SettlementRecord;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.interactor.repository.UseCaseGetSettlementHostList;
import com.vfi.android.domain.interactor.repository.UseCaseGetSettlementRecords;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.models.SettlementItemModel;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TvShowUtil;
import com.vfi.android.payment.presentation.view.contracts.SettlementUI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class SettlementPresenter extends BasePresenter<SettlementUI> {
    private final String TAG = TAGS.Settlement;
    private final CurrentTranData currentTranData;
    private final UseCaseGetSettlementRecords useCaseGetSettlementRecords;
    private final UseCaseGetSettlementHostList useCaseGetSettlementHostList;
    private SettlementRecord settlementRecords = null;

    @Inject
    public SettlementPresenter(UseCaseGetCurTranData useCaseGetCurTranData,
                               UseCaseGetSettlementHostList useCaseGetSettlementHostList,
                               UseCaseGetSettlementRecords useCaseGetSettlementRecords
                               ) {
        this.currentTranData = useCaseGetCurTranData.execute(null);
        this.useCaseGetSettlementRecords = useCaseGetSettlementRecords;
        this.useCaseGetSettlementHostList = useCaseGetSettlementHostList;
    }

    @Override
    protected void onFirstUIAttachment() {
        doUICmd_showTitle(currentTranData.getTitle(), "");
        loadSettlementData();
    }

    @SuppressLint("CheckResult")
    private void loadSettlementData() {
        doUICmd_setLoadingDialogStatus(true);
        useCaseGetSettlementRecords.asyncExecute(null).subscribe(settlementRecords -> {
            this.settlementRecords = settlementRecords;
            currentTranData.setCurrentSettlementRecord(settlementRecords);
            useCaseGetSettlementHostList.asyncExecute(null).subscribe(hostTypeList -> {
                List<SettlementItemModel> settlementItemModels = new ArrayList<>();
                Iterator<Integer> iterator = hostTypeList.iterator();
                long settlementRecordsTotalAmountLong = 0;

                while (iterator.hasNext()) {
                    int hostType = iterator.next();

                    String totalAmount = "";
                    long totalAmountLong = 0;

                    totalAmountLong = settlementRecords.getSettlementItemTotalAmount(hostType);
                    totalAmount = TvShowUtil.formatAmount(String.format(Locale.getDefault(),"%012d",totalAmountLong));
                    settlementItemModels.add(new SettlementItemModel(hostType, totalAmount, true, true));
                    settlementRecordsTotalAmountLong += totalAmountLong;
                }

                doUICmd_showTotalAmount(TvShowUtil.formatAmount(String.format("%012d", settlementRecordsTotalAmountLong)));
                doUICmd_showSettlementDetail(settlementItemModels);
                doUICmd_setLoadingDialogStatus(false);
            }, throwable -> {
                doUICmd_setLoadingDialogStatus(false);
                doUICmd_showToastMessage("" + throwable.getMessage());
                LogUtil.d(TAG, "errmsg=" + throwable.getMessage());
                throwable.printStackTrace();
            });
        }, throwable -> {
            doUICmd_setLoadingDialogStatus(false);
            doUICmd_showToastMessage("" + throwable.getMessage());
            LogUtil.d(TAG, "errmsg=" + throwable.getMessage());
            throwable.printStackTrace();
        });
    }

    public void calcSelectedSettlementTotalAmount(List<SettlementItemModel> settlementItemModels) {
        long selectSettlementTotalAmount = 0;
        Iterator<SettlementItemModel> iterator = settlementItemModels.iterator();
        while (iterator.hasNext()) {
            SettlementItemModel settlementItemModel = iterator.next();

            if (settlementItemModel.isSelected()) {
                long selectPaymentTotal = 0;
                try {
                    String paymentTotal = settlementItemModel.getSettlementItemTotalAmount().replace(".", "");
                    paymentTotal = paymentTotal.replace(",", "");
                    selectPaymentTotal = Long.parseLong(paymentTotal);
                    LogUtil.d(TAG, "Payment " + settlementItemModel.getSettlementItemTitle() + " total=" + selectPaymentTotal);
                } catch (Exception e) {
                    e.printStackTrace();
                    selectPaymentTotal = 0;
                }
                selectSettlementTotalAmount += selectPaymentTotal;
            }
        }

        LogUtil.d(TAG, "selectSettlementTotalAmount=" + selectSettlementTotalAmount);
        doUICmd_showTotalAmount(TvShowUtil.formatAmount(String.format("%012d", selectSettlementTotalAmount)));
    }

    public void doSettlementProcess(List<SettlementItemModel> settlementItemModels) {
        boolean isAnySettlementItemSelected = false;

        if (settlementItemModels != null && settlementRecords != null) {
            Iterator<SettlementItemModel> iterator = settlementItemModels.iterator();
            while (iterator.hasNext()) {
                SettlementItemModel settlementItemModel = iterator.next();
                if (settlementItemModel.isSelected()) {
                    isAnySettlementItemSelected = true;
                    settlementRecords.markSelectedSettlementRecordItem(settlementItemModel.getHostType(), false);
                }
            }

            calcSelectedSettlementTotalAmount(settlementItemModels);
        }

        if (isAnySettlementItemSelected) {
            if (settlementRecords.isAnyItemNeedSettlement()) {
                doUICmd_navigatorToNext();
            } else {
                doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_all_host_batches_are_empty));
            }
        } else {
            doUICmd_showToastMessage(ResUtil.getString(R.string.toast_hint_choose_item_to_settlement));
        }
    }

    private void doUICmd_showSettlementDetail(List<SettlementItemModel> settlementItemModels) {
        execute(ui -> ui.showSettlementDetail(settlementItemModels));
    }

    private void doUICmd_showTotalAmount(String amountTotal) {
        execute(ui -> ui.showTotalAmount(amountTotal));
    }
}
