package com.vfi.android.domain.interactor.transaction.iso8583.base;

import com.vfi.android.domain.entities.exceptions.CommonException;

public interface ITransPackage {
    byte[] packISO8583Message() throws CommonException;
    void unPackISO8583Message(byte[] message) throws CommonException;
    String getResponseField(int field);
}
