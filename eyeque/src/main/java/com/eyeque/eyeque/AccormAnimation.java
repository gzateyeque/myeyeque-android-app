package com.eyeque.eyeque;

import android.view.animation.Animation;
import android.view.animation.Transformation;


/**
 *
 * File:            AccormAnimation.java
 * Description:     This purpose of this animation is to reduce the accommodation
 *                  effort that could impact the accuracy of the reflective test.
 *                  Accommodation is a conscious effort which many human can, inparticular
 *                  young human eye vary the optical power to chnage focus from distance
 *                  to near.
 * Created:         2016/04/21
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class AccormAnimation extends Animation {
    private PatternView pv;
    private int type;
    private long sec;
    private float ii;

    public AccormAnimation(PatternView circle) {
        this.pv = circle;
        ii = 100f;
        type = 1;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        double elapsedTime = (double) (-(sec-System.currentTimeMillis())/5000f);
        double radiansToDraw = (double) ((2 * Constants.PI) * (double) 0.4) * elapsedTime;
        pv.setradians((double) radiansToDraw);
        pv.requestLayout();
    }

    public void setSeconds(long value) {
        this.sec = value;
    }

}

