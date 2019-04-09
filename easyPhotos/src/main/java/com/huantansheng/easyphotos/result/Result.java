package com.huantansheng.easyphotos.result;

import android.graphics.BitmapFactory;
import android.os.Build;

import com.huantansheng.easyphotos.constant.Type;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.setting.Setting;

import java.util.ArrayList;

/**
 * 存储的返回图片集
 * Created by huan on 2017/10/24.
 */

public class Result {
    public static ArrayList<Photo> photos = new ArrayList<>();

    /**
     * @return 0：添加成功 -2：超过视频选择数 -1：超过图片选择数
     */
    public static int addPhoto(Photo photo) {
        if (Setting.videoCount != -1 || Setting.pictureCount != -1) {
            int number = getVideoNumber();
            if (photo.type.contains(Type.VIDEO) && number >= Setting.videoCount) {
                return -2;
            }
            number = photos.size() - number;
            if ((!photo.type.contains(Type.VIDEO)) && number >= Setting.pictureCount) {
                return -1;
            }
        }
        photo.selected = true;
        photos.add(photo);
        return 0;
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

    private static int getVideoNumber() {
        int count = 0;
        for (Photo p : photos) {
            if (p.type.contains(Type.VIDEO)) {
                count += 1;
            }
        }
        return count;
    }

    public static void processOriginal() {
        boolean isIceApi = Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
        if (Setting.showOriginalMenu) {
            if (Setting.originalMenuUsable) {
                for (Photo photo : photos) {
                    photo.selectedOriginal = Setting.selectedOriginal;
                    if (isIceApi && photo.width == 0) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(photo.path, options);
                        photo.width = options.outWidth;
                        photo.height = options.outHeight;
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

    public static long getPhotoDuration(int position) {
        return photos.get(position).duration;
    }

}
