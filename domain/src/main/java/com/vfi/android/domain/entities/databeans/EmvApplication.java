package com.vfi.android.domain.entities.databeans;


import com.vfi.android.domain.utils.LogUtil;

public class EmvApplication {
    private String aid = "";
    private String verNum = "";
    private String appName = "";
    private String asi = "";
    private String floorLimit = "";
    private String threshold = "";
    private String targetPercentage = "";
    private String maxTargetPercentage = "";
    private String floorLimit_Intl = "";
    private String threshold_Intl = "";
    private String targetPercentage_Intl = "";
    private String maxTargetPercentage_Intl = "";
    private String tac_Denial = "";
    private String tac_Online = "";
    private String tac_Default = "";
    private String emv_Application = "";
    private String defaultTDOL = "";
    private String defaultDDOL = "";
    private String merchIdent = "";
    private String countryCodeTerm = "";
    private String currencyCode = "";
    private String appTerminalType = "";
    private String appTermCap = "";
    private String appTermAddCap = "";
    private String ecTransLimit = "";
    private String ctls_TAC_Denial = "";
    private String ctls_TAC_Online = "";
    private String ctls_TAC_Default = "";
    private String ctlsTransLimit = "";
    private String ctlsFloorLimit = "";
    private String ctlsCVMLimit = "";
    private String masterAID = "";
    private String terminalTransactionQualifiers = "";
    private String terminalPriorityIndicator;
    private String defaultUDOL = "";

    private boolean isCTLSEmvParam;

    public EmvApplication(boolean isCTLSEmvParam) {
        this.isCTLSEmvParam = isCTLSEmvParam;
    }

    public String toEmvAppString(boolean isCTLS) {
        String emvApp = "";
        emvApp += getEmvAppTagStr("AID", getAid());
        emvApp += getEmvAppTagStr("VerNum", getVerNum());
        emvApp += getEmvAppTagStr("AppName", getAppName());
        emvApp += getEmvAppTagStr("ASI", getAsi());
        emvApp += getEmvAppTagStr("FloorLimit", getFloorLimit());
        emvApp += getEmvAppTagStr("Threshold", getThreshold());
        emvApp += getEmvAppTagStr("TargetPercentage", getTargetPercentage());
        emvApp += getEmvAppTagStr("MaxTargetPercentage", getMaxTargetPercentage());
        emvApp += getEmvAppTagStr("FloorLimit_Intl", getFloorLimit_Intl());
        emvApp += getEmvAppTagStr("Threshold_Intl", getThreshold_Intl());
        emvApp += getEmvAppTagStr("TargetPercentage_Intl", getTargetPercentage_Intl());
        emvApp += getEmvAppTagStr("MaxTargetPercentage_Intl", getMaxTargetPercentage_Intl());
        emvApp += getEmvAppTagStr("EmvApplication", getEmv_Application());
        emvApp += getEmvAppTagStr("DefaultTDOL", getDefaultTDOL());
        emvApp += getEmvAppTagStr("DefaultDDOL", getDefaultDDOL());
        emvApp += getEmvAppTagStr("MerchIdent", getMerchIdent());
        emvApp += getEmvAppTagStr("CountryCodeTerm", getCountryCodeTerm());
        emvApp += getEmvAppTagStr("CurrencyCode", getCurrencyCode());
        emvApp += getEmvAppTagStr("AppTerminalType", getAppTerminalType());
        emvApp += getEmvAppTagStr("AppTermCap", getAppTermCap());
        emvApp += getEmvAppTagStr("AppTermAddCap", getAppTermAddCap());
        emvApp += getEmvAppTagStr("ECTransLimit", getEcTransLimit());
        emvApp += getEmvAppTagStr("MasterAID", getMasterAID());
        emvApp += getEmvAppTagStr("TerminalPriorityIndicator", getMasterAID());

        if (isCTLS) {
            if (ctls_TAC_Default == null || ctls_TAC_Default.length() == 0) {
                emvApp += getEmvAppTagStr("TAC_Denial", getTac_Denial());
                emvApp += getEmvAppTagStr("TAC_Online", getTac_Online());
                emvApp += getEmvAppTagStr("TAC_Default", getTac_Default());
            } else {
                emvApp += getEmvAppTagStr("CTLS_TAC_Denial", getCtls_TAC_Denial());
                emvApp += getEmvAppTagStr("CTLS_TAC_Online", getCtls_TAC_Online());
                emvApp += getEmvAppTagStr("CTLS_TAC_Default", getCtls_TAC_Default());
            }

            if (aid.startsWith("A000000004")) {
                long ctlsTransLimitLong = Long.parseLong(ctlsTransLimit);
                if (ctlsTransLimitLong >= 9999999999L) {
                    ctlsTransLimitLong = 9999999999L;
                    emvApp += getEmvAppTagStr("CTLSTransLimit", String.format("%012d", ctlsTransLimitLong));
                } else {
                    ctlsTransLimitLong += 1;
                    emvApp += getEmvAppTagStr("CTLSTransLimit", String.format("%012d", ctlsTransLimitLong));
                }
                // make cvm limit master card works
                emvApp += "DF81180170DF811E0120";
                //
                emvApp += "9F1D086CE2800000000000";
                emvApp += "DF811B01A0";//https://redmine.verifone.cn/redmine/issues/304
            } else {
                emvApp += getEmvAppTagStr("CTLSTransLimit", getCtlsTransLimit());
            }
            emvApp += getEmvAppTagStr("CTLSFloorLimit", getCtlsFloorLimit());
            emvApp += getEmvAppTagStr("CTLSCVMLimit", getCtlsCVMLimit());
            if (terminalTransactionQualifiers != null && !terminalTransactionQualifiers.equals("00000000")) {
                emvApp += getEmvAppTagStr("TerminalTransctionQualifiers", getTerminalTransactionQualifiers());
            }
            emvApp += getEmvAppTagStr("DefaultUDOL", getDefaultUDOL());
        } else {
            emvApp += getEmvAppTagStr("TAC_Denial", getTac_Denial());
            emvApp += getEmvAppTagStr("TAC_Online", getTac_Online());
            emvApp += getEmvAppTagStr("TAC_Default", getTac_Default());
        }

        emvApp += "DF180101"; // default

        LogUtil.d("TAG", "is contactless:" + isCTLS + ",aid:" + getAid() + ",Emv aid=[" + emvApp + "]");
        return emvApp;
    }

