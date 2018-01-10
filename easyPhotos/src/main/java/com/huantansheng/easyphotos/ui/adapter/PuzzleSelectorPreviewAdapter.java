package com.huantansheng.easyphotos.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Type;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.setting.Setting;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * 拼图相册适配器
 * Created by huan on 2017/10/23.
 */

public class PuzzleSelectorPreviewAdapter extends RecyclerView.Adapter {


    private ArrayList<Photo> dataList;
    private RequestManager mGlide;
    private LayoutInflater mInflater;
    private OnClickListener listener;


    public PuzzleSelectorPreviewAdapter(Context cxt, ArrayList<Photo> dataList, OnClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
        this.mInflater = LayoutInflater.from(cxt);
        this.mGlide = Glide.with(cxt);
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
        if (Setting.showGif) {
            if (path.endsWith(Type.GIF) || type.endsWith(Type.GIF)) {
                mGlide.asBitmap().load(path).into(((PhotoViewHolder) holder).ivPhoto);
                ((PhotoViewHolder) holder).tvGif.setVisibility(View.VISIBLE);
            } else {
                mGlide.load(path).transition(withCrossFade()).into(((PhotoViewHolder) holder).ivPhoto);
                ((PhotoViewHolder) holder).tvGif.setVisibility(View.GONE);
            }
        } else {
            mGlide.load(path).transition(withCrossFade()).into(((PhotoViewHolder) holder).ivPhoto);
            ((PhotoViewHolder) holder).tvGif.setVisibility(View.GONE);
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
        TextView tvGif;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            this.ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            this.ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            this.tvGif = (TextView) itemView.findViewById(R.id.tv_gif);
        }
    }
}
