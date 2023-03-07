package com.vfi.android.payment.presentation.presenters;

import com.vfi.android.domain.entities.businessbeans.CardBinInfo;
import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.consts.CVV2ControlType;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.databeans.HostMerchantItem;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.repository.UseCaseDoCardBinRouting;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.ExceptionErrMsgMapper;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.view.contracts.SelectHostMerchantUI;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by RuihaoS on 2019/9/4.
 */
public class SelectHostMerchantPresenter extends BasePresenter<SelectHostMerchantUI> {
    private final String TAG = TAGS.BIN_ROUTING;

    private final CurrentTranData currentTranData;
    private final UINavigator uiNavigator;
    private final UseCaseDoCardBinRouting useCaseDoCardBinRouting;

    private List<HostMerchantItem> hostMerchantItemList;
    private List<MerchantInfo> waitingSelectMerchantInfoList;

    @Inject
    public SelectHostMerchantPresenter(
            UseCaseGetCurTranData useCaseGetCurTranData,
            UseCaseDoCardBinRouting useCaseDoCardBinRouting,
            UINavigator uiNavigator) {
        this.currentTranData = useCaseGetCurTranData.execute(null);
        this.useCaseDoCardBinRouting = useCaseDoCardBinRouting;
        this.uiNavigator = uiNavigator;
    }

    protected void onFirstUIAttachment() {
        startBinRouting();
    }

    private void startBinRouting() {
        useCaseDoCardBinRouting.asyncExecute(null).doOnNext(hostMerchantItems -> {
            this.hostMerchantItemList = hostMerchantItems;
            if (hostMerchantItemList.size() == 1) {
                HostInfo hostInfo = hostMerchantItems.get(0).getHostInfo();
                currentTranData.setCardBinInfo(hostMerchantItemList.get(0).getCardBinInfo());
                selectHost(hostInfo);
            } else {
                doUICmd_showSelectHosts(getHostNameList(hostMerchantItems));
            }
        }).doOnError(throwable -> {
            doErrorProcess(throwable);
        }).subscribe();
    }

    private boolean isTransactionAllow() {
        CardBinInfo cardBinInfo = currentTranData.getCardBinInfo();
        int transType = currentTranData.getTransType();
        if (cardBinInfo != null) {
            if (transType == TransType.INSTALLMENT
                    && !cardBinInfo.isAllowInstallment()) {
                LogUtil.d(TAG, "Installment is not allow.");
                return false;
            }

            if (transType == TransType.OFFLINE && !cardBinInfo.isAllowOfflineSale()) {
                LogUtil.d(TAG, "Offline is not allow.");
                return false;
            }

            if (transType == TransType.REFUND && !cardBinInfo.isAllowRefund()) {
                LogUtil.d(TAG, "Refund is not allow.");
                return false;
            }
        }

        LogUtil.d(TAG, "isTransactionAllow=true");
        return true;
    }

    private void selectHost(HostInfo hostInfo) {
        if (!isTransactionAllow()) {
            currentTranData.setErrorMsg(ResUtil.getString(R.string.tv_hint_trans_not_allowed));
            uiNavigator.getUiFlowControlData().setTransFailed(true);
            doUICmd_navigatorToNext();
            return;
        }

        currentTranData.setHostInfo(hostInfo);

        for (HostMerchantItem item : hostMerchantItemList) {
            if (item.getHostInfo().getHostType() == hostInfo.getHostType()) {
                waitingSelectMerchantInfoList = item.getMerchantInfoList();
                break;
            }
        }

        if (waitingSelectMerchantInfoList == null || waitingSelectMerchantInfoList.size() == 0) {
            uiNavigator.getUiFlowControlData().setTransFailed(true);
            doUICmd_navigatorToNext();
            return;
        }

        if (waitingSelectMerchantInfoList.size() == 1) {
            MerchantInfo merchantInfo = waitingSelectMerchantInfoList.get(0);
            currentTranData.setMerchantInfo(merchantInfo);
            LogUtil.d(TAG, "Only one host one merchant, no need select now.");
            navigate2NextUIInterface();
        } else {
            doUICmd_showSelectMerchants(getMerchantNameList(waitingSelectMerchantInfoList));
        }
    }

    private List<String> getHostNameList(List<HostMerchantItem> hostMerchantItems) {
        List<String> hostNameList = new ArrayList<>();
        for (HostMerchantItem hostMerchantItem : hostMerchantItems) {
            hostNameList.add(hostMerchantItem.getHostInfo().getHostName());
        }

        return hostNameList;
    }

    private List<String> getMerchantNameList(List<MerchantInfo> merchantInfoList) {
        List<String> merchantNameList = new ArrayList<>();
        for (MerchantInfo merchantInfo : merchantInfoList) {
            if (merchantInfo.getMerchantName() == null || merchantInfo.getMerchantName().length() == 0) {
                merchantNameList.add("" + merchantInfo.getMerchantIndex());
            } else {
                merchantNameList.add(merchantInfo.getMerchantName());
            }
        }

        return merchantNameList;
    }

    public void selectHostNameIndex(int index) {
        HostMerchantItem hostMerchantItem = hostMerchantItemList.get(index);

        HostInfo hostInfo = hostMerchantItem.getHostInfo();
        currentTranData.setCardBinInfo(hostMerchantItem.getCardBinInfo());
        selectHost(hostInfo);
    }

    public void selectMerchantNameIndex(int index) {
        MerchantInfo merchantInfo = waitingSelectMerchantInfoList.get(index);
        currentTranData.setMerchantInfo(merchantInfo);
        navigate2NextUIInterface();
    }

    private void navigate2NextUIInterface() {
        if (currentTranData.getCardInfo().getCardEntryMode() == CardEntryMode.MANUAL
                && currentTranData.getCardBinInfo().getCvv2Control() != CVV2ControlType.NO_NEED) {
            uiNavigator.getUiFlowControlData().setNeedInputCVV2(true);
        }

        doUICmd_navigatorToNext();
    }

    private void doErrorProcess(Throwable exception) {
        if (exception instanceof CommonException) {
            CommonException commonException = (CommonException) exception;
            int exceptionType = commonException.getExceptionType();
            LogUtil.d(TAG, "exceptionType=" + exceptionType);
            currentTranData.setErrorMsg(ExceptionErrMsgMapper.toErrorMsg(exceptionType));
            uiNavigator.getUiFlowControlData().setTransFailed(true);
            doUICmd_navigatorToNext();
        } else {
            // other exception
            exception.printStackTrace();
            String errmsg = exception.getMessage();
            if (errmsg != null && errmsg.length() > 30) {
                errmsg = errmsg.substring(0, 30);
            }

            currentTranData.setErrorMsg("" + errmsg);
            uiNavigator.getUiFlowControlData().setTransFailed(true);
            doUICmd_navigatorToNext();
        }
    }

    private void doUICmd_showSelectHosts(List<String> hostNameList) {
        doUICmd_showTitle(currentTranData.getTitle(), "");
        execute(ui -> ui.showSelectHosts(hostNameList));
    }

    private void doUICmd_showSelectMerchants(List<String> merchantNameList) {
        doUICmd_showTitle(currentTranData.getTitle(), "");
        execute(ui -> ui.showSelectMerchants(merchantNameList));
    }
}
