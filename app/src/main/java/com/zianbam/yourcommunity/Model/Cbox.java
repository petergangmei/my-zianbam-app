package com.zianbam.yourcommunity.Model;

public class Cbox {
    private String receiver, isseen, lastmsg, deleted, deleted2, time ;

    public Cbox() {
    }

    public Cbox(String receiver, String isseen, String lastmsg, String deleted, String deleted2, String time, String conversationid) {
        this.receiver = receiver;
        this.isseen = isseen;
        this.lastmsg = lastmsg;
        this.deleted = deleted;
        this.deleted2 = deleted2;
        this.time = time;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getIsseen() {
        return isseen;
    }

    public void setIsseen(String isseen) {
        this.isseen = isseen;
    }

    public String getLastmsg() {
        return lastmsg;
    }

    public void setLastmsg(String lastmsg) {
        this.lastmsg = lastmsg;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getDeleted2() {
        return deleted2;
    }

    public void setDeleted2(String deleted2) {
        this.deleted2 = deleted2;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
