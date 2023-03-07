package com.vfi.android.domain.interactor.transaction.iso8583.trans;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.iso8583.base.AbsTransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.ITransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.TransPackageData;


/**
 * Created by fusheng.z on 2019/1/9
 */
public class PreAuthCompPackage extends AbsTransPackage implements ITransPackage {
    private static final String TAG = TAGS.ISO_8583;

    public PreAuthCompPackage(TransPackageData transPackageData) {
        super(transPackageData);
    }

    @Override
    protected int[] getTransFields() {
        return new int[] {0, 2, 3, 4, 11, 13, 14, 22, 23, 24, 25, 35, 37, 38, 41, 42, 52, 55, 62};
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
    public String f2_pan() {
        String track2 = getRecordInfo().getTrack2();
        if (track2 == null) {
            return getRecordInfo().getPan();
        } else {
            return null;
        }
    }

    @Override
    public String f13_localDate() {
        return getRecordInfo().getOrgTransDate();
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
    public String f37_retrievalRefNo() {
        return getRecordInfo().getOrgRefNo();
    }

    @Override
    public String f38_authIdResp() {
        return getRecordInfo().getOrgAuthCode();
    }
}
