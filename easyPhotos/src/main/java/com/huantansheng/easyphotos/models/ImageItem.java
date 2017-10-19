package com.huantansheng.easyphotos.models;

import android.util.Log;

import com.huantansheng.easyphotos.constant.Constant;

public class ImageItem {
    public static final String CAMERA_PATH = "Camera";
    private static final String TAG = "ImageItem";
    public String path;
    public String name;
    public long time;
    public int width;
    public int height;
    public String type;

    public ImageItem(String name, String path, long time, int width, int height, String type) {
        this.name = name;
        this.path = path;
        this.time = time;
        this.width = width;
        this.height = height;
        this.type = type;
    }


    public boolean isCamera() {
        return this.path.equals(Constant.CAMERA_ITEM_PATH);
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