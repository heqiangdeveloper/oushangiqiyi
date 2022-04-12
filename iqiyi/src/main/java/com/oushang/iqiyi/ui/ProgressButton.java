package com.oushang.iqiyi.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.oushang.iqiyi.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author: zeelang
 * @Description: 带进度条的button
 * @Time: 2021/11/8 0008  19:09
 * @Since: 1.0
 */
public class ProgressButton extends AppCompatButton {
    private static final String TAG = ProgressButton.class.getSimpleName();

    private int mMax;
    private int mProgress;
    private Drawable backGround;
    private Drawable progressBackGround;
    private Disposable mDisposable;
    private boolean mIsInProgress;

    public ProgressButton(@NonNull Context context) {
        this(context, null);
    }

    public ProgressButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton);
        progressBackGround = typedArray.getDrawable(R.styleable.ProgressButton_progressBackground);
        backGround = typedArray.getDrawable(R.styleable.ProgressButton_android_background);
        mMax = typedArray.getInt(R.styleable.ProgressButton_progressMax, 100);
        typedArray.recycle();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("checkTagTag","onDraw******");
        backGround.setBounds(0,0, backGround.getIntrinsicWidth(), backGround.getIntrinsicHeight());
        backGround.draw(canvas);
        progressBackGround.setBounds(0,0, (getWidth() * mProgress)/ mMax, getHeight());
        progressBackGround.draw(canvas);
        super.onDraw(canvas);
    }

    public void setProgress(int progress) {
        if (progress > mMax) {
            progress = mMax;
        }
        this.mProgress = progress;
        postInvalidate();
    }

    public int getProgress() {
        return mProgress;
    }

    public void start() {
        mProgress = 0;
        mIsInProgress = true;
        mDisposable = Observable.intervalRange(0, mMax, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> ProgressButton.this.setProgress(Math.toIntExact(aLong)));

    }

    public void stop() {
        if(mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mIsInProgress = false;
    }

    public void setCompleted() {
        mProgress = mMax;
        postInvalidate();
    }

    public boolean isInProgress() {
        return mIsInProgress;
    }



}
