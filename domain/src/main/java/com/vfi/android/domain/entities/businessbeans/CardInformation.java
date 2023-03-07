package com.vfi.android.domain.entities.businessbeans;


import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.DateTimeUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;

/**
 * Created by yunlongg1 on 26/10/2017.
 */

public class CardInformation {
    /**
     * {@link com.vfi.android.domain.entities.consts.CardEntryMode}
     */
    private int cardEntryMode;
    private String pan;
    private String track1;
    private String track2;
    private String track3;
    private String encryptedPan;
    private String encryptedNormalTrack2; // only track2 info be encrypted.
    private String encryptedTrack1;
    private String encryptedTrack2; // len + track2 info , then encrypted all.
    private String encryptedTrack3;
    private String serviceCode;
    private String expiredDate;
    private String reveralData;
    private String cardSequenceNum;
    private String cardHolderName;
    private String cvmResult; // TAG 9F34 only need in Magnetic strip contactless card
    private String mobileAcceptanceIndicator; // TAG 9F7E only need in Magnetic strip contactless card
    private String aid;
    private String CVC2OrCVV2;
    private String encryptedManualInputInfo; // pan + expiry date + cvc2
    private String applicationLabel;
    /**
     * Current used key index of track encryption
     */
    private int currentTrackKeyIndex;

    private boolean isMsdCard;
    private boolean isPhoneMsdCard;

    public int getCardEntryMode() {
        return cardEntryMode;
    }

    public void setCardEntryMode(int cardEntryMode) {
        this.cardEntryMode = cardEntryMode;
    }

    public boolean isCardExpired() {
        if (expiredDate != null && expiredDate.length() == 4) {
            String YYMM = DateTimeUtil.getCurrentDateYYMM();
            int currentYearMonth = Integer.parseInt(YYMM);
            int yearMonth = Integer.parseInt(expiredDate);

            LogUtil.d(TAGS.CHECK_CARD, "isCardExpired, cur=" + currentYearMonth + " card=" + yearMonth);
            if (yearMonth < currentYearMonth) {
                return true;
            }
        }

        return false;
    }

    public String getReveralData() {
        return reveralData;
    }

    public void setReveralData(String reveralData) {
        this.reveralData = reveralData;
    }

    public String getPan() {
        if (pan == null || pan.length() == 0) {
            return "0000000000000000";
        }

        return getNonNullString(pan);
    }

    public void setPan(String pan) {
        this.pan =  pan;
    }

    public String getTrack1() {
        return getNonNullString(track1);
    }

    public void setTrack1(String track1) {
        this.track1 = track1;
    }

    public String getTrack2() {
        return getNonNullString(track2);
    }

    public void setTrack2(String track2) {
        if(track2.length()%2==0){
            this.track2 = track2;
        }else{
            this.track2 = track2+"F";
        }

    } //apshara

    public String getTrack3() {
        return track3;
    }

    public void setTrack3(String track3) {
        this.track3 = track3;
    }

