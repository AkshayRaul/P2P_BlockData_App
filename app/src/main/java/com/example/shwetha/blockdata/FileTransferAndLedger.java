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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

///**
// * Created by SHWETHA on 29-10-2017.
// */
public class FileTransferAndLedger extends AppCompatActivity {
    Button filetransfer,fileupload;
    private Button start;
    private TextView output;
   public OkHttpClient client;
  public WebSocketClient mWebSocketClient;
    public EchoSocketListener listener;
    public WebSocket ws;
    public static final String URL = "ws://10.0.0.5:8080/Blockchain/ws/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filetransferandledger);
        filetransfer = (Button) findViewById(R.id.allow);
        fileupload=(Button)findViewById(R.id.uploadbutton);


        //startService(new Intent(this,MyService.class));
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


    }
    private void start() {
        client = new OkHttpClient.Builder()
                .readTimeout(120,  TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
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
            progress.show();


            Thread t = new Thread(new Runnable() {

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


                    try {
                        Uri selectedUri = Uri.fromFile(f);
                        String fileExtension
                                = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
                        String mimeType
                                = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                       listener.sendFileData(bytes,f.getName(), Long.parseLong(String.valueOf(f.length() / 1024)), mimeType);

                        progress.dismiss();

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

