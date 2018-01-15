package com.huantansheng.easyphotos.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 自定义图片加载方式
 * Created by huan on 2018/1/15.
 */
public interface ImageEngine {
    /**
     * 加载图片到ImageView
     *
     * @param context   上下文
     * @param photoPath 图片路径
     * @param imageView 加载到的ImageView
     */
    void loadPhoto(Context context, String photoPath, ImageView imageView);

    /**
     * 加载gif动图图片到ImageView，gif动图不动
     *
     * @param context   上下文
     * @param gifPath   gif动图路径
     * @param imageView 加载到的ImageView
     *                  <p>
     *                  备注：不支持动图显示的情况下可以不写
     */
    void loadGifAsBitmap(Context context, String gifPath, ImageView imageView);

    /**
     * 加载gif动图到ImageView，gif动图动
     *
     * @param context   上下文
     * @param gifPath   gif动图路径
     * @param imageView 加载动图的ImageView
     *                  <p>
     *                  备注：不支持动图显示的情况下可以不写
     */
    void loadGif(Context context, String gifPath, ImageView imageView);

    /**
     * 获取图片加载框架中的缓存Bitmap
     *
     * @param context 上下文
     * @param path    图片路径
     * @param width   图片宽度
     * @param height  图片高度
     * @return Bitmap
     * @throws Exception 异常直接抛出，EasyPhotos内部处理
     */
    Bitmap getCacheBitmap(Context context, String path, int width, int height) throws Exception;


}
