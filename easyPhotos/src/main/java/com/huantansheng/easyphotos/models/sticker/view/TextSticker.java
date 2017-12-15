package com.huantansheng.easyphotos.models.sticker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.models.sticker.listener.OnStickerClickListener;


/**
 * 自定义贴图view
 * Created by huan on 2017/7/24.
 */

public class TextSticker extends View {

    public boolean isChecked = false;
    private String text;
    private float textWidth;
    private float textHeight;
    private Bitmap bitmap;
    private Bitmap btDelete;
    private Bitmap btController;
    //    private Bitmap btEditor;
    //    private Bitmap btRotate;
    private int btSize;
    private Matrix mMatrix;
    private float[] srcPs, dstPs;
    private TextPaint textPaint;
    private Paint bitmapPaint;
    private Paint framePaint;
    private boolean isUsing = true;
    private float downX1;
    private float downY1;
    private float downX2;
    private float downY2;
    private ClickType clickType;
    private boolean isOut = false;
    private GestureDetector gestureDetector;
    private float lastDegree;
    private float lastDoubleDegress;
    private OnStickerClickListener listener;
    private int startX, startY;
    private int minWidth = 300, minHeight = 100;
    private float minScale;
    private StaticLayout textLayout;
    private int textLayoutWidth;

    private Canvas bitmapConvas;

    private Path path;

    public TextSticker(Context context, String text, int viewGroupCenterX, int viewGroupCenterY) {
        super(context);

        this.text = text;
        if (TextUtils.isEmpty(this.text)) {
            this.text = context.getString(R.string.text_sticker_hint_easy_photos);
        }
        path = new Path();
        textLayoutWidth = getResources().getDisplayMetrics().widthPixels / 2;
        initButtons();
        initPaints();
        resetSize();
        initStartPoint(viewGroupCenterX, viewGroupCenterY);
        initPs();
        resetBitmap();
        initMatrix();
        initCanvasPosition();
        lastDegree = computeDegree(new Point((int) textWidth, (int) textHeight), new Point((int) textWidth / 2, (int) textHeight / 2));
        lastDoubleDegress = 1000;
        gestureDetector = new GestureDetector(context, new StickerGestureListener());

    }

    private void resetBitmap() {
        EasyPhotos.recycle(bitmap);
        bitmap = Bitmap.createBitmap((int) textWidth, (int) textHeight, Bitmap.Config.ARGB_4444);
        bitmapConvas = new Canvas(bitmap);
//        bitmapConvas.drawText(text, 0, Math.abs(textPaint.getFontMetrics().top), textPaint);
        textLayout.draw(bitmapConvas);
    }

    private void initStartPoint(int viewGroupCenterX, int viewGroupCenterY) {
        this.startX = viewGroupCenterX - (int) textWidth / 2;
        if (this.startX < 100) {
            this.startX = viewGroupCenterX / 2;
        }
        this.startY = viewGroupCenterY - (int) textHeight / 2;
        if (this.startY < 100) {
            this.startY = viewGroupCenterY / 2;
        }
    }

    public void setOnStickerClickListener(OnStickerClickListener listener) {
        this.listener = listener;
    }

    private void initCanvasPosition() {
        mMatrix.postTranslate(startX, startY);
        mMatrix.mapPoints(dstPs, srcPs);
    }

    private void initMatrix() {
        mMatrix = new Matrix();
    }

    private void initPaints() {
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setFilterBitmap(true);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.sticker_text_size_easy_photos));
        textPaint.setColor(Color.WHITE);


        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setDither(true);
        bitmapPaint.setFilterBitmap(true);

        framePaint = new Paint();
        framePaint.setAntiAlias(true);
        bitmapPaint.setDither(true);
        bitmapPaint.setFilterBitmap(true);
        framePaint.setStrokeWidth(1);
        framePaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
    }


    private void resetSize() {
        textLayout = new StaticLayout(text, textPaint, textLayoutWidth,
                Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
        textWidth = minWidth;
        textHeight = minHeight;
        if (textWidth < textLayout.getWidth()) {
            textWidth = textLayout.getWidth();
        }
        if (textHeight < textLayout.getHeight()) {
            textHeight = textLayout.getHeight();
        }
        minScale = minWidth / textWidth;

    }

    private void initPs() {
        srcPs = new float[]{0, 0, textWidth, 0, textWidth, textHeight, 0, textHeight, textWidth / 2, textHeight / 2};
        dstPs = srcPs.clone();
    }

    private void initButtons() {
        btDelete = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_delete_easy_photos);
//        btEditor = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_editor_easy_photos);
        btController = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_controller_easy_photos);
