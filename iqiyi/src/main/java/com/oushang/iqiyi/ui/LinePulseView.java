package com.oushang.iqiyi.ui;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oushang.iqiyi.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author: DELL
 * @Description: 自定义view-播放线条脉冲
 * @Time: 2021/7/30 14:34
 * @Since:
 */
public class LinePulseView extends View implements Runnable{
    private static final String TAG = LinePulseView.class.getSimpleName();

    /**
     * 随机数
     */
    private static Random mRandom = new Random();

    /**
     * View默认最小宽度
     */
    private static final int DEFAULT_MIN_WIDTH = 3;

    /**
     * 默认3条音轨
     */
    private static final int DEFAULT_RAIL_COUNT = 3;

    /**
     * 控件宽
     */
    private int mViewWidth;
    /**
     * 控件高
     */
    private int mViewHeight;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 音轨数量
     */
    private int mRailCount;
    /**
     * 音轨颜色
     */
    private int mRailColor;
    /**
     * 每条音轨的线宽
     */
    private float mRailLineWidth;
    /**
     * Float类型估值器，用于在指定数值区域内进行估值
     */
    private FloatEvaluator mFloatEvaluator;

    //主要处理是否恢复播放状态，解决滑动时再回来不会自动播放
    //默认设置为true是为了viewdatabing的设置，是在view还没有附加到窗口就执行（会导致初始化主动设置不成功），
    // 所以第一次进去主要以隐藏显示去判别是否加载动画
    private  boolean isResetPlay=true;

    public LinePulseView(Context context) {
        this(context, null);
    }

    public LinePulseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinePulseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        mPaint = new Paint();//设置画笔
        mPaint.setColor(mRailColor);//设置颜色
        mPaint.setStrokeWidth(mRailLineWidth);//设置宽度
        mPaint.setStyle(Paint.Style.STROKE);//画笔样式
        mPaint.setStrokeCap(Paint.Cap.ROUND); //设置线头的形状，ROUND 圆头
        mPaint.setAntiAlias(true);//设置抗锯齿
        mFloatEvaluator = new FloatEvaluator();
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LinePulseView, defStyleAttr, 0);
        mRailCount = array.getInt(R.styleable.LinePulseView_rail_count, DEFAULT_RAIL_COUNT);
        mRailColor = array.getColor(R.styleable.LinePulseView_rail_color, Color.argb(255, 255, 255, 255));
        mRailLineWidth = array.getDimension(R.styleable.LinePulseView_rail_width, dip2px(context, 1f));
        array.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //计算可用高度
        float totalAvailableHeight = mViewHeight - getPaddingBottom() - getPaddingTop();
        //计算每条音轨平分宽度后的位置
        float averageBound = (mViewWidth * 1.0f) / mRailCount;

        //计算每条音轨的x坐标位置
        float x = averageBound - mRailLineWidth;
        float y = getBottom();

        //旋转画布，按控件中心旋转180度，即可让音轨反转
//        canvas.rotate(180, mViewWidth / 2f, mViewHeight / 2f);
        //保存画布
        canvas.save();
        for (int i = 1; i <= mRailCount; i++) {
            //估值y坐标
            float fraction = nextRandomFloat(1.0f);//动画完成度
            float evaluateY = (mFloatEvaluator.evaluate(fraction, 0.0f, 1.0f)) * totalAvailableHeight;
            //第一个不需要偏移
            if (i == 1) {
                canvas.drawLine(x, y, x, evaluateY, mPaint);
            } else {
                //后续，每个音轨都固定偏移间距后，再画
                canvas.translate(averageBound, 0);
                canvas.drawLine(x, y, x, evaluateY, mPaint);
            }
        }
        //恢复画布
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(handleMeasure(widthMeasureSpec), handleMeasure(heightMeasureSpec));
    }

    /**
     * 处理MeasureSpec
     */
    private int handleMeasure(int measureSpec) {
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            //处理wrap_content的情况
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow");
        if(getVisibility()==View.VISIBLE&&isResetPlay){
            start();
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        Log.d(TAG, "onVisibilityChanged:" + visibility);
        if (visibility == GONE) {
            stop();
        } else if (visibility == VISIBLE) {
            start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow");
        removeCallbacks(this);
    }


    @Override
    public void run() {
        invalidate();
        removeCallbacks(this);
        postDelayed(this, 200);
    }

    public void start() {
        removeCallbacks(this);
        postDelayed(this, 500);
        isResetPlay=true;
    }

    public void stop() {
        removeCallbacks(this);
        isResetPlay=false;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 产生一个随机float
     *
     * @param sl 随机数范围[0,sl)
     */
    public static float nextRandomFloat(float sl) {
        return mRandom.nextFloat() * sl;
    }
}
