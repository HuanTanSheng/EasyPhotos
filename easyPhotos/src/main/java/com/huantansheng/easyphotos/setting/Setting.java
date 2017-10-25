package com.huantansheng.easyphotos.setting;

import android.content.pm.ActivityInfo;

/**
 * EasyPhotos的设置值
 * Created by huan on 2017/10/24.
 */

public class Setting {
    public static int minWidth = 1;
    public static int minHeight = 1;
    public static int count = 1;
    public static int orientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    public static boolean shouldDestroy = true;

    public static boolean needResetOrientation() {
        return orientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    public static void clear() {
        minWidth = 1;
        minHeight = 1;
        count = 1;
        orientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        shouldDestroy = true;
    }
}
