package com.oushang.iqiyi.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.lang.reflect.Field;

/**
 * @Author: zeelang
 * @Description:
 * @Time: 2022/1/7 19:28
 * @Since: 1.0
 */
public class IqiyiRecyclerView extends RecyclerView {
    private static final String TAG = IqiyiRecyclerView.class.getSimpleName();

    private static final float FLING_SCALE_SLOW_FACTOR = 0.5f;
    private static final int FLING_MAX_VELOCITY = 5000;


    public IqiyiRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public IqiyiRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IqiyiRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        setMaxFlingVelocity(this, FLING_MAX_VELOCITY);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "dispatch action down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "dispacth action move");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "dispatch action up");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "dispatch action cancel");
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "action down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "action move");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "action up");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "action cancel");
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent move");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent up");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "onTouchEvent cancel");
                break;
        }
        return super.onTouchEvent(event);
    }

//    @Override
//    public boolean fling(int velocityX, int velocityY) {
//        velocityX = customVelocity(velocityX);
//        velocityY = customVelocity(velocityY);
//        return super.fling(velocityX, velocityY);
//    }
//
//    private int customVelocity(int velocity) {
//        if(velocity > 0) {
//            return Math.min(velocity, FLING_MAX_VELOCITY);
//        } else {
//            return Math.max(velocity, -FLING_MAX_VELOCITY);
//        }
//    }

    private void setMaxFlingVelocity(RecyclerView recyclerView, int velocity) {
        try {
            Field field= recyclerView.getClass().getDeclaredField("mMaxFlingVelocity");
            field.setAccessible(true);
            field.set(recyclerView, velocity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
