package com.vfi.android.payment.presentation.mappers;

import com.vfi.android.domain.entities.comm.CommErrorType;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.ResUtil;

public class CommErrorMapper {
    public static String toErrorString(int commErrorCode) {
        switch (commErrorCode) {
            case CommErrorType.COMM_PARAM_ERROR:
                return ResUtil.getString(R.string.tv_hint_comm_param_error);
            case CommErrorType.HOST_NOT_FOUND:
            case CommErrorType.CONNECT_FAILED:
                return ResUtil.getString(R.string.tv_hint_connect_failed);
            case CommErrorType.CONNECT_TIMEOUT:
                return ResUtil.getString(R.string.tv_hint_connect_timeout);
            case CommErrorType.SEND_DATA_FAILED:
                return ResUtil.getString(R.string.tv_hint_send_data_failed);
            case CommErrorType.READ_DATA_FAILED:
            case CommErrorType.READ_DATA_TIMEOUT:
                return ResUtil.getString(R.string.tv_hint_receive_data_failed);
        }

        return "";
    }
}
