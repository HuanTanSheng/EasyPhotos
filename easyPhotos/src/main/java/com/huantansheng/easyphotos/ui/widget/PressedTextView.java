package com.huantansheng.easyphotos.ui.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * 带点击状态的textview
 * Created by huan on 2017/9/15.
 */

public class PressedTextView extends android.support.v7.widget.AppCompatTextView {
    private float pressedScale;
    private AnimatorSet set;
    private int pressedFlag;

    public PressedTextView(Context context) {
        super(context);
        this.pressedScale = 1.1f;
        this.pressedFlag = 1;
    }

    public PressedTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.pressedScale = 1.1f;
        this.pressedFlag = 1;
    }

    public PressedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.pressedScale = 1.1f;
        this.pressedFlag = 1;
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if (isPressed()) {
            pressedFlag = 1;
            if (null == set) {
                set = new AnimatorSet();
                set.setDuration(5);
            }
            if (set.isRunning()) set.cancel();
            ObjectAnimator pScaleX = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, pressedScale);
            ObjectAnimator pScaleY = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, pressedScale);
            set.play(pScaleX).with(pScaleY);
            set.start();
        } else {
            if (pressedFlag != 1) {
                return;
            }
            pressedFlag = 2;
            if (null == set) {
                set = new AnimatorSet();
                set.setDuration(5);
            }
            if (set.isRunning()) set.cancel();
            ObjectAnimator nScaleX = ObjectAnimator.ofFloat(this, "scaleX", pressedScale, 1.0f);
            ObjectAnimator nScaleY = ObjectAnimator.ofFloat(this, "scaleY", pressedScale, 1.0f);
            set.play(nScaleX).with(nScaleY);
            set.start();
        }
    }

    public void setPressedScale(float pressedScale) {
        this.pressedScale = pressedScale;
    }

}
