package com.vfi.android.payment.presentation.presenters;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TransAttribute;
import com.vfi.android.domain.entities.consts.CurrencyCode;
import com.vfi.android.domain.interactor.deviceservice.UseCaseImportCardConfirmResult;
import com.vfi.android.domain.interactor.repository.UseCaseGetCurTranData;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.mappers.CurrencySelectorMapper;
import com.vfi.android.payment.presentation.navigation.UINavigator;
import com.vfi.android.payment.presentation.presenters.base.BasePresenter;
import com.vfi.android.payment.presentation.utils.ResUtil;
import com.vfi.android.payment.presentation.utils.TvShowUtil;
import com.vfi.android.payment.presentation.view.contracts.ShowTransInfoUI;

import java.util.Locale;

import javax.inject.Inject;

public class ShowTransInfoPresenter extends BasePresenter<ShowTransInfoUI> {
    private final static String TAG = "ShowTransInfoPresenter";
    private final CurrentTranData currentTranData;
    private final UseCaseImportCardConfirmResult useCaseImportCardConfirmResult;
    private final UINavigator uiNavigator;

    @Inject
    public ShowTransInfoPresenter(
            UseCaseGetCurTranData useCaseGetCurTranData,
            UseCaseImportCardConfirmResult useCaseImportCardConfirmResult,
            UINavigator uiNavigator) {
        this.currentTranData = useCaseGetCurTranData.execute(null);
        this.useCaseImportCardConfirmResult = useCaseImportCardConfirmResult;
        this.uiNavigator = uiNavigator;
    }

    @Override
    protected void onFirstUIAttachment() {
        doUICmd_showTitle(currentTranData.getTitle(), "");

        TransAttribute transAttribute = TransAttribute.findTypeByType(currentTranData.getTransType());

        switch (transAttribute) {
            default:
                loadAmount();
                break;
        }

        loadCardInfo();
    }


    private void loadAmount() {
        String currencySymbol = CurrencySelectorMapper.view2ShowInMultilingual(CurrencyCode.PHP);
        long amountLong = Long.parseLong(currentTranData.getRecordInfo().getAmount());
        long tipAmountLong = Long.parseLong(currentTranData.getRecordInfo().getTipAmount()==null?"0":currentTranData.getRecordInfo().getTipAmount());
//        if (currentTranData.getAcquirerHostType() == HostType.DCC
//                &&  currentTranData.getRecordInformation().getCurrencyCodeDCC() != null) {
//            if (currentTranData.getCurrencyCodeDCC().equals(CurrencySelectorMapper.JPY)
//                    || currentTranData.getCurrencyCodeDCC().equals(CurrencySelectorMapper.KRW) ) {
//                amountLong = Long.parseLong(currentTranData.getAmountDCC())*100;
//                tipAmountLong = Long.parseLong(currentTranData.getRecordInformation().getDCCTipAmount());
//            } else {
//                amountLong = Long.parseLong(currentTranData.getAmountDCC());
//                tipAmountLong = Long.parseLong(currentTranData.getRecordInformation().getDCCTipAmount());
//            }
//            currencySymbol = CurrencySelectorMapper.view2ShowInMultilingual(currentTranData.getCurrencyCodeDCC());
//        }


        String baseAmount = String.format(Locale.getDefault(),"%012d", amountLong);
        String tipAJD = String.format(Locale.getDefault(),"%012d", tipAmountLong);
        String totalAmount = String.format(Locale.getDefault(),"%012d", amountLong + tipAmountLong);

        TransAttribute transAttribute = TransAttribute.findTypeByType(currentTranData.getTransType());
        LogUtil.d("loadAmount transAttribute = "+transAttribute.getTransType());
        if (transAttribute == TransAttribute.TIP_ADJUST) {
            baseAmount = TvShowUtil.formatAmount(baseAmount);
            tipAJD = TvShowUtil.formatAmount(tipAJD);
            totalAmount = TvShowUtil.formatAmount(totalAmount);
            doUICmd_showTipAdjustAmount(baseAmount, tipAJD, totalAmount,currentTranData.getRecordInfo());
        } else {
            if (transAttribute == TransAttribute.VOID) {
                totalAmount = "-" + totalAmount;
            }
            totalAmount = TvShowUtil.formatAmount(totalAmount);
            doUICmd_showAmount(totalAmount,currencySymbol);
        }
    }

