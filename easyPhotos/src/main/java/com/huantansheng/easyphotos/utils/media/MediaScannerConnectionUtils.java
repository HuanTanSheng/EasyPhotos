package com.huantansheng.easyphotos.utils.media;

import android.content.Context;
import android.media.MediaScannerConnection;

import java.io.File;

/**
 * 更新媒体库
 * Created by huan on 2017/8/1.
 */

public class MediaScannerConnectionUtils {

    private static void refresh(Context cxt, String... filePath) {
        MediaScannerConnection.scanFile(cxt,
                filePath, null,
                null);
    }


    public static void refresh(Context cxt, File file) {
        String filePath = file.getAbsolutePath();
        refresh(cxt, filePath);
    }
}
