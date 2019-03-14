package com.zianbam.yourcommunity.Model;

import android.net.Uri;

public class GridModel {
    private Uri uri;
    private String Name;

    public GridModel() {
    }

    public GridModel(Uri uri, String name) {
        this.uri = uri;
        Name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
