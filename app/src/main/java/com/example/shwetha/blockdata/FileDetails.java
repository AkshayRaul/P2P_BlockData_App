package com.example.shwetha.blockdata;

/**
 * Created by SHWETHA on 23-02-2018.
 */

public class FileDetails {
    String fname;
    int file_id;

    public FileDetails(String fname) {
        this.fname = fname;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }
}
