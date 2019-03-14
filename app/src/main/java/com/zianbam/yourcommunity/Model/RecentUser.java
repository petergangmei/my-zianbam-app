package com.zianbam.yourcommunity.Model;

public class RecentUser {
  private String username, imageURL,id;

  public RecentUser() {
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getImageURL() {
    return imageURL;
  }

  public void setImageURL(String imageURL) {
    this.imageURL = imageURL;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public RecentUser(String username, String imageURL, String id) {
    this.username = username;
    this.imageURL = imageURL;
    this.id = id;

  }
}
