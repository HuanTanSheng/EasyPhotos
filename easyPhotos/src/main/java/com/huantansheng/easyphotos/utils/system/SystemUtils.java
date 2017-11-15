package com.huantansheng.easyphotos.utils.system;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Px;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

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
     * 全屏显示的初始化，在setContentView（）方法前调用
     *
     * @param decorView getWindow().getDecorView()，不同view也可以
     */
    public void systemUiInit(Activity activity, View decorView) {
        if (!hasNavigationBar()) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    private void hideStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }

    private void showStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }


    /**
     * 隐藏导航栏和状态栏
     *
     * @param activity  上下文
     * @param decorView getWindow().getDecorView()，不同view也可以
     */
    public void systemUiHide(Activity activity, View decorView) {

        if (!hasNavigationBar()) {
            hideStatusBar(activity);
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
        }

    }

    /**
     * 显示导航栏和状态栏
     *
     * @param activity  上下文
     * @param decorView getWindow().getDecorView()，不同view也可以
     */
    public void systemUiShow(Activity activity, View decorView) {
        if (!hasNavigationBar()) {
            showStatusBar(activity);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    /**
     * 获取状态栏高度
     * @param cxt 上下文
     * @return 状态栏高度，单位PX
     */
    public int getStatusBarHeight(Context cxt) {
        int statusBarHeight = -1;
        int resourceId = cxt.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = cxt.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }


}
