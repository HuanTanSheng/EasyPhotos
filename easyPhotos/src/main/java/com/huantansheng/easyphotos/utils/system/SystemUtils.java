package com.huantansheng.easyphotos.utils.system;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 系统工具类
 * Created by huan on 2017/11/13.
 */

public class SystemUtils {
    private static SystemUtils instance = null;
    private Boolean hasNavigation = null;//是否有导航栏

    /**
     * 私有构造方法
     */
    private SystemUtils() {
    }

    /**
     * 获取单例
     *
     * @return 单例
     */
    public static SystemUtils getInstance() {
        if (null == instance) {
            synchronized (SystemUtils.class) {
                if (null == instance) {
                    instance = new SystemUtils();
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
    public boolean hasNavigationBar(Activity activity) {
        if (null == hasNavigation) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                int windowHeight = activity.getResources().getDisplayMetrics().heightPixels;
                DisplayMetrics dm = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
                int screenHeight = dm.heightPixels;
                hasNavigation = screenHeight - windowHeight > 0;
            } else {
                hasNavigation = true;
            }
        }

        return hasNavigation;
    }

    /**
     * 全屏显示的初始化，在setContentView（）方法前调用
     *
     * @param decorView getWindow().getDecorView()，不同view也可以
     */
    public void systemUiInit(Activity activity, View decorView) {
        if (!hasNavigationBar(activity)) {
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

        if (!hasNavigationBar(activity)) {
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
        if (!hasNavigationBar(activity)) {
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
     *
     * @param cxt 上下文
     * @return 状态栏高度，单位PX
     */
    public int getStatusBarHeight(Context cxt) {
        int statusBarHeight = 0;
        int resourceId = cxt.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = cxt.getResources().getDimensionPixelSize(resourceId);
        } else {
            statusBarHeight = (int) (23 * (cxt.getResources().getDisplayMetrics().density) + 0.5f);
        }
        return statusBarHeight;
    }

    public void setStatusDark(Activity activity, boolean darkmode) {
        if (isFlymeV4OrAbove()) {
            MeiZuStatusUtils.setStatusBarDarkIcon(activity, darkmode);
            return;
        }
        if (isMIUIV6OrAbove()) {
            setStatusTextBlackMi(activity, darkmode);
            return;
        }
        setStatusTextBlackAndroid(activity, darkmode);
    }

    private void setStatusTextBlackMi(Activity activity, boolean darkmode) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setStatusTextBlackAndroid(activity, darkmode);
    }


    private void setStatusTextBlackAndroid(Activity activity, boolean darkmode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            if (darkmode) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                int flag = window.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(flag);
            }
        }
    }


    private boolean isFlymeV4OrAbove() {
        String displayId = Build.DISPLAY;
        if (!TextUtils.isEmpty(displayId) && displayId.contains("Flyme")) {
            String[] displayIdArray = displayId.split(" ");
            for (String temp : displayIdArray) {
                //版本号4以上，形如4.x.
                if (temp.matches("^[4-9]\\.(\\d+\\.)+\\S*")) {
                    return true;
                }
            }
        }
        return false;
    }

    //MIUI V6对应的versionCode是4
    //MIUI V7对应的versionCode是5
    private boolean isMIUIV6OrAbove() {
        String miuiVersionCodeStr = getSystemProperty("ro.miui.ui.version.code");
        if (!TextUtils.isEmpty(miuiVersionCodeStr)) {
            try {
                int miuiVersionCode = Integer.parseInt(miuiVersionCodeStr);
                if (miuiVersionCode >= 4) {
                    return true;
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    //Android Api 23以上
    private boolean isAndroidMOrAbove() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }
        return false;
    }

    private String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }


}
