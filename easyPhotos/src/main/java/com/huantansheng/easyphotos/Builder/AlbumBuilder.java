package com.huantansheng.easyphotos.Builder;

import android.app.Activity;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.constant.Type;
import com.huantansheng.easyphotos.engine.ImageEngine;
import com.huantansheng.easyphotos.models.ad.AdListener;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.result.Result;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.ui.EasyPhotosActivity;
import com.huantansheng.easyphotos.utils.result.EasyResult;
import com.huantansheng.easyphotos.utils.uri.UriUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * EasyPhotos的启动管理器
 * Created by huan on 2017/10/18.
 */
public class AlbumBuilder {

    /**
     * 启动模式
     * CAMERA-相机
     * ALBUM-相册专辑
     * ALBUM_CAMERA-带有相机按钮的相册专辑
     */
    private enum StartupType {
        CAMERA,
        ALBUM,
        ALBUM_CAMERA
    }

    private static final String TAG = "com.huantansheng.easyphotos";
    private static AlbumBuilder instance;
    private WeakReference<Activity> mActivity;
    private WeakReference<Fragment> mFragmentV;
    private WeakReference<android.app.Fragment> mFragment;
    private StartupType startupType;
    private WeakReference<AdListener> adListener;

    //私有构造函数，不允许外部调用，真正实例化通过静态方法实现
    private AlbumBuilder(Activity activity, StartupType startupType) {
        mActivity = new WeakReference<Activity>(activity);
        this.startupType = startupType;
    }

    private AlbumBuilder(android.app.Fragment fragment, StartupType startupType) {
        mFragment = new WeakReference<android.app.Fragment>(fragment);
        this.startupType = startupType;
    }

    private AlbumBuilder(FragmentActivity activity, StartupType startupType) {
        mActivity = new WeakReference<Activity>(activity);
        this.startupType = startupType;
    }

    private AlbumBuilder(Fragment fragment, StartupType startupType) {
        mFragmentV = new WeakReference<Fragment>(fragment);
        this.startupType = startupType;
    }

    /**
     * 内部处理相机和相册的实例
     *
     * @param activity Activity的实例
     * @return AlbumBuilder EasyPhotos的实例
     */

    private static AlbumBuilder with(Activity activity, StartupType startupType) {
        clear();
        instance = new AlbumBuilder(activity, startupType);
        return instance;
    }


    private static AlbumBuilder with(android.app.Fragment fragment, StartupType startupType) {
        clear();
        instance = new AlbumBuilder(fragment, startupType);
        return instance;
    }

    private static AlbumBuilder with(FragmentActivity activity, StartupType startupType) {
        clear();
        instance = new AlbumBuilder(activity, startupType);
        return instance;
    }

    private static AlbumBuilder with(Fragment fragmentV, StartupType startupType) {
        clear();
        instance = new AlbumBuilder(fragmentV, startupType);
        return instance;
    }


    /**
     * 创建相机
     *
     * @param activity 上下文
     * @return AlbumBuilder
     */

    public static AlbumBuilder createCamera(Activity activity) {
        return AlbumBuilder.with(activity, StartupType.CAMERA);
    }


    public static AlbumBuilder createCamera(android.app.Fragment fragment) {
        return AlbumBuilder.with(fragment, StartupType.CAMERA);
    }

    public static AlbumBuilder createCamera(FragmentActivity activity) {
        return AlbumBuilder.with(activity, StartupType.CAMERA);
    }

    public static AlbumBuilder createCamera(Fragment fragmentV) {
        return AlbumBuilder.with(fragmentV, StartupType.CAMERA);
    }

    /**
     * 创建相册
     *
     * @param activity     上下文
     * @param isShowCamera 是否显示相机按钮
     * @param imageEngine  图片加载引擎的具体实现
     * @return
     */
    public static AlbumBuilder createAlbum(Activity activity, boolean isShowCamera,
                                           @NonNull ImageEngine imageEngine) {
        if (Setting.imageEngine != imageEngine) {
            Setting.imageEngine = imageEngine;
        }
        if (isShowCamera) {
            return AlbumBuilder.with(activity, StartupType.ALBUM_CAMERA);
        } else {
            return AlbumBuilder.with(activity, StartupType.ALBUM);
        }
    }