    public String getEmvTagByBeanTagName(String beanTagName) {
        if (beanTagName != null) {
            switch (beanTagName) {
                case "AID":
                    return "9F06";
                case "VerNum":
                    return "9F09";
                case "AppName":
                    return "";
                case "ASI":
                    return "DF01";
                case "FloorLimit":
                    return "9F1B";
                case "Threshold":
                    return "DF15";
                case "TargetPercentage":
                    return "DF17";
                case "MaxTargetPercentage":
                    return "DF16";
                case "FloorLimit_Intl":
                    return "";
                case "Threshold_Intl":
                    return "DF25";
                case "TargetPercentage_Intl":
                    return "DF27";
                case "MaxTargetPercentage_Intl":
                    return "DF26";
                case "TAC_Denial":
                    return "DF13";
                case "TAC_Online":
                    return "DF12";
                case "TAC_Default":
                    return "DF11";
                case "EMV_Application":
                    return "";
                case "DefaultTDOL":
                    return "97";
                case "DefaultDDOL":
                    return "DF14";
                case "MerchIdent":
                    return "9F15";
                case "CountryCodeTerm":
                    return "9F1A";
                case "CurrencyCode":
                    return "5F2A";
                case "AppTerminalType":
                    return "9F35";
                case "AppTermCap":
                    return "9F33";
                case "AppTermAddCap":
                    return "9F40";
                case "ECTransLimit":
                    return "9F7B";
                case "CTLS_TAC_Denial":
                    return "DF13";
                case "CTLS_TAC_Online":
                    return "DF12";
                case "CTLS_TAC_Default":
                    return "DF11";
                case "CTLSTransLimit":
                    return "DF20";
                case "CTLSFloorLimit":
                    return "DF19";
                case "CTLSCVMLimit":
                    return "DF21";
                case "MasterAID":
                    return "";
                case "TerminalTransctionQualifiers":
                    return "9F66";
                case "TerminalPriorityIndicator":
                    return "DF08";
                case "DefaultUDOL":
                    return "DF811A";
                default:
                    return "";
            }
        }
        return "";
    }

    public String getEmvAppTagStr(String beanTagName, String value) {
        String emvAppStr = "";

        if (beanTagName == null || beanTagName.length() == 0
                || value == null || value.length() == 0) {
            return "";
        }

        String emvTag = getEmvTagByBeanTagName(beanTagName);
        if (emvTag.length() == 0) {
            return "";
        }

        if (value.length() % 2 != 0) {
            value = "0" + value;
        }

        emvAppStr += emvTag;
        String lenStr = getLenStr(value.length());
        emvAppStr += lenStr;
        emvAppStr += value;

        LogUtil.d("TAG", "emvAppStr=[" + emvAppStr + "]");
        return emvAppStr;
    }

