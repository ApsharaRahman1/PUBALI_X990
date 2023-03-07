package com.vfi.android.domain.interactor.transaction.iso8583.trans;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.iso8583.base.AbsTransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.ITransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.TransPackageData;
import com.vfi.android.domain.utils.StringUtil;

/**
 * Created by RuihaoS on 2019/8/8.
 */
public class VoidInstallmentPackage extends AbsTransPackage implements ITransPackage {
    private static final String TAG = TAGS.ISO_8583;

    public VoidInstallmentPackage(TransPackageData transPackageData) {
        super(transPackageData);
    }

    @Override
    protected int[] getTransFields() {
        return new int[] {0, 2, 3, 4, 11, 14, 22, 23, 24, 25, 37, 41, 42, 55, 61, 62, 63};
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
    public String f37_retrievalRefNo() {
        return getRecordInfo().getOrgRefNo();
    }

    @Override
    public String field_061() {
        String promoCode = StringUtil.getNonNullStringLeftPadding(getRecordInfo().getPromoCode(), 3);
        String term = StringUtil.getNonNullStringLeftPadding("" + getRecordInfo().getInstallmentTerm(), 2);
        return promoCode + term;
    }
}