    public static AlbumBuilder createAlbum(android.app.Fragment fragment, boolean isShowCamera,
                                           @NonNull ImageEngine imageEngine) {
        if (Setting.imageEngine != imageEngine) {
            Setting.imageEngine = imageEngine;
        }
        if (isShowCamera) {
            return AlbumBuilder.with(fragment, StartupType.ALBUM_CAMERA);
        } else {
            return AlbumBuilder.with(fragment, StartupType.ALBUM);
        }
    }

    public static AlbumBuilder createAlbum(FragmentActivity activity, boolean isShowCamera,
                                           @NonNull ImageEngine imageEngine) {
        if (Setting.imageEngine != imageEngine) {
            Setting.imageEngine = imageEngine;
        }
        if (isShowCamera) {
            return AlbumBuilder.with(activity, StartupType.ALBUM_CAMERA);
        } else {
            return AlbumBuilder.with(activity, StartupType.ALBUM);
        }
    }

    public static AlbumBuilder createAlbum(Fragment fragmentV, boolean isShowCamera,
                                           @NonNull ImageEngine imageEngine) {
        if (Setting.imageEngine != imageEngine) {
            Setting.imageEngine = imageEngine;
        }
        if (isShowCamera) {
            return AlbumBuilder.with(fragmentV, StartupType.ALBUM_CAMERA);
        } else {
            return AlbumBuilder.with(fragmentV, StartupType.ALBUM);
        }
    }

    /**
     * 设置fileProvider字段
     *
     * @param fileProviderAuthority fileProvider字段
     * @return AlbumBuilder
     */
    public AlbumBuilder setFileProviderAuthority(String fileProviderAuthority) {
        Setting.fileProviderAuthority = fileProviderAuthority;
        return AlbumBuilder.this;
    }

    /**
     * 设置选择数
     *
     * @param selectorMaxCount 最大选择数
     * @return AlbumBuilder
     */
    public AlbumBuilder setCount(int selectorMaxCount) {
        if (Setting.complexSelector) return AlbumBuilder.this;
        Setting.count = selectorMaxCount;
        return AlbumBuilder.this;
    }

    /**
     * 设置是否使用宽高数据
     *
     * @param useWidth 是否使用宽高数据，需要使用写true，不用写false。
     *                 true：会保证宽高数据的正确性，返回速度慢，耗时。
     *                 false:宽高数据为0。
     * @return AlbumBuilder
     */
    public AlbumBuilder setUseWidth(boolean useWidth) {
        Setting.useWidth = useWidth;
        return AlbumBuilder.this;
    }

    /**
     * 支持复杂选择情况
     *
     * @param singleType   是否只能选择一种文件类型，如用户选择视频后不可以选择图片，若false则可以同时选择
     * @param videoCount   可选择视频类型文件的最大数
     * @param pictureCount 可选择图片类型文件的最大数
     * @return
     */
    public AlbumBuilder complexSelector(boolean singleType, int videoCount, int pictureCount) {
        Setting.complexSelector = true;
        Setting.complexSingleType = singleType;
        Setting.complexVideoCount = videoCount;
        Setting.complexPictureCount = pictureCount;
        Setting.count = videoCount + pictureCount;
        Setting.showVideo = true;
        return AlbumBuilder.this;
    }

    /**
     * 设置相机按钮位置
     *
     * @param cLocation 使用Material Design风格相机按钮 默认 BOTTOM_RIGHT
     * @return AlbumBuilder
     */
    public AlbumBuilder setCameraLocation(@Setting.Location int cLocation) {
        Setting.cameraLocation = cLocation;
        return AlbumBuilder.this;
    }

