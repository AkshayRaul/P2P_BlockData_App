package com.example.shwetha.blockdata;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ListView;
import android.widget.TextView;
import org.java_websocket.client.WebSocketClient;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static android.app.PendingIntent.getActivity;

///**
// * Created by SHWETHA on 29-10-2017.
// */
public class FileTransferAndLedger extends AppCompatActivity {
    Button filetransfer, fileupload,receivefile;
    private Button start;
    private TextView output;
    private OkHttpClient client;
    private WebSocketClient mWebSocketClient;
    public EchoSocketListener listener;
    public WebSocket ws;
    ArrayList<String> fileinfo=new ArrayList<String>();
   ListView filedetails;

    File f;
    ProgressDialog progress;
    public static final String URL = "ws://172.16.41.234:8080/Blockchain/ws/";
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.filetransferandledger);

        fileupload = (Button) findViewById(R.id.uploadbutton);
        receivefile = (Button) findViewById(R.id.receivefile);
        //andriod server
       // filedetails = (ListView)
         //       findViewById(R.id.fileslist);
        sharedPref = getApplicationContext().getSharedPreferences("mypref",0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("FileKey", "MyDifficultPassw");
        editor.commit();
        //String keys=sharedPref.getString("FileKey", null);


//        receivefile.setOnClickListener(new View.OnClickListener() {
//
//
//            @Override
//            public void onClick(View v) {
//                try {
//                    String keys=sharedPref.getString("FileKey", null);
//                    byte[] decodedData = decodeFile(keys, readFile());
//                    String str = new String(decodedData);
//                    System.out.println("DECODED FILE CONTENTS : " + str);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        }
        fileupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialFilePicker()
                        .withActivity(FileTransferAndLedger.this)
                        .withRequestCode(10)
                        .start();

            }
        });

        client = new OkHttpClient();
//        Log.i("Websocket","Client");
        start();
        Log.d("stau", "here");


       }
//    public static byte[] decodeFile(SecretKey yourKey, byte[] fileData)
//            throws Exception {
//        byte[] decrypted = null;
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.DECRYPT_MODE, yourKey, new IvParameterSpec(
//                new byte[cipher.getBlockSize()]));
//        decrypted = cipher.doFinal(fileData);
//        return decrypted;
//    }


    //

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
//                return;
//            }
//        }

//
//
//    }
   private void start() {
        client = new OkHttpClient.Builder()
                .readTimeout(120,  TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .addHeader("UserId",UserKey.token)
                .url(URL)
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(requestCode == 10 && resultCode == RESULT_OK) {

            progress = new ProgressDialog(FileTransferAndLedger.this);
            progress.setTitle("Uploading");
            progress.setMessage("Please wait...");
            progress.show();


            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {

                    f = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                    Log.i("asd", data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));

                    File extStore = Environment.getExternalStorageDirectory();
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(f);
                        FileOutputStream fos = new FileOutputStream("/storage/emulated/0/Bluetooth/Encryt_"+f.getName());
                       // filedetails.setAdapter(new FileDetails
                        //        (FileTransferAndLedger.this,1 ,f.getName()));
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


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // This stream write the encrypted text. This stream will be wrapped by
                // another stream.
                        try {

                        File encF = new File("/storage/emulated/0/Bluetooth/Encryt_"+f.getName());
                        fileinfo.add(f.getName());
                        byte bytes[] = new byte[(int) encF.length()+2];
                        bytes[0]=bytes[1]=1;
                        FileInputStream encFis=new FileInputStream(encF);
                        encFis.read(bytes,2,bytes.length-2);
                        Uri selectedUri = Uri.fromFile(f);
                        String fileExtension
                                = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
                        String mimeType
                                = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                        listener.sendFileData(bytes, encF.getName(), Long.parseLong(String.valueOf(encF.length() / 1024)), mimeType);
                        progress.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
        });
        t.start();
        }
    }
//    public byte[] readFile() {
//        byte[] contents = null;
//
//        File file = new File(Environment.getExternalStorageDirectory()
//                + File.separator, e);
//        int size = (int) file.length();
//        contents = new byte[size];
//        try {
//            BufferedInputStream buf = new BufferedInputStream(
//                    new FileInputStream(file));
//            try {
//                buf.read(contents);
//                buf.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return contents;
//    }

//}


    private void hey() throws IOException, NoSuchAlgorithmException,
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













    private String getMimeType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }


        }


