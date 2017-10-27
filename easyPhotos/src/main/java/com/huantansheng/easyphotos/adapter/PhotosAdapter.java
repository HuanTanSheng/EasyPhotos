package com.huantansheng.easyphotos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.models.Album.entity.PhotoItem;
import com.huantansheng.easyphotos.result.Result;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.ui.view.PressedImageView;

import java.util.ArrayList;

/**
 * 专辑相册适配器
 * Created by huan on 2017/10/23.
 */

public class PhotosAdapter extends RecyclerView.Adapter {

    ArrayList<PhotoItem> dataList;
    RequestManager mGlide;
    LayoutInflater mInflater;
    OnClickListener listener;
    boolean unable, isSingle;
    int singlePosition;
    private int padding = 0;

    public interface OnClickListener {
        void onPhotoClick(int position);

        void onSelectorOutOfMax();

        void onSelectorChanged();

        void onCameraClicked();
    }


    public PhotosAdapter(Context cxt, ArrayList<PhotoItem> dataList, OnClickListener listener) {
        this.dataList = new ArrayList<>();
        this.dataList.addAll(dataList);
        this.listener = listener;
        this.mInflater = LayoutInflater.from(cxt);
        this.unable = false;
        this.isSingle = Setting.count == 1;
        this.mGlide = Glide.with(cxt);
        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.ic_photo_easy_photos).error(R.drawable.ic_photo_easy_photos);
        this.mGlide.applyDefaultRequestOptions(options);
    }

    public void setData(ArrayList<PhotoItem> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(mInflater.inflate(R.layout.item_rv_photos_easy_photos, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PhotoViewHolder) {
            final PhotoItem item = dataList.get(position);
            updateSelector(((PhotoViewHolder) holder).tvSelector, item.selected, item.path, position);
            if (item.isCamera) {
                if (padding == 0) {
                    padding = ((PhotoViewHolder) holder).ivPhoto.getPaddingBottom();
                }
                ((PhotoViewHolder) holder).ivPhoto.setPadding(padding, padding, padding, padding);
                ((PhotoViewHolder) holder).ivPhoto.setImageResource(R.drawable.ic_camera_easy_photos);
                ((PhotoViewHolder) holder).vSelector.setVisibility(View.GONE);
                ((PhotoViewHolder) holder).tvSelector.setVisibility(View.GONE);
                ((PhotoViewHolder) holder).ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onCameraClicked();
                    }
                });
            } else {
                ((PhotoViewHolder) holder).ivPhoto.setPadding(0, 0, 0, 0);
                mGlide.load(item.path).into(((PhotoViewHolder) holder).ivPhoto);
                ((PhotoViewHolder) holder).vSelector.setVisibility(View.VISIBLE);
                ((PhotoViewHolder) holder).tvSelector.setVisibility(View.VISIBLE);
//                ((PhotoViewHolder) holder).ivPhoto.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        listener.onPhotoClick(position);
//                    }
//                });
            }

            ((PhotoViewHolder) holder).vSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSingle) {
                        singleSelector(item, position);
                        return;
                    }
                    if (unable) {
                        if (item.selected) {
                            Result.removePhoto(item);
                            if (unable) {
                                unable = false;
                            }
                            listener.onSelectorChanged();
                            notifyDataSetChanged();
                            return;
                        }
                        listener.onSelectorOutOfMax();
                        return;
                    }
                    item.selected = !item.selected;
                    if (item.selected) {
                        Result.addPhoto(item);
                        ((PhotoViewHolder) holder).tvSelector.setBackgroundResource(R.drawable.bg_select_true_easy_photos);
                        ((PhotoViewHolder) holder).tvSelector.setText(String.valueOf(Result.photos.size()));
                        if (Result.photos.size() == Setting.count) {
                            unable = true;
                            notifyDataSetChanged();
                        }
                    } else {
                        Result.removePhoto(item);
                        if (unable) {
                            unable = false;
                        }
                        notifyDataSetChanged();
                    }
                    listener.onSelectorChanged();
                }
            });
        }
    }

    private void singleSelector(PhotoItem photoItem, int position) {
        if (Result.photos.size() > 0) {
            if (Result.photos.get(0).equals(photoItem.path)) {
                Result.removePhoto(photoItem);
                notifyItemChanged(position);
            } else {
                Result.removePhoto(0);
                Result.addPhoto(photoItem);
                notifyItemChanged(singlePosition);
                notifyItemChanged(position);
            }
        } else {
            Result.addPhoto(photoItem);
            notifyItemChanged(position);
        }
        listener.onSelectorChanged();
    }

    private void updateSelector(TextView tvSelector, boolean selected, String photoPath, int position) {
        if (selected) {
            tvSelector.setText(String.valueOf(Result.photos.indexOf(photoPath) + 1));
            tvSelector.setBackgroundResource(R.drawable.bg_select_true_easy_photos);
            if (isSingle) {
                singlePosition = position;
                tvSelector.setText("√");
            }
        } else {
            if (unable) {
                tvSelector.setBackgroundResource(R.drawable.bg_select_false_unable_easy_photos);
            } else {
                tvSelector.setBackgroundResource(R.drawable.bg_select_false_easy_photos);
            }
            tvSelector.setText(null);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        PressedImageView ivPhoto;
        TextView tvSelector;
        View vSelector;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            this.ivPhoto = (PressedImageView) itemView.findViewById(R.id.iv_photo);
            this.tvSelector = (TextView) itemView.findViewById(R.id.tv_selector);
            this.vSelector = itemView.findViewById(R.id.v_selector);
        }
    }
}
