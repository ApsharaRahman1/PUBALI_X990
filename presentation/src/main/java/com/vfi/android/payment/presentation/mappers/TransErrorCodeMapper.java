package com.vfi.android.payment.presentation.mappers;

import com.vfi.android.domain.entities.consts.TransErrorCode;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.ResUtil;

public class TransErrorCodeMapper {
    public static String toErrorString(int transErrorCode) {
        switch (transErrorCode) {
            case TransErrorCode.ARPC_CHECK_FAILED:
                return ResUtil.getString(R.string.tv_hint_arpc_error);
            case TransErrorCode.TRANS_REJECT:
                return ResUtil.getString(R.string.tv_hint_trans_declined);
            case TransErrorCode.TRANS_NOT_FOUND:
                return ResUtil.getString(R.string.tv_hint_trans_not_found);
            case TransErrorCode.RESP_F61_NOT_CORRECT:
                return ResUtil.getString(R.string.tv_hint_resp_msg_f61_not_correct);
        }

        return ResUtil.getString(R.string.tv_hint_trans_declined);
    }
}
