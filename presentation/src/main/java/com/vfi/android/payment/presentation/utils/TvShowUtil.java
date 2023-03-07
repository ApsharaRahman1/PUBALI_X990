package com.vfi.android.payment.presentation.utils;


import com.vfi.android.domain.utils.StringUtil;

import java.text.DecimalFormat;
import java.util.List;

public class TvShowUtil {

    /**
     * Transform amount 1234567.11 -> 1,234,567.11
     *
     * @param amount
     * @return
     */
    public static String formatAmount(String amount) {
        return StringUtil.formatAmount(amount);
    }

    /**
     * format like this:  nnnn nn** **** **** nnnn
     *
     * @param acount
     * @return
     */
    public static String formatAcount(String acount) {
        final int MAX_ACOUNT_LEN = 21;
        final int MIN_ACOUNT_LEN = 6;

        if (acount == null || acount.length() < MIN_ACOUNT_LEN) {
            return acount;
        }

        int acountLen = acount.length();

        if (acountLen > MAX_ACOUNT_LEN) {
            acount = acount.substring(0, MAX_ACOUNT_LEN);
        }

        if (acountLen >= 14) {
            int startMarkCount = acountLen - 10;
            int lastAcountIdx = (acountLen - 10 - 2) % 4;

            int starMarkSpcace = 0;
            if (startMarkCount < 3) {
                starMarkSpcace = 0;
            } else {
                starMarkSpcace = (startMarkCount - 2 + 3) / 4;
            }

            String preAcount = acount.substring(0, 4) + " " + acount.substring(4, 6);
            String midAcount = "** **** **** **** ****".substring(0, startMarkCount + starMarkSpcace);
            String lastAcount = "";

//            LogUtil.d("TAG", "lastAcountIdx=[" + lastAcountIdx + "]");
            if (lastAcountIdx == 0) {
                lastAcount = " " + acount.substring(acountLen - 4, acountLen);
            } else {
                int lastAcountEndIdx = acountLen - lastAcountIdx;
                lastAcount = acount.substring(acountLen - 4, lastAcountEndIdx) + " " + acount.substring(lastAcountEndIdx, acountLen);
            }
            acount = preAcount + midAcount + lastAcount;

//            LogUtil.d("TAG", "pre=[" + preAcount + "]");
//            LogUtil.d("TAG", "mid=[" + midAcount + "]");
//            LogUtil.d("TAG", "last=[" + lastAcount + "]");
//            LogUtil.d("TAG", "acount=[" + acount + "]");
        } else {
            int startMarkCount = acountLen - 6;
            int lastAcountIdx = (acountLen - 6 - 2) % 4;

            int starMarkSpcace = 0;
            if (startMarkCount < 3) {
                starMarkSpcace = 0;
            } else {
                starMarkSpcace = (startMarkCount - 2 + 3) / 4;
            }

            String preAcount = acount.substring(0, 2);
            String midAcount = "** **** **** **** ****".substring(0, startMarkCount + starMarkSpcace);
            String lastAcount = "";

//            LogUtil.d("TAG", "lastAcountIdx=[" + lastAcountIdx + "]");
            if (lastAcountIdx <= 0) {
                lastAcount = " " + acount.substring(acountLen - 4, acountLen);
            } else {
                int lastAcountEndIdx = acountLen - lastAcountIdx;
                lastAcount = acount.substring(acountLen - 4, lastAcountEndIdx) + " " + acount.substring(lastAcountEndIdx, acountLen);
            }
            acount = preAcount + midAcount + lastAcount;

//            LogUtil.d("TAG", "pre=[" + preAcount + "]");
//            LogUtil.d("TAG", "mid=[" + midAcount + "]");
//            LogUtil.d("TAG", "last=[" + lastAcount + "]");
//            LogUtil.d("TAG", "acount=[" + acount + "]");
        }

        return acount;
    }

    public static String[] toStringArray(List<String> list) {
        if (list == null) {
            return new String[0];
        }

        String[] strings = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            strings[i] = list.get(i);
        }
        return strings;
    }

    public static String formatAmount(String a1, String a2) {
        return formatAmount(sumAmount(a1, a2));
    }

    public static String sumAmount(String... amount) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        Double sum = 0d;
        for (String a : amount) {
            try {
                if (a != null && a.length() > 0) {
                    sum += Double.valueOf(a);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return decimalFormat.format(sum);
    }

    public static boolean checkCardNumLuhnResult(String cardNo) {
        try {
            int[] cardNoArr = new int[cardNo.length()];
            for (int i = 0; i < cardNo.length(); i++) {
                cardNoArr[i] = Integer.valueOf(String.valueOf(cardNo.charAt(i)));
            }
            for (int i = cardNoArr.length - 2; i >= 0; i -= 2) {
                cardNoArr[i] <<= 1;
                cardNoArr[i] = cardNoArr[i] / 10 + cardNoArr[i] % 10;
            }
            int sum = 0;
            for (int i = 0; i < cardNoArr.length; i++) {
                sum += cardNoArr[i];
            }
            return sum % 10 == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
