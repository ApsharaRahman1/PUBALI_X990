package com.vfi.android.domain.interactor.transaction.iso8583.trans;

import com.vfi.android.domain.entities.businessbeans.TransAttribute;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.iso8583.base.AbsTransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.ITransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.TransPackageData;


public class TCUploadPackage extends AbsTransPackage implements ITransPackage {
    private static final String TAG = TAGS.ISO_8583;

    public TCUploadPackage(TransPackageData transPackageData) {
        super(transPackageData);
    }

    @Override
    protected int[] getTransFields() {
        return new int[] {0, 2, 3, 4, 11, 12, 13, 14, 22, 23, 24, 25, 37, 38, 39, 41, 42, 54, 55, 60, 62};
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
    public String f55_icc_chip_data() {
        return getRecordInfo().getField55ForTC();
    }

    @Override
    public String field_060() {
        TransAttribute transAttribute = TransAttribute.findTypeByType(getRecordInfo().getTransType());
        return transAttribute.getMsgId() + getRecordInfo().getTraceNum() + getRecordInfo().getRefNo();
    }
}
