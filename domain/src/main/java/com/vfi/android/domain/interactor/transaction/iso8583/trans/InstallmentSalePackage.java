package com.vfi.android.domain.interactor.transaction.iso8583.trans;

import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransErrorCode;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.iso8583.base.AbsTransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.ITransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.TransPackageData;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;


/**
 * Created by fusheng.z on 2019/1/9
 */
public class InstallmentSalePackage extends AbsTransPackage implements ITransPackage {
    private static final String TAG = TAGS.ISO_8583;

    public InstallmentSalePackage(TransPackageData transPackageData) {
        super(transPackageData);
    }

    @Override
    protected int[] getTransFields() {
        // 52, 61 for cup Credit/Debit
        return new int[] {0, 2, 3, 4, 11, 14, 22, 23, 24, 25, 35, 41, 42, 52, 54, 55, 61, 62};
    }

    @Override
    public byte[] packISO8583Message() throws CommonException {
        return packISOMessage();
    }

    @Override
    public String f2_pan() {
        String track2 = getRecordInfo().getTrack2();
        if (track2 == null) {
            return getRecordInfo().getPan();
        } else {
            return null;
        }
    }

    @Override
    public String f14_expireDate() {
        String track2 = getRecordInfo().getTrack2();
        if (track2 == null) {
            return getRecordInfo().getCardExpiryDate();
        } else {
            return null;
        }
    }

    @Override
    public String field_061() {
        String promoCode = StringUtil.getNonNullStringLeftPadding(getRecordInfo().getPromoCode(), 3);
        String term = StringUtil.getNonNullStringLeftPadding("" + getRecordInfo().getInstallmentTerm(), 2);
        return promoCode + term;
    }

    @Override
    public void unPackISO8583Message(byte[] message) throws CommonException {
        unPackISOMessage(message);

        String respField39 = getReceiveDataField(39);
        if (respField39 == null && !respField39.equals("00")) {
            LogUtil.d(TAG, "Trans failed, no need parse field 61");
            return;
        }

        final int INSTALL_PLAN_LEN         = 3;
        final int PAYMENT_TERM_LEN         = 2;
        final int COMPUTED_METHOD_LEN      = 1;
        final int FACTOR_RATE_LEN          = 6;
        final int INTEREST_RATE_MOS_LEN    = 2;
        final int FIRST_PAYMENT_AMOUNT_LEN = 9;
        final int LAST_PAYMENT_AMOUNT_LEN  = 9;
        final int MONTHLY_INSTALLMENT_AMOUNT_LEN = 9;
        final int TOTAL_INSTALLMENT_AMOUNT_LEN   = 9;
        final int OUTSTANDING_PRINCIPAL_LEN= 9;
        final int INTEREST_LEN             = 9;
        final int HANDLING_FEE_LEN         = 5;
        final int TOTAL_FIELD_LEN          = 73;

        // parse field 63, now response ISO 63 not very clear.
        RecordInfo recordInfo = getRecordInfo();
        String rspField61 = getReceiveDataField(61);


        if (rspField61 == null || rspField61.length() < TOTAL_FIELD_LEN) {
            throw new CommonException(ExceptionType.TRANS_FAILED, TransErrorCode.RESP_F61_NOT_CORRECT);
        }

        int index = 0;

        String installPlan = rspField61.substring(index, index + INSTALL_PLAN_LEN);
        LogUtil.d(TAG, "installPlan=[" + installPlan + "]");
        index += INSTALL_PLAN_LEN;
        String paymentTerm = rspField61.substring(index, index + PAYMENT_TERM_LEN);
        LogUtil.d(TAG, "paymentTerm=[" + paymentTerm + "]");
        index += PAYMENT_TERM_LEN;
        String computedMethod = rspField61.substring(index, index + COMPUTED_METHOD_LEN);
        LogUtil.d(TAG, "computedMethod=[" + computedMethod + "]");
        index += COMPUTED_METHOD_LEN;
        String interest_or_factor_rate = rspField61.substring(index, index + FACTOR_RATE_LEN);
        LogUtil.d(TAG, "interest_or_factor_rate=[" + interest_or_factor_rate + "]");
        index += FACTOR_RATE_LEN;
        String interest_fee_mos = rspField61.substring(index, index + INTEREST_RATE_MOS_LEN);
        LogUtil.d(TAG, "interest_fee_mos=[" + interest_fee_mos + "]");
        index += INTEREST_RATE_MOS_LEN;
        String firstPaymentAmount = rspField61.substring(index, index + FIRST_PAYMENT_AMOUNT_LEN);
        LogUtil.d(TAG, "firstPaymentAmount=[" + firstPaymentAmount + "]");
        index += FIRST_PAYMENT_AMOUNT_LEN;
        String lastPaymentAmount = rspField61.substring(index, index + LAST_PAYMENT_AMOUNT_LEN);
        LogUtil.d(TAG, "lastPaymentAmount=[" + lastPaymentAmount + "]");
        index += LAST_PAYMENT_AMOUNT_LEN;
        String monthlyInstalAmount = rspField61.substring(index, index + MONTHLY_INSTALLMENT_AMOUNT_LEN);
        LogUtil.d(TAG, "monthlyInstalAmount=[" + monthlyInstalAmount + "]");
        index += MONTHLY_INSTALLMENT_AMOUNT_LEN;
        String totalInstallmentAmount = rspField61.substring(index, index + TOTAL_INSTALLMENT_AMOUNT_LEN);
        LogUtil.d(TAG, "totalInstallmentAmount=[" + totalInstallmentAmount + "]");
        index += TOTAL_INSTALLMENT_AMOUNT_LEN;
        String interest = rspField61.substring(index, index + INTEREST_LEN);
        LogUtil.d(TAG, "interest=[" + interest + "]");
        index += INTEREST_LEN;
        String handlingFee = rspField61.substring(index, index + HANDLING_FEE_LEN);
        LogUtil.d(TAG, "handlingFee=[" + handlingFee + "]");
        index += HANDLING_FEE_LEN;

        recordInfo.setInstallmentFactorRate(interest_or_factor_rate);
        recordInfo.setTotalAmountDue(totalInstallmentAmount);
        recordInfo.setMonthlyDue(monthlyInstalAmount);
    }
}
