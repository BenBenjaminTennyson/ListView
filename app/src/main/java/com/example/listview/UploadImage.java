package com.example.listview;


import android.graphics.drawable.Drawable;
import android.net.Uri;

public class UploadImage {
    private String name;
    private String status;
    private String url;
    private Drawable uri;

    public UploadImage(String name, String status, String url, Drawable uri) {
        this.name = name;
        this.status = status;
        this.url = url;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Drawable getUri() {
        return uri;
    }

    public void setUri(Drawable uri) {
        this.uri = uri;
    }
}
