//package com.vfi.android.domain.entities.memory;
//
//
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
//import javax.inject.Inject;
//import javax.inject.Singleton;
//
//@Singleton
//public class GlobalMemoryCache {
//    private CurrentTranData currentTranData;
//    private SystemParameter systemParameter;
//    private DeviceInformation deviceInformation;
//    private String debugSN;
//    private String debugRandom;
//    private Lock checkCardLock = new ReentrantLock();
//
//    private boolean isDoingTransaction = false; // Flag to identify whether in transaction or not.
//    private boolean isPowerConnected = false;
//    private boolean isLowPowerStatus = false;
//
//    @Inject
//    public GlobalMemoryCache() {
//        reInitGlobalMemoryCache();
//    }
//
//    public void reInitGlobalMemoryCache() {
//        currentTranData = null;
//        systemParameter = null;
//    }
//
//    public CurrentTranData getCurrentTranData() {
//        if (currentTranData == null) {
//            currentTranData = new CurrentTranData();
//            currentTranData.initDefaultValue();
//        }
//
//        return currentTranData;
//    }
//
//    public void setCurrentTranData(CurrentTranData currentTranData) {
//        this.currentTranData = currentTranData;
//    }
//
//    public SystemParameter getSystemParameter() {
//        return systemParameter;
//    }
//
//    public void setSystemParameter(SystemParameter systemParameter) {
//        this.systemParameter = systemParameter;
//    }
//
//    public boolean isDoingTransaction() {
//        return isDoingTransaction;
//    }
//
//    public void setDoingTransaction(boolean doingTransaction) {
//        isDoingTransaction = doingTransaction;
//    }
//
//    public boolean isPowerConnected() {
//        return isPowerConnected;
//    }
//
//    public void setPowerConnected(boolean powerConnected) {
//        isPowerConnected = powerConnected;
//    }
//
//    public boolean isLowPowerStatus() {
//        return isLowPowerStatus;
//    }
//
//    public void setLowPowerStatus(boolean lowPowerStatus) {
//        isLowPowerStatus = lowPowerStatus;
//    }
//
//    public DeviceInformation getDeviceInformation() {
//        return deviceInformation;
//    }
//
//    public void setDeviceInformation(DeviceInformation deviceInformation) {
//        this.deviceInformation = deviceInformation;
//    }
//
//    public void lockCheckCardLock() {
//        checkCardLock.lock();
//    }
//
//    public void unlockCheckCardLock() {
//        checkCardLock.unlock();
//    }
//
//    public String getDebugSN() {
//        return debugSN;
//    }
//
//    public void setDebugSN(String debugSN) {
//        this.debugSN = debugSN;
//    }
//
//    public String getDebugRandom() {
//        return debugRandom;
//    }
//
//    public void setDebugRandom(String debugRandom) {
//        this.debugRandom = debugRandom;
//    }
//}
