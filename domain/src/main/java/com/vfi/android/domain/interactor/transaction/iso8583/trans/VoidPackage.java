package com.vfi.android.domain.interactor.transaction.iso8583.trans;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.iso8583.base.AbsTransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.ITransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.TransPackageData;

/**
 * Created by RuihaoS on 2019/8/8.
 */
public class VoidPackage extends AbsTransPackage implements ITransPackage {
    private static final String TAG = TAGS.ISO_8583;

    public VoidPackage(TransPackageData transPackageData) {
        super(transPackageData);
    }

    @Override
    protected int[] getTransFields() {
//        return new int[] {0, 2, 3, 4, 11, 14, 22, 23, 24, 25, 37, 41, 42, 54, 55, 62, 63};
        // Vx not send F23, F54, F55
        return new int[] {0, 2, 3, 4, 11, 14, 22, 24, 25, 37, 41, 42, 62, 63};
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
    public String f37_retrievalRefNo() {
        return getRecordInfo().getOrgRefNo();
    }
}
