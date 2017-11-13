package com.huantansheng.easyphotos.sample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.huantansheng.easyphotos.models.album.entity.PhotoItem;
import com.huantansheng.easyphotos.sample.thisAppGlideModule.GlideApp;

import java.util.ArrayList;

/**
 * Created by huan on 2017/10/30.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainVH> {
    ArrayList<PhotoItem> list;
    LayoutInflater mInflater;
    RequestManager mGlide;

    public MainAdapter(Context cxt, ArrayList<PhotoItem> list) {
        this.list = list;
        mInflater = LayoutInflater.from(cxt);
        mGlide = GlideApp.with(cxt);
    }

    @Override
    public MainVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainVH(mInflater.inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(MainVH holder, int position) {
        mGlide.load(list.get(position).path).into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MainVH extends RecyclerView.ViewHolder {
        public ImageView ivPhoto;

        public MainVH(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
        }
    }
}
