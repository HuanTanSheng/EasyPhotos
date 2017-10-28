package com.huantansheng.easyphotos.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * 自带点击效果的imageview
 * Created by huan on 2017/8/15.
 */

public class PressedImageView extends android.support.v7.widget.AppCompatImageView {
    private int filterColor;//按压颜色

    public PressedImageView(Context context) {
        super(context);
        this.filterColor = Color.GRAY;
    }

    public PressedImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.filterColor = Color.GRAY;

    }

    public PressedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.filterColor = Color.GRAY;

    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if (isPressed()) {
            setColorFilter(filterColor, PorterDuff.Mode.MULTIPLY);
        } else {
            clearColorFilter();
        }
    }

    public void setPressedColor(int pressedColor) {
        filterColor = pressedColor;
    }
}
