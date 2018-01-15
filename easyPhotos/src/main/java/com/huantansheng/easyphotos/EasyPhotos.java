package com.huantansheng.easyphotos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;

import com.huantansheng.easyphotos.engine.ImageEngine;
import com.huantansheng.easyphotos.models.ad.AdListener;
import com.huantansheng.easyphotos.models.album.AlbumModel;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.models.sticker.StickerModel;
import com.huantansheng.easyphotos.models.sticker.entity.TextStickerData;
import com.huantansheng.easyphotos.result.Result;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.ui.EasyPhotosActivity;
import com.huantansheng.easyphotos.ui.PuzzleActivity;
import com.huantansheng.easyphotos.utils.bitmap.BitmapUtils;
import com.huantansheng.easyphotos.utils.bitmap.SaveBitmapCallBack;
import com.huantansheng.easyphotos.utils.media.MediaScannerConnectionUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * EasyPhotos的启动管理器
 * Created by huan on 2017/10/18.
 */
public class EasyPhotos {

    //easyPhotos的返回数据Key
    public static final String RESULT_PHOTOS = "keyOfEasyPhotosResult";
    public static final String RESULT_PATHS = "keyOfEasyPhotosResultPaths";
    public static final String RESULT_SELECTED_ORIGINAL = "keyOfEasyPhotosResultSelectedOriginal";
    public static final String RESULT_PUZZLE_PHOTO = "keyOfEasyPhotosResultPuzzlePhoto";
    public static final String RESULT_PUZZLE_PATH = "keyOfEasyPhotosResultPuzzlePath";

    /**
     * 启动模式
     * CAMERA-相机
     * ALBUM-相册专辑
     * ALBUM_CAMERA-带有相机按钮的相册专辑
     */
    private enum StartupType {
        CAMERA, ALBUM, ALBUM_CAMERA
    }

    private static EasyPhotos instance;
    private final WeakReference<Activity> mActivity;
    private StartupType startupType;
    private WeakReference<AdListener> adListener;

    //私有构造函数，不允许外部调用，真正实例化通过静态方法实现
    private EasyPhotos(Activity activity, StartupType startupType) {
        mActivity = new WeakReference<>(activity);
        this.startupType = startupType;
    }

    /**
     * 内部处理相机和相册的实例
     *
     * @param activity Activity的实例
     * @return EasyPhotos EasyPhotos的实例
     */
    private static EasyPhotos with(Activity activity, StartupType startupType) {
        clear();
        instance = new EasyPhotos(activity, startupType);
        return instance;
    }


    /**
     * 创建相机
     *
     * @param activity    上下文
     * @param imageEngine 图片加载引擎的具体实现
     * @return
     */
    public static EasyPhotos createCamera(Activity activity, @NonNull ImageEngine imageEngine) {
        if (Setting.imageEngine != imageEngine) {
            Setting.imageEngine = imageEngine;
        }
        return EasyPhotos.with(activity, StartupType.CAMERA);
    }

    /**
     * 创建相册
     *
     * @param activity     上下文
     * @param isShowCamera 是否显示相机按钮
     * @param imageEngine  图片加载引擎的具体实现
     * @return
     */
    public static EasyPhotos createAlbum(Activity activity, boolean isShowCamera, @NonNull ImageEngine imageEngine) {
        if (Setting.imageEngine != imageEngine) {
            Setting.imageEngine = imageEngine;
        }
        if (isShowCamera) {
            return EasyPhotos.with(activity, StartupType.ALBUM_CAMERA);
        } else {
            return EasyPhotos.with(activity, StartupType.ALBUM);
        }
    }

    /**
     * 设置fileProvider字段
     *
     * @param fileProviderAuthority fileProvider字段
     * @return EasyPhotos
     */
    public EasyPhotos setFileProviderAuthority(String fileProviderAuthority) {
        Setting.fileProviderAuthority = fileProviderAuthority;
        return EasyPhotos.this;
    }

    /**
     * 设置选择数
     *
     * @param selectorMaxCount 最大选择数
     * @return EasyPhotos
     */
    public EasyPhotos setCount(int selectorMaxCount) {
        Setting.count = selectorMaxCount;
        return EasyPhotos.this;
    }

    /**
     * 设置显示照片的最小文件大小
     *
     * @param minFileSize 最小文件大小，单位Bytes
     * @return EasyPhotos
     */
    public EasyPhotos setMinFileSize(long minFileSize) {
        Setting.minSize = minFileSize;
        return EasyPhotos.this;
    }

    /**
     * 设置显示照片的最小宽度
     *
     * @param minWidth 照片的最小宽度，单位Px
     * @return EasyPhotos
     */
    public EasyPhotos setMinWidth(int minWidth) {
        Setting.minWidth = minWidth;
        return EasyPhotos.this;
    }

