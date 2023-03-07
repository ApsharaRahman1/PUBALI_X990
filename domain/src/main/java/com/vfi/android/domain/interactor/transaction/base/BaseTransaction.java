package com.vfi.android.domain.interactor.transaction.base;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.EmvInformation;
import com.vfi.android.domain.entities.businessbeans.HostInfo;
import com.vfi.android.domain.entities.businessbeans.MerchantInfo;
import com.vfi.android.domain.entities.businessbeans.RecordInfo;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;
import com.vfi.android.domain.entities.businessbeans.TerminalStatus;
import com.vfi.android.domain.entities.businessbeans.TransAttribute;
import com.vfi.android.domain.entities.comm.CommParam;
import com.vfi.android.domain.entities.comm.CommStatus;
import com.vfi.android.domain.entities.comm.CommType;
import com.vfi.android.domain.entities.comm.SocketParam;
import com.vfi.android.domain.entities.consts.CardEntryMode;
import com.vfi.android.domain.entities.consts.EMVResult;
import com.vfi.android.domain.entities.consts.ExceptionType;
import com.vfi.android.domain.entities.consts.TransErrorCode;
import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.databeans.EmvOnlineResultParamIn;
import com.vfi.android.domain.entities.databeans.EmvProcessOnlineResult;
import com.vfi.android.domain.entities.databeans.SettlementPackInfo;
import com.vfi.android.domain.entities.databeans.SettlementRecordItem;
import com.vfi.android.domain.entities.exceptions.CommonException;
import com.vfi.android.domain.entities.tle.TLEPackageInfo;
import com.vfi.android.domain.interfaces.repository.IRepository;
import com.vfi.android.domain.interfaces.service.IPosService;
import com.vfi.android.domain.interactor.transaction.iso8583.TransPackageFactory;
import com.vfi.android.domain.interactor.transaction.iso8583.base.ITransPackage;
import com.vfi.android.domain.interactor.transaction.iso8583.base.TransPackageData;
import com.vfi.android.domain.utils.DateTimeUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.Tlv2Map;

import java.util.Map;

public class BaseTransaction {
    private final String TAG = TAGS.Transaction;
    private final IRepository iRepository;
    private final IPosService posService;

    private CurrentTranData currentTranData;
    private RecordInfo recordInfo;
    private TerminalCfg terminalCfg;
    private HostInfo hostInfo;
    private MerchantInfo merchantInfo;

    private ITransPackage transPackage;
    private int transType;
    private TransAttribute transAttribute;

    public BaseTransaction(IRepository iRepository, IPosService posService, int transType) {
        this.iRepository = iRepository;
        this.posService = posService;
        this.transType = transType;
        this.transAttribute = TransAttribute.findTypeByType(transType);

        currentTranData = iRepository.getCurrentTranData();
        hostInfo = currentTranData.getHostInfo();
        merchantInfo = currentTranData.getMerchantInfo();

        recordInfo = currentTranData.getRecordInfo();
        terminalCfg = iRepository.getTerminalCfg();
    }

    public BaseTransaction(IRepository iRepository, IPosService posService, RecordInfo recordInfo, int transType) {
        this.iRepository = iRepository;
        this.posService = posService;
        this.recordInfo = recordInfo;
        this.transType = transType;
        this.transAttribute = TransAttribute.findTypeByType(transType);

        currentTranData = iRepository.getCurrentTranData();

        hostInfo = iRepository.getHostInfoByHostType(recordInfo.getHostType());
        currentTranData.setHostInfo(hostInfo);
        merchantInfo = iRepository.getMerchantInfo(recordInfo.getMerchantIndex());
        currentTranData.setMerchantInfo(merchantInfo);

        terminalCfg = iRepository.getTerminalCfg();
    }

    public void doCommStatusProcess(int commStatus) {
        switch (commStatus) {
            case CommStatus.CONNECTING:
                break;
            case CommStatus.CONNECTED:
                break;
            case CommStatus.SENDING:
                if (transAttribute.isSupportReversal()) {
                    saveReversal();
                }
                if (transAttribute.isNeedIncreaseInvoice()) {
                    increaseInvoice();
                }
                if (transAttribute.isNeedIncreaseTrace()) {
                    increaseTrace();
                }
                break;
            case CommStatus.SENDED:
                break;
            case CommStatus.RECEIVING:
                break;
            case CommStatus.RECEIVED:
                if (transAttribute.isSupportReversal()) {
                    clearReversal();
                }
                break;
        }
    }

