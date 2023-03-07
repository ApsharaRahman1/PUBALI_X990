package com.vfi.android.payment.presentation.mappers;

import com.vfi.android.domain.entities.consts.PackageError;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.ResUtil;

public class PackageErrorMapper {
    public static String toErrorString(int packageErrorCode) {
        switch (packageErrorCode) {
            case PackageError.PROFILE_FORMAT_WRONG:
                return ResUtil.getString(R.string.tv_hint_profile_format_not_correct);
            case PackageError.TRACE_NUM_MISMATCH:
                return ResUtil.getString(R.string.tv_hint_trace_number_mismatch);
            case PackageError.NO_RESP_CODE:
                return ResUtil.getString(R.string.tv_hint_no_approval_code);
            case PackageError.TERMINAL_ID_MISMATCH:
                return ResUtil.getString(R.string.tv_hint_terminal_id_mismatch);
            case PackageError.MERCHANT_ID_MISMATCH:
                return ResUtil.getString(R.string.tv_hint_merchant_id_mismatch);
        }
        
        return "Other error";
    }
}
