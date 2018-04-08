package com.example.shwetha.blockdata;

/**
 * Created by raulakshay on 18/2/18.
 */

import java.nio.charset.*;
import java.util.*;

public class fileMetaData {
    private String fileName;
    private String fileId;
    private long fileSize;
    private String fileOwner;
    private String mimeType;
    private String date;

    fileMetaData() {
        this.fileName = "test";
        this.fileId = "";
        this.fileSize = -1;
        this.fileOwner = "null";
        this.mimeType = "null";
    }

    fileMetaData(String fileName, String fileId, long fileSize, String fileOwner, String date) {
        this.fileName = fileName;
        this.fileId = fileId;
        this.fileSize = fileSize;
        this.fileOwner = fileOwner;
        this.date = date;
    }

    fileMetaData(String fileName, String fileId, long fileSize) {
        this.fileName = fileName;
        this.fileId = fileId;
        this.fileSize = fileSize;
        this.fileOwner = fileName.substring(fileName.indexOf("."), fileName.length());
        this.mimeType = "null";
    }

    fileMetaData(String fileName, String fileId) {
        this.fileName = fileName;
        this.fileId = fileId;
        this.fileSize = fileSize;
        this.fileOwner = fileName.substring(fileName.indexOf("."), fileName.length());
        this.mimeType = "null";
    }

    String getFileName() {
        return this.fileName;
    }

    String getFileDate() {
        return this.date;
    }

    String getFileId() {
        return this.fileId;
    }

    String getFileOwner() {
        return this.fileOwner;
    }

    long getFileSize() {
        return this.fileSize;
    }

}