    private TransPackageData createTransPackageData(int transType) {
        TransPackageData transPackageData = new TransPackageData(transType);
        transPackageData.setRecordInfo(recordInfo);
        transPackageData.setMerchantInfo(merchantInfo);
        transPackageData.setiRepository(iRepository);

        if (hostInfo == null) {
            throw new RuntimeException("No host info found.");
        }

        if (merchantInfo == null) {
            throw new RuntimeException("No merchant info found.");
        }


        transPackageData.setTLEEnabled(merchantInfo.isEnableTle());
        if (merchantInfo.isEnableTle()) {
            // TODO
        } else {
            transPackageData.setTpdu(hostInfo.getTPDU());
        }

        return transPackageData;
    }

    public byte[] packISO8583Message() {
        return packISO8583Message(false);
    }

    public byte[] packISO8583Message(boolean isLastBatchUploadRecord) {
        byte[] message = null;

        try {
            TransPackageData transPackageData = createTransPackageData(transType);
            transPackageData.setLastBatchUploadRecord(isLastBatchUploadRecord);

            transPackage = TransPackageFactory.getTransPackage(transType, transPackageData);
            message = transPackage.packISO8583Message();
        } catch (CommonException e) {
            e.printStackTrace();
            message = null;
        }

        return message;
    }

    public byte[] packISO8583Message(SettlementPackInfo settlementPackInfo) {
        byte[] message = null;

        try {
            TransPackageData transPackageData = createTransPackageData(transType);
            transPackageData.setSettlementPackInfo(settlementPackInfo);

            transPackage = TransPackageFactory.getTransPackage(transType, transPackageData);
            message = transPackage.packISO8583Message();
        } catch (CommonException e) {
            e.printStackTrace();
            message = null;
        }

        return message;
    }

    public byte[] packISO8583Message(TLEPackageInfo tlePackageInfo) {
        byte[] message = null;
        try {
            TransPackageData transPackageData = createTransPackageData(transType);
            transPackageData.setTlePackageInfo(tlePackageInfo);
            transPackageData.setTpdu(tlePackageInfo.getTleConfig().getRkiTPDU());
            currentTranData.setRecordInfo(new RecordInfo());

            transPackage = TransPackageFactory.getTransPackage(transType, transPackageData);
            message = transPackage.packISO8583Message();
        } catch (CommonException e) {
            e.printStackTrace();
            message = null;
        }

        return message;
    }

    public void unPackISO8583Message(byte[] message) throws CommonException {
        try {
            transPackage.unPackISO8583Message(message);
        } catch (Exception e) {
            if (isHostApproved() && transAttribute.isSupportReversal()) {
                LogUtil.d(TAG, "Host approval, but app exception, save reversal.");
                saveReversal();
            }
            throw e;
        }
    }

    /**
     * doCommStatusProcess will process increase trace number and invoice number,
     * Please config in TransAttribute {@link TransAttribute}
     */
    public void increaseTrace() {
        if (merchantInfo == null) {
            // should never happened.
            throw new RuntimeException("Missing merchant info.");
        }

        int trace = Integer.parseInt(merchantInfo.getTraceNum()) + 1;
        if (trace > 999999) {
            trace = 1;
        }
        merchantInfo.setTraceNum(String.format("%06d", trace));
        LogUtil.i(TAG, "Increase merchantIndex[" + merchantInfo.getMerchantIndex() + "] traceNum to [" + trace + "]");
        iRepository.putMerchantInfo(merchantInfo);
    }

    /**
     * doCommStatusProcess will process increase trace number and invoice number,
     * Please config in TransAttribute {@link TransAttribute}
     */
    public void increaseInvoice() {
        int invoice = Integer.parseInt(terminalCfg.getSysInvoiceNum()) + 1;
        if (invoice > 999999) {
            invoice = 1;
        }

        terminalCfg.setSysInvoiceNum(String.format("%06d", invoice));
        LogUtil.i(TAG, "Increase invoiceNum to [" + terminalCfg.getSysInvoiceNum() + "]");
        iRepository.putTerminalCfg(terminalCfg);
    }

