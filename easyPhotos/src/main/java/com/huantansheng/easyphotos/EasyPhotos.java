package com.huantansheng.easyphotos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;

import com.huantansheng.easyphotos.models.ad.AdListener;
import com.huantansheng.easyphotos.models.album.AlbumModel;
import com.huantansheng.easyphotos.result.Result;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.ui.EasyPhotosActivity;
import com.huantansheng.easyphotos.utils.bitmap.BitmapUtils;
import com.huantansheng.easyphotos.utils.media.MediaScannerConnectionUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * EasyPhotos的启动管理器
 * Created by huan on 2017/10/18.
 */
public class EasyPhotos {

    //easyPhotos的返回数据Key
    public static final String RESULT = "keyOfEasyPhotosResult";
    private static EasyPhotos instance;
    private final WeakReference<Activity> mActivity;
    private StartupType startupType;
    private String fileProviderAuthoritiesText;
    private boolean isShowCamera = false;
    private boolean onlyStartCamera = false;
    private WeakReference<AdListener> adListener;
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
     * 清除所有数据
     */
    private static void clear() {
        Result.clear();
        Setting.clear();
        AlbumModel.clear();
        instance = null;
    }

    public static void setAdListener(AdListener adListener) {
        if (null == instance) return;
        instance.adListener = new WeakReference<AdListener>(adListener);
    }

    /**
     * 刷新图片列表广告数据
     */
    public static void notifyPhotosAdLoaded() {
        if (null == instance) {
            return;
        }
        if (null == instance.adListener) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (null != instance && null != instance.adListener) {
                        Setting.photoAdIsOk = true;
                        instance.adListener.get().onPhotosAdLoaded();
                    }
                }
            }).start();
            return;
        }
        Setting.photoAdIsOk = true;
        instance.adListener.get().onPhotosAdLoaded();
    }

    /**
     * 刷新专辑项目列表广告
     */
    public static void notifyAlbumItemsAdLoaded() {
        if (null == instance) {
            return;
        }
        if (null == instance.adListener) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (null != instance && null != instance.adListener) {
                        Setting.albumItemsAdIsOk = true;
                        instance.adListener.get().onAlbumItemsAdLoaded();
                    }
                }
            }).start();
            return;
        }
        Setting.albumItemsAdIsOk = true;
        instance.adListener.get().onAlbumItemsAdLoaded();
    }

    /**
     * 回收bitmap
     *
     * @param bitmap 要回收的bitmap
     */
    public static void recycle(Bitmap bitmap) {
        BitmapUtils.recycle(bitmap);
    }

    /**
     * 回收bitmap数组中的所有图片
     *
     * @param bitmaps 要回收的bitmap数组
     */
    public static void recycle(Bitmap... bitmaps) {
        BitmapUtils.recycle(bitmaps);
    }

    /**
     * 回收bitmap集合中的所有图片
     *
     * @param bitmaps 要回收的bitmap集合
     */
    public static void recycle(List<Bitmap> bitmaps) {
        BitmapUtils.recycle(bitmaps);
    }

    /**
     * 给图片添加水印，水印会根据图片宽高自动缩放处理
     *
     * @param watermark     水印
     * @param image         添加水印的图片
     * @param srcImageWidth 水印对应的原图片宽度,即ui制作水印时参考的要添加水印的图片的宽度
     * @param offsetX       添加水印的X轴偏移量
     * @param offsetY       添加水印的Y轴偏移量
     * @param addInLeft     true 在左下角添加水印，false 在右下角添加水印
     * @return 是否成功
     */
    public static boolean addWatermark(Bitmap watermark, Bitmap image, int srcImageWidth, int offsetX, int offsetY, boolean addInLeft) {
        return BitmapUtils.addWatermark(watermark, image, srcImageWidth, offsetX, offsetY, addInLeft);
    }

    /**
     * 给图片添加带文字和图片的水印，水印会根据图片宽高自动缩放处理
     *
     * @param watermark     水印图片
     * @param image         要加水印的图片
     * @param srcImageWidth 水印对应的原图片宽度,即ui制作水印时参考的要添加水印的图片的宽度
     * @param text          要添加的文字
     * @param offsetX       添加水印的X轴偏移量
     * @param offsetY       添加水印的Y轴偏移量
     * @param addInLeft     true 在左下角添加水印，false 在右下角添加水印
     * @return 是否成功
     */
    public static boolean addWatermarkWithText(Bitmap watermark, Bitmap image, int srcImageWidth, @NonNull String text, int offsetX, int offsetY, boolean addInLeft) {
        return BitmapUtils.addWatermarkWithText(watermark, image, srcImageWidth, text, offsetX, offsetY, addInLeft);
    }

    /**
     * 更新媒体文件到媒体库
     *
     * @param cxt       上下文
     * @param filePaths 更新的文件地址
     */
    public static void notifyMedia(Context cxt, String... filePaths) {
        MediaScannerConnectionUtils.refresh(cxt, filePaths);
    }

    /**
     * 更新媒体文件到媒体库
     *
     * @param cxt   上下文
     * @param files 更新的文件
     */
    public static void notifyMedia(Context cxt, File... files) {
        MediaScannerConnectionUtils.refresh(cxt, files);
    }

    /**
     * 更新媒体文件到媒体库
     *
     * @param cxt      上下文
     * @param fileList 更新的文件地址集合
     */
    public static void notifyMedia(Context cxt, List<String> fileList) {
        MediaScannerConnectionUtils.refresh(cxt, fileList);
    }

    //************bitmap功能***********************************

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
     * 设置广告(不设置该选项则表示不使用广告)
     *
     * @param photosAdView     使用图片列表的广告View
     * @param photosAdIsLoaded 图片列表广告是否加载完毕
     * @param albumItemsAdView 使用专辑项目列表的广告View
     * @param albumItemsAdView 专辑项目列表广告是否加载完毕
     * @return EasyPhotos
     */
    public EasyPhotos setAdView(View photosAdView, boolean photosAdIsLoaded, View albumItemsAdView, boolean albumItemsAdIsLoaded) {
        Setting.photosAdView = new WeakReference<View>(photosAdView);
        Setting.albumItemsAdView = new WeakReference<View>(albumItemsAdView);
        Setting.photoAdIsOk = photosAdIsLoaded;
        Setting.albumItemsAdIsOk = albumItemsAdIsLoaded;
        return EasyPhotos.this;
    }

    //**************更新媒体库***********************

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
     * 启动模式
     * CAMERA-相机
     * ALBUM-相册专辑
     * ALBUM_CAMERA-带有相机按钮的相册专辑
     */
    public enum StartupType {
        CAMERA, ALBUM, ALBUM_CAMERA
    }
}
