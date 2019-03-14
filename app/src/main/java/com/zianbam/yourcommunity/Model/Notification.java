package com.zianbam.yourcommunity.Model;

public class Notification {
    private String userid, text, postid, notificationid;
    private boolean ispost;


    public Notification() {
    }

    public Notification(String userid, String text, String postid, String notificationid, boolean ispost) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.notificationid = notificationid;
        this.ispost = ispost;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getNotificationid() {
        return notificationid;
    }

    public void setNotificationid(String notificationid) {
        this.notificationid = notificationid;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }
}
