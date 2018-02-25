package com.example.shwetha.blockdata;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raulakshay on 25/2/18.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private ArrayList<fileMetaData> mFileMetaData;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mFileName;

        public ViewHolder(View v) {
            super(v);
            mFileName = (TextView) v.findViewById(R.id.fileName);
        }
    }

    public MyAdapter(ArrayList<fileMetaData> fmd) {
        mFileMetaData = fmd;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mFileName.setText(mFileMetaData.get(position).getFileName());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return mFileMetaData.size();
    }
}
