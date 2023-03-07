package com.vfi.android.payment.presentation.mappers;

import com.vfi.android.domain.entities.consts.CurrencyCode;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.ResUtil;

import static com.vfi.android.domain.entities.consts.CurrencyCode.*;

/**
 * Created by huan.lu on 2018/9/27.
 */

public class CurrencySelectorMapper {

    public static String view2ShowInMultilingual(String currencyCode) {
        if (currencyCode == null || currencyCode.length() == 0) {
            return "";
        }

        if (currencyCode != null && currencyCode.length() == 4) {
            currencyCode = currencyCode.substring(1);
        }
        switch (currencyCode) {
            case AUD:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_aud);
            case BHD:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_bhd);
            case BDT:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_bdt);
            case BND:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_bnd);
            case CAD:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_cad);
            case LKR:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_lkr);
            case CNY:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_cny);
            case DKK:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_dkk);
            case HKD:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_hkd);
            case INR:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_inr);
            case ILS:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_ils);
            case JPY:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_jpy);
            case KRW:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_krw);
            case MYR:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_myr);
            case OMR:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_omr);
            case NPR:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_npr);
            case NZD:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_nzd);
            case NOK:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_nok);
            case PKR:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_pkr);
            case PHP:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_php);
            case QAR:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_qar);
            case SAR:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_sar);
            case SGD:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_sgd);
            case ZAR:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_zar);
            case SEK:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_sek);
            case CHF:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_chf);
            case THB:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_thb);
            case AED:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_aed);
            case EGP:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_egp);
            case GBP:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_gbp);
            case USD:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_usd);
            case TWD:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_twd);
            case EUR:
                return ResUtil.getString(R.string.tv_hint_currency_in_th_eur);
            default:
                return "Error";
        }
    }

    public static String view2ShowInEnMap(String currencyCode) {
        if (currencyCode == null || currencyCode.length() == 0) {
            return "";
        }

        return toFormat(CurrencyCode.toEngString(currencyCode));
    }



    private static String toFormat(String str) {
        return "(" + str + ")";
    }
}