    public String getLenStr(int len) {
        String lenStr = "";
        LogUtil.d("TAG", "In len=" + len);
        if (len == 0) {
            lenStr = "00";
        } else {
            lenStr = String.format("%02X", len / 2);
        }
        LogUtil.d("TAG", "Out len=" + lenStr);
        return lenStr;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public void setVerNum(String verNum) {
        this.verNum = verNum;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAsi(String asi) {
        this.asi = asi;
    }

    public void setFloorLimit(String floorLimit) {
        this.floorLimit = floorLimit;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public void setTargetPercentage(String targetPercentage) {
        this.targetPercentage = targetPercentage;
    }

    public void setMaxTargetPercentage(String maxTargetPercentage) {
        this.maxTargetPercentage = maxTargetPercentage;
    }

    public void setTac_Denial(String tac_Denial) {
        this.tac_Denial = tac_Denial;
    }

    public void setTac_Online(String tac_Online) {
        this.tac_Online = tac_Online;
    }

    public void setTac_Default(String tac_Default) {
        this.tac_Default = tac_Default;
    }

    public void setEmv_Application(String emv_Application) {
        this.emv_Application = emv_Application;
    }

    public void setDefaultTDOL(String defaultTDOL) {
        this.defaultTDOL = defaultTDOL;
    }

    public void setDefaultDDOL(String defaultDDOL) {
        this.defaultDDOL = defaultDDOL;
    }

    public void setMerchIdent(String merchIdent) {
        this.merchIdent = merchIdent;
    }

    public void setCountryCodeTerm(String countryCodeTerm) {
        this.countryCodeTerm = countryCodeTerm;
    }

    public void setCurrencyCode(String currencyCode) {

        this.currencyCode = currencyCode;

    }

    public void setAppTerminalType(String appTerminalType) {
        this.appTerminalType = appTerminalType;
    }

    public void setAppTermCap(String appTermCap) {
        this.appTermCap = appTermCap;
    }

    public void setAppTermAddCap(String appTermAddCap) {
        this.appTermAddCap = appTermAddCap;
    }

    public void setEcTransLimit(String ecTransLimit) {
        this.ecTransLimit = ecTransLimit;
    }

    public void setCtlsTransLimit(String ctlsTransLimit) {
        this.ctlsTransLimit = ctlsTransLimit;
    }

    public void setCtlsFloorLimit(String ctlsFloorLimit) {
        this.ctlsFloorLimit = ctlsFloorLimit;
    }

    public void setCtlsCVMLimit(String ctlsCVMLimit) {
        this.ctlsCVMLimit = ctlsCVMLimit;
    }

    public void setMasterAID(String masterAID) {
        this.masterAID = masterAID;
    }

    public void setFloorLimit_Intl(String floorLimit_Intl) {
        this.floorLimit_Intl = floorLimit_Intl;
    }

    public void setThreshold_Intl(String threshold_Intl) {
        this.threshold_Intl = threshold_Intl;
    }

    public void setTargetPercentage_Intl(String targetPercentage_Intl) {
        this.targetPercentage_Intl = targetPercentage_Intl;
    }

    public void setMaxTargetPercentage_Intl(String maxTargetPercentage_Intl) {
        this.maxTargetPercentage_Intl = maxTargetPercentage_Intl;
    }

    public void setCtls_TAC_Denial(String ctls_TAC_Denial) {
        this.ctls_TAC_Denial = ctls_TAC_Denial;
    }

    public void setCtls_TAC_Online(String ctls_TAC_Online) {
        this.ctls_TAC_Online = ctls_TAC_Online;
    }

    public void setCtls_TAC_Default(String ctls_TAC_Default) {
        this.ctls_TAC_Default = ctls_TAC_Default;
    }

    public String getAid() {
        return aid;
    }

    public String getVerNum() {
        if (isEmpty(verNum)) {
            verNum = "0000";
        }
        return verNum;
    }

    public String getAppName() {
        return appName;
    }

    public String getAsi() {
        if (isEmpty(asi)) {
            asi = "00";
        }
        return asi;
    }

    public String getFloorLimit() {
        if (isEmpty(floorLimit)) {
            floorLimit = "0000000000";
        }
        return floorLimit;
    }

    public String getThreshold() {
        if (isEmpty(threshold)) {
            threshold = "0000000000";
        }
        return threshold;
    }

    public String getTargetPercentage() {
        if (isEmpty(targetPercentage)) {
            targetPercentage = "00";
        }
        return targetPercentage;
    }

    public String getMaxTargetPercentage() {
        if (isEmpty(maxTargetPercentage)) {
            maxTargetPercentage = "00";
        }
        return maxTargetPercentage;
    }

    public String getFloorLimit_Intl() {
        return floorLimit_Intl;
    }

    public String getThreshold_Intl() {
        return threshold_Intl;
    }

    public String getTargetPercentage_Intl() {
        return targetPercentage_Intl;
    }

    public String getMaxTargetPercentage_Intl() {
        return maxTargetPercentage_Intl;
    }

    public String getTac_Denial() {
        if (isEmpty(tac_Denial)) {
            tac_Denial = "0000000000";
        }
        return tac_Denial;
    }

    public String getTac_Online() {
        if (isEmpty(tac_Online)) {
            tac_Online = "0000000000";
        }
        return tac_Online;
    }

    public String getTac_Default() {
        if (isEmpty(tac_Default)) {
            tac_Default = "0000000000";
        }
        return tac_Default;
    }

    public String getEmv_Application() {
        return emv_Application;
    }

    public String getDefaultTDOL() {
        return defaultTDOL;
    }

    public String getDefaultDDOL() {
        return defaultDDOL;
    }

    public String getMerchIdent() {
        return merchIdent;
    }

    public String getCountryCodeTerm() {
        return countryCodeTerm;
    }

    public String getCurrencyCode() {
    return currencyCode;


    }

    public String getAppTerminalType() {
        return appTerminalType;
    }

    public String getAppTermCap() {
        return appTermCap;
    }

    public String getAppTermAddCap() {
        return appTermAddCap;
    }

    public String getEcTransLimit() {
        return ecTransLimit;
    }

    public String getCtls_TAC_Denial() {
        if (isEmpty(ctls_TAC_Denial)) {
            ctls_TAC_Denial = "0000000000";
        }
        return ctls_TAC_Denial;
    }

    public String getCtls_TAC_Online() {
        if (isEmpty(ctls_TAC_Online)) {
            ctls_TAC_Online = "0000000000";
        }
        return ctls_TAC_Online;
    }

    public String getCtls_TAC_Default() {
        if (isEmpty(ctls_TAC_Default)) {
            ctls_TAC_Default = "0000000000";
        }
        return ctls_TAC_Default;
    }

    public String getCtlsTransLimit() {
        if (isEmpty(ctlsTransLimit)) {
            ctlsTransLimit = "0000000000";
        }
        return ctlsTransLimit;
    }

    public String getCtlsFloorLimit() {
        if (isEmpty(ctlsFloorLimit)) {
            ctlsFloorLimit = "0000000000";
        }
        return ctlsFloorLimit;
    }

    public String getCtlsCVMLimit() {
        if (isEmpty(ctlsCVMLimit)) {
            ctlsCVMLimit = "0000000000";
        }
        return ctlsCVMLimit;
    }

    public String getMasterAID() {
        return masterAID;
    }

    public String getTerminalTransactionQualifiers() {
        return terminalTransactionQualifiers;
    }

    public void setTerminalTransactionQualifiers(String terminalTransactionQualifiers) {
        this.terminalTransactionQualifiers = terminalTransactionQualifiers;
    }

    public String getTerminalPriorityIndicator() {
        return terminalPriorityIndicator;
    }

    public void setTerminalPriorityIndicator(String terminalPriorityIndicator) {
        this.terminalPriorityIndicator = terminalPriorityIndicator;
    }

    private boolean isEmpty(String value) {
        if (value == null || value.length() == 0) {
            return true;
        }

        return false;
    }

    public String getDefaultUDOL() {
        if (isEmpty(defaultDDOL)) {
            return "9F6A04";
        }
        return defaultUDOL;
    }

    public void setDefaultUDOL(String defaultUDOL) {
        this.defaultUDOL = defaultUDOL;
    }

    public boolean isCTLSEmvParam() {
        return isCTLSEmvParam;
    }

    public void setCTLSEmvParam(boolean CTLSEmvParam) {
        isCTLSEmvParam = CTLSEmvParam;
    }
}
