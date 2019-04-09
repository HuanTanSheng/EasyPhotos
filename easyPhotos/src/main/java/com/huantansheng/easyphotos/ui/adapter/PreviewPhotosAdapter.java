package com.huantansheng.easyphotos.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Type;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.setting.Setting;

import java.io.File;
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
        final String path = photos.get(position).path;
        final String type = photos.get(position).type;

        holder.ivPlay.setVisibility(View.GONE);
        if (type.contains(Type.VIDEO)) {
            Setting.imageEngine.loadPhoto(holder.ivPhoto.getContext(), path, holder.ivPhoto);
            holder.ivPlay.setVisibility(View.VISIBLE);
            holder.ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toPlayVideo(v, path, type);
                }
            });
        } else if (path.endsWith(Type.GIF) || type.endsWith(Type.GIF)) {
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

    private void toPlayVideo(View v, String path, String type) {
        Context context = v.getContext();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = getUri(context, path);
        intent.setDataAndType(uri, type);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private Uri getUri(Context context, String path) {
        File file = new File(path);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, Setting.fileProviderAuthority, file);
        } else {
            return Uri.fromFile(file);
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class PreviewPhotosViewHolder extends RecyclerView.ViewHolder {
        public PhotoView ivPhoto;
        ImageView ivPlay;

        PreviewPhotosViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (PhotoView) itemView.findViewById(R.id.iv_photo);
            ivPlay = (ImageView) itemView.findViewById(R.id.iv_play);
        }
    }
}
