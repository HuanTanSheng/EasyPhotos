package com.huantansheng.easyphotos;

import android.app.Activity;
import android.view.View;

import com.huantansheng.easyphotos.models.ad.AdListener;
import com.huantansheng.easyphotos.models.album.AlbumModel;
import com.huantansheng.easyphotos.result.Result;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.ui.EasyPhotosActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * EasyPhotos的启动管理器
 * Created by huan on 2017/10/18.
 */
public class EasyPhotos {

    //easyPhotos的返回数据Key
    public static final String RESULT = "keyOfEasyPhotosResult";

    /**
     * 启动模式
     * CAMERA-相机
     * ALBUM-相册专辑
     * ALBUM_CAMERA-带有相机按钮的相册专辑
     */
    public enum StartupType {
        CAMERA, ALBUM, ALBUM_CAMERA
    }

    private static EasyPhotos instance;
    private final WeakReference<Activity> mActivity;
    private StartupType startupType;
    private String fileProviderAuthoritiesText;
    private boolean isShowCamera = false;
    private boolean onlyStartCamera = false;
//    private WeakReference<AdListener> adListener;

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
    public static EasyPhotos with(Activity activity, StartupType startupType) {
        clear();
        instance = new EasyPhotos(activity, startupType);
        return instance;
    }

    /**
     * 设置fileProvider字段
     *
     * @param fileProviderAuthoritiesText fileProvider字段
     * @return EasyPhotos
     */
    public EasyPhotos setFileProviderAuthoritiesText(String fileProviderAuthoritiesText) {
        this.fileProviderAuthoritiesText = fileProviderAuthoritiesText;
        return EasyPhotos.this;
    }

    /**
     * 设置选择数
     *
     * @param selectorMaxCount 最大选择数
     * @return EasyPhotos
     */
    public EasyPhotos setCount(int selectorMaxCount) {
        if (Setting.count != 1 && Result.count() > selectorMaxCount) {
            Result.clear();
        }
        Setting.count = selectorMaxCount;
        return EasyPhotos.this;
    }

    /**
     * 设置显示照片的最小宽高
     *
     * @param minWidth  最小宽度
     * @param minHeight 最小高度
     * @return EasyPhotos
     */
    public EasyPhotos setMinSize(int minWidth, int minHeight) {
        Setting.minWidth = minWidth;
        Setting.minHeight = minHeight;
        return EasyPhotos.this;
    }

    /**
     * 设置默认选择图片集合
     *
     * @param selectedPhotos 默认选择图片集合
     * @return EasyPhotos
     */
    public EasyPhotos setSelectedPhotos(ArrayList<String> selectedPhotos) {
        Result.addSelectedPhotos(selectedPhotos);
        return EasyPhotos.this;
    }

    /**
     * 是否使用广告
     *
     * @param albumItemsAdView 是否使用图片列表广告
     * @param albumItemsAdView 是否使用专辑项目列表广告
     * @return
     */
    public EasyPhotos setAdView(View photosAdView, View albumItemsAdView) {
        Setting.photosAdView = photosAdView;
        Setting.albumItemsAdView = albumItemsAdView;
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
            case ALBUM_CAMERA:
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
        EasyPhotosActivity.start(mActivity.get(), onlyStartCamera, isShowCamera, fileProviderAuthoritiesText, requestCode);
    }

    /**
     * 清除所有数据
     */
    private static void clear() {
        Result.clear();
        Setting.clear();
        AlbumModel.clear();
        instance = null;
    }

//    public static void setAdListener(AdListener adListener) {
//        if (null == instance) return;
//        instance.adListener = new WeakReference<AdListener>(adListener);
//    }

    /**
     * 添加图片列表里面的广告
     *
     * @param adView 广告View
     */
//    public static void addPhotosAdView(final View adView) {
//        if (null == instance) {
//            return;
//        }
//        if (null == instance.adListener) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if (null != instance && null != instance.adListener) {
//                        instance.adListener.get().onPhotosAdLoaded(adView);
//                    }
//                }
//            }).start();
//            return;
//        }
//        instance.adListener.get().onPhotosAdLoaded(adView);
//    }

    /**
     * 向专辑项目列表添加广告
     *
     * @param adView 广告View
     */
//    public static void addAlbumItemsAdView(final View adView) {
//        if (null == instance) {
//            return;
//        }
//        if (null == instance.adListener) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if (null != instance && null != instance.adListener) {
//                        instance.adListener.get().onAlbumItemsAdLoaded(adView);
//                    }
//                }
//            }).start();
//            return;
//        }
//        instance.adListener.get().onAlbumItemsAdLoaded(adView);
//    }

}
