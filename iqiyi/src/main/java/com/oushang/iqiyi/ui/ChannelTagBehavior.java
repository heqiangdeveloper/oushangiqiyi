package com.oushang.iqiyi.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.oushang.iqiyi.R;

/**
 * @Author: Administrator
 * @Description: ***
 * @Time: 2021/12/18 0018  13:32
 * @Since: 1.0
 */
public class ChannelTagBehavior extends CoordinatorLayout.Behavior<View> {
    private static final String TAG = ChannelTagBehavior.class.getSimpleName();

    private int limitOffset = 500;
    private int currentOffset = 500;
    private int targetOffset = 0;


    private RelativeLayout commonChannelLayout;
    private RelativeLayout allChannelLayout;
    private RecyclerView mAllChannelRv;
    private int lastDependencyBottom = 0;
    private int lastOffset = 0;

    private boolean isTopTouch = false;

    public ChannelTagBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        commonChannelLayout = parent.findViewById(R.id.common_channel_layout);
        allChannelLayout = parent.findViewById(R.id.all_channel_layout);
        mAllChannelRv = parent.findViewById(R.id.all_channel_rv);
        return dependency instanceof RelativeLayout;
    }

    /**
     *  依赖视图发生变化时回调
     * @param parent
     * @param child       layout_behavior view
     * @param dependency
     * @return
     */
    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        Log.d(TAG, "onDependentViewChanged: bottom:" + dependency.getBottom());
        if(lastDependencyBottom > 0) {
            int curOffset = dependency.getHeight() - dependency.getBottom(); //移动距离
            int offset = Math.abs(curOffset - lastOffset);
            Log.d(TAG, "curOffset:" + curOffset + ",lastOffset:" + lastOffset);
            if (lastDependencyBottom > dependency.getBottom()) { //往上滑
                Log.d(TAG, "up offset:" + offset + ",bottom:" + child.getBottom());
                child.setBottom(child.getBottom() + offset);
                mAllChannelRv.setBottom(mAllChannelRv.getBottom() + offset);
            } else {  //往下滑
                Log.d(TAG, "down offset:" + offset + ",bottom:" + child.getBottom());
                child.setBottom(child.getBottom() - offset);
                mAllChannelRv.setBottom(mAllChannelRv.getBottom() - offset);
            }
            lastOffset = curOffset;
        }

        lastDependencyBottom = dependency.getBottom();
        return true;
    }


    /**
     * 开始滚动回调, 调用时机:子view调用startNestedScroll
     *
     * @param coordinatorLayout
     * @param child
     * @param directTargetChild
     * @param target
     * @param axes              滚动的方向
     * @param type
     * @return true支持嵌套滚动，false不支持
     */
    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL; //只接收垂直滑动事件
    }

    /**
     * 在消费滚动距离之前把总的滑动距离传给父布局coordinatorLayout, 调用时机：子view调用dispatchNestedPreScroll
     *
     * @param coordinatorLayout coordinatorLayout
     * @param child
     * @param target
     * @param dx                表示view本次x方向的滚动的总距离长度
     * @param dy                表示view本次y方向的滚动的总距离长度
     * @param consumed          表示父布局消费的距离,consumed[0]表示x方向,consumed[1]表示y方向
     * @param type
     */
    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (canScroll(target)) {
            return;
        }
        if (dy > 0) { //往下滑
            int parentCanConsume = currentOffset - targetOffset;
            if (parentCanConsume > 0) {
                if (dy > parentCanConsume) {
                    consumed[1] = parentCanConsume;
                    moveView(coordinatorLayout, child, commonChannelLayout, -parentCanConsume);
                } else {
                    consumed[1] = dy;
                    moveView(coordinatorLayout, child, commonChannelLayout, -dy);
                }
            }
        }
    }

    /**
     * 调用时机：子view调用dispatchNestedScroll
     *
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param dxConsumed        表示target已经消费的x方向的距离
     * @param dyConsumed        表示target已经消费的y方向的距离
     * @param dxUnconsumed      表示x方向剩下的滑动距离
     * @param dyUnconsumed      表示y方向剩下的滑动距离
     * @param type
     * @param consumed
     */
    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        if (dyUnconsumed < 0 && !canScroll(target)) {
            if (currentOffset < limitOffset) {
                int dy = currentOffset - dyUnconsumed <= limitOffset ? -dyUnconsumed : limitOffset - currentOffset;
                moveView(coordinatorLayout, child, commonChannelLayout, dy);
            }
        }

    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        Log.d(TAG, "onLayoutChild");
        lastOffset = 0;
        int childCount = parent.getChildCount();
        Log.d(TAG, "childCount:" + childCount);
        if (childCount > 1) {
            int left = parent.getLeft();
            int top = parent.getTop() + parent.getPaddingTop();
            for (int i = 0; i < childCount - 1; i++) {
                View childView = parent.getChildAt(i);
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) childView.getLayoutParams();
                top += childView.getMeasuredHeight();// + lp.topMargin ; //+ lp.bottomMargin;
                if (i == 1) { //常用频道布局位置
                    limitOffset = currentOffset = top - 30;//再减一个偏移量，使其不覆盖编辑按钮
                }
            }
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            limitOffset = currentOffset = currentOffset + layoutParams.topMargin;
            child.layout(left + layoutParams.leftMargin, top + layoutParams.topMargin, parent.getWidth() - parent.getPaddingRight() - layoutParams.rightMargin,
                    parent.getHeight() - parent.getPaddingBottom() - layoutParams.bottomMargin);
            return true;
        }
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onMeasureChild(@NonNull CoordinatorLayout parent, @NonNull View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        Log.d(TAG, "onMeasureChild");
        int height = View.MeasureSpec.getSize(parentHeightMeasureSpec); //获取父布局高度
        int childCount = parent.getChildCount(); //获取子view个数
        if (childCount > 1) {
            int maxHeight = parent.getTop() + parent.getPaddingTop();
            for (int i = 0; i < childCount; i++) {
                View childView = parent.getChildAt(i);
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) childView.getLayoutParams();
                maxHeight += childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            }
            if (maxHeight > height) {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(child.getMeasuredWidth(), layoutParams.width ==
                        ViewGroup.LayoutParams.MATCH_PARENT ? View.MeasureSpec.EXACTLY : layoutParams.width);
                int nHeight = child.getMeasuredHeight() - height + maxHeight;
                int nHeightSpec = View.MeasureSpec.makeMeasureSpec(nHeight, layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT ? View.MeasureSpec.EXACTLY : layoutParams.height);
                child.measure(widthMeasureSpec, nHeightSpec);
                return true;
            }
        }


        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    private boolean canScroll(View target) {
        return target.canScrollHorizontally(-1);
    }

    private void moveView(CoordinatorLayout coordinatorLayout, View scrollView, View child, int dy) {
        int dis = currentOffset + dy;
        dis = Math.max(dis, targetOffset);
        ViewCompat.offsetTopAndBottom(child, dis - currentOffset);
        ViewCompat.offsetTopAndBottom(scrollView, dis - currentOffset);
        currentOffset = dis;
    }

}
