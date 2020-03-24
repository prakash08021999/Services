package com.example.ratingbarviewcontroller;

import android.view.animation.Interpolator;

public class DampingInterpolator implements Interpolator {

    float mOvershootPercent = 0.5f;

    float mOvershootModulus;

    int mCount = 1;

    float mRegion;

    public DampingInterpolator() {
        this(1, 0.5f);
    }

    public DampingInterpolator(int count, float overshoot) {
        setOverShootCount(count);
        setOverShootPercent(overshoot);
    }

    public void setOverShootCount(int count) {
        mCount = Math.max(1, count);
        mRegion = (float) (Math.PI * 2 * (mCount - 1) + Math.PI / 2 * 3);
        mOvershootModulus = (float) Math.pow(mOvershootPercent, mRegion
                / Math.PI);
    }

    public void setOverShootPercent(float overshoot) {
        mOvershootPercent = Math.max(0, Math.min(1, overshoot));
        mOvershootModulus = (float) Math.pow(mOvershootPercent, mRegion
                / Math.PI);
    }

    public int getOverShootCount() {
        return mCount;
    }

    public float getOverShootPercent() {
        return mOvershootPercent;
    }

    @Override
    public float getInterpolation(float t) {
        if (t <= 0) {
            return 0;
        }
        if (t >= 1) {
            return 1;
        }
        return (float) (1 - Math.pow(mOvershootModulus, t)
                * Math.cos(mRegion * t));
    }

}
