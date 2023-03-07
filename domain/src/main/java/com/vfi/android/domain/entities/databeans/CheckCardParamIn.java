package com.vfi.android.domain.entities.databeans;

/**
 * Created by yunlongg1 on 20/10/2017.*/

public class CheckCardParamIn {

    private boolean isSupportMagCard;

    private boolean isSupportICCard;

    private boolean isSupportRFCard;


    public CheckCardParamIn() { }

    private int timeout;

    public boolean isSupportMagCard() {
        return isSupportMagCard;
    }

    public CheckCardParamIn setSupportMagCard(boolean supportMagCard) {
        isSupportMagCard = supportMagCard;
        return this;
    }

    public boolean isSupportICCard() {
        return isSupportICCard;
    }

    public CheckCardParamIn setSupportICCard(boolean supportICCard) {
        isSupportICCard = supportICCard;
        return this;
    }

    public boolean isSupportRFCard() {
        return isSupportRFCard;
    }

    public CheckCardParamIn setSupportRFCard(boolean supportRFCard) {
        isSupportRFCard = supportRFCard;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public CheckCardParamIn setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
}
