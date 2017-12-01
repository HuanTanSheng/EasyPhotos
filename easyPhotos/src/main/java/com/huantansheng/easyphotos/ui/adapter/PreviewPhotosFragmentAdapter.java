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
        RequestOptions options = new RequestOptions().centerCrop().error(R.drawable.ic_photo_error_easy_photos);
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
            holder.frame.setVisibility(View.VISIBLE);
        } else {
            holder.frame.setVisibility(View.GONE);
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
        if (checkedPosition == position) {
            return;
        }
        checkedPosition = position;
        notifyDataSetChanged();
    }

    class PreviewPhotoVH extends RecyclerView.ViewHolder {
        PressedImageView ivPhoto;
        View frame;

        public PreviewPhotoVH(View itemView) {
            super(itemView);
            ivPhoto = (PressedImageView) itemView.findViewById(R.id.iv_photo);
            frame = itemView.findViewById(R.id.v_selector);
        }
    }

    public interface OnClickListener {
        void onPhotoClick(int position);
    }
}
