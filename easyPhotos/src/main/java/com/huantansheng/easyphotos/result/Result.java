package com.huantansheng.easyphotos.result;

import com.huantansheng.easyphotos.models.album.entity.PhotoItem;

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

    public static boolean isEmpty() {
        return photos.isEmpty();
    }

    public static void addSelectedPhotos(ArrayList<String> selectedPhotos) {
        clear();
        photos.addAll(selectedPhotos);
    }

    public static int count() {
        return photos.size();
    }

    /**
     * 获取选择器应该显示的数字
     * @param photoPath 当前图片的地址
     * @return 选择器应该显示的数字
     */
    public static String getSelectorNumber(String photoPath){
        return String.valueOf(photos.indexOf(photoPath) + 1);
    }

    public static String getPhotoPath(int position) {
        return photos.get(position);
    }
}