    /**
     * 设置显示照片的最小文件大小
     *
     * @param minFileSize 最小文件大小，单位Bytes
     * @return AlbumBuilder
     */
    public AlbumBuilder setMinFileSize(long minFileSize) {
        Setting.minSize = minFileSize;
        return AlbumBuilder.this;
    }

    /**
     * 设置显示照片的最小宽度
     *
     * @param minWidth 照片的最小宽度，单位Px
     * @return AlbumBuilder
     */
    public AlbumBuilder setMinWidth(int minWidth) {
        Setting.minWidth = minWidth;
        return AlbumBuilder.this;
    }

    /**
     * 设置显示照片的最小高度
     *
     * @param minHeight 显示照片的最小高度，单位Px
     * @return AlbumBuilder
     */
    public AlbumBuilder setMinHeight(int minHeight) {
        Setting.minHeight = minHeight;
        return AlbumBuilder.this;
    }

    /**
     * 设置默认选择图片集合
     *
     * @param selectedPhotos 默认选择图片集合
     * @return AlbumBuilder
     */
    public AlbumBuilder setSelectedPhotos(ArrayList<Photo> selectedPhotos) {
        Setting.selectedPhotos.clear();
        if (selectedPhotos.isEmpty()) {
            return AlbumBuilder.this;
        }
        Setting.selectedPhotos.addAll(selectedPhotos);
        Setting.selectedOriginal = selectedPhotos.get(0).selectedOriginal;
        return AlbumBuilder.this;
    }

    /**
     * 设置默认选择图片集合
     *
     * @param selectedPhotos 默认选择图片集合
     * @param isSequentialSelectedPhotos 当传入已选中图片时，是否按照之前选中的顺序排序
     * @return AlbumBuilder
     */
    public AlbumBuilder setSelectedPhotos(ArrayList<Photo> selectedPhotos,boolean isSequentialSelectedPhotos) {
        Setting.selectedPhotos.clear();
        Setting.isSequentialSelectedPhotos = isSequentialSelectedPhotos;
        if (selectedPhotos.isEmpty()) {
            return AlbumBuilder.this;
        }
        Setting.selectedPhotos.addAll(selectedPhotos);
        Setting.selectedOriginal = selectedPhotos.get(0).selectedOriginal;
        return AlbumBuilder.this;
    }

    /**
     * 设置默认选择图片地址集合
     *
     * @param selectedPhotoPaths 默认选择图片地址集合
     * @return AlbumBuilder
     * @Deprecated android 10 不推荐使用直接使用Path方式，推荐使用Photo类
     */
    @Deprecated
    public AlbumBuilder setSelectedPhotoPaths(ArrayList<String> selectedPhotoPaths) {
        Setting.selectedPhotos.clear();
        ArrayList<Photo> selectedPhotos = new ArrayList<>();
        for (String path : selectedPhotoPaths) {
            File file = new File(path);
            Uri uri = null;
            if (null != mActivity && null != mActivity.get()) {
                uri = UriUtils.getUri(mActivity.get(), file);
            }
            if (null != mFragment && null != mFragment.get()) {
                uri = UriUtils.getUri(mFragment.get().getActivity(), file);
            }
            if (null != mFragmentV && null != mFragmentV.get()) {
                uri = UriUtils.getUri(mFragmentV.get().getActivity(), file);
            }
            if (uri == null) {
                uri = Uri.fromFile(file);
            }
            Photo photo = new Photo(null, uri, path, 0, 0, 0, 0, 0, 0, null);
            selectedPhotos.add(photo);
        }
        Setting.selectedPhotos.addAll(selectedPhotos);
        return AlbumBuilder.this;
    }