    public String getServiceCode() {
        return getNonNullString(serviceCode);
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getExpiredDate() {
        return getNonNullString(expiredDate);
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public boolean isServiceCodeValid() {
        LogUtil.d("serviceCode = "+serviceCode);
        if (this.serviceCode.length() == 0 ||
                null == this.serviceCode) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isTrack2Valid() {
        LogUtil.d("track2 = "+track2);
        int index = (this.track2 == null ? -1 : this.track2.indexOf('='));
        if (index > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isTrack3Valid() {
        LogUtil.d("track3 = "+track3);
        if (track3 == null || track3.length() == 0) {
            return true;
        }

        if (track3.length() > 14) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPanValid() {
        LogUtil.d("pan = "+pan);
        if (this.pan.length() < 13 || this.pan.length() > 19) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isICCard() {
        return (this.serviceCode.charAt(0) == '2' || this.serviceCode.charAt(0) == '6');
    }

    public boolean isValid() {
        LogUtil.d("TAG", "isTrack2Valid=" + isTrack2Valid());
        LogUtil.d("TAG", "isTrack3Valid=" + isTrack3Valid());
        LogUtil.d("TAG", "isServiceCodeValid=" + isServiceCodeValid());
        LogUtil.d("TAG", "isPanValid=" + isPanValid());
        if (isTrack2Valid() && isTrack3Valid() && isServiceCodeValid() && isPanValid()) {
            return true;
        } else {
            return false;
        }
    }

    public String getCardSequenceNum() {
        return getNonNullString(cardSequenceNum);
    }

    public void setCardSequenceNum(String sequenceNum) {
        this.cardSequenceNum = sequenceNum;
    }

    public String getCardHolderName() {
        return getNonNullString(cardHolderName);
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCvmResult() {
        return cvmResult;
    }

    public void setCvmResult(String cvmResult) {
        this.cvmResult = cvmResult;
    }

    public String getMobileAcceptanceIndicator() {
        return getNonNullString(mobileAcceptanceIndicator);
    }

    public void setMobileAcceptanceIndicator(String mobileAcceptanceIndicator) {
        this.mobileAcceptanceIndicator = mobileAcceptanceIndicator;
    }

    public String getAid() {
        return getNonNullString(aid);
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getCVC2OrCVV2() {
        return getNonNullString(CVC2OrCVV2);
    }

    public void setCVC2OrCVV2(String CVC2OrCVV2) {
        this.CVC2OrCVV2 = CVC2OrCVV2;
    }


    public boolean isMsdCard() {
        return isMsdCard;
    }

    public void setMsdCard(boolean msdCard) {
        isMsdCard = msdCard;
    }

    public boolean isPhoneMsdCard() {
        return isPhoneMsdCard;
    }

    public void setPhoneMsdCard(boolean phoneMsdCard) {
        isPhoneMsdCard = phoneMsdCard;
    }

    public String getEncryptedTrack1() {
        return getNonNullString(encryptedTrack1);
    }

    public void setEncryptedTrack1(String encryptedTrack1) {
        this.encryptedTrack1 = encryptedTrack1;
    }

    public String getEncryptedTrack2() {
        return getNonNullString(encryptedTrack2);
    }

    public void setEncryptedTrack2(String encryptedTrack2) {
        this.encryptedTrack2 = encryptedTrack2;
    }

    public String getEncryptedTrack3() {
        return getNonNullString(encryptedTrack3);
    }

    public void setEncryptedTrack3(String encryptedTrack3) {
        this.encryptedTrack3 = encryptedTrack3;
    }

    public String getEncryptedManualInputInfo() {
        return getNonNullString(encryptedManualInputInfo);
    }

    public void setEncryptedManualInputInfo(String encryptedManualInputInfo) {
        this.encryptedManualInputInfo = encryptedManualInputInfo;
    }

    public int getCurrentTrackKeyIndex() {
        return currentTrackKeyIndex;
    }

    public void setCurrentTrackKeyIndex(int currentTrackKeyIndex) {
        this.currentTrackKeyIndex = currentTrackKeyIndex;
    }

    public String getEncryptedPan() {
        return getNonNullString(encryptedPan);
    }

    public void setEncryptedPan(String encryptedPan) {
        this.encryptedPan = encryptedPan;
    }

    public String getEncryptedNormalTrack2() {
        return getNonNullString(encryptedNormalTrack2);
    }

    public void setEncryptedNormalTrack2(String encryptedNormalTrack2) {
        this.encryptedNormalTrack2 = encryptedNormalTrack2;
    }

    private String getNonNullString(String value) {
        return StringUtil.getNonNullString(value);
    }

    public String getApplicationLabel() {
        return getNonNullString(applicationLabel);
    }

    public void setApplicationLabel(String applicationLabel) {
        this.applicationLabel = applicationLabel;
    }
}
