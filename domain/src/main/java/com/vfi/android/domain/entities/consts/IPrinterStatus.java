package com.vfi.android.domain.entities.consts;

import io.reactivex.annotations.NonNull;

/**
 * Created by fusheng.z on 2017/12/29.
 */

public enum IPrinterStatus {
    ERROR_NONE(0x00, 0, "正常"),
    ERROR_PAPERENDED(0xF0, PrintError.SHORT_OF_PAPER, " 缺纸，不能打印"),
    ERROR_HARDERR(0xF2, PrintError.HARDWARE_WRONG," 硬件错误"),
    ERROR_OVERHEAT(0xF3, PrintError.OVERHEAT, " 打印头过热"),
    ERROR_BUFOVERFLOW(0xF5, PrintError.BUFFER_OVERFLOW, " 缓冲模式下所操作的位置超出范围 "),
    ERROR_LOWVOL(0xE1, PrintError.LOW_VOLTAGE, " 低压保护 "),
    ERROR_PAPERENDING(0xF4, PrintError.PAPERENDING, " 纸张将要用尽，还允许打印(单步进针打特有返回值)"),
    ERROR_MOTORERR(0xFB, PrintError.FAULT, " 打印机芯故障(过快或者过慢)"),
    ERROR_PENOFOUND(0xFC, PrintError.POSITION_NOT_FOUND, " 自动定位没有找到对齐位置,纸张回到原位置"),
    ERROR_PAPERJAM(0xEE, PrintError.PAPER_JAM, " 卡纸"),
    ERROR_NOBM(0xF6, PrintError.NO_BM, " 没有找到黑标"),
    ERROR_BUSY(0xF7, PrintError.BUSY, " 打印机处于忙状态"),
    ERROR_BMBLACK(0xF8, PrintError.BM_BLACK, " 黑标探测器检测到黑色信号"),
    ERROR_WORKON(0xE6, PrintError.WORK_ON, " 打印机电源处于打开状态"),
    ERROR_LIFTHEAD(0xE0, PrintError.LIFTHEAD, " 打印头抬起(自助热敏打印机特有返回值)"),
    ERROR_CUTPOSITIONERR(0xE2, PrintError.CURRENT_POSITION_NOT_CORRECT, " 切纸刀不在原位(自助热敏打印机特有返回值)"),
    ERROR_LOWTEMP(0xE3, PrintError.LOW_TEMPERATURE, " 低温保护或AD出错(自助热敏打印机特有返回值)");

    private int id;
    private int errorCode;
    private String msg;

    IPrinterStatus(int id, int errorCode, String msg) {
        this.id = id;
        this.errorCode = errorCode;
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


    public static IPrinterStatus findPrinterStatusById(@NonNull int id) {
        for (IPrinterStatus status : IPrinterStatus.values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        return ERROR_NONE;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }}
