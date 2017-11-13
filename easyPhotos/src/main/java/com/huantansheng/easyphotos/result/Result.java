package com.huantansheng.easyphotos.result;

import com.huantansheng.easyphotos.models.album.entity.PhotoItem;

import java.util.ArrayList;

/**
 * 存储的返回图片集
 * Created by huan on 2017/10/24.
 */

public class Result {
    public static ArrayList<PhotoItem> photos = new ArrayList<>();

    public static void addPhoto(PhotoItem photoItem) {
        photoItem.selected = true;
        photos.add(photoItem);
    }

    public static void removePhoto(PhotoItem photoItem) {
        photoItem.selected = false;
        photos.remove(photoItem);
    }

    public static void removePhoto(int photoIndex) {
        removePhoto(photos.get(photoIndex));
    }

    public static void removeAll() {
        int size = photos.size();
        for (int i = 0; i < size; i++) {
            removePhoto(0);
        }
    }

    public static void clear() {
        photos.clear();
    }

    public static boolean isEmpty() {
        return photos.isEmpty();
    }

    public static int count() {
        return photos.size();
    }

    /**
     * 获取选择器应该显示的数字
     *
     * @param photo 当前图片
     * @return 选择器应该显示的数字
     */
    public static String getSelectorNumber(PhotoItem photo) {
        return String.valueOf(photos.indexOf(photo) + 1);
    }

    public static String getPhotoPath(int position) {
        return photos.get(position).path;
    }
}