    /**
     * 设置显示照片的最小高度
     *
     * @param minHeight 显示照片的最小高度，单位Px
     * @return EasyPhotos
     */
    public EasyPhotos setMinHeight(int minHeight) {
        Setting.minHeight = minHeight;
        return EasyPhotos.this;
    }

    /**
     * 设置默认选择图片集合
     *
     * @param selectedPhotos 默认选择图片集合
     * @return EasyPhotos
     */
    public EasyPhotos setSelectedPhotos(ArrayList<Photo> selectedPhotos) {
        Setting.selectedPhotos.clear();
        if (selectedPhotos.isEmpty()) {
            return EasyPhotos.this;
        }
        Setting.selectedPhotos.addAll(selectedPhotos);
        Setting.selectedOriginal = selectedPhotos.get(0).selectedOriginal;
        return EasyPhotos.this;
    }

    /**
     * 设置默认选择图片地址集合
     *
     * @param selectedPhotoPaths 默认选择图片地址集合
     * @return EasyPhotos
     */
    public EasyPhotos setSelectedPhotoPaths(ArrayList<String> selectedPhotoPaths) {
        Setting.selectedPhotos.clear();
        ArrayList<Photo> selectedPhotos = new ArrayList<>();
        for (String path : selectedPhotoPaths) {
            Photo photo = new Photo(null, path, 0, 0, 0, 0, null);
            selectedPhotos.add(photo);
        }
        Setting.selectedPhotos.addAll(selectedPhotos);
        return EasyPhotos.this;
    }


    /**
     * 原图按钮设置,不调用该方法不显示原图按钮
     *
     * @param isChecked    原图选项默认状态是否为选中状态
     * @param usable       原图按钮是否可使用
     * @param unusableHint 原图按钮不可使用时给用户的文字提示
     * @return EasyPhotos
     */
    public EasyPhotos setOriginalMenu(boolean isChecked, boolean usable, String unusableHint) {
        Setting.showOriginalMenu = true;
        Setting.selectedOriginal = isChecked;
        Setting.originalMenuUsable = usable;
        Setting.originalMenuUnusableHint = unusableHint;
        return EasyPhotos.this;
    }


    /**
     * 是否显示拼图按钮
     *
     * @param isShow 是否显示
     * @return EasyPhotos
     */
    public EasyPhotos setPuzzleMenu(boolean isShow) {
        Setting.showPuzzleMenu = isShow;
        return EasyPhotos.this;
    }

    /**
     * 是否显示gif图
     *
     * @param isShow 是否显示
     * @return @return EasyPhotos
     */
    public EasyPhotos setGif(boolean isShow) {
        Setting.showGif = isShow;
        return EasyPhotos.this;
    }


