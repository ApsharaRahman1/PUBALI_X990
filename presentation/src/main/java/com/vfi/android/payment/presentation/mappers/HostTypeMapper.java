package com.vfi.android.payment.presentation.mappers;

import com.vfi.android.domain.entities.consts.HostType;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.ResUtil;

public class HostTypeMapper {
    public static String toHintString(int hostType) {
        switch (hostType) {
            case HostType.LOCAL:
                return ResUtil.getString(R.string.tv_hint_local);
            case HostType.CUP:
                return ResUtil.getString(R.string.tv_hint_cup);
            case HostType.DEBIT:
                return ResUtil.getString(R.string.tv_hint_debit);
            case HostType.LOYALTY:
                return ResUtil.getString(R.string.tv_hint_loyalty);
            case HostType.INSTALLMENT:
                return ResUtil.getString(R.string.tv_hint_installment);
            case HostType.DCC:
                return ResUtil.getString(R.string.tv_hint_dcc);
        }

        return "UnKnown hostType[" + hostType + "]";
    }

    public static String toSettlementItemTitle(int hostType) {
        String settlementItemTitle = "";
        switch (hostType) {
            case HostType.LOCAL:
                settlementItemTitle = ResUtil.getString(R.string.tv_hint_local);
                break;
            case HostType.CUP:
                settlementItemTitle = ResUtil.getString(R.string.tv_hint_cup);
                break;
            case HostType.DEBIT:
                settlementItemTitle = ResUtil.getString(R.string.tv_hint_debit);
                break;
            case HostType.LOYALTY:
                settlementItemTitle = ResUtil.getString(R.string.tv_hint_loyalty);
                break;
            case HostType.INSTALLMENT:
                settlementItemTitle = ResUtil.getString(R.string.tv_hint_installment);
                break;
            case HostType.DCC:
                settlementItemTitle = ResUtil.getString(R.string.tv_hint_dcc);
                break;
        }

        return settlementItemTitle;
    }
}
