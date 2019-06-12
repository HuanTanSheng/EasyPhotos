package com.huantansheng.easyphotos.models.sticker.cache;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.annotation.IdRes;

import com.huantansheng.easyphotos.EasyPhotos;

import java.util.LinkedHashMap;

/**
 * 贴纸的图片缓存器
 * Created by huan on 2017/12/8.
 */

public class StickerCache {
    private static StickerCache instance = null;

    public static StickerCache get() {
        if (null == instance) {
            synchronized (StickerCache.class) {
                if (null == instance) {
                    instance = new StickerCache();
                }
            }
        }
        return instance;
    }

    private LinkedHashMap<String, Bitmap> srcBitmapCache = null;
    private LinkedHashMap<String, Bitmap> mirrorBitmapCache = null;
    private LinkedHashMap<String, Integer> bitmapUsedCount = null;

    private StickerCache() {
        srcBitmapCache = new LinkedHashMap<>();
        mirrorBitmapCache = new LinkedHashMap<>();
        bitmapUsedCount = new LinkedHashMap<>();
    }

    public Bitmap getSrcBitmap(String path) {
        Bitmap bitmap = srcBitmapCache.get(path);
        if (null == bitmap) {
            bitmap = BitmapFactory.decodeFile(path);
            srcBitmapCache.put(path, bitmap);
            bitmapUsedCount.put(path, 0);
            convertMirror(path, bitmap);
        }

        int count = bitmapUsedCount.get(path);
        bitmapUsedCount.put(path, ++count);
        return bitmap;
    }

    public Bitmap getSrcBitmap(Resources resources, @IdRes int resId) {
        String path = String.valueOf(resId);
        Bitmap bitmap = srcBitmapCache.get(path);
        if (null == bitmap) {
            bitmap = BitmapFactory.decodeResource(resources, resId);
            srcBitmapCache.put(path, bitmap);
            bitmapUsedCount.put(path, 0);
            convertMirror(path, bitmap);
        }

        int count = bitmapUsedCount.get(path);
        bitmapUsedCount.put(path, ++count);
        return bitmap;
    }

    public Bitmap getMirrorBitmap(String key) {
        return mirrorBitmapCache.get(key);
    }


    public void clear() {
        for (String key : srcBitmapCache.keySet()) {
            recycle(key);
        }
    }

    public void recycle(String key) {
        if (!srcBitmapCache.containsKey(key)) {
            return;
        }

        int count = bitmapUsedCount.get(key);
        if (count > 1) {
            count--;
            bitmapUsedCount.put(key, count);
            return;
        }

        EasyPhotos.recycle(srcBitmapCache.get(key), mirrorBitmapCache.get(key));
        removeKey(key);
    }

    private void convertMirror(String key, Bitmap a) {
        int w = a.getWidth();
        int h = a.getHeight();

        Matrix m = new Matrix();
        m.postScale(-1, 1);   //镜像水平翻转
        Bitmap mirrorBitmap = Bitmap.createBitmap(a, 0, 0, w, h, m, true);
        mirrorBitmapCache.put(key, mirrorBitmap);
    }


    private void removeKey(String key) {
        srcBitmapCache.remove(key);
        mirrorBitmapCache.remove(key);
        bitmapUsedCount.remove(key);
    }
}
