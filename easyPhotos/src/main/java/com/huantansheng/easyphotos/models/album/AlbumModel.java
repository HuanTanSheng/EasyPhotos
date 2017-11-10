package com.huantansheng.easyphotos.models.album;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Path;
import com.huantansheng.easyphotos.models.album.entity.Album;
import com.huantansheng.easyphotos.models.album.entity.AlbumItem;
import com.huantansheng.easyphotos.models.album.entity.PhotoItem;
import com.huantansheng.easyphotos.result.Result;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.utils.String.StringUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * 专辑模型
 * Created by huan on 2017/10/20.
 */

public class AlbumModel {
    private static final String TAG = "AlbumModel";
    public static AlbumModel instance;
    private Album album;
    private CallBack callBack;

    /**
     * AlbumModel构造方法
     *
     * @param act          调用专辑的活动实体类
     * @param isShowCamera 是否显示相机按钮
     * @param callBack     初始化全部专辑后的回调
     */
    private AlbumModel(final Activity act, final boolean isShowCamera, AlbumModel.CallBack callBack) {
        album = new Album();
        this.callBack = callBack;
        init(act, isShowCamera);
    }

    public static AlbumModel getInstance(final Activity act, final boolean isShowCamera, AlbumModel.CallBack callBack) {
        if (null == instance) {
            synchronized (AlbumModel.class) {
                if (null == instance) {
                    instance = new AlbumModel(act, isShowCamera, callBack);
                }
            }
        }
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    private void init(final Activity act, final boolean isShowCamera) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initAlbum(act, isShowCamera);
                callBack.onAlbumWorkedCallBack();
            }
        }).start();
    }

    private void initAlbum(Activity act, boolean isShowCamera) {

        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        ContentResolver contentResolver = act.getContentResolver();
        String[] projections = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            projections = new String[]{
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media.MIME_TYPE,
                    MediaStore.Images.Media.WIDTH,
                    MediaStore.Images.Media.HEIGHT};

        } else {
            projections = new String[]{
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media.MIME_TYPE};
        }
        Cursor cursor = contentResolver.query(contentUri, projections, null, null, sortOrder);
        if (cursor == null) {
            Log.d(TAG, "call: " + "Empty photos");
        } else if (cursor.moveToFirst()) {
            String albumItem_all_name = act.getString(R.string.selector_folder_all_easy_photos);
            int pathCol = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int nameCol = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
            int DateCol = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
            int mimeType = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE);
            int WidthCol = 0;
            int HeightCol = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                WidthCol = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH);
                HeightCol = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT);
            }

            do {
                String path = cursor.getString(pathCol);
                String name = cursor.getString(nameCol);
                long dateTime = cursor.getLong(DateCol);
                String type = cursor.getString(mimeType);
                int width = 0;
                int height = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    width = cursor.getInt(WidthCol);
                    height = cursor.getInt(HeightCol);
                    if (width < Setting.minWidth && height < Setting.minHeight) {
                        continue;
                    }
                }

                PhotoItem imageItem = new PhotoItem(false, name, path, dateTime, width, height, type);
                if (!Result.isEmpty()) {
                    for (String photoPath : Result.photos) {
                        if (path.equals(photoPath)) {
                            imageItem.selected = true;
                            Result.map.put(path, imageItem);
                        }
                    }
                }

                // 初始化“全部”专辑
                if (album.isEmpty()) {
                    // 用第一个图片作为专辑的封面
                    album.addAlbumItem(albumItem_all_name, "", path);
                    // 是否显示相机按钮
                    if (isShowCamera) {
                        PhotoItem cameraItem = new PhotoItem(true, "", Path.CAMERA_ITEM_PATH, 0, 0, 0, "");
                        album.getAlbumItem(albumItem_all_name).addImageItem(cameraItem);
                    }
                }

                // 把图片全部放进“全部”专辑
                album.getAlbumItem(albumItem_all_name).addImageItem(imageItem);

                // 添加当前图片的专辑到专辑模型实体中
                String folderPath = new File(path).getParentFile().getAbsolutePath();
                String albumName = StringUtils.getLastPathSegment(folderPath);
                album.addAlbumItem(albumName, folderPath, path);
                album.getAlbumItem(albumName).addImageItem(imageItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    /**
     * 获取当前专辑项目的图片集
     *
     * @return 当前专辑项目的图片集
     */
    public ArrayList<PhotoItem> getCurrAlbumItemPhotos(int currAlbumItemIndex) {
        return album.getAlbumItem(currAlbumItemIndex).photos;
    }

    /**
     * 获取专辑项目集
     *
     * @return 专辑项目集
     */
    public ArrayList<AlbumItem> getAlbumItems() {
        return album.albumItems;
    }

    public interface CallBack {
        void onAlbumWorkedCallBack();

    }

}
