package com.huantansheng.easyphotos.models.Album.entity;

import android.util.Log;

/**
 * 图片item实体类
 * Created by huan on 2017/10/20.
 */

public class ImageItem {
    private static final String TAG = "ImageItem";
    public String name;
    public String path;
    public String type;
    public int width;
    public int height;
    public long time;
    public boolean isCamera;

    public ImageItem(boolean isCamera, String name, String path, long time, int width, int height, String type) {
        this.isCamera = isCamera;
        this.name = name;
        this.path = path;
        this.time = time;
        this.width = width;
        this.height = height;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        try {
            ImageItem other = (ImageItem) o;
            return this.path.equalsIgnoreCase(other.path);
        } catch (ClassCastException e) {
            Log.e(TAG, "equals: " + Log.getStackTraceString(e));
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "ImageItem{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", time=" + time + '\'' +
                ", width=" + width + '\'' +
                ", height=" + height +
                '}';
    }

}
