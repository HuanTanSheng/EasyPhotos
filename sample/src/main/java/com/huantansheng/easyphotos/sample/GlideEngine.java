package com.huantansheng.easyphotos.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huantansheng.easyphotos.engine.ImageEngine;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Glide4.x的加载图片引擎实现,单例模式
 * Created by huan on 2018/1/15.
 */

public class GlideEngine implements ImageEngine {
    //单例
    private static GlideEngine instance = null;
    //单例模式，私有构造方法
    private GlideEngine() {
    }
    //获取单例
    public static GlideEngine getInstance() {
        if (null == instance) {
            synchronized (GlideEngine.class) {
                if (null == instance) {
                    instance = new GlideEngine();
                }
            }
        }
        return instance;
    }

    /**
     * 加载图片到ImageView
     *
     * @param context   上下文
     * @param photoPath 图片路径
     * @param imageView 加载到的ImageView
     */
    @Override
    public void loadPhoto(Context context, String photoPath, ImageView imageView) {
        Glide.with(context).load(photoPath).transition(withCrossFade()).into(imageView);
    }

    /**
     * 加载gif动图图片到ImageView，gif动图不动
     *
     * @param context   上下文
     * @param gifPath   gif动图路径
     * @param imageView 加载到的ImageView
     *                  <p>
     *                  备注：不支持动图显示的情况下可以不写
     */
    @Override
    public void loadGifAsBitmap(Context context, String gifPath, ImageView imageView) {
        Glide.with(context).asBitmap().load(gifPath).into(imageView);
    }

    /**
     * 加载gif动图到ImageView，gif动图动
     *
     * @param context   上下文
     * @param gifPath   gif动图路径
     * @param imageView 加载动图的ImageView
     *                  <p>
     *                  备注：不支持动图显示的情况下可以不写
     */
    @Override
    public void loadGif(Context context, String gifPath, ImageView imageView) {
        Glide.with(context).asGif().load(gifPath).transition(withCrossFade()).into(imageView);
    }


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
    @Override
    public Bitmap getCacheBitmap(Context context, String path, int width, int height) throws Exception {
        return Glide.with(context).asBitmap().load(path).submit(width, height).get();
    }


}