    public void increaseBatchNum() {
        if (merchantInfo == null) {
            // should never happened.
            throw new RuntimeException("Missing merchant info.");
        }

        int batchNum = Integer.parseInt(merchantInfo.getBatchNum()) + 1;
        if (batchNum > 999999) {
            batchNum = 1;
        }
        merchantInfo.setBatchNum(String.format("%06d", batchNum));
        LogUtil.i(TAG, "Increase merchantIndex[" + merchantInfo.getMerchantId() + "] batchNum to [" + batchNum + "]");
        iRepository.putMerchantInfo(merchantInfo);
    }

    public void saveReversal() {
        LogUtil.d(TAG, "saveReversal executed, traceNum[" + recordInfo.getTraceNum() + "] invoiceNum[" + recordInfo.getInvoiceNum() + "]");

        RecordInfo reversalRecordInfo = recordInfo;
        iRepository.putReversalInfo(reversalRecordInfo);
    }

    public void clearOrigTransRecord() {
        LogUtil.d(TAG, "clearOrigTransRecord org traceNum[" + recordInfo.getVoidOrgTraceNum() + "] org invoiceNum[" + recordInfo.getOrgInvoiceNum() + "] org batchNum[" + recordInfo.getOrgBatchNo() + "]");

        if (recordInfo.getTransType() == TransType.VOID) {
            iRepository.deleteOrigTransRecord(recordInfo.getVoidOrgTraceNum(), recordInfo.getOrgInvoiceNum());
        }
    }

    public void clearBatch(int hostType, int merchantIndex) {
        LogUtil.d(TAG, "Clear batch hostType[" + hostType + "] merchantIndex[" + merchantIndex + "]");
        iRepository.clearBatch(hostType, merchantIndex);
    }

    public void saveTransactionRecord() {
        LogUtil.d(TAG, "saveTransactionRecord traceNum[" + recordInfo.getTraceNum() + "] invoiceNum[" + recordInfo.getInvoiceNum() + "] batchNum[" + recordInfo.getBatchNo() + "]");
        iRepository.putRecordInfo(recordInfo);
    }

    public void saveOfflineTransactionRecord() {
        LogUtil.d(TAG, "saveOfflineTransactionRecord traceNum[" + recordInfo.getTraceNum() + "] invoiceNum[" + recordInfo.getInvoiceNum() + "] batchNum[" + recordInfo.getBatchNo() + "]");
        long time = System.currentTimeMillis();
        recordInfo.setTransDate(DateTimeUtil.formatDate(time));
        recordInfo.setTransTime(DateTimeUtil.formatTime(time));
        saveTransactionRecord();
    }

    public void saveTipAdjustTransRecord() {
        iRepository.deleteOrigTransRecord(recordInfo.getTipAdjOrgTraceNum(), recordInfo.getInvoiceNum());
        recordInfo.setOfflineTransUploaded(false);
        int tipAdjustTimes = recordInfo.getTipAdjustTimes();
        tipAdjustTimes++;
        recordInfo.setTipAdjustTimes(tipAdjustTimes);
        LogUtil.d(TAG, "saveTipAdjustTransRecord tipAdjustTimes=" + tipAdjustTimes);
        saveTransactionRecord();
    }

    public void clearReversal() {
        LogUtil.d(TAG, "clearReversal traceNum[" + recordInfo.getTraceNum() + "] invoiceNum=[" + recordInfo.getInvoiceNum() + "]");
        iRepository.deleteReversalInfo(recordInfo.getTraceNum(), recordInfo.getInvoiceNum());
    }

    public void markOfflineTransUploaded() {
        LogUtil.d(TAG, "markOfflineTransUploaded traceNum[" + recordInfo.getTraceNum() + "] invoiceNum=[" + recordInfo.getInvoiceNum() + "]");
        iRepository.markOfflineTransUploaded(recordInfo.getTraceNum(), recordInfo.getInvoiceNum());
    }

    public void markTCUploaded() {
        LogUtil.d(TAG, "markOfflineTransUploaded traceNum[" + recordInfo.getTraceNum() + "] invoiceNum=[" + recordInfo.getInvoiceNum() + "]");
        iRepository.markTCUploaded(recordInfo.getTraceNum(), recordInfo.getInvoiceNum());
    }

