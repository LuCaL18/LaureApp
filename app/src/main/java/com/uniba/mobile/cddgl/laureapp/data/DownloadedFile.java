package com.uniba.mobile.cddgl.laureapp.data;

import android.app.DownloadManager;

public class DownloadedFile {

    private String filename;
    private DownloadManager.Request request;


    public DownloadedFile(String filename, DownloadManager.Request request) {
        this.filename = filename;
        this.request = request;
    }

    public DownloadedFile() {}

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public DownloadManager.Request getRequest() {
        return request;
    }

    public void setRequest(DownloadManager.Request request) {
        this.request = request;
    }
}
