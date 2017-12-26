package com.huantansheng.easyphotos.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.models.ad.AdViewHolder;
import com.huantansheng.easyphotos.models.album.entity.AlbumItem;
import com.huantansheng.easyphotos.setting.Setting;

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
    int adPosition = 0;

    public interface OnClickListener {
        void onAlbumItemClick(int position,int realPosition);
    }


    public AlbumItemsAdapter(Context cxt, ArrayList<Object> list, int selectedPosition, OnClickListener listener) {
        this.dataList = list;
        this.mInflater = LayoutInflater.from(cxt);
        this.listener = listener;
        this.mGlide = Glide.with(cxt);
        this.selectedPosition = selectedPosition;
        RequestOptions options = new RequestOptions().centerCrop();
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
                    int realPosition = position;
                    if (Setting.hasAlbumItemsAd()) {
                        if (position > adPosition) {
                            realPosition--;
                        }
                    }
                    int tempSelected = selectedPosition;
                    selectedPosition = position;
                    notifyItemChanged(tempSelected);
                    notifyItemChanged(position);
                    listener.onAlbumItemClick(position,realPosition);
                }
            });
            return;
        }

        if (holder instanceof AdViewHolder) {
            adPosition = position;
            if(!Setting.albumItemsAdIsOk){
                ((AdViewHolder) holder).adFrame.setVisibility(View.GONE);
                return;
            }

            WeakReference weakReference = (WeakReference) dataList.get(position);

            if (null != weakReference) {
                View adView = (View) weakReference.get();
                if (null != adView) {
                    if (null != adView.getParent()) {
                        if (adView.getParent() instanceof FrameLayout) {
                            ((FrameLayout) adView.getParent()).removeAllViews();
                        }
                    }
                    ((AdViewHolder) holder).adFrame.setVisibility(View.VISIBLE);
                    ((AdViewHolder) holder).adFrame.removeAllViews();
                    ((AdViewHolder) holder).adFrame.addView(adView);
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
