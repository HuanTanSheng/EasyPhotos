package com.huantansheng.easyphotos.models.puzzle;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.huantansheng.easyphotos.R;

/**
 * @author wupanjie
 */
public class DegreeSeekBar extends View {
    private static final String TAG = "DegreeSeekBar";
    private Paint mTextPaint;
    private Paint mCirclePaint;
    private Paint.FontMetricsInt mFontMetrics;
    private int mBaseLine;
    private float[] mTextWidths;

    private final Rect mCanvasClipBounds = new Rect();

    private ScrollingListener mScrollingListener;
    private float mLastTouchedPosition;

    private Paint mPointPaint;
    private float mPointMargin;

    private boolean mScrollStarted;
    private int mTotalScrollDistance;

    private Path mIndicatorPath = new Path();

    private int mCurrentDegrees = 0;
    private int mPointCount = 51;

    private int mPointColor;
    private int mTextColor;
    private int mCenterTextColor;

    //阻尼系数的倒数
    private float mDragFactor = 2.1f;

    private int mMinReachableDegrees = -45;
    private int mMaxReachableDegrees = 45;

    private String suffix = "";

    public DegreeSeekBar(Context context) {
        this(context, null);
    }

    public DegreeSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DegreeSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DegreeSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPointColor = ContextCompat.getColor(getContext(), R.color.easy_photos_fg_primary);
        mTextColor = ContextCompat.getColor(getContext(), R.color.easy_photos_fg_primary);
        mCenterTextColor = ContextCompat.getColor(getContext(), R.color.easy_photos_fg_accent);

        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setStyle(Paint.Style.STROKE);
        mPointPaint.setColor(mPointColor);
        mPointPaint.setStrokeWidth(2);

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(24f);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setAlpha(100);

        mFontMetrics = mTextPaint.getFontMetricsInt();

        mTextWidths = new float[1];
        mTextPaint.getTextWidths("0", mTextWidths);

        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAlpha(255);
        mCirclePaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPointMargin = (float) w / mPointCount;

        mBaseLine = (h - mFontMetrics.bottom + mFontMetrics.top) / 2 - mFontMetrics.top;

