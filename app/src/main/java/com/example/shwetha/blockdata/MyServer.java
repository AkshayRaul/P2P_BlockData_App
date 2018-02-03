package com.example.shwetha.blockdata;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by blockdata on 31/1/18.
 */

public class MyServer extends NanoHTTPD {


    public MyServer(int port) {
        super(port);
        Log.i("server", "Server Running");
    }

    public MyServer(String hostname, int port) {
        super(hostname, port);
        Log.i("server", "Server Running");
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> parms = session.getParms();
        File f = new File("/storage/emulated/0/Download/NIRF-Ranking.pdf");
        try {
            return newFixedLengthResponse(Response.Status.OK, "", new FileInputStream(f), f.length());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return newFixedLengthResponse("");
    }
}
