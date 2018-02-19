package com.example.shwetha.blockdata;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.java_websocket.client.WebSocketClient;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.webkit.MimeTypeMap;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import java.io.File;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

///**
// * Created by SHWETHA on 29-10-2017.
// */
public class FileTransferAndLedger extends AppCompatActivity {
    Button filetransfer, fileupload;
    private Button start;
    private TextView output;
    private OkHttpClient client;
    private WebSocketClient mWebSocketClient;
    public EchoSocketListener listener;
    public WebSocket ws;
    File f;
    public static final String URL = "http://192.168.0.107:8080/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filetransferandledger);
        filetransfer = (Button) findViewById(R.id.allow);
        fileupload = (Button) findViewById(R.id.uploadbutton);
        //andriod server
        Log.d("stau", "here");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        }
        try {
            Log.i("asd","hey");
            hey();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    //
//        client = new OkHttpClient();
//        Log.i("Websocket","Client");
//        //start();
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
//                return;
//            }
//        }
//        enable_button();
//
//
//    }
//    private void start() {
//        client = new OkHttpClient.Builder()
//                .readTimeout(120,  TimeUnit.SECONDS)
//                .build();
//        Request request = new Request.Builder()
//                .url("ws://10.0.0.3:8080/Blockchain/ws/")
//                .build();
//        Log.i("Websocket",request.toString());
//        listener = new EchoSocketListener();
//        WebSocket ws = client.newWebSocket(request, listener);
//
//        Log.i("ws", ws.toString());
//
//        client.dispatcher().executorService().shutdown();
//    }
//
//    private void output(final String txt) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                output.setText(output.getText().toString() + "\n\n" + txt);
//            }
//        });
//    }
//
//    private void enable_button() {
//        fileupload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {

//                new MaterialFilePicker()
//                        .withActivity(FileTransferAndLedger.this)
//                        .withRequestCode(10)
//                        .start();
//
//            }
//        });
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == 200 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//            // enable_button();
//                //this is not being called
//            Log.d("MyApp", "REQUEST");
//            fileupload.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    try {
//                        hey();
//                    } catch (InvalidKeyException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (NoSuchAlgorithmException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (NoSuchPaddingException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//        } else {
//            Log.d("MyApp", "ELSE REQUEST");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//            }
//            try{
//                hey();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    ProgressDialog progress;
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
//        if(requestCode == 10 && resultCode == RESULT_OK) {
//
//            progress = new ProgressDialog(FileTransferAndLedger.this);
//            progress.setTitle("Uploading");
//            progress.setMessage("Please wait...");
//            progress.show();
//
//
//            Thread t = new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//
//
//                    f = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
//                    Log.i("asd", data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
//
//                    byte bytes[] = new byte[(int) f.length()];

    static void hey() throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        // Here you read the cleartext.
        File extStore = Environment.getExternalStorageDirectory();
        FileInputStream fis = new FileInputStream("/storage/emulated/0/Bluetooth/shwetha.txt");
        // This stream write the encrypted text. This stream will be wrapped by
        // another stream.
        FileOutputStream fos = new FileOutputStream("/storage/emulated/0/Bluetooth/shwe.txt");

        // Length is 16 byte
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(),
                "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while ((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        Log.d("MyApp", "I am here");
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
    }
}







//                    try {
//                        Uri selectedUri = Uri.fromFile(f);
//                        String fileExtension
//                                = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
//                        String mimeType
//                                = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
//                        listener.sendFileData(bytes, f.getName(), Long.parseLong(String.valueOf(f.length() / 1024)), mimeType);
//
//                        progress.dismiss();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            });
//            t.start();
//        }}
//
//
//
//    private String getMimeType(String path) {
//
//        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
//
//        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//    }


