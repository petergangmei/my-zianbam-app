package com.zianbam.yourcommunity.Model;

public class Chat {
    private String sender,message, id,deleted, deleted2, time;

    public Chat() {
    }

    public Chat(String sender, String receiver, String message, String id, String deleted, String deleted2, String time) {
        this.sender = sender;
        this.message = message;
        this.id = id;
        this.deleted = deleted;
        this.deleted2 = deleted2;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
