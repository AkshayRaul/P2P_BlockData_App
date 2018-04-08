package com.example.shwetha.blockdata;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.MainThread;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okio.ByteString;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.webkit.MimeTypeMap;

import com.android.volley.toolbox.PoolingByteArrayOutputStream;
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

    public static final String URL = "ws://10.0.0.3:8080/Blockchain/ws/";
    public EchoSocketListener listener;
    static SharedPreferences sharedPref;
    static long currentTime = new Date().getTime();
    private OkHttpClient client;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    FloatingActionButton fab;
    FileOutputStream outputStream;
    ArrayList<String> fileinfo = new ArrayList<String>();
    File f;
    ProgressDialog progress;

    public void showPopup(View v) {

    }




    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = sharedPref.edit();
        String onlineTime = sharedPref.getString("onlineTime", null);
        if (onlineTime.split(",")[0].compareToIgnoreCase(new Date().getDate() + "") != 0 && onlineTime.split(",")[2].compareToIgnoreCase("0") != 0) {
            editor.putString("onlineTime", new Date().getDate() + "," + (new Date().getTime() - currentTime) + "," + (Integer.parseInt(onlineTime.split(",")[2]) + 1));
        } else if (onlineTime.split(",")[0].compareToIgnoreCase(new Date().getDate() + "") == 0 && onlineTime.split(",")[2].compareToIgnoreCase("0") != 0) {
            editor.putString("onlineTime", new Date().getDate() + "," + (new Date().getTime() - currentTime) + "," + (Integer.parseInt(onlineTime.split(",")[2])));
        } else {
            editor.putString("onlineTime", new Date().getDate() + "," + (new Date().getTime() - currentTime) + ",0");
        }
        editor.commit();
        Log.i("Date", new Date().toString() + "," + (currentTime - new Date().getTime()));
        Log.d("ondestro", "hey");
        String filename = "blockchain";
        String concatenate = "";

        String files = "";
        for (int i = 0; i < EchoSocketListener.fMD.size(); i++) {
            files += EchoSocketListener.fMD.get(i).getFileName() + "," + EchoSocketListener.fMD.get(i).getFileId() + ";";
        }
        Log.i("Fileoutput", files);
        Log.i("FileInputSize", files.getBytes().length + "");

        try {
            FileOutputStream fos = openFileOutput("fileList", Context.MODE_PRIVATE);
            fos.write(files.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
//
//        String fileContents = EchoSocketListener.fMD.get(i).getFileName();
//        String fileID = EchoSocketListener.fMD.get(i).getFileId();
//        concatenate = concatenate + fileContents + "," + fileID + ";";
//        Log.d("fiename", fileContents);
//
//        try {
//            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
//            outputStream.write(concatenate.getBytes());
//            outputStream.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }




    @Override
    protected void onStart() {
        super.onStart();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getApplicationContext().getSharedPreferences("mypref", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (sharedPref.getString("onlineTime", null) == null) {
            editor.putString("onlineTime", "0,0,1");
            editor.commit();
        }

        editor.putString("FileKey", "MyDifficultPassw");
        editor.commit();
        Map<String, ?> kv = sharedPref.getAll();
        Set<String> keys = kv.keySet();
        for (String val : keys) {
            Log.i(val, kv.get(val) + "");
        }
        setContentView(R.layout.filetransferandledger);
        byte bytes[];
        try {
            FileInputStream fis = openFileInput("fileList");
            int size = fis.available();
            Log.i("FileInputSize", size + "");
            bytes = new byte[size];
            fis.read(bytes);
            String fileMetaDataString = new String(bytes);
            Log.i("FileInput", fileMetaDataString);
            String files[] = fileMetaDataString.split(";");
            for (int i = files.length - 1; i >= 0; i--) {
                EchoSocketListener.fMD.add(new fileMetaData(files[i].split(",")[0], files[i].split(",")[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(EchoSocketListener.fMD);
        mRecyclerView.setAdapter(mAdapter);
        if (mAdapter.getItemCount() == 0) {
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialFilePicker()
                        .withActivity(FileTransferAndLedger.this)
                        .withRequestCode(10)
                        .start();

            }
        });

        client = new OkHttpClient();
        start();
        Log.d("stau", "here");
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        // TODO Handle item click
                        ImageView menu = (ImageView) view.findViewById(R.id.popmenu);
                        menu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //creating a popup menu
                                PopupMenu popup = new PopupMenu(v.getContext(), v);
                                popup.getMenuInflater().inflate(R.menu.overflowmenu, popup.getMenu());
                                //inflating menu from xml resource
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        switch (menuItem.getItemId()) {
                                            case R.id.download:
                                                try {
                                                    fileMetaData f = EchoSocketListener.fMD.get(position);
                                                    EchoSocketListener.getFile(f.getFileId(), f.getFileName(), f.getFileSize());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                return true;
                                            case R.id.delete:
                                                try {
                                                    fileMetaData f = EchoSocketListener.fMD.get(position);
                                                    EchoSocketListener.deleteFile(f.getFileId(), f.getFileName(), f.getFileSize());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                return true;
                                            default:
                                                return false;
                                        }
                                    }
                                });
                                //adding click listener

                                //displaying the popup
                                popup.show();
                            }
                        });

                    }
                })
        );


    }


    private void start() {
        client = new OkHttpClient.Builder()
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .addHeader("UserId", UserKey.token.trim())
                .url(URL)
                .build();
        Log.i("Websocket", request.toString());
        listener = new EchoSocketListener();
        WebSocket ws = client.newWebSocket(request, listener);

        Log.i("ws", ws.toString());

        client.dispatcher().executorService().shutdown();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK) {

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
                        FileOutputStream fos = new FileOutputStream("/storage/emulated/0/Bluetooth/Encryt_" + f.getName());
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

                        File encF = new File("/storage/emulated/0/Bluetooth/Encryt_" + f.getName());

                       // fileinfo.add(f.getName());
                        byte bytes[] = new byte[(int) encF.length() + 2];
                        bytes[0] = bytes[1] = 1;
                        FileInputStream encFis = new FileInputStream(encF);
                        encFis.read(bytes, 2, bytes.length - 2);
                        Uri selectedUri = Uri.fromFile(f);
                        String fileExtension
                                = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
                        String mimeType
                                = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                        listener.sendFileData(bytes, f.getName(), Long.parseLong(String.valueOf(encF.length() / 1024)), mimeType);
                        progress.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();

                            }
                        });


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