    /**
     * 设置默认选择图片地址集合
     *
     * @param selectedPhotoPaths 默认选择图片地址集合
     * @param isSequentialSelectedPhotos 当传入已选中图片时，是否按照之前选中的顺序排序
     * @return AlbumBuilder
     * @Deprecated android 10 不推荐使用直接使用Path方式，推荐使用Photo类
     */
    @Deprecated
    public AlbumBuilder setSelectedPhotoPaths(ArrayList<String> selectedPhotoPaths,boolean isSequentialSelectedPhotos) {
        Setting.selectedPhotos.clear();
        Setting.isSequentialSelectedPhotos = isSequentialSelectedPhotos;
        ArrayList<Photo> selectedPhotos = new ArrayList<>();
        for (String path : selectedPhotoPaths) {
            File file = new File(path);
            Uri uri = null;
            if (null != mActivity && null != mActivity.get()) {
                uri = UriUtils.getUri(mActivity.get(), file);
            }
            if (null != mFragment && null != mFragment.get()) {
                uri = UriUtils.getUri(mFragment.get().getActivity(), file);
            }
            if (null != mFragmentV && null != mFragmentV.get()) {
                uri = UriUtils.getUri(mFragmentV.get().getActivity(), file);
            }
            if (uri == null) {
                uri = Uri.fromFile(file);
            }
            Photo photo = new Photo(null, uri, path, 0, 0, 0, 0, 0, 0, null);
            selectedPhotos.add(photo);
        }
        Setting.selectedPhotos.addAll(selectedPhotos);
        return AlbumBuilder.this;
    }


    /**
     * 原图按钮设置,不调用该方法不显示原图按钮
     *
     * @param isChecked    原图选项默认状态是否为选中状态
     * @param usable       原图按钮是否可使用
     * @param unusableHint 原图按钮不可使用时给用户的文字提示
     * @return AlbumBuilder
     */
    public AlbumBuilder setOriginalMenu(boolean isChecked, boolean usable, String unusableHint) {
        Setting.showOriginalMenu = true;
        Setting.selectedOriginal = isChecked;
        Setting.originalMenuUsable = usable;
        Setting.originalMenuUnusableHint = unusableHint;
        return AlbumBuilder.this;
    }


    /**
     * 是否显示拼图按钮
     *
     * @param shouldShow 是否显示
     * @return AlbumBuilder
     */
    public AlbumBuilder setPuzzleMenu(boolean shouldShow) {
        Setting.showPuzzleMenu = shouldShow;
        return AlbumBuilder.this;
    }

    /**
     * 只显示Video
     *
     * @return @return AlbumBuilder
     */

    public AlbumBuilder onlyVideo() {
        return filter(Type.VIDEO);
    }

    /**
     * 过滤
     *
     * @param types {@link Type}
     * @return @return AlbumBuilder
     */
    public AlbumBuilder filter(String... types) {
        Setting.filterTypes = Arrays.asList(types);
        return AlbumBuilder.this;
    }

    /**
     * 是否显示gif图
     *
     * @param shouldShow 是否显示
     * @return @return AlbumBuilder
     */
    public AlbumBuilder setGif(boolean shouldShow) {
        Setting.showGif = shouldShow;
        return AlbumBuilder.this;
    }

    /**
     * 是否显示video
     *
     * @param shouldShow 是否显示
     * @return @return AlbumBuilder
     */
    public AlbumBuilder setVideo(boolean shouldShow) {
        Setting.showVideo = shouldShow;
        return AlbumBuilder.this;
    }

    /**
     * 显示最少多少秒的视频
     *
     * @param second 秒
     * @return @return AlbumBuilder
     */
    public AlbumBuilder setVideoMinSecond(int second) {
        Setting.videoMinSecond = second * 1000;
        return AlbumBuilder.this;
    }

    /**
     * 显示最多多少秒的视频
     *
     * @param second 秒
     * @return @return AlbumBuilder
     */
    public AlbumBuilder setVideoMaxSecond(int second) {
        Setting.videoMaxSecond = second * 1000;
        return AlbumBuilder.this;
    }

    /**
     * 相册选择页是否显示清空按钮
     *
     * @param shouldShow
     * @return
     */
    public AlbumBuilder setCleanMenu(boolean shouldShow) {
        Setting.showCleanMenu = shouldShow;
        return AlbumBuilder.this;
    }

