package com.vfi.android.payment.presentation.mappers;

import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.ResUtil;

public class MenuTitleMapper {

    public static String toString(int transType) {
        switch (transType) {
            case TransType.SALE:
                return ResUtil.getString(R.string.title_sale);
            case TransType.SETTLEMENT:
                return ResUtil.getString(R.string.title_settlement);
            case TransType.PREAUTH:
                return ResUtil.getString(R.string.title_preauth);
            case TransType.PREAUTH_COMP:
                return ResUtil.getString(R.string.title_preauth_comp);
            case TransType.VOID:
                return ResUtil.getString(R.string.title_void);
            case TransType.TIP_ADJUST:
                return ResUtil.getString(R.string.title_tip_adjust);
            case TransType.OFFLINE:
                return ResUtil.getString(R.string.title_offline);
            case TransType.INSTALLMENT:
                return ResUtil.getString(R.string.title_installment);
        }

        return "";
    }
}
