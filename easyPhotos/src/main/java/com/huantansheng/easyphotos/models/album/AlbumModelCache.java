package com.huantansheng.easyphotos.models.album;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

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
 * <p>
 * Modified by Eagle on 2018/08/31.
 * 修改内容：将AlbumModel的实例化与数据查询分开
 */

public class AlbumModelCache {
    private static final String TAG = "AlbumModel";
    public static AlbumModelCache instance;
    public Album album;

    private AlbumModelCache() {
        album = new Album();
    }

    public static AlbumModelCache getInstance() {
        if (null == instance) {
            synchronized (AlbumModelCache.class) {
                if (null == instance) {
                    instance = new AlbumModelCache();
                }
            }
        }
        return instance;
    }

    /**
     * 专辑查询
     *
     * @param context  调用查询方法的context
     * @param callBack 查询完成后的回调
     */
    public void query(final Context context, final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                album.clear();
                initAlbum(context);
                if (null != callBack) callBack.onAlbumWorkedCallBack();
            }
        }).start();
    }

    private void initAlbum(Context context) {
        if (Setting.selectedPhotos.size() > Setting.count) {
            throw new RuntimeException("AlbumBuilder: 默认勾选的图片张数不能大于设置的选择数！" + "|默认勾选张数：" +
                    Setting.selectedPhotos.size() + "|设置的选择数：" + Setting.count);
        }

        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        ContentResolver contentResolver = context.getContentResolver();
        String[] projections = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            projections = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.WIDTH, MediaStore
                    .Images.Media.HEIGHT, MediaStore.Images.Media.SIZE};

        } else {
            projections = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.SIZE};
        }
        Cursor cursor = contentResolver.query(contentUri, projections, null, null, sortOrder);
        if (cursor == null) {
//            Log.d(TAG, "call: " + "Empty photos");
        } else if (cursor.moveToFirst()) {
            String albumItem_all_name = context.getString(R.string.selector_folder_all_easy_photos);
            int pathCol = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int nameCol = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
            int DateCol = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int mimeType = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE);
            int sizeCol = cursor.getColumnIndex(MediaStore.Images.Media.SIZE);
            int WidthCol = 0;
            int HeightCol = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
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
                if (TextUtils.isEmpty(path) || TextUtils.isEmpty(type)) {
                    continue;
                }
                if (!Setting.showGif) {
                    if (path.endsWith(Type.GIF) || type.endsWith(Type.GIF)) {
                        continue;
                    }
                }
                if (size < Setting.minSize) {
                    continue;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    width = cursor.getInt(WidthCol);
                    height = cursor.getInt(HeightCol);
                    if (width < Setting.minWidth || height < Setting.minHeight) {
                        continue;
                    }
                }

                File file = new File(path);
                if (!file.exists() || !file.isFile()) {
                    continue;
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
