package com.huantansheng.easyphotos.models.album.entity;

import android.net.Uri;

import java.util.ArrayList;

/**
 * 专辑项目实体类
 * Created by huan on 2017/10/20.
 */

public class AlbumItem {
    public String name;
    public String folderPath;
    public String coverImagePath;
    public Uri coverImageUri;
    public ArrayList<Photo> photos;

    AlbumItem(String name, String folderPath, String coverImagePath, Uri coverImageUri) {
        this.name = name;
        this.folderPath = folderPath;
        this.coverImagePath = coverImagePath;
        this.coverImageUri = coverImageUri;
        this.photos = new ArrayList<>();
    }

    public void addImageItem(Photo imageItem) {
        this.photos.add(imageItem);
    }

    public void addImageItem(int index, Photo imageItem) {
        this.photos.add(index, imageItem);
    }
}
