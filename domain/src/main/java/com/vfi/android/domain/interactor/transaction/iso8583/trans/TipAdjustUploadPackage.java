package com.vfi.android.domain.interactor.transaction.iso8583.trans;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.iso8583.base.AbsTransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.ITransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.TransPackageData;


public class TipAdjustUploadPackage extends AbsTransPackage implements ITransPackage {
    private static final String TAG = TAGS.ISO_8583;

    public TipAdjustUploadPackage(TransPackageData transPackageData) {
        super(transPackageData);
    }

    @Override
    protected int[] getTransFields() {
        // Field 23 , 55 only for chip entry, tip adjust transaction no need chip entry, so remove field 23, 55
        return new int[] {0, 2, 3, 4, 11, 12, 13, 14, 22, 24, 25, 37, 38, 39, 41, 42, 54, 60, 62};
//        return new int[] {0, 2, 3, 4, 11, 12, 13, 14, 22, 23, 24, 25, 37, 38, 39, 41, 42, 54, 55, 60, 62};
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
    public String f4_amount() {
        if (getRecordInfo().getTransType() == TransType.VOID) {
            return "000000000000";
        }

        return super.f4_amount();
    }

    @Override
    public String field_060() {
        return getRecordInfo().getAmount();
    }
}
