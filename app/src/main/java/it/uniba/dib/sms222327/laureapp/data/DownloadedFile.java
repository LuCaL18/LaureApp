package it.uniba.dib.sms222327.laureapp.data;

import android.app.DownloadManager;

import androidx.annotation.Keep;

/**
 * Classe che rappresneta l'istanza di un file scaricato
 */
public class DownloadedFile {

    private String filename;
    private DownloadManager.Request request;


    public DownloadedFile(String filename, DownloadManager.Request request) {
        this.filename = filename;
        this.request = request;
    }

    @Keep
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
