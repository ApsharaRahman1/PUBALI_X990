package com.vfi.android.payment.presentation.mappers;

import com.vfi.android.domain.entities.consts.EMVResult;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.ResUtil;

public class EmvErrorCodeMapper {
    public static String toErrorString(int emvResult) {
        if (emvResult == EMVResult.AARESULT_AAC.getId()) {
            return ResUtil.getString(R.string.tv_hint_trans_reject);
        } else if (emvResult == EMVResult.EMV_ERROR.getId()) {
            return ResUtil.getString(R.string.tv_hint_trans_reject);
        } else if (emvResult == EMVResult.EMV_DATA_AUTH_FAIL.getId()) {
            return ResUtil.getString(R.string.tv_hint_offline_dda_failed);
        } else if (emvResult == EMVResult.EMV_APP_BLOCKED.getId()) {
            return ResUtil.getString(R.string.tv_hint_app_blocked);
        } else if (emvResult == EMVResult.EMV_PAN_NOT_MATCH_TRACK2.getId()) {
            return ResUtil.getString(R.string.tv_hint_pan_not_match_track2);
        } else if (emvResult == EMVResult.EMV_CARD_HOLDER_VALIDATE_ERROR.getId()) {
            return ResUtil.getString(R.string.tv_hint_card_validate_error);
        } else if (emvResult == EMVResult.EMV_AMOUNT_EXCEED_ON_RFLIMIT_CHECK.getId()) {
            return ResUtil.getString(R.string.tv_hint_amount_exceeded_limit);
        } else if (emvResult == EMVResult.EMV_CARD_BIN_CHECK_FAIL.getId()) {
            return ResUtil.getString(R.string.tv_hint_read_card_failed);
        } else if (emvResult == EMVResult.EMV_CARD_BLOCKED.getId()) {
            return ResUtil.getString(R.string.tv_hint_card_blocked);
        } else if (emvResult == EMVResult.EMV_MULTI_CARD_ERROR.getId()) {
            return ResUtil.getString(R.string.tv_hint_multi_card_conflict);
        } else if (emvResult == EMVResult.EMV_RFCARD_PASS_FAIL.getId()) {
            return ResUtil.getString(R.string.tv_hint_read_rf_card_failed);
        } else if (emvResult == EMVResult.ONLINE_RESULT_SCRIPT_NOT_EXECUTE.getId()) {
            return ResUtil.getString(R.string.tv_hint_script_not_execute);
        } else if (emvResult == EMVResult.ONLINE_RESULT_SCRIPT_EXECUTE_FAIL.getId()) {
            return ResUtil.getString(R.string.tv_hint_script_execute_failed);
        } else if (emvResult == EMVResult.ONLINE_RESULT_NO_SCRIPT.getId()) {
            return ResUtil.getString(R.string.tv_hint_no_script);
        } else if (emvResult == EMVResult.ONLINE_RESULT_TOO_MANY_SCRIPT.getId()) {
            return ResUtil.getString(R.string.tv_hint_too_many_script);
        } else if (emvResult == EMVResult.ONLINE_RESULT_TERMINATE.getId()) {
            return ResUtil.getString(R.string.tv_hint_arpc_mismatch);
        } else if (emvResult == EMVResult.CTLS_NOT_CPU_CARD.getId()) {
            return ResUtil.getString(R.string.tv_hint_not_correct_card_type);
        } else if (emvResult == EMVResult.EMV_INITIAL_FAILED.getId()) {
            return ResUtil.getString(R.string.tv_hint_no_aid_and_capk);
        }

        return ResUtil.getString(R.string.tv_hint_trans_reject);
    }
}

