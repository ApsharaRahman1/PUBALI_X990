package com.vfi.android.domain.entities.consts;

/**
 * Created by Tony
 * 2018/12/17
 * email: chong.z@verifone.cn
 */
public class TransSort {
    /**
     * Normal transaction and has itself reversal. like sale, void, refund
     */
    public static final int NORMAL   = 1;

    /**
     * Normal transaction and has no itself reversal. like Balance inquiry, offline sale
     */
    public static final int NORMAL_NO_REVERSAL   = 2;

    /**
     * Managerial, like logon, settlement and so on
     */
    public static final int MANAGE   = 11;

    /**
     * Feature, like print last transaction, settlement total ...
     */
    public static final int FEATURE  = 21;
}
