package com.huantansheng.easyphotos.utils.media;

import android.content.Context;
import android.media.MediaScannerConnection;

import java.io.File;
import java.util.List;

/**
 * 更新媒体库
 * Created by huan on 2017/8/1.
 */

public class MediaScannerConnectionUtils {

    public static void refresh(Context cxt, String... filePaths) {
        MediaScannerConnection.scanFile(cxt.getApplicationContext(),
                filePaths, null,
                null);
    }


    public static void refresh(Context cxt, File... files) {
        for (File file : files) {
            String filePath = file.getAbsolutePath();
            refresh(cxt, filePath);
        }
    }

    public static void refresh(Context cxt, List<String> filePathList) {
        for (String filePath : filePathList) {
            refresh(cxt, filePath);
        }
    }

}
