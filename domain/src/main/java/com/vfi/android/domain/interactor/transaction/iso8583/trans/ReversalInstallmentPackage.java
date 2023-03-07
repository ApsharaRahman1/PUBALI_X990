package com.vfi.android.domain.interactor.transaction.iso8583.trans;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.iso8583.base.AbsTransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.ITransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.TransPackageData;
import com.vfi.android.domain.utils.StringUtil;


public class ReversalInstallmentPackage extends AbsTransPackage implements ITransPackage {
    private static final String TAG = TAGS.ISO_8583;

    public ReversalInstallmentPackage(TransPackageData transPackageData) {
        super(transPackageData);
    }

    @Override
    protected int[] getTransFields() {
        return new int[] {0, 2, 3, 4, 11, 14, 22, 23, 24, 25, 41, 42, 48, 54, 55, 61, 62};
    }

    @Override
    public byte[] packISO8583Message() throws CommonException {
        return packISOMessage();
    }

    @Override
    public void unPackISO8583Message(byte[] message) throws CommonException {
        unPackISOMessage(message);
    }

    @Override
    public String f3_processCode() {
        return getRecordInfo().getProcessCode();
    }

    @Override
    public String field_061() {
        String promoCode = StringUtil.getNonNullStringLeftPadding(getRecordInfo().getPromoCode(), 3);
        String term = StringUtil.getNonNullStringLeftPadding("" + getRecordInfo().getInstallmentTerm(), 2);
        return promoCode + term;
    }
}
