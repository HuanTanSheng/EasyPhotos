package com.huantansheng.easyphotos.utils.bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextPaint;

import java.util.List;

/**
 * bitmap工具类
 * Created by huan on 2017/9/4.
 */

public class BitmapUtils {
    /**
     * 回收bitmap
     *
     * @param bitmap 回收的bitmap
     */
    public static void recycle(Bitmap bitmap) {
        if (null != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

    public static void recycle(Bitmap... bitmaps) {
        for (Bitmap b : bitmaps) {
            recycle(b);
        }
    }

    public static void recycle(List<Bitmap> bitmaps) {
        for (Bitmap b : bitmaps) {
            recycle(b);
        }
    }

    /**
     * 给图片添加水印，水印会根据图片宽高自动缩放处理
     *
     * @param watermark              水印
     * @param image                  添加水印的图片
     * @param offsetX                添加水印的X轴偏移量
     * @param offsetY                添加水印的Y轴偏移量
     * @param srcWaterMarkImageWidth 水印对应的原图片宽度,即ui制作水印时候参考的图片画布宽度,应该是已知的图片最大宽度
     * @param addInLeft              true 在左下角添加水印，false 在右下角添加水印
     */
    public static void addWatermark(Bitmap watermark, Bitmap image, int srcWaterMarkImageWidth, int offsetX, int offsetY, boolean addInLeft) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        if (0 == imageWidth || 0 == imageHeight) {
            throw new RuntimeException("EasyPhotos: 加水印的原图宽或高不能为0！");
        }
        int watermarkWidth = watermark.getWidth();
        int watermarkHeight = watermark.getHeight();
        float scale = imageWidth / (float) srcWaterMarkImageWidth;
        if (scale > 1) scale = 1;
        else if (scale < 0.4) scale = 0.4f;
        int scaleWatermarkWidth = (int) (watermarkWidth * scale);
        int scaleWatermarkHeight = (int) (watermarkHeight * scale);
        Bitmap scaleWatermark = Bitmap.createScaledBitmap(watermark, scaleWatermarkWidth, scaleWatermarkHeight, true);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (addInLeft) {
            canvas.drawBitmap(scaleWatermark, offsetX, imageHeight - scaleWatermarkHeight - offsetY, paint);
        } else {
            canvas.drawBitmap(scaleWatermark, imageWidth - offsetX - scaleWatermarkWidth, imageHeight - scaleWatermarkHeight - offsetY, paint);
        }
        recycle(scaleWatermark);
    }

    /**
     * 给图片添加带文字和图片的水印，水印会根据图片宽高自动缩放处理
     *
     * @param watermark              水印图片
     * @param image                  要加水印的图片
     * @param srcWaterMarkImageWidth 水印对应的原图片宽度,即ui制作水印时候参考的图片画布宽度,应该是已知的图片最大宽度
     * @param text                   要添加的文字
     * @param offsetX                添加水印的X轴偏移量
     * @param offsetY                添加水印的Y轴偏移量
     * @param addInLeft              true 在左下角添加水印，false 在右下角添加水印
     */
    public static void addWatermarkWithText(Bitmap watermark, Bitmap image, int srcWaterMarkImageWidth, @NonNull String text, int offsetX, int offsetY, boolean addInLeft) {
        float imageWidth = image.getWidth();
        float imageHeight = image.getHeight();
        if (0 == imageWidth || 0 == imageHeight) {
            throw new RuntimeException("EasyPhotos: 加水印的原图宽或高不能为0！");
        }
        float watermarkWidth = watermark.getWidth();
        float watermarkHeight = watermark.getHeight();
        float scale = imageWidth / (float) srcWaterMarkImageWidth;
        if (scale > 1) scale = 1;
        else if (scale < 0.4) scale = 0.4f;
        float scaleWatermarkWidth = watermarkWidth * scale;
        float scaleWatermarkHeight = watermarkHeight * scale;
        Bitmap scaleWatermark = Bitmap.createScaledBitmap(watermark, (int) scaleWatermarkWidth, (int) scaleWatermarkHeight, true);
        Canvas canvas = new Canvas(image);
        Paint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        float textsize = (float) (scaleWatermark.getHeight() * 2) / (float) 3;
        textPaint.setTextSize(textsize);
        Rect textRect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textRect);
        if (addInLeft) {
            canvas.drawText(text, scaleWatermarkWidth + offsetX, imageHeight - textRect.height() - textRect.top - offsetY, textPaint);
        } else {
            canvas.drawText(text, imageWidth - offsetX - textRect.width() - textRect.left, imageHeight - textRect.height() - textRect.top - offsetY, textPaint);
        }

        Paint sacleWatermarkPaint = new Paint();
        sacleWatermarkPaint.setAntiAlias(true);
        if (addInLeft) {
            canvas.drawBitmap(scaleWatermark, offsetX, imageHeight - textRect.height() - offsetY - scaleWatermarkHeight / 6, sacleWatermarkPaint);
        } else {
            canvas.drawBitmap(scaleWatermark, imageWidth - textRect.width() - offsetX - scaleWatermarkWidth / 6, imageHeight - textRect.height() - offsetY - scaleWatermarkHeight / 6, sacleWatermarkPaint);
        }
        recycle(scaleWatermark);
    }

}
