package com.example.shwetha.blockdata;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import android.webkit.MimeTypeMap;
import android.widget.Toast;


import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.MediaType;
import okhttp3.MultipartBody;

import okhttp3.RequestBody;
import okhttp3.Response;

///**
// * Created by SHWETHA on 29-10-2017.
// */
public class FileTransferAndLedger extends AppCompatActivity {
    Button filetransfer,fileupload;
    private Button start;
    private TextView output;
    private OkHttpClient client;
    private WebSocketClient mWebSocketClient;
    public EchoSocketListener listener;
    public WebSocket ws;
    //public static final String URL ="http://192.168.1.103:8080/ShwethaBlockchain/BlockchainServer?data=5&req='file'";
    public static final String URL = "http://192.168.0.107:8080/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filetransferandledger);
        filetransfer = (Button) findViewById(R.id.allow);
        fileupload=(Button)findViewById(R.id.uploadbutton);
        //andriod server

//       `


        client = new OkHttpClient();
        Log.i("Websocket","Client");
        start();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
                return;
            }
        }
        enable_button();

//        filetransfer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GetXMLTask task = new GetXMLTask();
//                task.execute(new String[] { URL });
//
//            }
//        });
    }
    private void start() {
        client = new OkHttpClient.Builder()
                .readTimeout(120,  TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url("ws://172.16.41.234:8080/Blockchain/ws/")
                .build();
        Log.i("Websocket",request.toString());
        listener = new EchoSocketListener();
        WebSocket ws = client.newWebSocket(request, listener);

        Log.i("ws", ws.toString());

        client.dispatcher().executorService().shutdown();
    }

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText(output.getText().toString() + "\n\n" + txt);
            }
        });
    }

    private void enable_button() {
        fileupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialFilePicker()
                        .withActivity(FileTransferAndLedger.this)
                        .withRequestCode(10)
                        .start();

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            enable_button();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
            }
        }
    }

    ProgressDialog progress;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(requestCode == 10 && resultCode == RESULT_OK){

            progress = new ProgressDialog(FileTransferAndLedger.this);
            progress.setTitle("Uploading");
            progress.setMessage("Please wait...");
//            progress.show();

            Thread t = new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {

                    File f  = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                    Log.i("asd",data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));

                    byte bytes[]=new byte[(int)f.length()];
                    try {
                        FileInputStream fileInputStream = new FileInputStream(f);
                        fileInputStream.read(bytes);
//                        for (int i = 0; i < bytes.length; i++) {
//                            Log.i("char",(char)bytes[i] +"");
//                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("File Not Found.");
                        e.printStackTrace();
                    }
                    catch (IOException e1) {
                        System.out.println("Error Reading The File.");
                        e1.printStackTrace();
                    }
                    String content_type  = getMimeType(f.getPath());
                    String file_path = f.getAbsolutePath();
                    //RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);

                    try {
                        Uri selectedUri = Uri.fromFile(f);
                        String fileExtension
                                = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
                        String mimeType
                                = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                       listener.sendFileData(bytes,f.getName(), Long.parseLong(String.valueOf(f.length() / 1024)), mimeType);

//                        progress.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }

    private String getMimeType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}

    class GetXMLTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            String output = null;
            for (String url : urls) {
                output = getOutputFromUrl(url);
            }
            return output;
        }

        private String getOutputFromUrl(String url) {
            StringBuffer output = new StringBuffer("");
            try {
                Log.d("URLS",url);
                InputStream stream = getHttpConnection(url);

               BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(stream));
                String s = "";
                while ((s = buffer.readLine()) != null)
                    output.append(s);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return output.toString();
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }

        @Override
        protected void onPostExecute(String output) {
           // Toast.makeText(getApplica,output,   Toast.LENGTH_LONG).show();
            System.out.println("DONE");
        }
      }

