package com.huantansheng.easyphotos.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.models.sticker.StickerModel;
import com.huantansheng.easyphotos.models.sticker.entity.TextStickerData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huan
 */
public class TextStickerAdapter extends RecyclerView.Adapter<TextStickerAdapter.TextViewHolder> {

    private List<TextStickerData> datas;
    private OnItemClickListener onItemClickListener;

    public TextStickerAdapter(Context cxt, OnItemClickListener listener) {
        super();
        this.onItemClickListener = listener;
        this.datas = new ArrayList<>();
        TextStickerData data = new TextStickerData(cxt.getString(R.string.text_sticker_hint_name_easy_photos), cxt.getString(R.string.text_sticker_hint_easy_photos));
        this.datas.add(0, data);
        TextStickerData d = new TextStickerData(cxt.getString(R.string.text_sticker_date_easy_photos), "-1");
        datas.add(d);
        datas.addAll(StickerModel.textDataList);
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_sticker_easy_photos, parent, false);
        return new TextViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TextViewHolder holder, int position) {
        final TextStickerData data = datas.get(position);

        holder.tvSticker.setText(data.stickerName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(data.stickerValue);
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }


    public static class TextViewHolder extends RecyclerView.ViewHolder {

        TextView tvSticker;

        public TextViewHolder(View itemView) {
            super(itemView);
            tvSticker = (TextView) itemView.findViewById(R.id.puzzle);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String stickerValue);
    }
}
