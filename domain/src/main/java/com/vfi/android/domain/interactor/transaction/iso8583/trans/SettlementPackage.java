package com.vfi.android.domain.interactor.transaction.iso8583.trans;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.databeans.SettlementPackInfo;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.iso8583.base.AbsTransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.ITransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.TransPackageData;
import com.vfi.android.domain.utils.StringUtil;

/**
 * Created by RuihaoS on 2019/8/8.
 */
public class SettlementPackage extends AbsTransPackage implements ITransPackage {
    private static final String TAG = TAGS.ISO_8583;
    private final SettlementPackInfo settlementPackInfo;

    public SettlementPackage(TransPackageData transPackageData) {
        super(transPackageData);
        this.settlementPackInfo = transPackageData.getSettlementPackInfo();
        if (settlementPackInfo == null) {
            throw new RuntimeException("Settlement info should not be empty.");
        }
    }

    @Override
    protected int[] getTransFields() {
        return new int[] {0, 3, 11, 24, 41, 42, 60, 63};
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
    public String f11_traceNum() {
        return settlementPackInfo.getSysTraceNum();
    }

    @Override
    public String f24_nii() {
        return settlementPackInfo.getNii();
    }

    @Override
    public String f41_terminalId() {
        return settlementPackInfo.getTerminalId();
    }

    @Override
    public String f42_cardAcquirerId() {
        return settlementPackInfo.getMerchantId();
    }

    @Override
    public String field_060() {
        return StringUtil.getNonNullStringLeftPadding(settlementPackInfo.getBatchNum(), 6);
    }

    @Override
    public String field_063() {
        String countIn = String.format("%03d", settlementPackInfo.getCountIn());
        String amountIn = String.format("%012d", settlementPackInfo.getTotalAmountIn());
        String countOut = String.format("%03d", settlementPackInfo.getCountOut());
        String amountOut = String.format("%012d", settlementPackInfo.getTotalAmountOut());
        String padding60byteSpace = "000000000000000000000000000000000000000000000000000000000000";

        return countIn + amountIn + countOut + amountOut + padding60byteSpace;
    }
}
