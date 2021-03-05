package com.huantansheng.easyphotos.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

/**
 * 自定义图片加载方式
 * Created by huan on 2018/1/15.
 */
public interface ImageEngine {
    /**
     * 加载图片到ImageView
     *
     * @param context   上下文
     * @param uri 图片Uri
     * @param imageView 加载到的ImageView
     */
    //安卓10推荐uri，并且path的方式不再可用
    void loadPhoto(@NonNull Context context, @NonNull Uri uri,@NonNull ImageView imageView);

    /**
     * 加载gif动图图片到ImageView，gif动图不动
     *
     * @param context   上下文
     * @param gifUri  gif动图路径Uri
     * @param imageView 加载到的ImageView
     *                  <p>
     *                  备注：不支持动图显示的情况下可以不写
     */
    //安卓10推荐uri，并且path的方式不再可用
    void loadGifAsBitmap(@NonNull Context context,@NonNull Uri gifUri,@NonNull ImageView imageView);

    /**
     * 加载gif动图到ImageView，gif动图动
     *
     * @param context   上下文
     * @param gifUri   gif动图路径Uri
     * @param imageView 加载动图的ImageView
     *                  <p>
     *                  备注：不支持动图显示的情况下可以不写
     */
    //安卓10推荐uri，并且path的方式不再可用
    void loadGif(@NonNull Context context,@NonNull Uri gifUri,@NonNull ImageView imageView);

    /**
     * 获取图片加载框架中的缓存Bitmap，不用拼图功能可以直接返回null
     *
     * @param context 上下文
     * @param uri    图片路径
     * @param width   图片宽度
     * @param height  图片高度
     * @return Bitmap
     * @throws Exception 异常直接抛出，EasyPhotos内部处理
     */
    //安卓10推荐uri，并且path的方式不再可用
    Bitmap getCacheBitmap(@NonNull Context context,@NonNull Uri uri, int width, int height) throws Exception;


}
