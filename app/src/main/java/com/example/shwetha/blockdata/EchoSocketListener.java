package com.example.shwetha.blockdata;

import android.app.DownloadManager;
import android.content.SharedPreferences;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
    static ArrayList<fileMetaData> storage = new ArrayList<fileMetaData>();
    static ArrayList<Download> downloadList = new ArrayList<Download>();
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
            if (reader.getString("messageType").compareToIgnoreCase("storage") == 0) {
                String fileName = reader.getString("fileName");
                long fileSize = reader.getLong("fileSize");
                String fileOwner = reader.getString("owner");
                String fileId = reader.getString("fileId");
                storage.add(new fileMetaData(fileName, fileId, fileSize, fileOwner));
            } else {
                String fileId = reader.getString("fileId");
                webSocket.send(reader.toString());
                File file = new File("/storage/emulated/0/BlockStorage/" + (fileId));
                byte byt[] = new byte[(int) file.length()];
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    fileInputStream.read(byt);
                    webSocket.send(ByteString.of(byt));
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * Invoked when a binary (type {@code 0x2}) message has been received.
     */
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {

        byte byt[] = bytes.toByteArray();
        Log.i("BC",bytes.toString());
        Log.i("Index","Index0="+byt[0]+"Indeex1="+byt[1]);
        if (byt[0] == 1 && byt[1] == 1) {
            File file = new File("/storage/emulated/0/BlockStorage/" + (storage.get(0).getFileId()));
            try {
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                FileOutputStream fileOuputStream = new FileOutputStream(file);
                byte storeFile[] = bytes.toByteArray();
                storeFile[0] = storeFile[1] = 0;
                fileOuputStream.write(storeFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            storage.remove(0);
        } else if (byt[0] == 0 && byt[1] == 1) {

            File ledger = new File("/storage/emulated/0/BlockStorage/blockchain.csv");
            try {
                if (!ledger.exists()) {
                    ledger.getParentFile().mkdirs();
                    ledger.createNewFile();
                }
                FileOutputStream fileOuputStream = new FileOutputStream(ledger);
                fileOuputStream.write(byt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Download", "HERE");
            Cipher cipher = null;
            byte byteFile[] = new byte[byt.length - 2];
            for (int i = 0; i < byteFile.length; i++) {
                byteFile[i] = byt[i + 2];
            }
            String keys = FileTransferAndLedger.sharedPref.getString("FileKey", null);
            try {
                SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(),
                        "AES");
                cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(
                        new byte[cipher.getBlockSize()]));
                cipher.doFinal(byteFile);
                File file = new File("/storage/emulated/0/Download/" + (downloadList.get(0)).fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                try {
                    FileOutputStream fileOuputStream = new FileOutputStream(file);
                    fileOuputStream.write(byteFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                downloadList.remove(0);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

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

    public static void getFile(String fileId, String fileName, Long fileSize) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("messageType", "fetchFile");
        json.put("fileId", fileId);
        downloadList.add(new Download(fileName, fileId, fileSize));
        ws.send(json.toString());
    }

    public void sendFileData(byte[] bytes,String fileName, Long fileSize, String fileType) throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject fileJSONArray = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }

        try {
            json.put("messageType", "fileUpload");
            fileJSONArray.put("fileName", fileName);
            fileJSONArray.put("fileSize", fileSize);
            fileJSONArray.put("fileType", fileType);
            fileJSONArray.put("fileId", salt.toString());
            jsonArray.put(fileJSONArray);
            json.put("files", jsonArray);
            fMD.add(new fileMetaData(fileName, (String)fileJSONArray.get("fileId"), fileSize, UserKey.token));


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
