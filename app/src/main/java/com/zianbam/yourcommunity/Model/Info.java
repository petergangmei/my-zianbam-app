package com.zianbam.yourcommunity.Model;

public class Info {
  private String title, info;

  public Info() {
  }

  public Info(String title, String info) {
    this.title = title;
    this.info = info;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }
}
