package com.vfi.android.domain.entities.businessbeans;

import com.vfi.android.domain.entities.consts.TransType;
import com.vfi.android.domain.entities.databeans.CheckCardParamIn;
import com.vfi.android.domain.entities.databeans.SettlementRecord;
import com.vfi.android.domain.utils.StringUtil;


/**
 * Created by fusheng.z on 2018/7/19
 * 全局的动态数据
 */
public class CurrentTranData {
    private static final String TAG = "CurrentTranData";

    /**
     * title
     */
    private String title;

    /**
     * Current menu Id, Sub menu interface will use it.
     */
    private int menuId;

    /**
     * transaction error message
     */
    private String errorMsg;

    /**
     * Indicator need input pin or not
     */
    private boolean isBinRoutingNeedOnlinePin;

    /**
     * Current transaction switch info
     */
    private SwitchParameter switchParameter;

    /**
     * The current host
     */
    private HostInfo hostInfo;

    /**
     * The current cardBinInfo
     */
    private CardBinInfo cardBinInfo;

    /**
     * Current merchant info
     */
    private MerchantInfo merchantInfo;

    /**
     * field 53 dukpt ksn
     */
    private byte[] dukptKsn;

    /**
     * Current record
     */
    private RecordInfo recordInfo;

    /**
     * Current card info
     */
    private CardInformation cardInfo;

    /**
     * Current pin info
     */
    private PinInformation pinInfo;

    /**
     * Current emv info
     */
    private EmvInformation emvInfo;

    /**
     * fallback allow ic card retry times, default 0.
     */
    private int icCardfallbackRemainTimes;

    /**
     * fallback allow rf card retry times, default 1.
     */
    private int rfCardFallbackRemainTimes;

    /**
     * Current check card param
     */
    private CheckCardParamIn currCheckCardParam;

    /**
     * Current settlement records
     */
    private SettlementRecord currentSettlementRecord;

    /**
     * Current installment promo
     */
    private InstallmentPromo currentInstallmentPromo;

    /**
     * Term of currently selected, unit(month)
     */
    private int installmentTerm;

    public CurrentTranData() {
        title = "";
        recordInfo = new RecordInfo();
        cardInfo = new CardInformation();
        pinInfo = new PinInformation();
        emvInfo = new EmvInformation();
        icCardfallbackRemainTimes = 1;
        rfCardFallbackRemainTimes = 1;
        currCheckCardParam = null;
    }

    public String getTransAmount() {
        return recordInfo.getAmount();
    }

    public String getTransTipAmount() {
        return recordInfo.getTipAmount();
    }

    public String getTotalAmount() {
        long amount = StringUtil.parseLong(recordInfo.getAmount(), 0);
        long tipAmount = StringUtil.parseLong(recordInfo.getTipAmount(), 0);

        if (getTransType() == TransType.VOID) {
            return "-" + (amount + tipAmount);
        } else {
            return "" + (amount + tipAmount);
        }
    }

    public void setCurrencyCode(String currencyCode) {
        getRecordInfo().setCurrencyCode(currencyCode);
    }

    public boolean isEmvRequireOnlinePin() {
        EmvInformation emvInformation = getEmvInfo();
        if (emvInformation.isRequestInputPin() && emvInformation.isRequestOfflinePin()) {
            return false;
        } else {
            return true;
        }
    }

    public void setEmvRequireOnlinePin(boolean emvRequireOnlinePin) {
        EmvInformation emvInformation = getEmvInfo();
        emvInformation.setRequestInputPin(true);
        if (!emvRequireOnlinePin) {
            emvInformation.setRequestOfflinePin(true);
        }
    }

    public boolean isEMVNeedFallback() {
        return getEmvInfo().isEMVNeedFallback();
    }

    public void setEMVNeedFallback(boolean EMVNeedFallback) {
        getEmvInfo().setEMVNeedFallback(EMVNeedFallback);
    }

    public boolean isEMVNeedTapToInsert() {
        return getEmvInfo().isEMVNeedTapToInsert();
    }

