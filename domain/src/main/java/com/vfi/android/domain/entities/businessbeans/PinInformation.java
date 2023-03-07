package com.vfi.android.domain.entities.businessbeans;


import com.vfi.android.domain.utils.StringUtil;

public class PinInformation {
    private byte[] encryptedPinblock;
    private int pinLength;
    private String currentPinKeyZoneIndex;
    private String currentPinSlot;
    private String panFromMsg0050;
    private boolean isInputPinRequested;
    private boolean isPinBypassed;
    private boolean isEndInputPin;

    public void setDefaultValue() {
        pinLength = 0;
        currentPinKeyZoneIndex = "00";
        currentPinSlot = "00";
    }

    public byte[] getEncryptedPinblock() {
        return encryptedPinblock;
    }

    public void setEncryptedPinblock(byte[] encryptedPinblock) {
        this.encryptedPinblock = encryptedPinblock;
    }

    public int getPinLength() {
        return pinLength;
    }

    public void setPinLength(int pinLength) {
        this.pinLength = pinLength;
    }

    public String getCurrentPinKeyZoneIndex() {
        return getNonNullString(currentPinKeyZoneIndex);
    }

    public void setCurrentPinKeyZoneIndex(String currentPinKeyZoneIndex) {
        this.currentPinKeyZoneIndex = currentPinKeyZoneIndex;
    }

    public String getCurrentPinSlot() {
        return getNonNullString(currentPinSlot);
    }

    public void setCurrentPinSlot(String currentPinSlot) {
        this.currentPinSlot = currentPinSlot;
    }

    private String getNonNullString(String value) {
        return StringUtil.getNonNullString(value);
    }

    public String getPanFromMsg0050() {
        return getNonNullString(panFromMsg0050);
    }

    public void setPanFromMsg0050(String panFromMsg0050) {
        this.panFromMsg0050 = panFromMsg0050;
    }

    public boolean isInputPinRequested() {
        return isInputPinRequested;
    }

    public void setInputPinRequested(boolean inputPinRequested) {
        isInputPinRequested = inputPinRequested;
    }

    public boolean isEndInputPin() {
        return isEndInputPin;
    }

    public void setEndInputPin(boolean endInputPin) {
        isEndInputPin = endInputPin;
    }

    public boolean isPinBypassed() {
        return isPinBypassed;
    }

    public void setPinBypassed(boolean pinBypassed) {
        isPinBypassed = pinBypassed;
    }
}
