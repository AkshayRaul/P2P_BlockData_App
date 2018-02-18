package com.example.shwetha.blockdata;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

/**
 * Created by blockdata on 9/2/18.
 */

public class MyService extends Service {
    public static OkHttpClient client;
    public WebSocketClient mWebSocketClient;
    public EchoSocketListener listener;
    public WebSocket ws;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void  onCreate(){


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        client = new OkHttpClient.Builder()
                .readTimeout(120,  TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url("ws://10.0.0.5:8080/Blockchain/ws/")
                .build();
        Log.i("Websocket",request.toString());
        listener = new EchoSocketListener();
        WebSocket ws = client.newWebSocket(request, listener);

        Log.i("ws", ws.toString());

        client.dispatcher().executorService().shutdown();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("myservice","closed called");
        super.onDestroy();
    }
}