    public CommParam getCommParam(int commType) {
        CommParam commParam = null;
        LogUtil.d(TAG, "commType=" + commType);

        switch (commType) {
            case CommType.SOCKET:
                commParam = getSocketCommParam();
                break;
        }

        return commParam;
    }

    public CommParam getSocketCommParam() {
        if (hostInfo == null) {
            LogUtil.e(TAG, "hostInfo not found.Please set hostInfo at Constructor");
            return null;
        }
        LogUtil.d(TAG, "hostIndex=" + hostInfo.getHostIndex());
        LogUtil.d(TAG, "hostIp=" + hostInfo.getPrimaryIp() + " port=" + hostInfo.getPrimaryPort());
        SocketParam socketParam = new SocketParam(hostInfo.getPrimaryIp(), hostInfo.getPrimaryPort());
        int connectTimeout = iRepository.getTerminalCfg().getConnectTimeout();
        if (connectTimeout <= 0) {
            connectTimeout = 30;
        }
        socketParam.setConnectTimeout(connectTimeout);
        return new CommParam(CommType.SOCKET, socketParam);
    }

    public CommParam getOfflineTransCommParam() {
        LogUtil.d(TAG, "return offline transaction comm parameter");
        return new CommParam(CommType.SIMULATE_COMM);
    }

    public SettlementPackInfo toSettlementPackInfo(SettlementRecordItem settlementRecordItem) {
        if (hostInfo == null) {
            throw new RuntimeException("No HostInfo found.");
        }

        SettlementPackInfo settlementPackInfo = new SettlementPackInfo();
        MerchantInfo merchantInfo = getiRepository().getMerchantInfo(settlementRecordItem.getMerchantIndex());
        settlementPackInfo.setSysTraceNum(merchantInfo.getTraceNum());
        settlementPackInfo.setNii(hostInfo.getNII());
        settlementPackInfo.setTerminalId(merchantInfo.getTerminalId());
        settlementPackInfo.setMerchantId(merchantInfo.getMerchantId());
        settlementPackInfo.setBatchNum(merchantInfo.getBatchNum());
        settlementPackInfo.setCountIn(settlementRecordItem.getSaleTotalCount());
        settlementPackInfo.setTotalAmountIn(settlementRecordItem.getSaleTotalAmount());
        settlementPackInfo.setCountOut(settlementRecordItem.getVoidTotalCount());
        settlementPackInfo.setTotalAmountOut(settlementRecordItem.getVoidTotalAmount());

        return settlementPackInfo;
    }

    public boolean isHostApproved() {
        String rspCode = transPackage.getResponseField(39);
        LogUtil.d(TAG, "rspCode=" + rspCode);
        if (rspCode != null && rspCode.equals("00")) {
            return true;
        }

        return false;
    }

    public boolean isNeedBatchUpload() {
        String rspCode = transPackage.getResponseField(39);
        if (rspCode != null && rspCode.equals("95")) {
            return true;
        }

        return false;
    }

    public void markTerminalStatusForceSettlement() {
        LogUtil.d(TAG, "markTerminalStatusForceSettlement");
        TerminalStatus terminalStatus = iRepository.getTerminalStatus();
        terminalStatus.setNeedForceSettlement(true);
        iRepository.putTerminalStatus(terminalStatus);
    }

    public boolean isInsertCardTransaction() {
        boolean isInsertCardTransaction = false;
        int cardEntryMode = currentTranData.getCardInfo().getCardEntryMode();
        if (cardEntryMode == CardEntryMode.IC) {
            isInsertCardTransaction = true;
        }

        LogUtil.d(TAG, "isInsertCardTransaction=" + isInsertCardTransaction);
        return isInsertCardTransaction;
    }

    public boolean isFullEmvFlow() {
        boolean isFullEmvFlow = false;

        if (currentTranData.getEmvInfo().isFullEmvFlow()) {
            isFullEmvFlow = true;
        }
        
        LogUtil.d(TAG, "isFullEmvFlow = " + isFullEmvFlow);
        return isFullEmvFlow;
    }