    /**
     * 设置启动属性
     *
     * @param requestCode startActivityForResult的请求码
     */
    public void start(int requestCode) {
        switch (startupType) {
            case CAMERA:
                Setting.onlyStartCamera = true;
                break;
            case ALBUM:
                Setting.isShowCamera = false;
                break;
            case ALBUM_CAMERA:
                Setting.isShowCamera = true;
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
        EasyPhotosActivity.start(mActivity.get(), requestCode);
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

//*********************AD************************************

    /**
     * 设置广告(不设置该选项则表示不使用广告)
     *
     * @param photosAdView         使用图片列表的广告View
     * @param photosAdIsLoaded     图片列表广告是否加载完毕
     * @param albumItemsAdView     使用专辑项目列表的广告View
     * @param albumItemsAdIsLoaded 专辑项目列表广告是否加载完毕
     * @return EasyPhotos
     */
    public EasyPhotos setAdView(View photosAdView, boolean photosAdIsLoaded, View albumItemsAdView, boolean albumItemsAdIsLoaded) {
        Setting.photosAdView = new WeakReference<View>(photosAdView);
        Setting.albumItemsAdView = new WeakReference<View>(albumItemsAdView);
        Setting.photoAdIsOk = photosAdIsLoaded;
        Setting.albumItemsAdIsOk = albumItemsAdIsLoaded;
        return EasyPhotos.this;
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


//*************************bitmap功能***********************************/

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
     */
    public static void addWatermark(Bitmap watermark, Bitmap image, int srcImageWidth, int offsetX, int offsetY, boolean addInLeft) {
        BitmapUtils.addWatermark(watermark, image, srcImageWidth, offsetX, offsetY, addInLeft);
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
    public static void addWatermarkWithText(Bitmap watermark, Bitmap image, int srcImageWidth, @NonNull String text, int offsetX, int offsetY, boolean addInLeft) {
        BitmapUtils.addWatermarkWithText(watermark, image, srcImageWidth, text, offsetX, offsetY, addInLeft);
    }

    /**
     * 保存Bitmap到指定文件夹
     *
     * @param act         上下文
     * @param dirPath     文件夹全路径
     * @param bitmap      bitmap
     * @param namePrefix  保存文件的前缀名，文件最终名称格式为：前缀名+自动生成的唯一数字字符+.png
     * @param notifyMedia 是否更新到媒体库
     * @param callBack    保存图片后的回调，回调已经处于UI线程
     */
    public static void saveBitmapToDir(Activity act, String dirPath, String namePrefix, Bitmap bitmap, boolean notifyMedia, SaveBitmapCallBack callBack) {
        BitmapUtils.saveBitmapToDir(act, dirPath, namePrefix, bitmap, notifyMedia, callBack);
    }


    /**
     * 把View画成Bitmap
     *
     * @param view 要处理的View
     * @return Bitmap
     */
    public static Bitmap createBitmapFromView(View view) {
        return BitmapUtils.createBitmapFromView(view);
    }


    /**
     * 启动拼图（最多对9张图片进行拼图）
     *
     * @param act                  上下文
     * @param photos               图片集合（最多对9张图片进行拼图）
     * @param puzzleSaveDirPath    拼图完成保存的文件夹全路径
     * @param puzzleSaveNamePrefix 拼图完成保存的文件名前缀，最终格式：前缀+默认生成唯一数字标识+.png
     * @param requestCode          请求code
     * @param replaceCustom        单击替换拼图中的某张图片时，是否以startForResult的方式启动你的自定义界面，该界面与传进来的act为同一界面。false则在EasyPhotos内部完成，正常需求直接写false即可。 true的情况适用于：用于拼图的图片集合中包含网络图片，是在你的act界面中获取并下载的（也可以直接用网络地址，不用下载后的本地地址，也就是可以不下载下来），而非单纯本地相册。举例：你的act中有两个按钮，一个指向本地相册，一个指向网络相册，用户在该界面任意选择，选择好图片后跳转到拼图界面，用户在拼图界面点击替换按钮，将会启动一个新的act界面，这时，act只让用户在网络相册和本地相册选择一张图片，选择好执行
     *                             Intent intent = new Intent();
     *                             intent.putParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS , photos);
     *                             act.setResult(RESULT_OK,intent); 并关闭act，回到拼图界面，完成替换。
     * @param imageEngine          图片加载引擎的具体实现
     */
    public static void startPuzzleWithPhotos(Activity act, ArrayList<Photo> photos, String puzzleSaveDirPath, String puzzleSaveNamePrefix, int requestCode, boolean replaceCustom, @NonNull ImageEngine imageEngine) {
        act.setResult(Activity.RESULT_OK);
        PuzzleActivity.startWithPhotos(act, photos, puzzleSaveDirPath, puzzleSaveNamePrefix, requestCode, replaceCustom, imageEngine);
    }

    /**
     * 启动拼图（最多对9张图片进行拼图）
     *
     * @param act                  上下文
     * @param paths                图片地址集合（最多对9张图片进行拼图）
     * @param puzzleSaveDirPath    拼图完成保存的文件夹全路径
     * @param puzzleSaveNamePrefix 拼图完成保存的文件名前缀，最终格式：前缀+默认生成唯一数字标识+.png
     * @param requestCode          请求code
     * @param replaceCustom        单击替换拼图中的某张图片时，是否以startForResult的方式启动你的自定义界面，该界面与传进来的act为同一界面。false则在EasyPhotos内部完成，正常需求直接写false即可。 true的情况适用于：用于拼图的图片集合中包含网络图片，是在你的act界面中获取并下载的（也可以直接用网络地址，不用下载后的本地地址，也就是可以不下载下来），而非单纯本地相册。举例：你的act中有两个按钮，一个指向本地相册，一个指向网络相册，用户在该界面任意选择，选择好图片后跳转到拼图界面，用户在拼图界面点击替换按钮，将会启动一个新的act界面，这时，act只让用户在网络相册和本地相册选择一张图片，选择好执行
     *                             Intent intent = new Intent();
     *                             intent.putStringArrayListExtra(EasyPhotos.RESULT_PATHS , paths);
     *                             act.setResult(RESULT_OK,intent); 并关闭act，回到拼图界面，完成替换。
     * @param imageEngine          图片加载引擎的具体实现
     */
    public static void startPuzzleWithPaths(Activity act, ArrayList<String> paths, String puzzleSaveDirPath, String puzzleSaveNamePrefix, int requestCode, boolean replaceCustom, @NonNull ImageEngine imageEngine) {
        PuzzleActivity.startWithPaths(act, paths, puzzleSaveDirPath, puzzleSaveNamePrefix, requestCode, replaceCustom, imageEngine);
    }


    //**************更新媒体库***********************

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


    //*********************************贴纸***************************


    /**
     * 添加文字贴纸的文字数据
     *
     * @param textStickerData 文字贴纸的文字数据
     */
    public static void addTextStickerData(TextStickerData... textStickerData) {
        StickerModel.textDataList.addAll(Arrays.asList(textStickerData));
    }

    /**
     * 清空文字贴纸的数据
     */
    public static void clearTextStickerDataList() {
        StickerModel.textDataList.clear();
    }


}
