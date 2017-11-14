package com.huantansheng.easyphotos.utils.system;

import android.content.Context;
import android.os.Build;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * 系统工具类
 * Created by huan on 2017/11/13.
 */

public class SystemUtils {
    private static SystemUtils instance = null;
    private Context cxt = null;
    private Boolean hasNavigation = null;//是否有导航栏

    /**
     * 私有构造方法
     *
     * @param context 上下任
     */
    private SystemUtils(Context context) {
        this.cxt = context.getApplicationContext();
    }

    /**
     * 获取单例
     *
     * @param context 上下文
     * @return 单例
     */
    public static SystemUtils getInstance(Context context) {
        if (null == instance) {
            synchronized (SystemUtils.class) {
                if (null == instance) {
                    instance = new SystemUtils(context);
                }
            }
        }
        return instance;
    }

    /**
     * 是否有导航栏
     *
     * @return 有或没有
     */
    public boolean hasNavigationBar() {
        if (null == hasNavigation) {
            boolean hasMenuKey = ViewConfiguration.get(this.cxt)
                    .hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap
                    .deviceHasKey(KeyEvent.KEYCODE_BACK);
            hasNavigation = !hasMenuKey && !hasBackKey;
        }
        return hasNavigation;
    }

    /**
     * 全屏显示的初始化，在onCreate（）中调用
     *
     * @param decorView getWindow().getDecorView()，不同view也可以
     */
    public void systemUiInit(View decorView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (hasNavigationBar()) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        }
    }


    /**
     * 隐藏导航栏和状态栏
     *
     * @param decorView getWindow().getDecorView()，不同view也可以
     */
    public void systemUiHide(View decorView) {

        if (!hasNavigationBar()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            return;
        }

        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

    }

    /**
     * 显示导航栏和状态栏
     * @param decorView getWindow().getDecorView()，不同view也可以
     */
    public void systemUiShow(View decorView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (hasNavigationBar()) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            }
        }
    }


}