    /**
     * Execute second GAC
     *
     * @return true - Card approved transaction;  false - Card rejected transaction.
     */
    public void inputOnlineResult() throws CommonException {
        EmvProcessOnlineResult emvOnlineResult = posService.inputOnlineResult(getEmvInputOnlineParam()).blockingSingle();
        int resultCode = emvOnlineResult.getResult();
        // Transaction approval & offline approval
        if (resultCode == 0 || resultCode == EMVResult.ONLINE_RESULT_OFFLINE_TC.getId()) {
            // save TC for upload
            if (transAttribute.isNeedSaveTCForUpload()) {
                saveTCData(emvOnlineResult);
            }
            saveTags();

            if (resultCode == 0) {
                recordInfo.setEmvOfflineApproval(false);
            } else {
                recordInfo.setEmvOfflineApproval(true);
                // When offline sale upload, it need it.
                saveOfflineSaleISOFeild55Data();
                // record current date time .....
                recordOfflineApprovalTransInfo();
            }

            saveTransactionRecord();
        } else {
            recordInfo.setEmvOfflineApproval(false);
            // online response is approval, need reversal.
            if (isHostApproved()) {
                saveReversal();
            }

            if (resultCode == EMVResult.ONLINE_RESULT_TERMINATE.getId()) {
                LogUtil.d(TAG, "ARPC check failed.");
                saveReversal();
                throw new CommonException(ExceptionType.TRANS_FAILED, TransErrorCode.ARPC_CHECK_FAILED);
            } else {
                throw new CommonException(ExceptionType.TRANS_FAILED, TransErrorCode.TRANS_REJECT);
            }
        }
    }

    private EmvOnlineResultParamIn getEmvInputOnlineParam() {
        EmvOnlineResultParamIn param = EmvOnlineResultParamIn.getInstance();

        if (isReceivedHostResponse()) {
            param.setOnline(true);
            param.setAuthCode(recordInfo.getAuthCode());
            param.setRespCode(recordInfo.getRspCode());
            param.setField55(checkFeild55(recordInfo.getField55()));
        } else {
            // Terminal request offline approval
            param.setOnline(false);
            param.setAuthCode("");
            param.setRespCode("");
            param.setField55("");
        }

        return param;
    }

    private boolean isReceivedHostResponse() {
        boolean isReceivedHostResponse = true;

        String responseCode = currentTranData.getRecordInfo().getRspCode();
        if (responseCode == null || responseCode.length() == 0) {
            isReceivedHostResponse = false;
        }

        LogUtil.i(TAG, "isReceivedHostResponse=" + isReceivedHostResponse);
        return isReceivedHostResponse;
    }

    private String checkFeild55(String field55) {
        if (field55.length() > 0) {
            try {
                Tlv2Map.tlv2Map(field55);
            } catch (Exception e) {
                LogUtil.d(TAG, "Tlv format error, filed55=[" + field55 + "]");
                field55 = getIssuerDataFromResponse(field55);
                e.printStackTrace();
            }
        }

        return field55;
    }

    private String getIssuerDataFromResponse(String fld55) {
        int index = 0;
        int valueLen = 0;
        int tagFullLen = 0;
        StringBuffer fld55Ret = new StringBuffer();
        StringBuffer dataBuffer = new StringBuffer(fld55.toUpperCase().trim());
        while (index < dataBuffer.length()) {
            String tag = dataBuffer.substring(index, index + 2);
            LogUtil.d(TAG, "tag=" + tag);
            switch (tag) {
                case "5F":
                case "9F":
                    valueLen = hexLen2IntLen(dataBuffer.substring(index + 4, index + 6)); // Tag begin with 5F and 9F must tag len 4, so len str is in 4 - 6;
                    LogUtil.d(TAG, "valueLen=" + valueLen);
                    if (valueLen > dataBuffer.length()) {
                        index += 2;
                        break;
                    }
                    tagFullLen = 4 + 2 + valueLen * 2; // Tag len = 4, length len = 2 , value len = valueLen * 2;
                    String tagFull = dataBuffer.substring(index, index + tagFullLen);
                    LogUtil.d(TAG, "tagFull=[" + tagFull + "]");
                    fld55Ret.append("" + tagFull);
                    index += tagFullLen;
                    break;
                case "91":
                case "71":
                case "72":
                    valueLen = hexLen2IntLen(dataBuffer.substring(index + 2, index + 4));
                    LogUtil.d(TAG, "valueLen=" + valueLen);
                    if (valueLen > dataBuffer.length()) {
                        index += 2;
                        break;
                    }
                    tagFullLen = 2 + 2 + valueLen * 2; // Tag len = 2, length len = 2, value len = valueLen * 2;
                    tagFull = dataBuffer.substring(index, index + tagFullLen);
                    LogUtil.d(TAG, "tagFull=[" + tagFull + "]");
                    fld55Ret.append("" + tagFull);
                    index += tagFullLen;
                    break;
                default:
                    index += 2;
            }
        }

        LogUtil.d(TAG, "getIssuerDataFromResponse ret=" + fld55Ret.toString());
        return fld55Ret.toString();
    }

