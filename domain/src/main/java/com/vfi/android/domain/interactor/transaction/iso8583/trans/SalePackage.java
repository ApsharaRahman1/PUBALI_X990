package com.vfi.android.domain.interactor.transaction.iso8583.trans;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.iso8583.base.AbsTransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.ITransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.TransPackageData;


/**
 * Created by fusheng.z on 2019/1/9
 */
public class SalePackage extends AbsTransPackage implements ITransPackage {
    private static final String TAG = TAGS.ISO_8583;

    public SalePackage(TransPackageData transPackageData) {
        super(transPackageData);
    }

    @Override
    protected int[] getTransFields() {
        // 52, 61 for cup Credit/Debit
        return new int[] {0, 3, 4, 11, 22, 24, 25, 35, 41, 42,49, 52, 55, 62};

        //return new int[] {0, 2, 3, 4, 11, 14, 22, 23, 24, 25, 35, 41, 42, 52, 54, 55, 61, 62};
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
    public String f3_processCode() {
        return getRecordInfo().getProcessCode();
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
    public void unPackISO8583Message(byte[] message) throws CommonException {
        unPackISOMessage(message);
    }
}
