package com.vfi.android.payment.presentation.utils;

import android.text.InputType;

import com.vfi.android.domain.utils.EncryptionUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.presentation.models.ParamItemModel;

public class ParamFormatUtil {
    private final int paramFormat;
    private FormatOperation formatOperation;

    public ParamFormatUtil(int paramFormat) {
        this.paramFormat = paramFormat;

        switch (paramFormat) {
            case ParamItemModel.FMT_TIME_HH_MM:
                formatOperation = new DateHHMMFormater();
                break;
            case ParamItemModel.FMT_IP_ADDR:
                formatOperation = new IpAddressFormater(15);
                break;
            case ParamItemModel.FMT_AMOUNT:
                formatOperation = new AmountFormater();
                break;
            case ParamItemModel.FMT_NUMBER_PASSWD_MAX_6:
                formatOperation = new NumberPasswordFormater(6);
                break;
            case ParamItemModel.FMT_NUMBER:
                formatOperation = new BaseNumberKeyboardFormatOperation();
                break;
            case ParamItemModel.FMT_NUM_L_PADDING_FIX_6:
                formatOperation = new FixedNumberLenFormater(6, true);
                break;
            case ParamItemModel.FMT_NUMBER_MAX_6:
                formatOperation = new NumberFormater(6);
                break;
            case ParamItemModel.FMT_PAN_10:
                formatOperation = new FixedNumberLenFormater(10, false);
                break;
            case ParamItemModel.FMT_NUMBER_MAX_2:
                formatOperation = new FixedNumberLenFormater(2, true);
                break;
            case ParamItemModel.FMT_NUMBER_MAX_1:
                formatOperation = new FixedNumberLenFormater(1, false);
                break;
            case ParamItemModel.FMT_NUMBER_MAX_5:
                formatOperation = new FixedNumberLenFormater(5, true);
                break;
            case ParamItemModel.FMT_STR_L_PADDING_FIX_8:
                formatOperation = new FixedStringLenFormater(8, true);
                break;
            case ParamItemModel.FMT_STR_L_PADDING_FIX_15:
                formatOperation = new FixedNumberLenFormater(15, true);
                break;
            case ParamItemModel.FMT_INDEXS_LIST:
                formatOperation = new IndexListFormater(5);
                break;
            case ParamItemModel.FMT_CVV2_MAX:
                formatOperation = new Cvv2Formater(true);
                break;
            case ParamItemModel.FMT_CVV2_MIN:
                formatOperation = new Cvv2Formater(false);
                break;
            default:
                formatOperation = new BaseFormatOperation();
                break;
        }
    }

    public String getFormatHint() {
        if (formatOperation != null) {
            return formatOperation.getFormatHint();
        } else {
            return "";
        }
    }

    public String formatParameter(String parameter) {
        if (formatOperation != null) {
            return formatOperation.formatParam(parameter);
        } else {
            return parameter;
        }
    }

    public boolean isValidParamter(String parameter) {
        if (formatOperation != null) {
            return formatOperation.isValidParam(parameter);
        } else {
            return true;
        }
    }

    public String getInvalidParamHint() {
        if (formatOperation != null) {
            return formatOperation.getInvalidParamHint();
        } else {
            return null;
        }
    }

    public String getFinalSaveValue(String formatParamValue) {
        if (formatOperation != null) {
            return formatOperation.getFinalSaveValue(formatParamValue);
        } else {
            return formatParamValue;
        }
    }

    public int getKeyboardInputType() {
        if (formatOperation != null) {
            return formatOperation.getKeyboardInputType();
        } else {
            return InputType.TYPE_CLASS_TEXT;
        }
    }

    private interface FormatOperation {
        int getKeyboardInputType();
        String getFormatHint();
        String formatParam(String param);
        boolean isValidParam(String param);
        String getInvalidParamHint();
        String getFinalSaveValue(String formatParamValue);
    }

    private class DateHHMMFormater extends BaseNumberKeyboardFormatOperation {
        @Override
        public String getFormatHint() {
            return "00:00";
        }

        @Override
        public String formatParam(String param) {
            param = StringUtil.getNonNullStringRightPadding(param, 4);
            return param.substring(0, 2) + ":" + param.substring(2, 4);
        }
    }

    private class IpAddressFormater extends StringFormater {
        StringBuffer stringBuffer;

        public IpAddressFormater(int maxLen) {
            super(maxLen);
            stringBuffer = new StringBuffer();
        }

        @Override
        public String getFormatHint() {
            return "0.0.0.0";
        }

        @Override
        public boolean isValidParam(String param) {
            return param.matches("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
        }

        @Override
        public String formatParam(String param) {
            stringBuffer.setLength(0);
            byte[] paramBytes = param.getBytes();
            for (int i = 0 ; i < paramBytes.length; i++) {
                if (paramBytes[i] == '.' || Character.isDigit(paramBytes[i])) {
                    if (paramBytes[i] == '.') {
                        stringBuffer.append(".");
                    } else {
                        stringBuffer.append((paramBytes[i] - 48));
                    }
                }
            }
            return stringBuffer.toString();
        }
    }

    private class AmountFormater extends BaseNumberKeyboardFormatOperation {
        @Override
        public String getFormatHint() {
            return "0.00";
        }

        @Override
        public String formatParam(String param) {
            param = param.replace(".", "");
            if (getNoNullString(param).length() > 12) {
                param = param.substring(0, 12);
            }
            return StringUtil.formatAmount(param);
        }

        @Override
        public String getFinalSaveValue(String formatParamValue) {
            formatParamValue = formatParamValue.replace(".", "");
            formatParamValue = formatParamValue.replace(",", "");
            return formatParamValue;
        }
    }