//        btRotate = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_rotate);
        btSize = btDelete.getWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmap, mMatrix, bitmapPaint);
        if (isUsing)
            drawOthers(canvas);
    }

    private void drawOthers(Canvas canvas) {
        path.reset();
        path.moveTo(dstPs[0], dstPs[1]);
        path.lineTo(dstPs[2], dstPs[3]);
        path.lineTo(dstPs[4], dstPs[5]);
        path.lineTo(dstPs[6], dstPs[7]);
        path.lineTo(dstPs[0], dstPs[1]);
        for (int i = 0; i < 7; i += 2) {
            if (i == 6) {
                canvas.drawLine(dstPs[i], dstPs[i + 1], dstPs[0], dstPs[1], framePaint);
                break;
            }
            canvas.drawLine(dstPs[i], dstPs[i + 1], dstPs[i + 2], dstPs[i + 3], framePaint);
        }


        canvas.drawBitmap(btDelete, dstPs[2] - btSize / 2, dstPs[3] - btSize / 2, bitmapPaint);
//        canvas.drawBitmap(btEditor, dstPs[0] - btSize / 2, dstPs[1] - btSize / 2, bitmapPaint);
        canvas.drawBitmap(btController, dstPs[4] - btSize / 2, dstPs[5] - btSize / 2, bitmapPaint);
//        canvas.drawBitmap(btRotate, dstPs[6] - btSize / 2, dstPs[7] - btSize / 2, bitmapPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);
        if (MotionEvent.ACTION_UP == event.getAction() || MotionEvent.ACTION_POINTER_UP == event.getAction() || MotionEvent.ACTION_POINTER_1_UP == event.getAction() || MotionEvent.ACTION_POINTER_2_UP == event.getAction()) {
            setDoubleDownPoints(0, 0, 0, 0);
            lastDoubleDegress = 1000;
            lastDegree = computeDegree(new Point((int) dstPs[4], (int) dstPs[5]), new Point((int) dstPs[8], (int) dstPs[9]));
        }

        return !isOut;
    }

    private void setDoubleDownPoints(float x1, float y1, float x2, float y2) {
        downX1 = x1;
        downY1 = y1;
        downX2 = x2;
        downY2 = y2;
    }

    private void calculateClickType(int x, int y) {
        RectF rectF = new RectF(x - btSize / 2 - 40, y - btSize / 2 - 40, x + btSize / 2 + 40, y + btSize / 2 + 40);

        Rect rect = new Rect();


        if (rectF.contains(dstPs[2] - 20, dstPs[3])) {
            clickType = ClickType.DELETE;
        }
//        else if (rectF.contains(dstPs[0], dstPs[1])) {
//            clickType = ClickType.EDITOR;
//        }
        else if (rectF.contains(dstPs[4] + 20, dstPs[5])) {
            clickType = ClickType.SCALE;
        }
//        else if (rectF.contains(dstPs[6] - 20, dstPs[7])) {
//            clickType = ClickType.IMAGE;
//        }
        else {
            RectF bounds = new RectF();
            path.computeBounds(bounds, true);
            Region region = new Region();
            region.setPath(path, new Region((int) bounds.left, (int) bounds.top, (int) bounds.right, (int) bounds.bottom));

            if (region.contains(x, y)) {
                if (isOut) {
                    isOut = false;
                }
                if (!isUsing) {
                    isUsing = true;
                    listener.onUsing();
                    postInvalidate();
                }
                clickType = ClickType.IMAGE;
            } else {
                if (isUsing) {
                    isUsing = false;
                    postInvalidate();
                }
                if (!isOut) {
                    isOut = true;
                }
                clickType = ClickType.OUT;
            }
        }
    }

    public void delete() {
        if (null == listener) {
            throw new NullPointerException("OnStickerClickListener listener is null");
        }
        setVisibility(GONE);
        EasyPhotos.recycle(bitmap);
        listener.onDelete();
    }

    private void editor() {
        listener.onEditor();
    }

    private void top() {
        bringToFront();//置顶
        invalidate();
        listener.onTop();
    }

    private void move(float distansX, float distansY) {
        mMatrix.postTranslate(distansX, distansY);
        matrixMap();
    }

    public void moveTo(float x, float y) {
        move(x - dstPs[8], y - dstPs[1]);
    }

    private void controller(MotionEvent event) {
        scale(event);
        rotate(event);
    }

    private void scale(MotionEvent event) {
        float originalX1;
        float originalY1;
        float originalX2;
        float originalY2;
        float moveX1;
        float moveY1;
        float moveX2;
        float moveY2;
        if (event.getPointerCount() == 2) {
            originalX2 = downX2;
            originalY2 = downY2;
            originalX1 = downX1;
            originalY1 = downY1;

            moveX2 = event.getX(1);
            moveY2 = event.getY(1);
            moveX1 = event.getX(0);
            moveY1 = event.getY(0);

        } else {
            originalX2 = dstPs[4];
            originalY2 = dstPs[5];
            originalX1 = dstPs[0];
            originalY1 = dstPs[1];

            moveX2 = event.getX();
            moveY2 = event.getY();
            moveX1 = originalX1;
            moveY1 = originalY1;
        }

        float temp1 = getDistanceOfTwoPoints(originalX2, originalY2, originalX1, originalY1);
        float temp2 = getDistanceOfTwoPoints(moveX2, moveY2, moveX1, moveY1);

        float scalValue = temp2 / temp1;

        if (getScaleValue() < minScale && scalValue < (float) 1) {
            return;
        }
        mMatrix.postScale(scalValue, scalValue, dstPs[8], dstPs[9]);
        matrixMap();
        if (event.getPointerCount() == 2) {
            setDoubleDownPoints(moveX1, moveY1, moveX2, moveY2);
        }
    }

    private void rotate(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float preDegree = computeDegree(new Point((int) event.getX(0), (int) event.getY(0)), new Point((int) event.getX(1), (int) event.getY(1)));
            if (lastDoubleDegress == 1000) {
                lastDoubleDegress = preDegree;
            }
            mMatrix.postRotate(preDegree - lastDoubleDegress, dstPs[8], dstPs[9]);
            matrixMap();
            lastDoubleDegress = preDegree;

        } else {
            float preDegree = computeDegree(new Point((int) event.getX(), (int) event.getY()), new Point((int) dstPs[8], (int) dstPs[9]));
            mMatrix.postRotate(preDegree - lastDegree, dstPs[8], dstPs[9]);
            matrixMap();
            lastDegree = preDegree;
        }
    }

    // 获取饰品缩放比例(与原图相比)
    public float getScaleValue() {
        float preDistance = (srcPs[8] - srcPs[0]) * (srcPs[8] - srcPs[0]) + (srcPs[9] - srcPs[1]) * (srcPs[9] - srcPs[1]);
        float lastDistance = (dstPs[8] - dstPs[0]) * (dstPs[8] - dstPs[0]) + (dstPs[9] - dstPs[1]) * (dstPs[9] - dstPs[1]);
        float scaleValue = (float) Math.sqrt(lastDistance / preDistance);
        return scaleValue;
    }

    private void matrixMap() {
        mMatrix.mapPoints(dstPs, srcPs);
        postInvalidate();
    }

    public void setUsing(boolean isUsing) {
        this.isUsing = isUsing;
        postInvalidate();
    }

    private float getDistanceOfTwoPoints(float x1, float y1, float x2, float y2) {
        return (float) (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }

    public float computeDegree(Point p1, Point p2) {
        float tran_x = p1.x - p2.x;
        float tran_y = p1.y - p2.y;
        float degree = 0.0f;
        float angle = (float) (Math.asin(tran_x / Math.sqrt(tran_x * tran_x + tran_y * tran_y)) * 180 / Math.PI);
        if (!Float.isNaN(angle)) {
            if (tran_x >= 0 && tran_y <= 0) {//第一象限
                degree = angle;
            } else if (tran_x <= 0 && tran_y <= 0) {//第二象限
                degree = angle;
            } else if (tran_x <= 0 && tran_y >= 0) {//第三象限
                degree = -180 - angle;
            } else if (tran_x >= 0 && tran_y >= 0) {//第四象限
                degree = 180 - angle;
            }
        }
        return degree;
    }

    private enum ClickType {
        DELETE, EDITOR, SCALE, ROTATE, IMAGE, OUT
    }

    private class StickerGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            switch (clickType) {
                case DELETE:
                    delete();
                    break;
                case EDITOR:
//                    editor();
                    break;
                case SCALE:
                    break;
                case ROTATE:
                    break;
                case IMAGE:
//                    editor();
                    break;
                case OUT:
                    break;
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            switch (clickType) {
                case DELETE:
                    break;
                case EDITOR:
                    break;
                case SCALE:
                    break;
                case ROTATE:
                    break;
                case IMAGE:
                    editor();
                    break;
                case OUT:
                    break;
            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            switch (clickType) {
                case DELETE:
                    break;
                case EDITOR:
                    break;
                case SCALE:
                    if (e2.getPointerCount() > 1) break;
                    controller(e2);
                    break;
                case ROTATE:
//                    rotate(e2);
                    break;
                case IMAGE:
                    if (e2.getPointerCount() == 2) {
                        if (downX1 + downY1 + downX2 + downY2 == 0) {
                            setDoubleDownPoints(e2.getX(0), e2.getY(0), e2.getX(1), e2.getY(1));
                        }
                        controller(e2);
                    } else if (e2.getPointerCount() == 1) {
                        move(-distanceX, -distanceY);
                    }
                    break;
                case OUT:
                    break;
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            isChecked = true;
            calculateClickType((int) e.getX(), (int) e.getY());
            if (clickType == ClickType.IMAGE) {
                top();
            }
            return true;
        }

    }

    public void resetText(String text) {
        this.text = text;
        resetSize();
        resetPoints();
        resetBitmap();
        matrixMap();
    }

    private void resetPoints() {
        srcPs = new float[]{0, 0, textWidth, 0, textWidth, textHeight, 0, textHeight, textWidth / 2, textHeight / 2};
    }

    public String getText() {
        return this.text;
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
        resetSize();
        resetPoints();
        resetBitmap();
        matrixMap();
    }

    public int getTextColor() {
        return textPaint.getColor();
    }

    public void setTextAlpha(int alpha) {
        textPaint.setAlpha(alpha);
        resetSize();
        resetPoints();
        resetBitmap();
        matrixMap();
    }

    public int getTextAlpha() {
        return textPaint.getAlpha();
    }

    public boolean isUsing() {
        return this.isUsing;
    }


}
