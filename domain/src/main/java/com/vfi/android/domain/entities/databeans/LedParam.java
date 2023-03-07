package com.vfi.android.domain.entities.databeans;

public class LedParam {
    private boolean isBlueLightOn;
    private boolean isYellowLightOn;
    private boolean isGreenLightOn;
    private boolean isRedLightOn;

    public LedParam(boolean isBlueLightOn, boolean isYellowLightOn, boolean isGreenLightOn, boolean isRedLightOn) {
        this.isBlueLightOn = isBlueLightOn;
        this.isYellowLightOn = isYellowLightOn;
        this.isGreenLightOn = isGreenLightOn;
        this.isRedLightOn = isRedLightOn;
    }

    public boolean isRedLightOn() {
        return isRedLightOn;
    }

    public void setRedLightOn(boolean redLightOn) {
        isRedLightOn = redLightOn;
    }

    public boolean isYellowLightOn() {
        return isYellowLightOn;
    }

    public void setYellowLightOn(boolean yellowLightOn) {
        isYellowLightOn = yellowLightOn;
    }

    public boolean isGreenLightOn() {
        return isGreenLightOn;
    }

    public void setGreenLightOn(boolean greenLightOn) {
        isGreenLightOn = greenLightOn;
    }

    public boolean isBlueLightOn() {
        return isBlueLightOn;
    }

    public void setBlueLightOn(boolean blueLightOn) {
        isBlueLightOn = blueLightOn;
    }
}
