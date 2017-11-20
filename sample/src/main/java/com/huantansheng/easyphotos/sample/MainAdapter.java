package com.huantansheng.easyphotos.sample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.sample.thisAppGlideModule.GlideApp;

import java.util.ArrayList;

/**
 * Created by huan on 2017/10/30.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainVH> {
    ArrayList<Photo> list;
    LayoutInflater mInflater;
    RequestManager mGlide;

    public MainAdapter(Context cxt, ArrayList<Photo> list) {
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
        Photo photo = list.get(position);
        mGlide.load(photo.path).into(holder.ivPhoto);
        holder.tvMessage.setText("[图片名称]： "+photo.name+"\n[宽]："+photo.width+"\n[高]："+photo.height+"\n[文件大小,单位bytes]："+photo.size+"\n[日期，时间戳，秒]："+photo.time+"\n[图片地址]："+photo.path+"\n[图片类型]："+photo.type+"\n[是否选择原图]："+photo.selectedOriginal);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MainVH extends RecyclerView.ViewHolder {
        public ImageView ivPhoto;
        public TextView tvMessage;
        public MainVH(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            tvMessage = (TextView) itemView.findViewById(R.id.tv_message);
        }
    }
}
