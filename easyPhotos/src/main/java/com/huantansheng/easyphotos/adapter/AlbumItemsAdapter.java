package com.huantansheng.easyphotos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.models.Album.entity.AlbumItem;

import java.util.ArrayList;

/**
 * 媒体列表适配器
 * Created by huan on 2017/10/23.
 */

public class AlbumItemsAdapter extends RecyclerView.Adapter {
    ArrayList<AlbumItem> albumItems;
    RequestManager mGlide;
    LayoutInflater mInflater;
    int selectedPosition;
    OnClickListener listener;

    public interface OnClickListener {
        void onAlbumItemClick(int position);
    }


    public AlbumItemsAdapter(Context cxt, ArrayList<AlbumItem> list, int selectedPosition, OnClickListener listener) {
        this.albumItems = list;
        this.mInflater = LayoutInflater.from(cxt);
        this.listener = listener;
        this.mGlide = Glide.with(cxt);
        this.selectedPosition = selectedPosition;
        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.ic_photo).error(R.drawable.ic_photo);
        mGlide.applyDefaultRequestOptions(options);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlbumItemsViewHolder(mInflater.inflate(R.layout.item_dialog_album_items, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AlbumItemsViewHolder) {
            AlbumItem item = albumItems.get(position);
            mGlide.load(item.coverImagePath).into(((AlbumItemsViewHolder) holder).ivAlbumCover);
            ((AlbumItemsViewHolder) holder).tvAlbumName.setText(item.name);
            ((AlbumItemsViewHolder) holder).tvAlbumPhotosCount.setText(item.photos.size() + "张");
            if (selectedPosition == position) {
                ((AlbumItemsViewHolder) holder).ivSelected.setVisibility(View.VISIBLE);
            } else {
                ((AlbumItemsViewHolder) holder).ivSelected.setVisibility(View.INVISIBLE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int tempSelected = selectedPosition;
                    selectedPosition = position;
                    notifyItemChanged(tempSelected);
                    notifyItemChanged(position);
                    listener.onAlbumItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return albumItems.size();
    }

    public class AlbumItemsViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAlbumCover;
        TextView tvAlbumName;
        TextView tvAlbumPhotosCount;
        ImageView ivSelected;

        public AlbumItemsViewHolder(View itemView) {
            super(itemView);
            this.ivAlbumCover = (ImageView) itemView.findViewById(R.id.iv_album_cover);
            this.tvAlbumName = (TextView) itemView.findViewById(R.id.tv_album_name);
            this.tvAlbumPhotosCount = (TextView) itemView.findViewById(R.id.tv_album_photos_count);
            this.ivSelected = (ImageView) itemView.findViewById(R.id.iv_selected);
        }

    }
}
