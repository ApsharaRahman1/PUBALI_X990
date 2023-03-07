package com.vfi.android.payment.presentation.mappers;

import com.vfi.android.domain.entities.consts.HostRespError;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.ResUtil;

public class HostRespErrorMapper {
    public static String toErrorString(int hostRespErrorCode) {
        switch (hostRespErrorCode) {
            case HostRespError.HOST_RESP_ERR_01:
                return ResUtil.getString(R.string.host_resp_hint_please_call_issuer);
            case HostRespError.HOST_RESP_ERR_02:
                return ResUtil.getString(R.string.host_resp_hint_referral);
            case HostRespError.HOST_RESP_ERR_03:
                return ResUtil.getString(R.string.host_resp_hint_err_call_help_sn);
            case HostRespError.HOST_RESP_ERR_04:
                return ResUtil.getString(R.string.host_resp_hint_pick_up_card);
            case HostRespError.HOST_RESP_ERR_05:
                return ResUtil.getString(R.string.host_resp_hint_decline);
            case HostRespError.HOST_RESP_ERR_07:
                return ResUtil.getString(R.string.host_resp_hint_pick_up_card);
            case HostRespError.HOST_RESP_ERR_12:
                return ResUtil.getString(R.string.host_resp_hint_err_call_help_tr);
            case HostRespError.HOST_RESP_ERR_13:
                return ResUtil.getString(R.string.host_resp_hint_err_call_help_am);
            case HostRespError.HOST_RESP_ERR_14:
                return ResUtil.getString(R.string.host_resp_hint_err_call_help_re);
            case HostRespError.HOST_RESP_ERR_15:
                return ResUtil.getString(R.string.host_resp_hint_card_not_valid);
            case HostRespError.HOST_RESP_ERR_19:
                return ResUtil.getString(R.string.host_resp_hint_reenter_trans);
            case HostRespError.HOST_RESP_ERR_21:
                return ResUtil.getString(R.string.host_resp_hint_not_correct_response);
            case HostRespError.HOST_RESP_ERR_25:
                return ResUtil.getString(R.string.host_resp_hint_err_call_help_nt);
            case HostRespError.HOST_RESP_ERR_28:
                return ResUtil.getString(R.string.host_resp_hint_call_card_center);
            case HostRespError.HOST_RESP_ERR_30:
                return ResUtil.getString(R.string.host_resp_hint_err_call_help_fe);
            case HostRespError.HOST_RESP_ERR_31:
                return ResUtil.getString(R.string.host_resp_hint_call_help_ns);
            case HostRespError.HOST_RESP_ERR_41:
                return ResUtil.getString(R.string.host_resp_hint_lost_card);
            case HostRespError.HOST_RESP_ERR_43:
                return ResUtil.getString(R.string.host_resp_hint_pick_up_card);
            case HostRespError.HOST_RESP_ERR_51:
                return ResUtil.getString(R.string.host_resp_hint_card_declined);
            case HostRespError.HOST_RESP_ERR_52:
            case HostRespError.HOST_RESP_ERR_53:
                return ResUtil.getString(R.string.host_resp_hint_call_card_center);
            case HostRespError.HOST_RESP_ERR_54:
                return ResUtil.getString(R.string.host_resp_hint_card_expired);
            case HostRespError.HOST_RESP_ERR_55:
                return ResUtil.getString(R.string.host_resp_hint_incorrect_pin);
            case HostRespError.HOST_RESP_ERR_57:
                return ResUtil.getString(R.string.host_resp_hint_trans_not_valid);
            case HostRespError.HOST_RESP_ERR_58:
                return ResUtil.getString(R.string.host_resp_hint_trans_not_valid);
            case HostRespError.HOST_RESP_ERR_67:
                return ResUtil.getString(R.string.host_resp_hint_call_card_center);
            case HostRespError.HOST_RESP_ERR_76:
                return ResUtil.getString(R.string.host_resp_hint_err_call_help_dc);
            case HostRespError.HOST_RESP_ERR_77:
                return ResUtil.getString(R.string.host_resp_hint_reconcle_error);
            case HostRespError.HOST_RESP_ERR_78:
                return ResUtil.getString(R.string.host_resp_hint_trace_not_found);
            case HostRespError.HOST_RESP_ERR_80:
                return ResUtil.getString(R.string.host_resp_hint_bad_batch_number);
            case HostRespError.HOST_RESP_ERR_81:
                return ResUtil.getString(R.string.host_resp_hint_pin_cvv_error);
            case HostRespError.HOST_RESP_ERR_82:
                return ResUtil.getString(R.string.host_resp_hint_invalid_cvv);
            case HostRespError.HOST_RESP_ERR_83:
                return ResUtil.getString(R.string.host_resp_hint_no_pin_verify);
            case HostRespError.HOST_RESP_ERR_85:
                return ResUtil.getString(R.string.host_resp_hint_bad_batch_number);
            case HostRespError.HOST_RESP_ERR_89:
                return ResUtil.getString(R.string.host_resp_hint_bad_terminal_id);
            case HostRespError.HOST_RESP_ERR_91:
                return ResUtil.getString(R.string.host_resp_hint_err_call_help_na);
            case HostRespError.HOST_RESP_ERR_93:
                return ResUtil.getString(R.string.host_resp_hint_call_card_center);
            case HostRespError.HOST_RESP_ERR_94:
                return ResUtil.getString(R.string.host_resp_hint_err_call_help_sq);
            case HostRespError.HOST_RESP_ERR_95:
                return ResUtil.getString(R.string.host_resp_hint_batch_xfer_wait);
            case HostRespError.HOST_RESP_ERR_96:
                return ResUtil.getString(R.string.host_resp_hint_err_call_help_se);
        }

        return ResUtil.getString(R.string.host_resp_hint_please_call_issuer);
    }
}
