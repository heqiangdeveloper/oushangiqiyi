package com.oushang.iqiyi.ui;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author: DELL
 * @Description:
 * @Time: 2021/8/14 17:11
 * @Since:
 */
public class HorizontalLinearLayoutManager extends LinearLayoutManager {
    private boolean mIsScrollEnable = true;
    private double speedRatio = 0.99;

    public HorizontalLinearLayoutManager(Context context) {
        super(context);
    }

    public HorizontalLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public void setScrollEnabled(boolean enabled) {
        this.mIsScrollEnable = enabled;
    }

    @Override
    public boolean canScrollHorizontally() {
        return mIsScrollEnable && super.canScrollHorizontally();
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        super.smoothScrollToPosition(recyclerView, state, position);
    }

//    @Override
//    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
//        return super.scrollHorizontallyBy((int) (dx * speedRatio),recycler,state);
//    }

    private class CenterSmoothScroller extends LinearSmoothScroller {

        public CenterSmoothScroller(Context context) {
            super(context);
        }

        @Nullable
        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return HorizontalLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) /2);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return 0.2f;
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }
    }

}
