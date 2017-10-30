package com.huantansheng.easyphotos.models.album.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 图片item实体类
 * Created by huan on 2017/10/20.
 */

public class PhotoItem implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.type);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeLong(this.time);
        dest.writeByte(this.isCamera ? (byte) 1 : (byte) 0);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    protected PhotoItem(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.type = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.time = in.readLong();
        this.isCamera = in.readByte() != 0;
        this.selected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PhotoItem> CREATOR = new Parcelable.Creator<PhotoItem>() {
        @Override
        public PhotoItem createFromParcel(Parcel source) {
            return new PhotoItem(source);
        }

        @Override
        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };
}
