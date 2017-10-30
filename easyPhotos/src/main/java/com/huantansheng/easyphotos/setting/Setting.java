package com.huantansheng.easyphotos.setting;

import android.view.View;

/**
 * EasyPhotos的设置值
 * Created by huan on 2017/10/24.
 */

public class Setting {
    public static int minWidth = 1;
    public static int minHeight = 1;
    public static int count = 1;
    public static View photosAdView = null;
    public static View albumItemsAdView = null;


    public static void clear() {
        minWidth = 1;
        minHeight = 1;
        count = 1;
        photosAdView = null;
        albumItemsAdView = null;
    }
}
