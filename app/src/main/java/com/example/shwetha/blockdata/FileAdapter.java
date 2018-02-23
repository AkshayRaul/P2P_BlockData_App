//package com.example.shwetha.blockdata;
//
//import android.content.Context;
//import android.support.annotation.LayoutRes;
//import android.support.annotation.NonNull;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.BaseAdapter;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//import static java.util.Objects.*;
//
///**
// * Created by SHWETHA on 23-02-2018.
// */
//
//public class FileAdapter extends ArrayAdapter<FileDetails> {
//
//    private List<FileDetails> fileDetailsList=new ArrayList<>();
//    private Context fContext;
//    public FileAdapter(@NonNull Context context, int resource, ArrayList<FileDetails> list) {
//        super(context, 0,list );
//        fContext=context;
//        fileDetailsList=list;
//    }
//
//
//    @Override
//    public int getCount() {
//        return 0;
//    }
//
//
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        View listitem=convertView;
//          if(listitem==null){
//              listitem= LayoutInflater.from(fContext).inflate(R.layout.filetransferandledger)
//          }
//
//        return null;
//    }
//}
