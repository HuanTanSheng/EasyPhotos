package com.huantansheng.easyphotos.result;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.setting.Setting;

import java.util.ArrayList;

/**
 * 存储的返回图片集
 * Created by huan on 2017/10/24.
 */

public class Result {
    public static ArrayList<Photo> photos = new ArrayList<>();

    public static void addPhoto(Photo photo) {
        photo.selected = true;
        photos.add(photo);
    }

    public static void removePhoto(Photo photo) {
        photo.selected = false;
        photos.remove(photo);
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

    public static void processOriginal() {
        boolean isIceApi = Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
        if (Setting.showOriginalMenu) {
            if (Setting.originalMenuUsable) {
                for (Photo photo : photos) {
                    photo.selectedOriginal = Setting.selectedOriginal;
                    if (isIceApi && photo.width == 0) {
                        Bitmap b = BitmapFactory.decodeFile(photo.path);
                        photo.width = b.getWidth();
                        photo.height = b.getHeight();
                    }
                }
            }
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
    public static String getSelectorNumber(Photo photo) {
        return String.valueOf(photos.indexOf(photo) + 1);
    }

    public static String getPhotoPath(int position) {
        return photos.get(position).path;
    }

    public static String getPhotoType(int position) {
        return photos.get(position).type;
    }
}
