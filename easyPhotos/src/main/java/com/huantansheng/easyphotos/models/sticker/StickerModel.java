package com.huantansheng.easyphotos.models.sticker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import androidx.fragment.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.sticker.cache.StickerCache;
import com.huantansheng.easyphotos.models.sticker.entity.TextStickerData;
import com.huantansheng.easyphotos.models.sticker.view.BitmapSticker;
import com.huantansheng.easyphotos.models.sticker.view.TextSticker;
import com.huantansheng.easyphotos.models.sticker.listener.OnStickerClickListener;
import com.huantansheng.easyphotos.models.sticker.view.EditFragment;
import com.huantansheng.easyphotos.utils.bitmap.BitmapUtils;
import com.huantansheng.easyphotos.utils.bitmap.SaveBitmapCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 贴图view的管理器，用于module与外部解耦
 * Created by huan on 2017/7/24.
 */

public class StickerModel {
    public static final ArrayList<TextStickerData> textDataList = new ArrayList<>();

    public List<BitmapSticker> bitmapStickers;
    public List<TextSticker> textStickers;
    public BitmapSticker currBitmapSticker;
    public TextSticker currTextSticker;


    public StickerModel() {
        super();
        this.bitmapStickers = new ArrayList<>();
        this.textStickers = new ArrayList<>();
    }

    public void addBitmapSticker(Context cxt, String imagePath, int imageResourceId, ViewGroup rootgroup) {

        if (bitmapStickers.size() > 0 && !bitmapStickers.get(bitmapStickers.size() - 1).isChecked) {
            bitmapStickers.get(bitmapStickers.size() - 1).delete();
        }
        final BitmapSticker sticker = new BitmapSticker(cxt, imagePath, imageResourceId, rootgroup.getWidth() / 2, rootgroup.getHeight() / 2);
        sticker.setOnStickerClickListener(new OnStickerClickListener() {
            @Override
            public void onDelete() {
                bitmapStickers.remove(sticker);
            }

            @Override
            public void onEditor() {

            }

            @Override
            public void onTop() {
                bitmapStickers.remove(sticker);
                bitmapStickers.add(sticker);
            }

            @Override
            public void onUsing() {
                if (currBitmapSticker != null && currBitmapSticker != sticker) {
                    currBitmapSticker.setUsing(false);
                    currBitmapSticker = sticker;
                }
            }
        });
        if (currBitmapSticker != null) {
            currBitmapSticker.setUsing(false);
        }
        rootgroup.addView(sticker);
        currBitmapSticker = sticker;
        bitmapStickers.add(sticker);
    }


    public void addTextSticker(final Context cxt, final FragmentManager fragmentManager, String text, ViewGroup rootgroup) {

        if (textStickers.size() > 0 && !textStickers.get(textStickers.size() - 1).isChecked) {
            textStickers.get(textStickers.size() - 1).delete();
        }
        final TextSticker sticker = new TextSticker(cxt, text, rootgroup.getWidth() / 2, rootgroup.getHeight() / 2);
        sticker.setOnStickerClickListener(new OnStickerClickListener() {
            @Override
            public void onDelete() {
                textStickers.remove(sticker);
            }

            @Override
            public void onEditor() {
                EditFragment.show(fragmentManager, sticker);
            }

            @Override
            public void onTop() {
                textStickers.remove(sticker);
                textStickers.add(sticker);
            }

            @Override
            public void onUsing() {
                if (currTextSticker != null && currTextSticker != sticker) {
                    currTextSticker.setUsing(false);
                    currTextSticker = sticker;
                }
            }
        });
        if (currBitmapSticker != null) {
            currBitmapSticker.setUsing(false);
        }
        rootgroup.addView(sticker);
        currTextSticker = sticker;
        textStickers.add(sticker);
    }

    public void save(Activity act, ViewGroup stickerGroup, View imageGroup, int imageWidth, int imageHeight, final String dirPath, final String namePrefix, final boolean notifyMedia, final SaveBitmapCallBack callBack) {

        if (null != this.currBitmapSticker && this.currBitmapSticker.isUsing()) {
            this.currBitmapSticker.setUsing(false);
        }
        if (null != this.currTextSticker && this.currTextSticker.isUsing()) {
            this.currTextSticker.setUsing(false);
        }

        for (BitmapSticker bs : bitmapStickers) {
            if (bs.isUsing()) {
                bs.setUsing(false);
            }
        }

        for (TextSticker ts : textStickers) {
            if (ts.isUsing()) {
                ts.setUsing(false);
            }
        }

        Bitmap srcBitmap = Bitmap.createBitmap(stickerGroup.getWidth(), stickerGroup.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(srcBitmap);
        stickerGroup.draw(canvas);

        Bitmap cropBitmap = Bitmap.createBitmap(srcBitmap, imageGroup.getLeft(), imageGroup.getTop(), imageGroup.getWidth(), imageGroup.getHeight());
        BitmapUtils.recycle(srcBitmap);
        Bitmap saveBitmap = null;
        if (imageGroup.getWidth() > imageWidth || imageGroup.getHeight() > imageHeight) {
            saveBitmap = Bitmap.createScaledBitmap(cropBitmap, imageWidth, imageHeight, true);
            BitmapUtils.recycle(cropBitmap);
        } else {
            saveBitmap = cropBitmap;
        }

        EasyPhotos.saveBitmapToDir(act, dirPath, namePrefix, saveBitmap, notifyMedia, callBack);

    }

    public void setCanvasSize(final Bitmap b, final ViewGroup imageGroup) {
        if (imageGroup.getMeasuredWidth() == 0) {
            imageGroup.post(new Runnable() {
                @Override
                public void run() {
                    setSize(b, imageGroup);
                }
            });
        } else {
            setSize(b, imageGroup);
        }
    }

    private void setSize(Bitmap b, ViewGroup v) {
        int bW = b.getWidth();
        int bH = b.getHeight();

        int vW = v.getMeasuredWidth();
        int vH = v.getMeasuredHeight();

        float scalW = (float) vW / (float) bW;
        float scalH = (float) vH / (float) bH;

        ViewGroup.LayoutParams params = v.getLayoutParams();
        //如果图片小于viewGroup的宽高则把viewgroup设置为图片宽高
//        if (bW < vW && bH < vH) {
//            params.width = bW;
//            params.height = bH;
//            v.setLayoutParams(params);
//            return;
//        }
        if (bW >= bH) {
            params.width = vW;
            params.height = (int) (scalW * bH);
        } else {
            params.width = (int) (scalH * bW);
            params.height = vH;
        }
        if (params.width > vW) {
            float tempScaleW = (float) vW / (float) params.width;
            params.width = vW;
            params.height = (int) (params.height * tempScaleW);
        }
        if (params.height > vH) {
            float tempScaleH = (float) vH / (float) params.height;
            params.height = vH;
            params.width = (int) (params.width * tempScaleH);
        }
        v.setLayoutParams(params);
    }

    /**
     * 释放资源
     */
    public void release() {
        StickerCache.get().clear();
    }

}
