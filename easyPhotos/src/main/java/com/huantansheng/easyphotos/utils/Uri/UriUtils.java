package com.huantansheng.easyphotos.utils.Uri;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Uri的处理工具类
 * Created by huan on 2017/8/3.
 */

public class UriUtils {
    /**
     * 获取Uri的真实路径
     *
     * @param context 上下文
     * @param uri     uri
     * @return uri对应的真实路径
     */
    public static String getUriFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String realPath = null;
        if (scheme == null)
            realPath = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            realPath = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        realPath = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return realPath;
    }

    public static Uri getFileUri(Context applicationContext, String fileProvider, File file) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(applicationContext, fileProvider, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }
}
