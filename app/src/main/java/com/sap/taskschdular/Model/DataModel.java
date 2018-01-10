package com.sap.taskschdular.Model;

/**
 * Created by Sanu on 11/30/2017.
 */

public class DataModel {
    private String consinmentNo;
    private String mobileNo;
    private String startTime;
    private String EndTime;
    private String rate;
    private String rateType;

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getConsinmentNo() {
        return consinmentNo;
    }

    public void setConsinmentNo(String consinmentNo) {
        this.consinmentNo = consinmentNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }
}
