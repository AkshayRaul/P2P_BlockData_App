package com.example.shwetha.blockdata;

/**
 * Created by raulakshay on 19/2/18.
 */

public class Download {
    String fileId;
    long fileSize;
    String fileName;

    public Download(String fileName, String fileId, long fileSize) {
        this.fileId = fileId;
        this.fileSize = fileSize;
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileName() {
        return fileName;
    }
}