        mIndicatorPath.moveTo(w / 2, h / 2 + mFontMetrics.top / 2 - 18);
        mIndicatorPath.rLineTo(-8, -8);
        mIndicatorPath.rLineTo(16, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchedPosition = event.getX();
                if (!mScrollStarted) {
                    mScrollStarted = true;
                    if (mScrollingListener != null) {
                        mScrollingListener.onScrollStart();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mScrollStarted = false;
                if (mScrollingListener != null) {
                    mScrollingListener.onScrollEnd();
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = event.getX() - mLastTouchedPosition;
                if (mCurrentDegrees >= mMaxReachableDegrees && distance < 0) {
                    mCurrentDegrees = mMaxReachableDegrees;
                    invalidate();
                    break;
                }
                if (mCurrentDegrees <= mMinReachableDegrees && distance > 0) {
                    mCurrentDegrees = mMinReachableDegrees;
                    invalidate();
                    break;
                }
                if (distance != 0) {
                    onScrollEvent(event, distance);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.getClipBounds(mCanvasClipBounds);

        int zeroIndex = mPointCount / 2 + (0 - mCurrentDegrees) / 2;
        mPointPaint.setColor(mPointColor);
        for (int i = 0; i < mPointCount; i++) {

            if (i > zeroIndex - Math.abs(mMinReachableDegrees) / 2
                    && i < zeroIndex + Math.abs(mMaxReachableDegrees) / 2
                    && mScrollStarted) {
                mPointPaint.setAlpha(255);
            } else {
                mPointPaint.setAlpha(100);
            }

            if (i > mPointCount / 2 - 8
                    && i < mPointCount / 2 + 8
                    && i > zeroIndex - Math.abs(mMinReachableDegrees) / 2
                    && i < zeroIndex + Math.abs(mMaxReachableDegrees) / 2) {
                if (mScrollStarted) {
                    mPointPaint.setAlpha(Math.abs(mPointCount / 2 - i) * 255 / 8);
                } else {
                    mPointPaint.setAlpha(Math.abs(mPointCount / 2 - i) * 100 / 8);
                }
            }

            canvas.drawPoint(mCanvasClipBounds.centerX() + (i - mPointCount / 2) * mPointMargin,
                    mCanvasClipBounds.centerY(), mPointPaint);

            if (mCurrentDegrees != 0 && i == zeroIndex) {
                if (mScrollStarted) {
                    mTextPaint.setAlpha(255);
                } else {
                    mTextPaint.setAlpha(192);
                }
                mPointPaint.setStrokeWidth(4);
                canvas.drawPoint((mCanvasClipBounds.centerX() + (i - mPointCount / 2) * mPointMargin),
                        mCanvasClipBounds.centerY(), mPointPaint);
                mPointPaint.setStrokeWidth(2);
                mTextPaint.setAlpha(100);
            }
        }

        for (int i = -180; i <= 180; i += 15) {
            if (i >= mMinReachableDegrees && i <= mMaxReachableDegrees) {
                drawDegreeText(i, canvas, true);
            } else {
                drawDegreeText(i, canvas, false);
            }
        }

        mTextPaint.setTextSize(28f);
        mTextPaint.setAlpha(255);
        mTextPaint.setColor(mCenterTextColor);

        if (mCurrentDegrees >= 10) {
            canvas.drawText(mCurrentDegrees + suffix, getWidth() / 2 - mTextWidths[0], mBaseLine,
                    mTextPaint);
        } else if (mCurrentDegrees <= -10) {
            canvas.drawText(mCurrentDegrees + suffix, getWidth() / 2 - mTextWidths[0] / 2 * 3, mBaseLine,
                    mTextPaint);
        } else if (mCurrentDegrees < 0) {
            canvas.drawText(mCurrentDegrees + suffix, getWidth() / 2 - mTextWidths[0], mBaseLine,
                    mTextPaint);
        } else {
            canvas.drawText(mCurrentDegrees + suffix, getWidth() / 2 - mTextWidths[0] / 2, mBaseLine,
                    mTextPaint);
        }

        mTextPaint.setAlpha(100);
        mTextPaint.setTextSize(24f);
        mTextPaint.setColor(mTextColor);
        //画中心三角
        mCirclePaint.setColor(mCenterTextColor);
        canvas.drawPath(mIndicatorPath, mCirclePaint);
        mCirclePaint.setColor(mCenterTextColor);
    }

    private void drawDegreeText(int degrees, Canvas canvas, boolean canReach) {
        if (canReach) {
            if (mScrollStarted) {
                mTextPaint.setAlpha(Math.min(255, Math.abs(degrees - mCurrentDegrees) * 255 / 15));
                if (Math.abs(degrees - mCurrentDegrees) <= 7) {
                    mTextPaint.setAlpha(0);
                }
            } else {
                mTextPaint.setAlpha(100);
                if (Math.abs(degrees - mCurrentDegrees) <= 7) {
                    mTextPaint.setAlpha(0);
                }
            }
        } else {
            mTextPaint.setAlpha(100);
        }
        if (degrees == 0) {
            if (Math.abs(mCurrentDegrees) >= 15 && !mScrollStarted) {
                mTextPaint.setAlpha(180);
            }
            canvas.drawText("0°",
                    getWidth() / 2 - mTextWidths[0] / 2 - mCurrentDegrees / 2 * mPointMargin,
                    getHeight() / 2 - 10, mTextPaint);
        } else {
            canvas.drawText(degrees + suffix, getWidth() / 2 + mPointMargin * degrees / 2
                    - mTextWidths[0] / 2 * 3
                    - mCurrentDegrees / 2 * mPointMargin, getHeight() / 2 - 10, mTextPaint);
        }
    }

    private void onScrollEvent(MotionEvent event, float distance) {
        mTotalScrollDistance -= distance;
        postInvalidate();
        mLastTouchedPosition = event.getX();
        mCurrentDegrees = (int) ((mTotalScrollDistance * mDragFactor) / mPointMargin);
        if (mScrollingListener != null) {
            mScrollingListener.onScroll(mCurrentDegrees);
        }
    }

    public void setDegreeRange(int min, int max) {
        if (min > max) {
            Log.e(TAG, "setDegreeRange: error, max must greater than min");
        } else {
            mMinReachableDegrees = min;
            mMaxReachableDegrees = max;

            if (mCurrentDegrees > mMaxReachableDegrees || mCurrentDegrees < mMinReachableDegrees) {
                mCurrentDegrees = (mMinReachableDegrees + mMaxReachableDegrees) / 2;
            }
            mTotalScrollDistance = (int) (mCurrentDegrees * mPointMargin / mDragFactor);
            invalidate();
        }
    }

    public void setCurrentDegrees(int degrees) {
        if (degrees <= mMaxReachableDegrees && degrees >= mMinReachableDegrees) {
            mCurrentDegrees = degrees;
            mTotalScrollDistance = (int) (degrees * mPointMargin / mDragFactor);
            invalidate();
        }
    }

    public void setScrollingListener(ScrollingListener scrollingListener) {
        mScrollingListener = scrollingListener;
    }

    public int getPointColor() {
        return mPointColor;
    }

    public void setPointColor(int pointColor) {
        mPointColor = pointColor;
        mPointPaint.setColor(mPointColor);
        postInvalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        mTextPaint.setColor(textColor);
        postInvalidate();
    }

    public int getCenterTextColor() {
        return mCenterTextColor;
    }

    public void setCenterTextColor(int centerTextColor) {
        mCenterTextColor = centerTextColor;
        postInvalidate();
    }

    public float getDragFactor() {
        return mDragFactor;
    }

    public void setDragFactor(float dragFactor) {
        mDragFactor = dragFactor;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public interface ScrollingListener {

        void onScrollStart();

        void onScroll(int currentDegrees);

        void onScrollEnd();
    }
}

