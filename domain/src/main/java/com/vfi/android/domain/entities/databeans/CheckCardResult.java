package com.vfi.android.domain.entities.databeans;


import com.vfi.android.domain.entities.businessbeans.CardInformation;

/**
 * CheckCardResult created on 1/20/18 12:44 PM
 *
 * @author yunlongg1@verifone.com
 * @version 1.0
 * description:
 * TODO
 */

public class CheckCardResult {
    private int inputMode;
    private boolean isSupportMagCard;
    private boolean isSupportICCard;
    private boolean isSupportRFCard;
    private boolean isFallBack;
    private boolean isNeedRetry;

    public CheckCardResult getCloneCheckCardResult() {
        CheckCardResult checkCardResult = new CheckCardResult();
        checkCardResult.setInputMode(inputMode);
        checkCardResult.setSupportMagCard(isSupportMagCard);
        checkCardResult.setSupportICCard(isSupportICCard);
        checkCardResult.setSupportRFCard(isSupportRFCard);
        checkCardResult.setFallBack(isFallBack);
        checkCardResult.setNeedRetry(isNeedRetry);
        return checkCardResult;
    }

    public boolean isSupportALLCardType() {
        if (isSupportICCard && isSupportRFCard && isSupportMagCard) {
            return true;
        }

        return false;
    }

    public boolean isOnlySupportMagCard() {
        if (!isSupportICCard && !isSupportRFCard && isSupportMagCard) {
            return true;
        }

        return false;
    }

    public boolean isSupportMagCard() {
        return isSupportMagCard;
    }

    public void setSupportMagCard(boolean supportMagCard) {
        isSupportMagCard = supportMagCard;
    }

    public boolean isSupportICCard() {
        return isSupportICCard;
    }

    public void setSupportICCard(boolean supportICCard) {
        isSupportICCard = supportICCard;
    }

    public boolean isSupportRFCard() {
        return isSupportRFCard;
    }

    public void setSupportRFCard(boolean supportRFCard) {
        isSupportRFCard = supportRFCard;
    }

    public boolean isFallBack() {
        return isFallBack;
    }

    public void setFallBack(boolean fallBack) {
        isFallBack = fallBack;
    }

    public boolean isNeedRetry() {
        return isNeedRetry;
    }

    public void setNeedRetry(boolean needRetry) {
        isNeedRetry = needRetry;
    }

    public int getInputMode() {
        return inputMode;
    }

    public void setInputMode(int inputMode) {
        this.inputMode = inputMode;
    }
}