    private class NumberPasswordFormater extends BaseNumberPasswdKeyboardFormatOperation {
        private String value = "";
        private int maxPasswordLen;

        public NumberPasswordFormater(int maxPasswordLen) {
            this.maxPasswordLen = maxPasswordLen;
        }

        @Override
        public String formatParam(String param) {
            if (getNoNullString(param).length() > maxPasswordLen) {
                param = param.substring(0, maxPasswordLen);
            }

            value = getNoNullString(param);

            String format = "%0" + value.length() + "d";
            String starStr = String.format(format, 0);
            return starStr.replace("0", "*");
        }

        @Override
        public String getFinalSaveValue(String formatParamValue) {
            return EncryptionUtil.getMd5HexString(value.getBytes());
        }
    }

    private class IndexListFormater extends StringFormater {
        StringBuffer stringBuffer;

        public IndexListFormater(int maxIndexNum) {
            super(maxIndexNum * 2);
            stringBuffer = new StringBuffer();
        }

        @Override
        public String formatParam(String param) {
            stringBuffer.setLength(0);
            byte[] paramBytes = getNoNullString(param).getBytes();
            for (int i = 0 ; i < paramBytes.length; i++) {
                if ((i != 0 && paramBytes[i] == ',') || Character.isDigit(paramBytes[i])) {
                    if (paramBytes[i] == ',') {
                        stringBuffer.append(",");
                    } else {
                        stringBuffer.append((paramBytes[i] - 48));
                    }
                }
            }
            return stringBuffer.toString();
        }

        @Override
        public String getFinalSaveValue(String formatParamValue) {
            formatParamValue = getNoNullString(formatParamValue);
            if (formatParamValue.length() > 1 && formatParamValue.substring(formatParamValue.length() - 1).equals(",")) {
                formatParamValue = formatParamValue.substring(0, formatParamValue.length() - 1);
            }
            return formatParamValue;
        }
    }

    private class FixedStringLenFormater extends StringFormater {
        private int fixedLen;
        private boolean leftPadding;

        public FixedStringLenFormater(int fixedLen, boolean leftPadding) {
            super(fixedLen);
            this.fixedLen = fixedLen;
            this.leftPadding = leftPadding;
        }

        @Override
        public String getFinalSaveValue(String formatParamValue) {
            if (leftPadding) {
                return StringUtil.getNonNullStringLeftPadding(formatParamValue, fixedLen);
            } else {
                return StringUtil.getNonNullStringLeftPadding(formatParamValue, fixedLen);
            }
        }
    }

    private class StringFormater extends BaseFormatOperation {
        private int maxLen;

        public StringFormater(int maxLen) {
            this.maxLen = maxLen;
        }

        @Override
        public String formatParam(String param) {
            param = getNoNullString(param);
            if (param.length() > maxLen) {
                param = param.substring(0, maxLen);
            }
            return param;
        }
    }

    private class Cvv2Formater extends FixedNumberLenFormater {
        private boolean isCvv2Max;

        public Cvv2Formater(boolean isCvv2Max) {
            super(1, true);
            this.isCvv2Max = isCvv2Max;
        }

        @Override
        public boolean isValidParam(String param) {
            int cvv2Max = StringUtil.parseInt(param, -1);
            if (cvv2Max < 0 || cvv2Max > 9) {
                return false;
            }
            return true;
        }

        @Override
        public String getInvalidParamHint() {
            if (isCvv2Max) {
                return "MAX CVV2[0 - 9]";
            } else {
                return "MIN CVV2[0 - 9]";
            }
        }
    }

    private class FixedNumberLenFormater extends NumberFormater {
        private int fixedLen;
        private boolean leftPadding;

        public FixedNumberLenFormater(int fixedLen, boolean leftPadding) {
            super(fixedLen);
            this.fixedLen = fixedLen;
            this.leftPadding = leftPadding;
        }

        @Override
        public String getFinalSaveValue(String formatParamValue) {
            formatParamValue = getNoNullString(formatParamValue);
            if (formatParamValue.length() < fixedLen) {
                String format = "%0" + (fixedLen - formatParamValue.length()) + "d";
                if (leftPadding) {
                    formatParamValue = String.format(format, 0) + formatParamValue;
                } else {
                    formatParamValue = formatParamValue + String.format(format, 0);
                }
            }

            return formatParamValue;
        }
    }

    private class NumberFormater extends BaseNumberKeyboardFormatOperation {
        private int maxLen;

        public NumberFormater(int maxLen) {
            this.maxLen = maxLen;
        }

        @Override
        public String formatParam(String param) {
            param = getNoNullString(param);
            param = param.replace(".", "");
            if (param.length() > maxLen) {
                param = param.substring(0, maxLen);
            }
            return param;
        }
    }

    private class BaseNumberPasswdKeyboardFormatOperation extends BaseFormatOperation {
        @Override
        public int getKeyboardInputType() {
            return InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER;
        }
    }

    private class BaseNumberKeyboardFormatOperation extends BaseFormatOperation {
        @Override
        public int getKeyboardInputType() {
            return InputType.TYPE_CLASS_NUMBER;
        }
    }

    private class BaseFormatOperation implements FormatOperation {
        @Override
        public int getKeyboardInputType() {
            return InputType.TYPE_CLASS_TEXT;
        }

        @Override
        public String getFormatHint() {
            return "";
        }

        @Override
        public String formatParam(String param) {
            return param;
        }

        @Override
        public boolean isValidParam(String param) {
            return true;
        }

        @Override
        public String getInvalidParamHint() {
            return null;
        }

        @Override
        public String getFinalSaveValue(String formatParamValue) {
            return formatParamValue;
        }
    }

    private String getNoNullString(String value) {
        return StringUtil.getNonNullString(value);
    }
}