    private void loadCardInfo() {
//        if (AlipayWechatUtil.isQrAliWeByAcHostType(currentTranData.getRecordInformation())) {
//            String title;
//            switch (currentTranData.getRecordInformation().getAcquirerHostType()) {
//                case HostType.ALIPAY:
//                    title = ResUtil.getString(R.string.ali_pay_title);
//                    break;
//                case HostType.WECHAT:
//                    title =ResUtil.getString(R.string.wechat_pay_title);
//                    break;
//                default:
//                    title = ResUtil.getString(R.string.ali_pay_title);
//                    break;
//            }
//            LogUtil.d("ShowTransInfoPresenter",title);
//            doUICmd_showAlipayWechatInfo(currentTranData.getRecordInformation().getPartnerTransactionId(),
//                    currentTranData.getRecordInformation().getPaymentId(),title);
//        }else if (AlipayWechatUtil.isThaiQrByAcHostType(currentTranData.getRecordInformation())){
//            doUICmd_showThaiQr(currentTranData.getRecordInformation().getRef1(),currentTranData.getRecordInformation().getRef2(),
//                    currentTranData.getRecordInformation().getRef3());
//        }else if (AlipayWechatUtil.isQrcsByAcHostType(currentTranData.getRecordInformation())){
//            doUICmd_showQrcs();
//        }else {
//            String acount = currentTranData.getPan();
//            acount = TvShowUtil.formatAcount(acount);
//            String cardHolderName = currentTranData.getRecordInformation().getCardHolderName();
//            if (cardHolderName == null || cardHolderName.length() == 0) {
//                cardHolderName = ResUtil.getString(R.string.unknown_name);
//            }
//            doUICmd_showCardInfo(acount, cardHolderName);
//        }

        String acount = currentTranData.getRecordInfo().getPan();
            acount = TvShowUtil.formatAcount(acount);
            String cardHolderName = currentTranData.getRecordInfo().getCardHolderName();
            if (cardHolderName == null || cardHolderName.length() == 0) {
                cardHolderName = ResUtil.getString(R.string.unknown_name);
            }
            doUICmd_showCardInfo(acount, cardHolderName);
    }


    public void cancelTrans() {
        uiNavigator.getUiFlowControlData().setGoBackToMainMenu(true);
        doUICmd_navigatorToNext();
    }

    public void confirmTrans() {
        // 如果是SCB的退货并且系统参数配置的是全流程则进行卡号确认操作
//        SystemParameter systemParameter = useCaseGetSystemParameter.execute(null);
//        if (currentTranData.getTranID().equals(TradingType.REFUND.getId())
//                && systemParameter.isScbRefundSupFullEmv()) {
//            useCaseImportCardConfirmResult.asyncExecuteWithoutResult(true);
//        }
//        uiNavigator.getUiFlowControlData().setPaymentType(currentTranData.getRecordInformation().getPaymentType());
//        if (AlipayWechatUtil.isQrPaymentHostType(currentTranData.getRecordInformation())){
//            uiNavigator.getUiFlowControlData().setGotoQrPayment(true);
//        }else {
//            uiNavigator.getUiFlowControlData().setGotoQrPayment(false);
//        }
//        if (uiNavigator.getUiFlowControlData().isAgodaPreAuthCom()){
//            doUICmd_showChangeAmountdDialog(ResUtil.getString(R.string.redcue_amount),false);
//            return;
//        }
      doUICmd_navigatorToNext();
    }


    private void doUICmd_showAmount(String amount, String currencySymbol) {
        execute(ui -> ui.showAmount(amount, currencySymbol));
    }

    private void doUICmd_showTipAdjustAmount(String baseAmount, String tipAJD, String totalAmount, RecordInfo recordInfo) {
        execute(ui -> ui.showTipAdjustAmount(baseAmount, tipAJD, totalAmount, recordInfo));
    }

    private void doUICmd_showAlipayWechatInfo(String partnerTransactionId, String paymentId, String title) {
        execute(ui -> ui.showAlipayWechatInfo(partnerTransactionId, paymentId, title));
    }

    private void doUICmd_showCardInfo(String acount, String cardHolder) {
        execute(ui -> ui.showCardInfo(acount, cardHolder));
    }

    private void doUICmd_showThaiQr(String ref1, String ref2, String ref3) {
        execute(ui -> ui.showThaqiQrInfo(ref1, ref2, ref3));
    }

    private void doUICmd_showQrcs() {
        execute(ui -> ui.showQrcsInfo());
    }


    private void doUICmd_showPrintFailedDialog(String errMsg, boolean isNeedBackToMainMenu) {
        execute(ui -> ui.showPrintFailedDialog(errMsg, isNeedBackToMainMenu));
    }

    private void doUICmd_showChangeAmountdDialog(String errMsg, boolean isNeedBackToMainMenu) {
        execute(ui -> ui.showChangeAmountdDialog(errMsg, isNeedBackToMainMenu));
    }

    private void doUICmd_EnAbledBackKey(boolean isEnabled) {
        execute(ui -> ui.enableBackKey(isEnabled));
    }

}
