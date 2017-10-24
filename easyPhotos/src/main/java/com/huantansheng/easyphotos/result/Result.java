package com.huantansheng.easyphotos.result;

import com.huantansheng.easyphotos.models.Album.entity.PhotoItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 存储的返回图片集
 * Created by huan on 2017/10/24.
 */

public class Result {
    public static ArrayList<String> photos = new ArrayList<>();
    public static LinkedHashMap<String, PhotoItem> map = new LinkedHashMap<>();

    public static void addPhoto(PhotoItem photoItem) {
        photoItem.selected = true;
        photos.add(photoItem.path);
        map.put(photoItem.path, photoItem);
    }

    public static void removePhoto(PhotoItem photoItem) {
        photoItem.selected = false;
        photos.remove(photoItem.path);
        map.remove(photoItem.path);
    }

    public static void removePhoto(int photosIndex) {
        String itemPath = photos.get(photosIndex);
        PhotoItem item = map.get(itemPath);
        removePhoto(item);
    }

    public static void clear() {
        photos.clear();
        map.clear();
    }
}
