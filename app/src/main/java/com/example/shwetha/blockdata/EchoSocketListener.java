package com.example.shwetha.blockdata;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by raulakshay on 31/1/18.
 */

public final class EchoSocketListener extends WebSocketListener {

    static WebSocket ws;
    static ArrayList<fileMetaData> fMD = new ArrayList<fileMetaData>();
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d("Websocker", "Connected");
        ws = webSocket;
        JSONObject json = new JSONObject();
//        File f  = new File("/storage/emulated/0");
        try {
            json.put("messageType", "metaData");
            json.put("userId", UserKey.token);
            json.put("storage", new Double(5.1));
            json.put("rating", new Double(4.5));
            json.put("onlinePercent", new Integer(50));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webSocket.send(json.toString());


    }

    /**
     * Invoked when a text (type {@code 0x1}) message has been received.
     */
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.i("toast", text);
        try {
            JSONObject reader = new JSONObject(text);
            String fileName = reader.getString("fileName");
            long fileSize = reader.getLong("fileSize");
            String fileOwner = reader.getString("owner");
            String fileId = reader.getString("fileId");
            fMD.add(new fileMetaData(fileName, fileId, fileSize, fileOwner));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * Invoked when a binary (type {@code 0x2}) message has been received.
     */
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        File file = new File("/storage/emulated/0/" + (fMD.get(0).getFileName()));
        try {
            FileOutputStream fileOuputStream = new FileOutputStream(file);
            fileOuputStream.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        fMD.remove(0);

    }

    /**
     * Invoked when the remote peer has indicated that no more incoming messages will be
     * transmitted.
     */
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        Log.i("WebsocketClose", reason);
    }

    /**
     * Invoked when both peers have indicated that no more messages will be transmitted and the
     * connection has been successfully released. No further calls to this listener will be made.
     */
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.i("Closed",reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }

    public void sendFileData(byte[] bytes,String fileName, Long fileSize, String fileType) throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject fileJSONArray = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            json.put("messageType", "fileUpload");
            fileJSONArray.put("fileName", fileName);
            fileJSONArray.put("fileSize", fileSize);
            fileJSONArray.put("fileType", fileType);
            jsonArray.put(fileJSONArray);
            json.put("files", jsonArray);

        } catch (JSONException e) {

        }
        Log.i("WebosocketFile", json.toString());
        try {
            ws.send(json.toString());
            ws.send(ByteString.of(bytes));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
