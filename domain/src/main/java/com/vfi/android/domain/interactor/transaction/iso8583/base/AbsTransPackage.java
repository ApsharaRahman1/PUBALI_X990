package com.vfi.android.domain.interactor.transaction.iso8583.base;

import com.vfi.android.domain.BuildConfig;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.entities.businessbeans.TransAttribute;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.PackageError;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.interactor.transaction.iso8583.Iso8583BitProfile;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.utils.Isojson;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbsTransPackage implements ISO8583Field {
    protected final String TAG = TAGS.ISO_8583;
    private final TransPackageData transPackageData;
    private int transType;
    private RecordInfo recordInfo;
    private JSONObject jsonSendData;
    private JSONObject jsonRecvData;

    public AbsTransPackage(TransPackageData transPackageData) {
        this.transPackageData = transPackageData;

        this.transType = transPackageData.getTransType();
        this.recordInfo = transPackageData.getRecordInfo();
    }

    protected abstract int[] getTransFields();

    public byte[] packISOMessage() throws CommonException {
        String bitAttrStr = Iso8583BitProfile.bitArr_req.replaceAll( "'", "\"" );
        if (!Isojson.setBitFormat( bitAttrStr )) {
            throw new CommonException(ExceptionType.PACKAGE_EXCEPTION, PackageError.PROFILE_FORMAT_WRONG);
        }

        jsonSendData = new JSONObject();
        setISOFieldData(jsonSendData);

        String tpdu = getTPDU();
        LogUtil.d(TAG, "TPDU=" + tpdu);

        String message = tpdu + Isojson.json2IsoData(jsonSendData);

        if (isEnableISOLog()) {
            saveRequestISOMessage(tpdu, jsonSendData);
        }

        return StringUtil.asc2Bcd(message);
    }

    private boolean isEnableISOLog() {
        IRepository iRepository = transPackageData.getiRepository();
        if (iRepository == null) {
            return false;
        }

        TerminalCfg terminalCfg = transPackageData.getiRepository().getTerminalCfg();
        LogUtil.d(TAG, "isEnableISOLog=" + terminalCfg.isEnableISOLog());
        return terminalCfg.isEnableISOLog();
    }

    private void saveRequestISOMessage(String tpdu, JSONObject jsonObject) {
        try {
            jsonObject.put("TPDU", tpdu);
            IRepository iRepository = transPackageData.getiRepository();
            TerminalStatus terminalStatus = iRepository.getTerminalStatus();
            terminalStatus.setReqIsoLog(jsonObject.toString());
            iRepository.putTerminalStatus(terminalStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveResponseISOMessage(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }

        IRepository iRepository = transPackageData.getiRepository();
        TerminalStatus terminalStatus = iRepository.getTerminalStatus();
        terminalStatus.setRespIsoLog(jsonObject.toString());
        iRepository.putTerminalStatus(terminalStatus);
    }

    public void unPackISOMessage(byte[] message) throws CommonException {
        LogUtil.i(TAG, "unPackISOMessage excuted.");
        /**
         * TPDU 5 bytes + Header 6 bytes
         */
        byte[] btTpdu = new byte[5];
        byte[] btIso8583 = new byte[message.length - 5];
        boolean isTraceNumNotMatch = false;
        boolean isTerminalIdMismatch = false;
        boolean isMerchantIdMismatch = false;

        String bitAttrStr = Iso8583BitProfile.bitArr_req.replaceAll("'", "\"");
        if (!Isojson.setBitFormat( bitAttrStr )) {
            throw new CommonException(ExceptionType.PACKAGE_EXCEPTION, PackageError.PROFILE_FORMAT_WRONG);
        }

        System.arraycopy(message, 0, btTpdu, 0, 5);
        System.arraycopy(message, 5, btIso8583, 0, message.length - 5);

        String strIso8583 = StringUtil.bcd2Str(btIso8583);
        LogUtil.i(TAG, "strIso8583: " + strIso8583);

        jsonRecvData = Isojson.isoData2Json(strIso8583);

        if (isEnableISOLog()) {
            saveResponseISOMessage(jsonRecvData);
        }

        if (getReceiveDataField(11) != null) {
            if (!getSendDataField(11).equals(getReceiveDataField(11))) {
                LogUtil.i(TAG, "check SysTrace error => " + " send->SysTrace " + getSendDataField(11) + " receive->SysTrace " + getReceiveDataField(11));
                isTraceNumNotMatch = true;
            }
        }

        if (getReceiveDataField(12) != null) {
            transPackageData.getRecordInfo().setTransTime(getReceiveDataField(12));
        }

        if (getReceiveDataField(13) != null) {
            transPackageData.getRecordInfo().setTransDate(StringUtil.getSystemYear()+getReceiveDataField(13));
        }

        if (getReceiveDataField(15) != null) {
            transPackageData.getRecordInfo().setBatchSettleDate(getReceiveDataField(15));
        }

        if (getReceiveDataField(23) != null) {
            transPackageData.getRecordInfo().setCardSequenceNum(getReceiveDataField(23));
        }

        if (getReceiveDataField(37) != null) {
            transPackageData.getRecordInfo().setRefNo(getReceiveDataField(37));
        }

        if (getReceiveDataField(38) != null) {
            transPackageData.getRecordInfo().setAuthCode(getReceiveDataField(38));
        }

        String resCode = getReceiveDataField(39);
        LogUtil.d(TAG, "respCode=" + resCode);
        if (resCode == null || resCode.length() == 0) {
            throw new CommonException(ExceptionType.PACKAGE_EXCEPTION, PackageError.NO_RESP_CODE);
        } else {
            transPackageData.getRecordInfo().setRspCode(resCode);
        }

        if (getReceiveDataField(41) != null) {
            if (!getSendDataField(41).equals(getReceiveDataField(41))) {
                LogUtil.i(TAG, "check TerminalID error => " + " send->TerminalID [" + getSendDataField(41) + "] receive->TerminalID [" + getReceiveDataField(41) + "]");
                isTerminalIdMismatch = true;
            }
        }

        if (getReceiveDataField(42) != null) {
            if (!getSendDataField(42).equals(getReceiveDataField(42))) {
                if (!getSendDataField(42).equals(getReceiveDataField(42).replace(" ", ""))) {
                    LogUtil.i(TAG, "check MerchantID error => " + " send->MerchantID " + getSendDataField(42) + "] receive->MerchantID [" + getReceiveDataField(42) + "]");
                    isMerchantIdMismatch = true;
                }
            }
        }

        if (getReceiveDataField(55) != null){
            transPackageData.getRecordInfo().setField55(getReceiveDataField(55));
        }

        String filed63 = getReceiveDataField(63);
        if (filed63 != null) {

        }

        if (isTraceNumNotMatch) {
            throw new CommonException(ExceptionType.PACKAGE_EXCEPTION, PackageError.TRACE_NUM_MISMATCH);
        }

        if (isTerminalIdMismatch) {
            throw new CommonException(ExceptionType.PACKAGE_EXCEPTION, PackageError.TERMINAL_ID_MISMATCH);
        }

        if (isMerchantIdMismatch) {
            throw new CommonException(ExceptionType.PACKAGE_EXCEPTION, PackageError.MERCHANT_ID_MISMATCH);
        }
    }

    public String getResponseField(int field) {
        return getReceiveDataField(field);
    }

    private String getTPDU() {
        return transPackageData.getTpdu();
    }

    private void setISOFieldData(JSONObject jsonData) {
        if (jsonData == null) {
            return;
        }

        int[] transFields = getTransFields();
        if (transFields == null) {
            LogUtil.d(TAG, "No fields found.");
            return;
        }

        for(int i = 0; i < transFields.length; i++) {
            String fieldData = getFieldData(transFields[i]);
            if (BuildConfig.DEBUG) {
                LogUtil.d(TAG, "field[" + transFields[i] + "]" + "=" + fieldData);
            }
            Isojson.setBit(jsonData, transFields[i], fieldData);
        }
    }

    private String getFieldData(int field) {
        String fieldData = null;

        switch (field) {
            case 0:
                fieldData = f0_messageType();
                break;
            case 2:
                fieldData = f2_pan();
                break;
            case 3:
                fieldData = f3_processCode();
                break;
            case 4:
                fieldData = f4_amount();
                break;
            case 6:
                fieldData = f6_foreignAmount();
                break;
            case 7:
                fieldData = f7_transDateTime();
                break;
            case 11:
                fieldData = f11_traceNum();
                break;
            case 12:
                fieldData = f12_localTime();
                break;
            case 13:
                fieldData = f13_localDate();
                break;
            case 14:
                fieldData = f14_expireDate();
                break;
            case 15:
                fieldData = f15_settleDate();
                break;
            case 22:
                fieldData = f22_posEntryMode();
                break;
            case 23:
                fieldData = f23_cardSeqNum();
                break;
            case 24:
                fieldData = f24_nii();
                break;
            case 25:
                fieldData = f25_posConditionCode();
                break;
            case 26:
                fieldData = f26_servicePCode();
                break;
            case 28:
                fieldData = f28_feeAmount();
                break;
            case 35:
                fieldData = f35_track2Data();
                break;
            case 36:
                fieldData = f36_track3Data();
                break;
            case 37:
                fieldData = f37_retrievalRefNo();
                break;
            case 38:
                fieldData = f38_authIdResp();
                break;
            case 39:
                fieldData = f39_responseCode();
                break;
            case 41:
                fieldData = f41_terminalId();
                break;
            case 42:
                fieldData = f42_cardAcquirerId();
                break;
            case 43:
                fieldData = f43_cardAcquirerName();
                break;
            case 45:
                fieldData = f45_track1Data();
                break;
            case 48:
                fieldData = f48_additionalTextData();
                break;
            case 49:
                fieldData = f49_currencyCode();
                break;
            case 52:
                fieldData = f52_pinData();
                break;
            case 53:
                fieldData = f53_secControlInfo();
                break;
            case 54:
                fieldData = f54_additionalAmount();
                break;
            case 55:
                fieldData = f55_icc_chip_data();
                break;
            case 57:
                fieldData = f57_tleData();
                break;
            case 59:
                fieldData = field_059();
                break;
            case 60:
                fieldData = field_060();
                break;
            case 61:
                fieldData = field_061();
                break;
            case 62:
                fieldData = field_062();
                break;
            case 63:
                fieldData = field_063();
                break;
            case 64:
                fieldData = field_064();
                break;
            default:
                throw new RuntimeException("Must add field here.");
        }

        return fieldData;
    }

    public TransPackageData getTransPackageData() {
        return transPackageData;
    }

    public RecordInfo getRecordInfo() {
        return recordInfo;
    }

    protected String getSendDataField(int bit) {
        String value = Isojson.getField(jsonSendData, bit);
        //LogUtil.i(TAG, "bit: " + bit + "   value: " + value);
        return value;
    }

    protected String getReceiveDataField(int bit) {
        String value = Isojson.getField(jsonRecvData, bit);
        //LogUtil.i(TAG, "bit: " + bit + "   value: " + value);
        return value;
    }

    @Override
    public String f0_messageType() {
        TransAttribute transAttribute = TransAttribute.findTypeByType(transType);
        if (transAttribute == null) {
            throw new RuntimeException("TransAttribute not found.");
        }
        return transAttribute.getMsgId();
    }

    @Override
    public String f2_pan() {
        return recordInfo.getPan();
    }

    @Override
    public String f3_processCode() {
        TransAttribute transAttribute = TransAttribute.findTypeByType(transType);
        return transAttribute.getProcessCode();
    }

    @Override
    public String f4_amount() {
        String amount = recordInfo.getAmount();
        long amountLong = StringUtil.parseLong(amount, 0);
        String tip = recordInfo.getTipAmount();
        long tipAmountLong = StringUtil.parseLong(tip, 0);
        long totalAmount = amountLong + tipAmountLong;
        return String.format("%012d", totalAmount);
    }

    @Override
    public String f6_foreignAmount() {
        return recordInfo.getCurrencyAmount();
    }

    @Override
    public String f7_transDateTime() {
        return null;
    }

    @Override
    public String f11_traceNum() {
        TransAttribute transAttribute = TransAttribute.findTypeByType(transType);
        if (transAttribute.isUseRecordTraceNum()) {
            return recordInfo.getTraceNum();
        } else {
            if (transPackageData.getMerchantInfo() == null) {
                throw new RuntimeException("Missing merchant info");
            } else {
                return transPackageData.getMerchantInfo().getTraceNum();
            }
        }
    }

    @Override
    public String f12_localTime() {
        return recordInfo.getTransTime();
    }

    @Override
    public String f13_localDate() {
        String transDate = recordInfo.getTransDate();
        if (transDate != null && transDate.length() == 8) {
            return transDate.substring(4);
        }

        return transDate;
    }

    @Override
    public String f14_expireDate() {
        return recordInfo.getCardExpiryDate();
    }

    @Override
    public String f15_settleDate() {
        return null;
    }

    @Override
    public String f22_posEntryMode() {
        return recordInfo.getPosEntryMode();
    }

    @Override
    public String f23_cardSeqNum() {
        return recordInfo.getCardSequenceNum();
    }

    @Override
    public String f24_nii() {
        return recordInfo.getNii();
    }

    @Override
    public String f25_posConditionCode() {
        return "00";
    }

    @Override
    public String f26_servicePCode() {
        return null;
    }

    @Override
    public String f28_feeAmount() {
        return null;
    }

    @Override
    public String f35_track2Data() {
        return recordInfo.getTrack2();
    }

    @Override
    public String f36_track3Data() {
        return recordInfo.getTrack3();
    }

    @Override
    public String f37_retrievalRefNo() {
        return recordInfo.getRefNo();
    }

    @Override
    public String f38_authIdResp() {
        return recordInfo.getAuthCode();
    }

    @Override
    public String f39_responseCode() {
        String rspCode = recordInfo.getRspCode();
        if (rspCode != null && rspCode.length() > 0) {
            return rspCode;
        } else {
            return "96";
        }
    }

    @Override
    public String f41_terminalId() {
        return recordInfo.getTerminalId();
    }

    @Override
    public String f42_cardAcquirerId() {
        return recordInfo.getMerchantId();
    }

    @Override
    public String f43_cardAcquirerName() {
        return null;
    }

    @Override
    public String f45_track1Data() {
        return recordInfo.getTrack1();
    }

    @Override
    public String f48_additionalTextData() {
        return recordInfo.getCvv2();
    }

    @Override
    public String f49_currencyCode() {
        return recordInfo.getCurrencyCode();
    }

    @Override
    public String f52_pinData() {
        return recordInfo.getPinBlock();
    }

    @Override
    public String f53_secControlInfo() {
        return null;
    }

    @Override
    public String f54_additionalAmount() {
        return recordInfo.getTipAmount();
    }

    @Override
    public String f55_icc_chip_data() {
        return recordInfo.getField55();
    }

    @Override
    public String f57_tleData() {
        return null;
    }

    @Override
    public String field_059() {
        return null;
    }

    @Override
    public String field_060() {
        return null;
    }

    @Override
    public String field_061() {
        return null;
    }

    @Override
    public String field_062() {
        return recordInfo.getInvoiceNum();
    }

    @Override
    public String field_063() {
        return null;
    }

    @Override
    public String field_064() {
        return null;
    }

}
