package com.vfi.android.domain.memory;

import com.vfi.android.domain.entities.businessbeans.CurrentTranData;
import com.vfi.android.domain.entities.businessbeans.DeviceInformation;
import com.vfi.android.domain.entities.businessbeans.TerminalCfg;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GlobalMemoryCache {
    private CurrentTranData currentTranData;
    private DeviceInformation deviceInformation;
    private TerminalCfg terminalCfg;

    private Lock checkCardLock = new ReentrantLock();

    private boolean isDoingTransaction = false; // Flag to identify whether in transaction or not.
    private boolean isPowerConnected = false;
    private boolean isLowPowerStatus = false;

    @Inject
    public GlobalMemoryCache() {

    }

    public CurrentTranData getCurrentTranData() {
        if (currentTranData == null) {
            currentTranData = new CurrentTranData();
        }
        return currentTranData;
    }

    public void setCurrentTranData(CurrentTranData currentTranData) {
        this.currentTranData = currentTranData;
    }

    public boolean isDoingTransaction() {
        return isDoingTransaction;
    }

    public void setDoingTransaction(boolean doingTransaction) {
        isDoingTransaction = doingTransaction;
    }

    public boolean isPowerConnected() {
        return isPowerConnected;
    }

    public void setPowerConnected(boolean powerConnected) {
        isPowerConnected = powerConnected;
    }

    public boolean isLowPowerStatus() {
        return isLowPowerStatus;
    }

    public void setLowPowerStatus(boolean lowPowerStatus) {
        isLowPowerStatus = lowPowerStatus;
    }

    public void lockCheckCardLock() {
        checkCardLock.lock();
    }

    public void unlockCheckCardLock() {
        checkCardLock.unlock();
    }

    public DeviceInformation getDeviceInformation() {
        return deviceInformation;
    }

    public void setDeviceInformation(DeviceInformation deviceInformation) {
        this.deviceInformation = deviceInformation;
    }

    public TerminalCfg getTerminalCfg() {
        return terminalCfg;
    }

    public void setTerminalCfg(TerminalCfg terminalCfg) {
        this.terminalCfg = terminalCfg;
    }
}
