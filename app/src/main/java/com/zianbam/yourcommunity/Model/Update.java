package com.zianbam.yourcommunity.Model;

public class Update {
  private String verson, message, level;

  public Update(String verson, String message, String level) {
    this.verson = verson;
    this.message = message;
    this.level = level;
  }

  public Update() {
  }

  public String getVerson() {
    return verson;
  }

  public void setVerson(String verson) {
    this.verson = verson;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }
}