    private void setSettingParams() {
        switch (startupType) {
            case CAMERA:
                Setting.onlyStartCamera = true;
                Setting.isShowCamera = true;
                break;
            case ALBUM:
                Setting.isShowCamera = false;
                break;
            case ALBUM_CAMERA:
                Setting.isShowCamera = true;
                break;
        }
        if (!Setting.filterTypes.isEmpty()) {
            if (Setting.isFilter(Type.GIF)) {
                Setting.showGif = true;
            }
            if (Setting.isFilter(Type.VIDEO)) {
                Setting.showVideo = true;
            }
        }
        if (Setting.isOnlyVideo()) {
            //只选择视频 暂不支持拍照/拼图等
            Setting.isShowCamera = false;
            Setting.showPuzzleMenu = false;
            Setting.showGif = false;
            Setting.showVideo = true;
        }
    }

    /**
     * 启动，onActivityResult方式
     *
     * @param requestCode startActivityForResult的请求码
     */

    public void start(int requestCode) {
        setSettingParams();
        launchEasyPhotosActivity(requestCode);
    }

    /**
     * 启动，链式调用
     */
    public void start(SelectCallback callback) {
        setSettingParams();
        if (null != mActivity && null != mActivity.get() && mActivity.get() instanceof FragmentActivity) {
            EasyResult.get((FragmentActivity) mActivity.get()).startEasyPhoto(callback);
            return;
        }
        if (null != mFragmentV && null != mFragmentV.get()) {
            EasyResult.get(mFragmentV.get()).startEasyPhoto(callback);
            return;
        }
        throw new RuntimeException("mActivity or mFragmentV maybe null, you can not use this " +
                "method... ");
    }

    /**
     * 正式启动
     *
     * @param requestCode startActivityForResult的请求码
     */
    private void launchEasyPhotosActivity(int requestCode) {
        if (null != mActivity && null != mActivity.get()) {
            EasyPhotosActivity.start(mActivity.get(), requestCode);
            return;
        }
        if (null != mFragment && null != mFragment.get()) {
            EasyPhotosActivity.start(mFragment.get(), requestCode);
            return;
        }
        if (null != mFragmentV && null != mFragmentV.get()) {
            EasyPhotosActivity.start(mFragmentV.get(), requestCode);
        }
    }

    /**
     * 清除所有数据
     */
    private static void clear() {
        Result.clear();
        Setting.clear();
        instance = null;
    }

//*********************AD************************************

    /**
     * 设置广告(不设置该选项则表示不使用广告)
     *
     * @param photosAdView         使用图片列表的广告View
     * @param photosAdIsLoaded     图片列表广告是否加载完毕
     * @param albumItemsAdView     使用专辑项目列表的广告View
     * @param albumItemsAdIsLoaded 专辑项目列表广告是否加载完毕
     * @return AlbumBuilder
     */
    public AlbumBuilder setAdView(View photosAdView, boolean photosAdIsLoaded,
                                  View albumItemsAdView, boolean albumItemsAdIsLoaded) {
        Setting.photosAdView = new WeakReference<View>(photosAdView);
        Setting.albumItemsAdView = new WeakReference<View>(albumItemsAdView);
        Setting.photoAdIsOk = photosAdIsLoaded;
        Setting.albumItemsAdIsOk = albumItemsAdIsLoaded;
        return AlbumBuilder.this;
    }

    /**
     * 设置广告监听
     * 内部使用，无需关心
     *
     * @param adListener 广告监听
     */
    public static void setAdListener(AdListener adListener) {
        if (null == instance) return;
        if (instance.startupType == StartupType.CAMERA) return;
        instance.adListener = new WeakReference<AdListener>(adListener);
    }

    /**
     * 刷新图片列表广告数据
     */
    public static void notifyPhotosAdLoaded() {
        if (Setting.photoAdIsOk) {
            return;
        }
        if (null == instance) {
            return;
        }
        if (instance.startupType == StartupType.CAMERA) {
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
        if (Setting.albumItemsAdIsOk) {
            return;
        }
        if (null == instance) {
            return;
        }
        if (instance.startupType == StartupType.CAMERA) {
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

}
