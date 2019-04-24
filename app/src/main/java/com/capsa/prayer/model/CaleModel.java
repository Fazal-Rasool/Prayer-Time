package com.capsa.prayer.model;

public class CaleModel {

    String date, sehriTime, iftaarTime;

    public CaleModel(String mDate, String mSehriTime, String mIftaarTime){
        date = mDate;
        sehriTime = mSehriTime;
        iftaarTime = mIftaarTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSehriTime() {
        return sehriTime;
    }

    public void setSehriTime(String sehriTime) {
        this.sehriTime = sehriTime;
    }

    public String getIftaarTime() {
        return iftaarTime;
    }

    public void setIftaarTime(String iftaarTime) {
        this.iftaarTime = iftaarTime;
    }
}
