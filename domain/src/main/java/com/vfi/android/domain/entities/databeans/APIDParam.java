package com.vfi.android.domain.entities.databeans;

public class APIDParam {
    private int operationType;
    public static final int OP_ADD = 1;
    public static final int OP_DELETE_ALL = 2;

    private String programId;
    private String transLimit;
    private String floorLimit;
    private String cvmLimit;

    public APIDParam(int operationType, String programId, String transLimit, String floorLimit, String cvmLimit) {
        this.operationType = operationType;
        this.programId = programId;
        this.transLimit = transLimit;
        this.floorLimit = floorLimit;
        this.cvmLimit = cvmLimit;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public String getTransLimit() {
        return transLimit;
    }

    public void setTransLimit(String transLimit) {
        this.transLimit = transLimit;
    }

    public String getFloorLimit() {
        return floorLimit;
    }

    public void setFloorLimit(String floorLimit) {
        this.floorLimit = floorLimit;
    }

    public String getCvmLimit() {
        return cvmLimit;
    }

    public void setCvmLimit(String cvmLimit) {
        this.cvmLimit = cvmLimit;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }
}
