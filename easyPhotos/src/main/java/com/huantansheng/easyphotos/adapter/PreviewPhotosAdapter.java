package com.huantansheng.easyphotos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.huantansheng.easyphotos.R;

import java.util.ArrayList;

/**
 * 预览界面图片集合的适配器
 * Created by huan on 2017/10/26.
 */

public class PreviewPhotosAdapter extends RecyclerView.Adapter<PreviewPhotosAdapter.PreviewPhotosViewHolder> {
    ArrayList<String> photos;
    RequestManager mGlide;
    LayoutInflater inflater;
    OnClickListener listener;

    public interface OnClickListener {
        void onPhotoClick();
    }

    public PreviewPhotosAdapter(Context cxt, ArrayList<String> photos, OnClickListener listener) {
        this.photos = new ArrayList<>();
        this.photos.addAll(photos);
        this.inflater = LayoutInflater.from(cxt);
        this.listener = listener;
        this.mGlide = Glide.with(cxt);
        RequestOptions options = new RequestOptions().centerInside().placeholder(R.drawable.ic_photo_easy_photos).error(R.drawable.ic_photo_easy_photos);
        this.mGlide.applyDefaultRequestOptions(options);
    }

    @Override
    public PreviewPhotosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PreviewPhotosViewHolder(inflater.inflate(R.layout.item_preview_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(final PreviewPhotosViewHolder holder, int position) {
        mGlide.load(photos.get(position)).into(holder.ivPhoto);
        holder.ivPhoto.setScale(1f);
        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhotoClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class PreviewPhotosViewHolder extends RecyclerView.ViewHolder {
        public PhotoView ivPhoto;

        public PreviewPhotosViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (PhotoView) itemView.findViewById(R.id.iv_photo);
        }
    }
}
