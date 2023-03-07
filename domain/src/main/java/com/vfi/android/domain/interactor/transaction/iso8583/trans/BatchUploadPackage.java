package com.vfi.android.domain.interactor.transaction.iso8583.trans;

import com.vfi.android.domain.entities.businessbeans.TransAttribute;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.iso8583.base.AbsTransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.ITransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.TransPackageData;
import com.vfi.android.domain.utils.StringUtil;

/**
 * Created by RuihaoS on 2019/8/8.
 */
public class BatchUploadPackage extends AbsTransPackage implements ITransPackage {
    private static final String TAG = TAGS.ISO_8583;

    public BatchUploadPackage(TransPackageData transPackageData) {
        super(transPackageData);
    }

    @Override
    protected int[] getTransFields() {
        // specs fields
        return new int[] {0, 2, 3, 4, 11, 12, 13, 14, 22, 23, 24, 25, 37, 38, 39, 41, 42, 54, 55, 60, 62};
        // Vx send fields, specs no F52
//        return new int[] {0, 2, 3, 4, 11, 12, 13, 14, 22, 23, 24, 25, 37, 38, 39, 41, 42, 52, 54, 55, 60, 62};
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
        String processCode = getRecordInfo().getProcessCode();
        if (getTransPackageData().isLastBatchUploadRecord()) {
            return processCode.substring(0, processCode.length() - 1) + "0";
        } else {
            return processCode.substring(0, processCode.length() - 1) + "1";
        }
    }

    @Override
    public String field_060() {
        TransAttribute transAttribute = TransAttribute.findTypeByType(getRecordInfo().getTransType());
        if (transAttribute == null) {
            throw new RuntimeException("Transaction attribute not found.");
        }
        String originMessageType = StringUtil.getNonNullStringLeftPadding(transAttribute.getMsgId(), 4);
        String originStan = StringUtil.getNonNullStringLeftPadding(getRecordInfo().getTraceNum(), 6);
        String originRRN = StringUtil.getNonNullStringLeftPadding(getRecordInfo().getRefNo(), 12);

        return originMessageType + originStan + originRRN;
    }
}