    private int hexLen2IntLen(String hexLen) {
        return Integer.parseInt(hexLen, 16);
    }

    private void saveTCData(EmvProcessOnlineResult inputOnlineResult) {
        String[] tcTagList = null; //{"TAG1", "TAG2", "TAG..."}; //
        String tcData = inputOnlineResult.getTcData();

        if (tcTagList != null) {
            String tcTags = posService.getEMVTagList(tcTagList);
            if (tcTags != null && tcTags.length() > 0) {
                tcData = tcTags;
            }
        }

        LogUtil.d(TAG, "saveTCData=[" + tcData + "]");
        getCurrentRecordInfo().setField55ForTC(tcData);
    }

    private void saveTags() {
        String[] tagsList = {"9B", "9F26"}; //TSI, TC

        String tags = posService.getEMVTagList(tagsList);
        Map<String, String> tlvMap = Tlv2Map.tlv2Map(tags);
        if (tlvMap.containsKey("9F26")) {
            String tc = tlvMap.get("9F26");
            LogUtil.d(TAG, "tc=" + tc);
            currentTranData.getEmvInfo().setTc(tc);
            getCurrentRecordInfo().setTc(tc);
        }
    }

    private void saveOfflineSaleISOFeild55Data() {
        String authRequestData = "";
        String[] field55Tags = { "82", "84", "95", "9A", "9C", "5F2A", "9F02",
                "9F03", "9F06", "9F07", "9F09", "9F10", "9F1A", "9F1E", "9F21", "9F26", "9F27",
                "9F33", "9F34", "9F35", "9F36", "9F37", "9F41", "9F53", "9F5B", "9F63",
                "9F74"}; //apshara

        authRequestData = posService.getEMVTagList(field55Tags);

        EmvInformation emvInformation = currentTranData.getEmvInfo();
        LogUtil.d(TAG, "authRequestData=[" + authRequestData + "]");
        emvInformation.setAuthReqData(authRequestData);

        getCurrentRecordInfo().setField55(authRequestData);
    }

    private void recordOfflineApprovalTransInfo() {
        long time = System.currentTimeMillis();
        recordInfo.setTransDate(DateTimeUtil.formatDate(time));
        recordInfo.setTransTime(DateTimeUtil.formatTime(time));
        String invoiceNum = terminalCfg.getSysInvoiceNum();
        if (merchantInfo == null) {
            throw new RuntimeException("Missing Merchant info");
        }
        String traceNum = merchantInfo.getTraceNum();
        LogUtil.d(TAG, "Online failed, offline approval InvoiceNum=" + invoiceNum);
        LogUtil.d(TAG, "Online failed, offline approval stanNum=" + traceNum);
        recordInfo.setInvoiceNum(invoiceNum);
        recordInfo.setTraceNum(traceNum);
        if (hostInfo == null) {
            throw new RuntimeException("Missing Host info");
        }
        recordInfo.setHostType(hostInfo.getHostType());
        recordInfo.setMerchantIndex(merchantInfo.getMerchantIndex());
//        recordInfo.setCardBinIndex(currentTranData.getCardBinInfo().getCardIndex());
        recordInfo.setAuthCode("Y1"); // Check with Vx then change it.
//        increaseInvoice();
//        increaseTrace();
    }

    public String getResponseIso8583Field(int field) {
        return transPackage.getResponseField(field);
    }

    public RecordInfo getCurrentRecordInfo() {
        return recordInfo;
    }

    public TerminalCfg getTerminalCfg() {
        return terminalCfg;
    }

    public IPosService getPosService() {
        return posService;
    }

    public IRepository getiRepository() {
        return iRepository;
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }

    public MerchantInfo getMerchantInfo() {
        return merchantInfo;
    }

    public void setMerchantInfo(MerchantInfo merchantInfo) {
        this.merchantInfo = merchantInfo;
    }

    public TransAttribute getTransAttribute() {
        return transAttribute;
    }
}
