package com.vfi.android.payment.presentation.mappers;

import com.vfi.android.domain.entities.consts.CheckCardErrorConst;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.ResUtil;

public class CheckCardErrorMapper {

    public static String toErrorString(int checkCardErrorCode) {
        switch (checkCardErrorCode) {
            case CheckCardErrorConst.SWIPE_CARD_FAILED:
            case CheckCardErrorConst.INSERT_CARD_FAILED:
            case CheckCardErrorConst.READ_CHIP_FAILED:
            case CheckCardErrorConst.TAP_CARD_FAILED:
                return ResUtil.getString(R.string.tv_hint_read_card_failed);
            case CheckCardErrorConst.ACTIVATE_CONTACTLESS_CARD_FAILED:
                return ResUtil.getString(R.string.tv_hint_activate_card_failed);
            case CheckCardErrorConst.CARD_CONFLICT:
                return ResUtil.getString(R.string.tv_hint_multi_card_conflict);
            case CheckCardErrorConst.CARD_INFO_INVALID:
                return ResUtil.getString(R.string.tv_hint_invalid_card_info);
            case CheckCardErrorConst.IC_CARD_NOT_ALLOW_SWIPE_FIRST:
                return ResUtil.getString(R.string.tv_hint_not_allow_swipe_chip_card);
        }

        return "Unknown error";
    }
}
