package com.oushang.iqiyi.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;

import com.youth.banner.Banner;
import com.youth.banner.config.BannerConfig;

/**
 * @Author: Administrator
 * @Description: ***
 * @Time: 2021/12/16 0016  10:42
 * @Since: 1.0
 */
public class IqiyiBanner extends Banner {
    private static final String TAG = IqiyiBanner.class.getSimpleName();

    // 滑动距离范围
    private int mTouchSlop;

    // 轮播切换时间
    private int mScrollTime = 110;

    // 是否要拦截事件
    private boolean isIntercept = true;

    // 记录触摸的位置（主要用于解决事件冲突问题）
    private float mStartX, mStartY;

    // 记录viewpager2是否被拖动
    private boolean mIsViewPager2Drag;

    private OnNestedScrollListener mOnNestedScrollListener;

    public IqiyiBanner(Context context) {
        this(context, null);
    }

    public IqiyiBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IqiyiBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop() / 4;
        setTouchSlop(mTouchSlop);
        setScrollTime(mScrollTime);
    }

    @Override
    public Banner setIntercept(boolean intercept) {
        isIntercept = intercept;
        return super.setIntercept(intercept);
    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_CANCEL:
                if(mOnNestedScrollListener != null) {
                    mOnNestedScrollListener.onStartScroll();
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mOnNestedScrollListener != null) {
                    mOnNestedScrollListener.onEndScroll();
                }
                break;

        }

        return super.dispatchTouchEvent(ev);
    }

    public void setOnNestedScrollListener(OnNestedScrollListener listener) {
        this.mOnNestedScrollListener = listener;
    }

    public interface OnNestedScrollListener {
        void onStartScroll();

        void onEndScroll();
    }


}
