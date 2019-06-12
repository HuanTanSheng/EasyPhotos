package com.huantansheng.easyphotos.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Type;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.utils.media.DurationUtils;

import java.util.ArrayList;

/**
 * 拼图相册适配器
 * Created by huan on 2017/10/23.
 */

public class PuzzleSelectorPreviewAdapter extends RecyclerView.Adapter {


    private ArrayList<Photo> dataList;
    private LayoutInflater mInflater;
    private OnClickListener listener;


    public PuzzleSelectorPreviewAdapter(Context cxt, ArrayList<Photo> dataList, OnClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
        this.mInflater = LayoutInflater.from(cxt);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new PhotoViewHolder(mInflater.inflate(R.layout.item_puzzle_selector_preview_easy_photos, parent, false));

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final int p = position;
        Photo photo = dataList.get(position);
        String path = photo.path;
        String type = photo.type;
        long duration = photo.duration;
        final boolean isGif = path.endsWith(Type.GIF) || type.endsWith(Type.GIF);
        if (Setting.showGif && isGif) {
            Setting.imageEngine.loadGifAsBitmap(((PhotoViewHolder) holder).ivPhoto.getContext(), path, ((PhotoViewHolder) holder).ivPhoto);
            ((PhotoViewHolder) holder).tvType.setText(R.string.gif_easy_photos);
            ((PhotoViewHolder) holder).tvType.setVisibility(View.VISIBLE);
        } else if (Setting.showVideo && type.contains(Type.VIDEO)) {
            Setting.imageEngine.loadPhoto(((PhotoViewHolder) holder).ivPhoto.getContext(), path, ((PhotoViewHolder) holder).ivPhoto);
            ((PhotoViewHolder) holder).tvType.setText(DurationUtils.format(duration));
            ((PhotoViewHolder) holder).tvType.setVisibility(View.VISIBLE);
        } else {
            Setting.imageEngine.loadPhoto(((PhotoViewHolder) holder).ivPhoto.getContext(), path, ((PhotoViewHolder) holder).ivPhoto);
            ((PhotoViewHolder) holder).tvType.setVisibility(View.GONE);
        }

        ((PhotoViewHolder) holder).ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteClick(p);
            }
        });
    }


    @Override
    public int getItemCount() {
        return null == dataList ? 0 : dataList.size();
    }


    public interface OnClickListener {
        void onDeleteClick(int position);
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        ImageView ivDelete;
        TextView tvType;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            this.ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            this.ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            this.tvType = (TextView) itemView.findViewById(R.id.tv_type);
        }
    }
}
