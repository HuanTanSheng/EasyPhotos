package com.huantansheng.easyphotos.models.album.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 图片item实体类
 * Created by huan on 2017/10/20.
 */

public class Photo implements Parcelable {
    private static final String TAG = "Photo";
    public Uri uri;//图片Uri
    public String name;//图片名称
    public String path;//图片全路径
    public String type;//图片类型
    public int width;//图片宽度
    public int height;//图片高度
    public int orientation;//图片旋转角度
    public long size;//图片文件大小，单位：Bytes
    public long duration;//视频时长，单位：毫秒
    public long time;//图片拍摄的时间戳,单位：毫秒
    public boolean selected;//是否被选中,内部使用,无需关心
    public boolean selectedOriginal;//用户选择时是否选择了原图选项

    public Photo(String name, Uri uri, String path, long time, int width, int height,int orientation, long size, long duration, String type) {
        this.name = name;
        this.uri = uri;
        this.path = path;
        this.time = time;
        this.width = width;
        this.height = height;
        this.orientation = orientation;
        this.type = type;
        this.size = size;
        this.duration = duration;
        this.selected = false;
        this.selectedOriginal = false;
    }

    @Override
    public boolean equals(Object o) {
        try {
            Photo other = (Photo) o;
            return this.path.equalsIgnoreCase(other.path);
        } catch (ClassCastException e) {
            Log.e(TAG, "equals: " + Log.getStackTraceString(e));
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "Photo{" +
                "name='" + name + '\'' +
                ", uri='" + uri.toString() + '\'' +
                ", path='" + path + '\'' +
                ", time=" + time + '\'' +
                ", minWidth=" + width + '\'' +
                ", minHeight=" + height +
                ", orientation=" + orientation +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.uri, flags);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.type);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.orientation);
        dest.writeLong(this.size);
        dest.writeLong(this.duration);
        dest.writeLong(this.time);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.selectedOriginal ? (byte) 1 : (byte) 0);
    }

    protected Photo(Parcel in) {
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.name = in.readString();
        this.path = in.readString();
        this.type = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.orientation = in.readInt();
        this.size = in.readLong();
        this.duration = in.readLong();
        this.time = in.readLong();
        this.selected = in.readByte() != 0;
        this.selectedOriginal = in.readByte() != 0;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
