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
import com.bumptech.glide.request.RequestOptions;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.models.ad.AdViewHolder;
import com.huantansheng.easyphotos.models.album.entity.AlbumItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * 媒体列表适配器
 * Created by huan on 2017/10/23.
 */

public class AlbumItemsAdapter extends RecyclerView.Adapter {
    private static final int TYPE_AD = 0;
    private static final int TYPE_ALBUM_ITEMS = 1;

    ArrayList<Object> dataList;
    RequestManager mGlide;
    LayoutInflater mInflater;
    int selectedPosition;
    OnClickListener listener;

    public interface OnClickListener {
        void onAlbumItemClick(int position);
    }


    public AlbumItemsAdapter(Context cxt, ArrayList<Object> list, int selectedPosition, OnClickListener listener) {
        this.dataList = list;
        this.mInflater = LayoutInflater.from(cxt);
        this.listener = listener;
        this.mGlide = Glide.with(cxt);
        this.selectedPosition = selectedPosition;
        RequestOptions options = new RequestOptions().centerCrop().error(R.drawable.ic_photo_error_easy_photos);
        mGlide.applyDefaultRequestOptions(options);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_AD:
                return new AdViewHolder(mInflater.inflate(R.layout.item_ad_easy_photos, parent, false));
            default:
                return new AlbumItemsViewHolder(mInflater.inflate(R.layout.item_dialog_album_items_easy_photos, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AlbumItemsViewHolder) {
            AlbumItem item = (AlbumItem) dataList.get(position);
            mGlide.load(item.coverImagePath).transition(withCrossFade()).into(((AlbumItemsViewHolder) holder).ivAlbumCover);
            ((AlbumItemsViewHolder) holder).tvAlbumName.setText(item.name);
            ((AlbumItemsViewHolder) holder).tvAlbumPhotosCount.setText(String.valueOf(item.photos.size()));
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
            return;
        }

        if (holder instanceof AdViewHolder) {
            WeakReference weakReference = (WeakReference) dataList.get(position);
            ((AdViewHolder) holder).adFrame.removeAllViews();
            if (weakReference != null) {
                View ad = (View) weakReference.get();
                if (null != ad) {
                    ((AdViewHolder) holder).adFrame.addView(ad);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = dataList.get(position);
        if (null == item || item instanceof WeakReference) {
            return TYPE_AD;
        } else {
            return TYPE_ALBUM_ITEMS;
        }
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