    public void setEMVNeedTapToInsert(boolean EMVNeedTapToInsert) {
        getEmvInfo().setEMVNeedTapToInsert(EMVNeedTapToInsert);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }

    public CardBinInfo getCardBinInfo() {
        return cardBinInfo;
    }

    public void setCardBinInfo(CardBinInfo cardBinInfo) {
        this.cardBinInfo = cardBinInfo;
    }

    public byte[] getDukptKsn() {
        return dukptKsn;
    }

    public void setDukptKsn(byte[] dukptKsn) {
        this.dukptKsn = dukptKsn;
    }

    public RecordInfo getRecordInfo() {
        return recordInfo;
    }

    public void setRecordInfo(RecordInfo recordInfo) {
        this.recordInfo = recordInfo;
    }

    public int getTransType() {
        return recordInfo.getTransType();
    }

    public void setTransType(int transType) {
        recordInfo.setTransType(transType);
    }

    public MerchantInfo getMerchantInfo() {
        return merchantInfo;
    }

    public void setMerchantInfo(MerchantInfo merchantInfo) {
        this.merchantInfo = merchantInfo;
    }

    public CardInformation getCardInfo() {
        if (cardInfo == null) {
            cardInfo = new CardInformation();
        }
        return cardInfo;
    }

    public void setCardInfo(CardInformation cardInfo) {
        this.cardInfo = cardInfo;
    }

    public PinInformation getPinInfo() {
        if (pinInfo == null) {
            pinInfo = new PinInformation();
        }
        return pinInfo;
    }

    public void setPinInfo(PinInformation pinInfo) {
        this.pinInfo = pinInfo;
    }

    public EmvInformation getEmvInfo() {
        if (emvInfo == null) {
            emvInfo = new EmvInformation();
        }
        return emvInfo;
    }

    public void setEmvInfo(EmvInformation emvInfo) {
        this.emvInfo = emvInfo;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getIcCardfallbackRemainTimes() {
        return icCardfallbackRemainTimes;
    }

    public void setIcCardfallbackRemainTimes(int icCardfallbackRemainTimes) {
        this.icCardfallbackRemainTimes = icCardfallbackRemainTimes;
    }

    public int getRfCardFallbackRemainTimes() {
        return rfCardFallbackRemainTimes;
    }

    public void setRfCardFallbackRemainTimes(int rfCardFallbackRemainTimes) {
        this.rfCardFallbackRemainTimes = rfCardFallbackRemainTimes;
    }

    public CheckCardParamIn getCurrCheckCardParam() {
        return currCheckCardParam;
    }

    public void setCurrCheckCardParam(CheckCardParamIn currCheckCardParam) {
        this.currCheckCardParam = currCheckCardParam;
    }

    public SwitchParameter getSwitchParameter() {
        return switchParameter;
    }

    public void setSwitchParameter(SwitchParameter switchParameter) {
        this.switchParameter = switchParameter;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isBinRoutingNeedOnlinePin() {
        return isBinRoutingNeedOnlinePin;
    }

    public void setBinRoutingNeedOnlinePin(boolean binRoutingNeedOnlinePin) {
        isBinRoutingNeedOnlinePin = binRoutingNeedOnlinePin;
    }

    public SettlementRecord getCurrentSettlementRecord() {
        return currentSettlementRecord;
    }

    public void setCurrentSettlementRecord(SettlementRecord currentSettlementRecord) {
        this.currentSettlementRecord = currentSettlementRecord;
    }

    public InstallmentPromo getCurrentInstallmentPromo() {
        return currentInstallmentPromo;
    }

    public void setCurrentInstallmentPromo(InstallmentPromo currentInstallmentPromo) {
        this.currentInstallmentPromo = currentInstallmentPromo;
    }

    public int getInstallmentTerm() {
        return installmentTerm;
    }

    public void setInstallmentTerm(int installmentTerm) {
        this.installmentTerm = installmentTerm;
    }
}
