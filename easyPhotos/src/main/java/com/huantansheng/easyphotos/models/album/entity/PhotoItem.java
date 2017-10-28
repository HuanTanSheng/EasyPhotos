package com.huantansheng.easyphotos.models.album.entity;

import android.util.Log;

/**
 * 图片item实体类
 * Created by huan on 2017/10/20.
 */

public class PhotoItem {
    private static final String TAG = "PhotoItem";
    public String name, path, type;
    public int width, height;
    public long time;
    public boolean isCamera, selected;

    public PhotoItem(boolean isCamera, String name, String path, long time, int width, int height, String type) {
        this.isCamera = isCamera;
        this.name = name;
        this.path = path;
        this.time = time;
        this.width = width;
        this.height = height;
        this.type = type;
        this.selected = false;
    }

    @Override
    public boolean equals(Object o) {
        try {
            PhotoItem other = (PhotoItem) o;
            return this.path.equalsIgnoreCase(other.path);
        } catch (ClassCastException e) {
            Log.e(TAG, "equals: " + Log.getStackTraceString(e));
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "PhotoItem{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", time=" + time + '\'' +
                ", minWidth=" + width + '\'' +
                ", minHeight=" + height +
                '}';
    }

}
