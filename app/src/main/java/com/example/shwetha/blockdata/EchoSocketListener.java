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

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d("Websocker", "Connected");
        ws = webSocket;

    }

    /**
     * Invoked when a text (type {@code 0x1}) message has been received.
     */
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.i("toast", text);

    }

    /**
     * Invoked when a binary (type {@code 0x2}) message has been received.
     */
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Toast.makeText(new FileTransferAndLedger().getApplicationContext(), bytes.toString(), Toast.LENGTH_LONG).show();
        Log.i("toast", bytes.toString());
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
