package com.zianbam.yourcommunity.Model;

public class ReferralCode {
    private long endon;
    private String date, time,userid;

    public ReferralCode() {
    }

    public ReferralCode(long endon, String date, String time, String userid) {
        this.endon = endon;
        this.date = date;
        this.time = time;
        this.userid = userid;
    }

    public long getEndon() {
        return endon;
    }

    public void setEndon(long endon) {
        this.endon = endon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
