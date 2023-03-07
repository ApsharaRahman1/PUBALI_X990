package com.vfi.android.payment.presentation.mappers;

import com.vfi.android.domain.entities.consts.NotAllowError;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.ResUtil;

public class NotAllowErrorMapper {
    public static String toErrorString(int errorCode) {
        switch (errorCode) {
            case NotAllowError.TRANS_RECORD_NOT_FOUND:
                return ResUtil.getString(R.string.tv_hint_origin_record_not_found);
            case NotAllowError.ORIGIN_TRANS_NOT_ALLOW_ADJUST:
                return ResUtil.getString(R.string.tv_hint_origin_record_not_allow_adjust);
            case NotAllowError.ORIGIN_TRANS_NOT_ALLOW_VOID:
                return ResUtil.getString(R.string.tv_hint_origin_record_not_allow_void);
            case NotAllowError.EXCEED_MAX_ADJUST_TIME:
                return ResUtil.getString(R.string.tv_hint_exceed_max_adjust);
            case NotAllowError.ORIGIN_TRANS_HOST_NOT_FOUND:
                return ResUtil.getString(R.string.tv_hint_not_found_origin_trans_host);
            case NotAllowError.ORIGIN_TRANS_MERCHANT_NOT_FOUND:
                return ResUtil.getString(R.string.tv_hint_not_found_origin_trans_merchant);
            case NotAllowError.CARD_BIN_NOT_FOUND:
                return ResUtil.getString(R.string.tv_hint_not_found_card_bin_info);

        }

        return "Unknown error";
    }
}
