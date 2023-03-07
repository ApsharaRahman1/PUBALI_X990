package com.vfi.android.domain.entities.exceptions;

public class CommonException extends Exception {
    private int exceptionType;
    private int subErrType;

    public CommonException(int exceptionType) {
        this.exceptionType = exceptionType;
    }

    public CommonException(int exceptionType, int subType) {
        this.exceptionType = exceptionType;
        this.subErrType = subType;
    }

    public int getExceptionType() {
        return exceptionType;
    }

    public int getSubErrType() {
        return subErrType;
    }
}
