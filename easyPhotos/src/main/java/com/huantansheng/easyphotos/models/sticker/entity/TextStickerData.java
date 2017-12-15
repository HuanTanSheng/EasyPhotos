package com.huantansheng.easyphotos.models.sticker.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 文字贴纸数据类
 * Created by huan on 2017/12/14.
 */

public class TextStickerData implements Parcelable {

    public String stickerName;//文字贴纸的名字
    public String stickerValue;//文字贴纸的文字内容

    public TextStickerData(String stickerName, String stickerValue) {
        this.stickerName = stickerName;
        this.stickerValue = stickerValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.stickerName);
        dest.writeString(this.stickerValue);
    }

    protected TextStickerData(Parcel in) {
        this.stickerName = in.readString();
        this.stickerValue = in.readString();
    }

    public static final Parcelable.Creator<TextStickerData> CREATOR = new Parcelable.Creator<TextStickerData>() {
        @Override
        public TextStickerData createFromParcel(Parcel source) {
            return new TextStickerData(source);
        }

        @Override
        public TextStickerData[] newArray(int size) {
            return new TextStickerData[size];
        }
    };
}
