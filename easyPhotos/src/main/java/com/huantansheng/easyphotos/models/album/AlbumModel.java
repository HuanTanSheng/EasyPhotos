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
public class AlbumModel {
    private static final String TAG = "AlbumModel";
    public static AlbumModel instance;
    public Album album;

    private AlbumModel() {
        album = new Album();
    }

    public static AlbumModel getInstance() {
        if (null == instance) {
            synchronized (AlbumModel.class) {
                if (null == instance) {
                    instance = new AlbumModel();
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
    public boolean canRun = true;

    public void query(final Context context, final CallBack callBack) {
        canRun = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                album.clear();
                initAlbum(context);
                if (null != callBack) callBack.onAlbumWorkedCallBack();
            }
        }).start();
    }

    public void stopQuery() {
        canRun = false;
    }

    private void initAlbum(Context context) {
        if (Setting.selectedPhotos.size() > Setting.count) {
            throw new RuntimeException("AlbumBuilder: 默认勾选的图片张数不能大于设置的选择数！" + "|默认勾选张数：" + Setting.selectedPhotos.size() + "|设置的选择数：" + Setting.count);
        }

        final Uri contentUri = MediaStore.Files.getContentUri("external");
        final String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";
        final String selection =
                "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)" + " AND " + MediaStore.MediaColumns.SIZE + ">0";
        String[] selectionAllArgs =
                {String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};

        if (Setting.isOnlyVideo()){
            selectionAllArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
        }

        ContentResolver contentResolver = context.getContentResolver();
        String[] projections;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            projections = new String[]{MediaStore.Files.FileColumns._ID,
                    MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME,
                    MediaStore.MediaColumns.DATE_MODIFIED, MediaStore.MediaColumns.MIME_TYPE,
                    MediaStore.MediaColumns.WIDTH, MediaStore.MediaColumns.HEIGHT,
                    MediaStore.MediaColumns.SIZE, MediaStore.Video.Media.DURATION};

        } else {
            projections = new String[]{MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA,
                    MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED,
                    MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.SIZE,
                    MediaStore.Video.Media.DURATION};
        }
        Cursor cursor = contentResolver.query(contentUri, projections, selection,
                selectionAllArgs, sortOrder);
        if (cursor == null) {
//            Log.d(TAG, "call: " + "Empty photos");
        } else if (cursor.moveToFirst()) {
            String albumItem_all_name = getAllAlbumName(context);
            String albumItem_video_name =
                    context.getString(R.string.selector_folder_video_easy_photos);
            int idCol = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
            int pathCol = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            int nameCol = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            int DateCol = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED);
            int mimeType = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
            int sizeCol = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE);
            int durationCol = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
            int WidthCol = 0;
            int HeightCol = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                WidthCol = cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH);
                HeightCol = cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT);
            }

            do {
                String id = cursor.getString(idCol);
                String path = cursor.getString(pathCol);
                String name = cursor.getString(nameCol);
                long dateTime = cursor.getLong(DateCol);
                String type = cursor.getString(mimeType);
                long size = cursor.getLong(sizeCol);
                long duration = cursor.getLong(durationCol);
                int width = 0;
                int height = 0;

                if (TextUtils.isEmpty(path) || TextUtils.isEmpty(type)) {
                    continue;
                }

                boolean isVideo = type.contains(Type.VIDEO);// 是否是视频
                Uri uri = Uri.withAppendedPath(isVideo?MediaStore.Video.Media.EXTERNAL_CONTENT_URI:MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                if (Setting.isOnlyVideo() && !isVideo) {
                    continue;
                }
                if (!Setting.filterTypes.isEmpty() && !Setting.isFilter(type)) {
                    continue;
                }

                if (!Setting.showGif) {
                    if (path.endsWith(Type.GIF) || type.endsWith(Type.GIF)) {
                        continue;
                    }
                }
                if (!Setting.showVideo) {
                    if (isVideo) {
                        continue;
                    }
                }

                if (size < Setting.minSize) {
                    continue;
                }
                if (isVideo && (duration <= Setting.videoMinSecond || duration >= Setting.videoMaxSecond)) {
                    continue;
                }
                if (!isVideo && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    width = cursor.getInt(WidthCol);
                    height = cursor.getInt(HeightCol);
                    if (width>0 && height>0){
                        if (width < Setting.minWidth || height < Setting.minHeight) {
                            continue;
                        }
                    }

                }

                File file = new File(path);
                if (!file.exists() || !file.isFile()) {
                    continue;
                }

                Photo imageItem = new Photo(name,uri, path, dateTime, width, height, size, duration,
                        type);
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
                    album.addAlbumItem(albumItem_all_name, "", path,uri);
                }
                // 把图片全部放进“全部”专辑
                album.getAlbumItem(albumItem_all_name).addImageItem(imageItem);

                if (Setting.showVideo && isVideo && !albumItem_video_name.equals(albumItem_all_name)) {
                    album.addAlbumItem(albumItem_video_name, "", path,uri);
                    album.getAlbumItem(albumItem_video_name).addImageItem(imageItem);
                }

                // 添加当前图片的专辑到专辑模型实体中
                File parentFile = new File(path).getParentFile();
                if (null == parentFile) {
                    continue;
                }
                String folderPath = parentFile.getAbsolutePath();
                String albumName = StringUtils.getLastPathSegment(folderPath);
                album.addAlbumItem(albumName, folderPath, path,uri);
                album.getAlbumItem(albumName).addImageItem(imageItem);
            } while (cursor.moveToNext() && canRun);
            cursor.close();
        }
    }

    /**
     * 获取全部专辑名
     *
     * @return 专辑名
     */
    public String getAllAlbumName(Context context) {
        String albumItem_all_name =
                context.getString(R.string.selector_folder_all_video_photo_easy_photos);
        if (Setting.isOnlyVideo()) {
            albumItem_all_name = context.getString(R.string.selector_folder_video_easy_photos);
        } else if (!Setting.showVideo) {
            //不显示视频
            albumItem_all_name = context.getString(R.string.selector_folder_all_easy_photos);
        }
        return albumItem_all_name;
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
