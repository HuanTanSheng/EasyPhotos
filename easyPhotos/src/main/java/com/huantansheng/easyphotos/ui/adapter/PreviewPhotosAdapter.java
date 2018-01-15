package com.huantansheng.easyphotos.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Type;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.setting.Setting;

import java.util.ArrayList;

/**
 * 大图预览界面图片集合的适配器
 * Created by huan on 2017/10/26.
 */

public class PreviewPhotosAdapter extends RecyclerView.Adapter<PreviewPhotosAdapter.PreviewPhotosViewHolder> {
    private ArrayList<Photo> photos;
    private OnClickListener listener;
    private LayoutInflater inflater;

    public interface OnClickListener {
        void onPhotoClick();

        void onPhotoScaleChanged();
    }

    public PreviewPhotosAdapter(Context cxt, ArrayList<Photo> photos, OnClickListener listener) {
        this.photos = photos;
        this.inflater = LayoutInflater.from(cxt);
        this.listener = listener;
    }

    @Override
    public PreviewPhotosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PreviewPhotosViewHolder(inflater.inflate(R.layout.item_preview_photo_easy_photos, parent, false));
    }

    @Override
    public void onBindViewHolder(final PreviewPhotosViewHolder holder, int position) {
        String path = photos.get(position).path;
        String type = photos.get(position).type;
        if (path.endsWith(Type.GIF) || type.endsWith(Type.GIF)) {
            Setting.imageEngine.loadGif(holder.ivPhoto.getContext(), path, holder.ivPhoto);
        } else {
            Setting.imageEngine.loadPhoto(holder.ivPhoto.getContext(), path, holder.ivPhoto);
        }
        holder.ivPhoto.setScale(1f);
        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhotoClick();
            }
        });
        holder.ivPhoto.setOnScaleChangeListener(new OnScaleChangedListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
                listener.onPhotoScaleChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class PreviewPhotosViewHolder extends RecyclerView.ViewHolder {
        public PhotoView ivPhoto;

        PreviewPhotosViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (PhotoView) itemView.findViewById(R.id.iv_photo);
        }
    }
}
