package com.zianbam.yourcommunity.Model;

public class AvailableUpdate {
  private String id, text;

  public AvailableUpdate() {
  }

  public AvailableUpdate(String id, String text) {
    this.id = id;
    this.text = text;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
