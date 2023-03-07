package com.vfi.android.domain.entities.consts;

import io.reactivex.annotations.NonNull;

/**
 * Created by Yaping.z on 2018/1/4.
 */

public enum EMVResult {
    AARESULT_TC(0, "交易批准 = 脱机"),
    AARESULT_AAC(1, "交易拒绝"),
    AARESULT_ARQC(2, "请求联机"),
    EMV_NO_APP(8, "EMV - No application"),
    EMV_COMPLETE(9, "EMV简易流程结束"),

    EMV_ERROR(11, "交易终止"),
    EMV_FALLBACK(12, " 读卡失败"),
    EMV_DATA_AUTH_FAIL(13, " 脱机数据认证失败"),
    EMV_APP_BLOCKED(14, " 应用被锁定"),
    EMV_NOT_ECCARD(15, " 非电子现金卡 "),
    EMV_UNSUPPORT_ECCARD(16, " 该交易不支持电子现金 "),
    EMV_AMOUNT_EXCEED_ON_PURELYEC(17, " 纯电子现金卡消费金额超限"),
    EMV_SET_PARAM_ERROR(18, " 参数设置错误 = 9F7A"),
    EMV_PAN_NOT_MATCH_TRACK2(19, " 主账号与二磁道不符"),
    EMV_CARD_HOLDER_VALIDATE_ERROR(20, " 持卡人认证失败"),
    EMV_PURELYEC_REJECT(21, " 纯电子现金卡被拒绝交易"),
    EMV_BALANCE_INSUFFICIENT(22, " 余额不足"),
    EMV_AMOUNT_EXCEED_ON_RFLIMIT_CHECK(23, " 交易金额超过非接限额检查"),
    EMV_CARD_BIN_CHECK_FAIL(24, " 读卡失败"),
    EMV_CARD_BLOCKED(25, " 卡被锁"),
    EMV_MULTI_CARD_ERROR(26, " 多卡冲突"),
    EMV_BALANCE_EXCEED(27, " 余额超出"),
    EMV_COMM_TIMEOUT(29,"EMV指令超时没返回"),
    EMV_RFCARD_PASS_FAIL(60, " 挥卡失败 "),
    EMV_FOREIGNCARD_NOTSUPPORT(90, " 外卡暂不支持 "),

    ONLINE_RESULT_OFFLINE_TC(101, " online false, offline success"),
    ONLINE_RESULT_SCRIPT_NOT_EXECUTE(102, " the script not execute"),
    ONLINE_RESULT_SCRIPT_EXECUTE_FAIL(103, " filure while execute script"),
    ONLINE_RESULT_NO_SCRIPT(104, " online failure, not send the script"),
    ONLINE_RESULT_TOO_MANY_SCRIPT(105, " online failure, more than one script"),
    ONLINE_RESULT_TERMINATE(106, " ARPC mismatch"),
    ONLINE_RESULT_ERROR(107, " online failure, error in EMV"),

    CTLS_ARQC(201, " CTLS request online"),
    CTLS_AAC(202, " CTLS transaction reject"),
    CTLS_ERROR(203, " CTLS transaction failed"),
    CTLS_TC(204, " CTLS transaction approve"),
    CTLS_CONT(205, " qPBOC转接触式卡"),
    CTLS_NO_APP(206, " qPBOC无应用"),
    CTLS_NOT_CPU_CARD(207, " qPBOC交易结果，该卡非TYPE B/PRO卡"),

    CTLS_SEE_PHONE(150," CTLS see phone"),

    EMV_INITIAL_FAILED(301, "Emv init failed"); // NO AID&RID

    private int id;
    private String msg;

    EMVResult(int id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static EMVResult findPbocResultById(@NonNull int id) {
        for (EMVResult EMVResult : EMVResult.values()) {
            if (EMVResult.getId() == id) {
                return EMVResult;
            }
        }
        return EMV_ERROR;
    }
}
