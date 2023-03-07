package com.vfi.android.payment.presentation.mappers;

import com.vfi.android.domain.entities.consts.PrintError;
import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.ResUtil;

public class PrintErrorMapper {
    public static String toErrorString(int printErrorCode) {
        switch (printErrorCode) {
            case PrintError.SHORT_OF_PAPER:
                return ResUtil.getString(R.string.tv_hint_print_out_of_paper);
            case PrintError.HARDWARE_WRONG:
                return ResUtil.getString(R.string.tv_hint_printer_hardware_wrong);
            case PrintError.OVERHEAT:
                return ResUtil.getString(R.string.tv_hint_printer_overheating);
            case PrintError.BUFFER_OVERFLOW:
                return ResUtil.getString(R.string.tv_hint_print_buffer_overflow);
            case PrintError.LOW_VOLTAGE:
                return ResUtil.getString(R.string.tv_hint_printer_low_voltage);
            case PrintError.PAPER_JAM:
                return ResUtil.getString(R.string.tv_hint_printer_jam);
            case PrintError.BUSY:
                return ResUtil.getString(R.string.tv_hint_printer_busy);
            default:
                return ResUtil.getString(R.string.tv_hint_print_failed);
        }
    }
}
