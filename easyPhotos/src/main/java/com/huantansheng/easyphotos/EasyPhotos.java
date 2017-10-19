package com.huantansheng.easyphotos;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.huantansheng.easyphotos.view.EasyPhotosActivity;

import java.lang.ref.WeakReference;

/**
 * EasyPhotos的启动管理器
 * Created by huan on 2017/10/18.
 */

public class EasyPhotos {
    //easyPhotos的返回数据
    public static final String RESULT = "keyOfEasyPhotosResult";
    /**
     * 启动模式
     * CAMERA-相机
     * ALBUM-相册专辑
     * ALL-带有相机按钮的相册专辑
     */
    public enum StartupType {
        CAMERA, ALBUM, ALL
    }

    private final WeakReference<Activity> mActivity;
    private StartupType startupType;
    private int count = 1;
    private String fileProviderText;
    private boolean isShowCamera = false;
    private boolean onlyStartCamera = false;

    //私有构造函数，不允许外部调用，真正实例化通过静态方法实现
    private EasyPhotos(Activity activity, StartupType startupType) {
        mActivity = new WeakReference<>(activity);
        this.startupType = startupType;
    }

    /**
     * 从activity启动
     *
     * @param activity Activity的实例
     * @return EasyPhotos EasyPhotos的实例
     */
    public static EasyPhotos from(Activity activity, StartupType startupType) {
        return new EasyPhotos(activity, startupType);
    }

    /**
     * 从Fragment启动
     *
     * @param fragment Fragment的实例
     * @return EasyPhotos EasyPhotos的实例
     */
    public static EasyPhotos from(Fragment fragment, StartupType startupType) {
        return new EasyPhotos(fragment.getActivity(), startupType);
    }

    /**
     * 设置选择数
     *
     * @param selectorCount 选择数
     * @return EasyPhotos
     */
    public EasyPhotos count(int selectorCount) {
        this.count = selectorCount;
        return EasyPhotos.this;
    }

    /**
     * 设置fileProvider字段
     *
     * @param fileProviderText fileProvider字段
     * @return EasyPhotos
     */
    public EasyPhotos setFileProviderText(String fileProviderText) {
        this.fileProviderText = fileProviderText;
        return EasyPhotos.this;
    }

    /**
     * 正式启动
     *
     * @param requestCode startActivityForResult的请求码
     */
    public void start(int requestCode) {
        switch (startupType) {
            case CAMERA:
                onlyStartCamera = true;
                break;
            case ALBUM:
                isShowCamera = false;
                break;
            case ALL:
                isShowCamera = true;
                break;
        }
        launchEasyPhotosActivity(requestCode);
    }

    /**
     * 正式启动
     *
     * @param requestCode startActivityForResult的请求码
     */
    private void launchEasyPhotosActivity(int requestCode) {
        EasyPhotosActivity.start(mActivity.get(), onlyStartCamera, isShowCamera, count, fileProviderText, requestCode);
    }


}
