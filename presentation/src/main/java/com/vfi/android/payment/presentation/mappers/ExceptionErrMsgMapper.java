package com.vfi.android.payment.presentation.mappers;

import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.ResUtil;

public class ExceptionErrMsgMapper {
    public static String toErrorMsg(int exceptionType) {
        String errorMsg = "";
        switch (exceptionType) {
            case ExceptionType.CHECK_CARD_FAILED:
                errorMsg = ResUtil.getString(R.string.tv_hint_check_card_failed);
                break;
            case ExceptionType.CHECK_CARD_TIMEOUT:
                errorMsg = ResUtil.getString(R.string.tv_hint_check_card_timeout);
                break;
            case ExceptionType.TRACK_KEY_NOT_FOUND:
                errorMsg = ResUtil.getString(R.string.tv_hint_track_key_not_found);
                break;
            case ExceptionType.GET_MERCHANT_INFO_FAILED:
                errorMsg = ResUtil.getString(R.string.tv_hint_merchant_not_found);
                break;
            case ExceptionType.GET_HOST_INFO_FAILED:
                errorMsg = ResUtil.getString(R.string.tv_hint_host_not_found);
                break;
            case ExceptionType.GET_CARD_BIN_FAILED:
                errorMsg = ResUtil.getString(R.string.tv_hint_trans_not_allowed);
                break;
            case ExceptionType.PIN_KEY_NOT_FOUND:
                errorMsg = ResUtil.getString(R.string.tv_hint_pin_key_not_found);
                break;
        }

        return errorMsg;
    }
}
