package com.huantansheng.easyphotos.models.album;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Type;
import com.huantansheng.easyphotos.models.album.entity.Album;
import com.huantansheng.easyphotos.models.album.entity.AlbumItem;
import com.huantansheng.easyphotos.models.album.entity.Photo;
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
    public Album album;
    private CallBack callBack;

    /**
     * AlbumModel构造方法
     *
     * @param act      调用专辑的活动实体类
     * @param callBack 初始化全部专辑后的回调
     */
    private AlbumModel(final Activity act, AlbumModel.CallBack callBack) {
        album = new Album();
        this.callBack = callBack;
        init(act);
    }

    public static AlbumModel getInstance(final Activity act, AlbumModel.CallBack callBack) {
        if (null == instance) {
            synchronized (AlbumModel.class) {
                if (null == instance) {
                    instance = new AlbumModel(act, callBack);
                }
            }
        }
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    private void init(final Activity act) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initAlbum(act);
                callBack.onAlbumWorkedCallBack();
            }
        }).start();
    }

    private void initAlbum(Activity act) {
        if (Setting.selectedPhotos.size() > Setting.count) {
            throw new RuntimeException("AlbumBuilder: 默认勾选的图片张数不能大于设置的选择数！" + "|默认勾选张数：" + Setting.selectedPhotos.size() + "|设置的选择数：" + Setting.count);
        }

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
                    MediaStore.Images.Media.HEIGHT,
                    MediaStore.Images.Media.SIZE};

        } else {
            projections = new String[]{
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media.MIME_TYPE,
                    MediaStore.Images.Media.SIZE};
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
            int sizeCol = cursor.getColumnIndex(MediaStore.Images.Media.SIZE);
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
                long size = cursor.getInt(sizeCol);
                int width = 0;
                int height = 0;
                if (!Setting.showGif) {
                    if (path.endsWith(Type.GIF) || type.endsWith(Type.GIF)) {
                        continue;
                    }
                }
                if (size < Setting.minSize) {
                    continue;
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    width = cursor.getInt(WidthCol);
                    height = cursor.getInt(HeightCol);
                    if (width < Setting.minWidth || height < Setting.minHeight) {
                        continue;
                    }
                }

                Photo imageItem = new Photo(name, path, dateTime, width, height, size, type);
                if (!Setting.selectedPhotos.isEmpty()) {
                    for (Photo selectedPhoto : Setting.selectedPhotos) {
                        if (path.equals(selectedPhoto.path)) {
                            imageItem.selectedOriginal = Setting.selectedOriginal;
                            Result.addPhoto(imageItem);
                        }
                    }
                }

                // 初始化“全部”专辑
                if (album.isEmpty()) {
                    // 用第一个图片作为专辑的封面
                    album.addAlbumItem(albumItem_all_name, "", path);

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
    public ArrayList<Photo> getCurrAlbumItemPhotos(int currAlbumItemIndex) {
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
