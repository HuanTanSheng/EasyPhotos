package com.huantansheng.easyphotos.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.result.Result;
import com.huantansheng.easyphotos.ui.widget.PressedImageView;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * 预览所有选中图片集合的适配器
 * Created by huan on 2017/12/1.
 */

public class PreviewPhotosFragmentAdapter extends RecyclerView.Adapter<PreviewPhotosFragmentAdapter.PreviewPhotoVH> {
    private LayoutInflater inflater;
    private RequestManager mGlide;
    private OnClickListener listener;
    private int checkedPosition = -1;

    public PreviewPhotosFragmentAdapter(Context context, OnClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.mGlide = Glide.with(context);
        RequestOptions options = new RequestOptions().centerInside().error(R.drawable.ic_photo_error_easy_photos);
        this.mGlide.applyDefaultRequestOptions(options);
    }


    @Override
    public PreviewPhotoVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PreviewPhotoVH(inflater.inflate(R.layout.item_preview_selected_photos_easy_photos, parent, false));
    }

    @Override
    public void onBindViewHolder(PreviewPhotoVH holder, int position) {
        final int p = position;
        mGlide.load(Result.getPhotoPath(position)).transition(withCrossFade()).into(holder.ivPhoto);
        if (checkedPosition == p) {
            holder.ivPhoto.setBackgroundColor(ContextCompat.getColor(holder.ivPhoto.getContext(), R.color.menu_easy_photos));
        } else {
            holder.ivPhoto.setBackgroundColor(ContextCompat.getColor(holder.ivPhoto.getContext(), R.color.preview_selected_photos_background_easy_photos));
        }
        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPhotoClick(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Result.count();
    }

    public void setChecked(int position) {
        checkedPosition = position;
        notifyDataSetChanged();
    }

    class PreviewPhotoVH extends RecyclerView.ViewHolder {
        PressedImageView ivPhoto;

        public PreviewPhotoVH(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
        }
    }

    public interface OnClickListener {
        void onPhotoClick(int position);
    }
}
