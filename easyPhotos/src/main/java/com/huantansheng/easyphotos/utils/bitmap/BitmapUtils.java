package com.huantansheng.easyphotos.utils.bitmap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.support.annotation.NonNull;
import android.text.TextPaint;

import com.huantansheng.easyphotos.utils.bitmap.face.FaceCallBackOnUiThread;
import com.huantansheng.easyphotos.utils.bitmap.face.FaceInformation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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


    /**
     * 获取脸部信息，无需考虑线程问题，EasyPhotos已经处理好了。目前只支持正脸检测
     *
     * @param activity 上下文
     * @param bitmap   获取脸部信息的图片
     * @param maxFaces 最大可检测到的人脸数
     * @param callBack 回调
     */
    public static void getFaces(Activity activity, final Bitmap bitmap, final int maxFaces, final FaceCallBackOnUiThread callBack) {

        final WeakReference<Activity> act = new WeakReference<Activity>(activity);

        new Thread(new Runnable() {
            @Override
            public void run() {

                Bitmap desBitmap = bitmap.copy(Bitmap.Config.RGB_565, true);

                FaceDetector faceDetector = new FaceDetector(desBitmap.getWidth(), desBitmap.getHeight(), maxFaces);
                FaceDetector.Face[] faces = new FaceDetector.Face[maxFaces];
                int realFaceNum = faceDetector.findFaces(desBitmap, faces);

                if (realFaceNum > 0) {

                    final ArrayList<FaceInformation> list = new ArrayList<>();
                    for (FaceDetector.Face face : faces) {
                        if (face.confidence() < 0.3) {
                            continue;
                        }
                        PointF midPoint = new PointF();
                        face.getMidPoint(midPoint);
                        float eyesDistance = face.eyesDistance();

                        FaceInformation faceInformation = new FaceInformation();
                        faceInformation.midEyesPoint = midPoint;
                        faceInformation.eyesDistance = eyesDistance;

                        faceInformation.faceRect = new RectF(midPoint.x - eyesDistance, midPoint.y - eyesDistance, midPoint.x + eyesDistance, midPoint.y + eyesDistance + eyesDistance / 2);

                        faceInformation.rightEsyRect = new RectF(midPoint.x + eyesDistance / 4, midPoint.y - eyesDistance / 4, midPoint.x + eyesDistance / 2 + eyesDistance / 4, midPoint.y + eyesDistance / 4);

                        faceInformation.leftEsyRect = new RectF(midPoint.x - eyesDistance / 2 - eyesDistance / 4, midPoint.y - eyesDistance / 4, midPoint.x - eyesDistance / 4, midPoint.y + eyesDistance / 4);

                        faceInformation.noseRect = new RectF(midPoint.x - eyesDistance / 3, midPoint.y + eyesDistance / 4, midPoint.x + eyesDistance / 3, midPoint.y + eyesDistance * 0.75f);

                        faceInformation.mouthRect = new RectF(midPoint.x - eyesDistance / 2, midPoint.y + eyesDistance * 0.75f, midPoint.x + eyesDistance / 2, midPoint.y + eyesDistance * 1.5f);

                        list.add(faceInformation);

                    }

                    recycle(desBitmap);
                    if (list.isEmpty()) {
                        if (null == act.get()) return;
                        act.get().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onFailed();
                            }
                        });

                        return;
                    }
                    if (null == act.get()) return;
                    act.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(list);
                        }
                    });
                    return;
                }

                recycle(desBitmap);
                if (null == act.get()) return;
                act.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailed();
                    }
                });

            }
        }).start();


    }
}
