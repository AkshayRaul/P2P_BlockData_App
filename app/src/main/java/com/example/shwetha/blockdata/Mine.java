package com.example.shwetha.blockdata;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.test.ActivityUnitTestCase;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by raulakshay on 9/4/18.
 */

public class Mine extends AppCompatActivity{


    @Override
    public void onCreate(Bundle savedInstances){
        super.onCreate(savedInstances);
        setContentView(R.layout.content_main);

        String content="";
        try{
            File blockchain= new File("/storage/emulated/0/BlockStorage/blockchain.csv");
            byte[] file=new byte[(int)blockchain.length()];
            FileInputStream fis= new FileInputStream(blockchain);
            fis.read(file);
            content=new String(file);
            Log.i("BLOCKCHAIN",content);

        }catch (FileNotFoundException f){

        }catch (IOException ioe){

        }
        String blocks[]=content.split("\n");
        int size=blocks.length;
        int mode[]=new int[size];
        String currhash[]=new String[size];
        String prevhash[]=new String[size];
        String fileId[]=new String[size];
        String penalizedUsers[]=new String[size/2];
        for(int i=0;i<size;i++){
            String block[]=blocks[i].split(",");
            if(block[0].compareToIgnoreCase("Create")==0){
                mode[i]=0;
            }else if(block[0].compareToIgnoreCase("Fetch")==0){
                mode[i]=1;
            }
            currhash[i]=block[3];
            prevhash[i]=block[4];
            fileId[i]=block[7];
            Log.i("Mode",mode[i]+"");
            Log.i("prevHash",prevhash[i]);
            Log.i("CH",currhash[i]);
            Log.i("fileId",fileId[i]);
        }
        int flag=0;
        for(int i=0;i<size;i++){
            if(mode[i]==1){
                for(int j=0;j<=i;j++){
                    TextView tamper=(TextView) findViewById(R.id.tamper);
                    Log.i("fileId1+fileId2",fileId[i]+" "+fileId[j]);
                    Log.i("ch1+ch2",currhash[i]+" "+currhash[j]);
                    if(currhash[i].compareToIgnoreCase(currhash[j])==0&&fileId[i].compareToIgnoreCase(fileId[j])==0){
                        Log.i("Tamper","Tamper");
                        tamper.setText("File "+fileId[i]+" Tampered!!");
                        flag=1;
                        break;
                    }
                }
                if(flag==1){
                    break;
                }else{
                    TextView tamper=(TextView) findViewById(R.id.tamper);

                    tamper.setText("All Clear!");
                }
            }
        }
    }
}
