package com.huantansheng.easyphotos.utils.bitmap;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.utils.uri.UriUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
     * @param orientation            Bitmap的旋转角度。当useWidth为true时，Photo实体类中会有orientation，若bitmap
     *                               不是用户手机内图片，填0即可。
     * @return                       添加水印后的bitmap
     */
    public static Bitmap addWatermark(Bitmap watermark, Bitmap image, int srcWaterMarkImageWidth,
                                    int offsetX, int offsetY, boolean addInLeft, int orientation) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        if (0 == imageWidth || 0 == imageHeight) {
            throw new RuntimeException("AlbumBuilder: 加水印的原图宽或高不能为0！");
        }
        switch (orientation) {
            case 90:
            case 270:
                image = adjustBitmapRotation(image, orientation);
                int temp = imageHeight;
                imageHeight = imageWidth;
                imageWidth = temp;
                break;
            case 180:
                image = adjustBitmapRotation(image, orientation);
                break;

        }
        int watermarkWidth = watermark.getWidth();
        int watermarkHeight = watermark.getHeight();
        float scale = imageWidth / (float) srcWaterMarkImageWidth;
        if (scale > 1) scale = 1;
        else if (scale < 0.4) scale = 0.4f;
        int scaleWatermarkWidth = (int) (watermarkWidth * scale);
        int scaleWatermarkHeight = (int) (watermarkHeight * scale);
        Bitmap scaleWatermark = Bitmap.createScaledBitmap(watermark, scaleWatermarkWidth,
                scaleWatermarkHeight, true);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (addInLeft) {
            canvas.drawBitmap(scaleWatermark, offsetX,
                    imageHeight - scaleWatermarkHeight - offsetY, paint);
        } else {
            canvas.drawBitmap(scaleWatermark, imageWidth - offsetX - scaleWatermarkWidth,
                    imageHeight - scaleWatermarkHeight - offsetY, paint);
        }
        recycle(scaleWatermark);
        return image;
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
     * @param orientation            Bitmap的旋转角度。当useWidth为true时，Photo实体类中会有orientation，若bitmap
     *                               不是用户手机内图片，填0即可。
     * @return                       添加水印后的bitmap
     */
    public static Bitmap addWatermarkWithText(@NonNull Bitmap watermark, Bitmap image,
                                            int srcWaterMarkImageWidth, @NonNull String text,
                                            int offsetX, int offsetY, boolean addInLeft,
                                            int orientation) {
        float imageWidth = image.getWidth();
        float imageHeight = image.getHeight();
        if (0 == imageWidth || 0 == imageHeight) {
            throw new RuntimeException("AlbumBuilder: 加水印的原图宽或高不能为0！");
        }
        switch (orientation) {
            case 90:
            case 270:
                image = adjustBitmapRotation(image, orientation);
                float temp = imageHeight;
                imageHeight = imageWidth;
                imageWidth = temp;
                break;
            case 180:
                image = adjustBitmapRotation(image, orientation);
                break;

        }
        float watermarkWidth = watermark.getWidth();
        float watermarkHeight = watermark.getHeight();
        float scale = imageWidth / (float) srcWaterMarkImageWidth;
        if (scale > 1) scale = 1;
        else if (scale < 0.4) scale = 0.4f;
        float scaleWatermarkWidth = watermarkWidth * scale;
        float scaleWatermarkHeight = watermarkHeight * scale;
        Bitmap scaleWatermark = Bitmap.createScaledBitmap(watermark, (int) scaleWatermarkWidth,
                (int) scaleWatermarkHeight, true);
        Canvas canvas = new Canvas(image);

        Paint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        float textsize = (float) (scaleWatermark.getHeight() * 2) / (float) 3;
        textPaint.setTextSize(textsize);
        Rect textRect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textRect);
        if (addInLeft) {
            canvas.drawText(text, scaleWatermarkWidth + offsetX,
                    imageHeight - textRect.height() - textRect.top - offsetY, textPaint);
        } else {
            canvas.drawText(text, imageWidth - offsetX - textRect.width() - textRect.left,
                    imageHeight - textRect.height() - textRect.top - offsetY, textPaint);
        }

        Paint sacleWatermarkPaint = new Paint();
        sacleWatermarkPaint.setAntiAlias(true);
        if (addInLeft) {
            canvas.drawBitmap(scaleWatermark, offsetX,
                    imageHeight - textRect.height() - offsetY - scaleWatermarkHeight / 6,
                    sacleWatermarkPaint);
        } else {
            canvas.drawBitmap(scaleWatermark,
                    imageWidth - textRect.width() - offsetX - scaleWatermarkWidth / 6,
                    imageHeight - textRect.height() - offsetY - scaleWatermarkHeight / 6,
                    sacleWatermarkPaint);
        }
        recycle(scaleWatermark);
        return image;
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
    public static void saveBitmapToDir(final Activity act, final String dirPath,
                                       final String namePrefix, final Bitmap bitmap,
                                       final boolean notifyMedia,
                                       final SaveBitmapCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    //android10+
                    saveBitmapToDirQ(act, dirPath, namePrefix, bitmap, notifyMedia, callBack);
                    return;
                }

                File dirF = new File(dirPath);
                if (!dirF.exists() || !dirF.isDirectory()) {
                    if (!dirF.mkdirs()) {
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onCreateDirFailed();
                            }
                        });
                        return;
                    }
                }

                try {
                    final File writeFile = File.createTempFile(namePrefix, ".png", dirF);

                    FileOutputStream fos = null;
                    fos = new FileOutputStream(writeFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                    if (notifyMedia) {
                        EasyPhotos.notifyMedia(act, writeFile);
                    }
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(writeFile);
                        }
                    });

                } catch (final IOException e) {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onIOFailed(e);
                        }
                    });

                }
            }
        }).start();

    }

    private static void saveBitmapToDirQ(final Activity act, final String dirPath,
                                         final String namePrefix, final Bitmap bitmap,
                                         final boolean notifyMedia,
                                         final SaveBitmapCallBack callBack) {
        long dataTake = System.currentTimeMillis();
        String jpegName = namePrefix + dataTake + ".png";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, jpegName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        int dirIndex = dirPath.lastIndexOf("/");
        if (dirIndex == dirPath.length()) {
            String dirPath2 = dirPath.substring(0, dirIndex - 1);
            dirIndex = dirPath2.lastIndexOf("/");
        }
        String dirName = dirPath.substring(dirIndex + 1);
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/" + dirName);

        Uri external;
        ContentResolver resolver = act.getContentResolver();
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            external = MediaStore.Images.Media.getContentUri("external");
        } else {
            external = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        }

        final Uri insertUri = resolver.insert(external, values);
        if (insertUri == null) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callBack.onCreateDirFailed();
                }
            });
            return;
        }
        OutputStream os;
        try {
            os = resolver.openOutputStream(insertUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            if (os != null) {
                os.flush();
                os.close();
            }
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String uriPath = UriUtils.getPathByUri(act, insertUri);
                    if (null == uriPath) {
                        callBack.onCreateDirFailed();
                    } else {
                        callBack.onSuccess(new File(uriPath));
                    }
                }
            });
        } catch (final IOException e) {
            e.printStackTrace();
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callBack.onIOFailed(e);
                }
            });
        }
    }


    /**
     * 把View画成Bitmap
     *
     * @param view 要处理的View
     * @return Bitmap
     */
    public static Bitmap createBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    /**
     * 计算获取的照片的宽高是否需要交换，如果图片是旋转了90度或270度的，那么就需要交换
     *
     * @param photo 需要计算的图片
     * @return 宽高是否需要交换
     */
    public static Boolean needChangeWidthAndHeight(Photo photo) throws IOException {

        ExifInterface exifInterface = new ExifInterface(photo.path);
        int exifOrientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        // 如果拿到的照片是旋转了90度或270度的，意味着通过MediaStore获取的宽高需要交换
        return exifOrientation == ExifInterface.ORIENTATION_ROTATE_90 || exifOrientation == ExifInterface.ORIENTATION_ROTATE_270;

    }

    /**
     * 通过BitmapFactory.Options重设图片的宽高，在通过MediaStore获取的图片宽高为0时使用
     *
     * @param photo 需要计算的图片
     */
    public static void calculateLocalImageSizeThroughBitmapOptions(Photo photo) throws IOException {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photo.path, options);

        photo.width = options.outWidth;
        photo.height = options.outHeight;
    }

    public static Bitmap adjustBitmapRotation(Bitmap bm, final int orientationDegree) {

        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);


        return bm1;
    }
}
