package com.example.shwetha.blockdata;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raulakshay on 25/2/18.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private ArrayList<fileMetaData> mFileMetaData;
    private Context mCtx;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mFileName;
        public ImageView menuItem;

        public ViewHolder(View v) {
            super(v);
            mFileName = (TextView) v.findViewById(R.id.fileName);
            menuItem = (ImageView) v.findViewById(R.id.popmenu);
        }
    }

    public MyAdapter(ArrayList<fileMetaData> fmd) {
        mFileMetaData = fmd;
        // this.mCtx=mCtx;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mFileName.setText(mFileMetaData.get(position).getFileName());
        holder.menuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

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
