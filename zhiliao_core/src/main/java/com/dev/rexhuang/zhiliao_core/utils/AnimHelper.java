package com.dev.rexhuang.zhiliao_core.utils;

import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;

/**
 * *  created by RexHuang
 * *  on 2019/9/10
 */
public class AnimHelper {

    public static final float DEFAULT_START_ROTATE = 0f;
    public static final float DEFAULT_END_ROTATE = 360f;
    public static final long DEFAULT_DURATION = 80000;

    public static ObjectAnimator rotate(Object target, String propertyName, float startRotate, float endRotate, long duration, int repeatCount, int repeatMode) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(target, propertyName, startRotate, endRotate)
                .setDuration(duration);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        return objectAnimator;
    }
}
