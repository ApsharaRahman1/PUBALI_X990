package com.vfi.android.domain.entities.consts;

/**
 * Created by laikey on 2017/7/4.
 */
public class EMVConstant {
    //卡类别
    public static final int CARD_INSERT = 0;
    public static final int CARD_RF = 1;

    //交易类型
    public static final int EC_BALANCE = 1; //查询电子现金余额</li>
    public static final int TRANSFER = 2; //转账</li>
    public static final int EC_LOAD = 3; //指定账户圈存</li>
    public static final int EC_LOAD_U = 4; //非制定账户圈存</li>
    public static final int EC_LOAD_CASH = 5; //现金圈存/现金充值</li>
    public static final int EC_LOAD_CASH_VOID = 6; //圈存撤销</li>
    public static final int PURCHASE = 7; //消费</li>
    public static final int Q_PURCHASE = 8; //快速支付</li>
    public static final int CHECK_BALANCE = 9; //余额查询</li>
    public static final int PRE_AUTH = 10; //预授权交易</li>
    public static final int SALE_VOID = 11; //消费撤销</li>
    public static final int SIMPLE_PROCESS = 12; //简易流程交易</li>
    public static final int REFUND = 13; //退货全流程</li>

    //错误返回码
    public static final int ONLINE_RESULT_TC = 0;// 联机成功 </li>
    public static final int ONLINE_RESULT_AAC = 1;// 联机拒绝 </li>
    public static final int ONLINE_RESULT_OFFLINE_TC = 101;// 联机失败，脱机成功 </li>
    public static final int ONLINE_RESULT_SCRIPT_NOT_EXECUTE = 102;// 脚本未执行 </li>
    public static final int ONLINE_RESULT_SCRIPT_EXECUTE_FAIL = 103;// 脚本执行失败 </li>
    public static final int ONLINE_RESULT_NO_SCRIPT = 104;// 联机失败，未下送脚本 </li>
    public static final int ONLINE_RESULT_TOO_MANY_SCRIPT = 105;// 联机失败，脚本超过1个 </li>
    public static final int ONLINE_RESULT_TERMINATE = 106;// 联机失败，交易终止(GAC返回非9000，要提示交易终止,0x8F)
    public static final int ONLINE_RESULT_ERROR = 107;// 联机失败，EMV内核错误</li>
    /**
     * PAN
     */
    public static final String PAN = "PAN";
    /**
     * track 1 data
     */
    public static final String TRACK1 = "TRACK1";
    /**
     * track 2 data
     */
    public static final String TRACK2 = "TRACK2";
    /**
     * track 3 data
     */
    public static final String TRACK3 = "TRACK3";
    /**
     * card service code
     */
    public static final String SERVICE_CODE = "SERVICE_CODE";
    /**
     * card expired date
     */
    public static final String EXPIRED_DATE = "EXPIRED_DATE";
    /**
     * card sn number
     */
    public static final String CARD_SN = "CARD_SN";
    /**
     * card issuer
     */
    public static final String CARD_ORG = "CARD_ORG";
    /**
     * transaction date
     */
    public static final String DATE = "DATE";
    /**
     * transaction time
     */
    public static final String TIME = "TIME";
    /**
     * card holder name
     */
    public static final String CARD_HOLDER_NAME = "CARD_HOLDER_NAME";
    /**
     * 手机芯片卡读取包含DF32，DF33，DF34标签的TLV数据
     */
    public static final String UPCARD_TLV = "UPCARD_TLV";
    /**
     *  first ec balance
     */
    public static final String FIR_EC_BALANCE = "FIR_EC_BALANCE";
    /**
     * first ec currency code
     */
    public static final String FIR_EC_CURRENCY = "FIR_EC_CURRENCY";
    /**
     * second ec balance
     */
    public static final String SEC_EC_BALANCE = "SEC_EC_BALANCE";
    /**
     * second ec currency code
     */
    public static final String SEC_EC_CURRENCY = "SEC_EC_CURRENCY";
    /**
     * ARQC request data(TLV format)(upload，CTLS_ARQC or AARRESULT_ARQC)
     */
    public static final String ARQC_DATA = "ARQC_DATA";
    /**
     * Transaction result(TLV format)(TC、AAC、EMV_ERROR)
     */
    public static final String TC_DATA = "TC_DATA";
    /**
     * reversal data(TLV format)
     */
    public static final String REVERSAL_DATA = "REVERSAL_DATA";
    /**
     * script excute result(TLV format)
     */
    public static final String SCRIPT_DATA = "SCRIPT_DATA";
    /**
     * 行为分析结果：AARESULT_ARQC, CTLS_ARQC
     */
    public static final String AARESULT = "AARESULT";
    /**
     * EMV flow result code
     */
    public static final String RESULT = "RESULT";

    /**
     * EMV flow result description
     */
    public static final String ERROR = "ERROR";
    /**
     *PBOC 是否签名
     */
    public static final String SIGNATURE = "SIGNATURE";
    /**
     * PBOC CVM type
     */
    public static final String CTLS_CVMR = "CTLS_CVMR";
}
