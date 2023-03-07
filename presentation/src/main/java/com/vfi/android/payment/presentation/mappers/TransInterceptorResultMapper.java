package com.vfi.android.payment.presentation.mappers;

import com.vfi.android.domain.entities.consts.InterceptorResult;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.ResUtil;

public class TransInterceptorResultMapper {

    public static String toString(int interceptorResult) {
        switch (interceptorResult) {
            case InterceptorResult.LOW_POWER:
                return ResUtil.getString(R.string.toast_hint_low_power);
            case InterceptorResult.TRANS_NOT_SUPPORT:
                return ResUtil.getString(R.string.toast_hint_trans_not_allow);
            case InterceptorResult.NEED_SETTLEMENT:
                return ResUtil.getString(R.string.toast_hint_do_settlement_first);
            case InterceptorResult.OVER_MAX_SETTLEMENT_AMOUNT:
                return ResUtil.getString(R.string.toast_hint_over_amount_do_settlement_first);
            case InterceptorResult.OVER_MAX_SETTLEMENT_COUNT:
                return ResUtil.getString(R.string.toast_hint_over_count_do_settlement_first);
        }

        return "";
    }
}
