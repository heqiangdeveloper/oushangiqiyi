package com.oushang.iqiyi.ui;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author: zeelang
 * @Description: GridLayout布局item Decoration
 * @Time: 2021/7/8 16:54
 * @Since: 1.0
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = GridLayout.HORIZONTAL;
    public static final int VERTICAL = GridLayout.VERTICAL;

    private int mSpace;
    private int orientation;
    private int firstSpace = 0;
    private int mSpanCount = 0;

    public GridItemDecoration(){}

    public GridItemDecoration(int space, int orientation) {
        this.mSpace = space;
        this.orientation = orientation;
    }

    public GridItemDecoration(int space, int spanCount, int orientation) {
        this.mSpace = space;
        this.mSpanCount = spanCount;
        this.orientation = orientation;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mSpace < 0) {
            return;
        }
        int currentPos = parent.getChildLayoutPosition(view);
        if (currentPos == 0 && firstSpace > 0) {

            outRect.bottom = firstSpace;
            return;
        }
        if (GridLayoutManager.HORIZONTAL == orientation) {
            if(mSpanCount > 0) {
                int position = (parent.getChildLayoutPosition(view)+1) % mSpanCount;
                Log.d("Decoration", "pos:" + parent.getChildLayoutPosition(view) + ",position:" + position);
                if(position != 0) {
                    outRect.right = mSpace;
                }
            } else {
                outRect.right = mSpace;
            }
        } else {
            outRect.bottom = mSpace;
        }
    }

    /**
     * 设置间距
     * @param space 间距
     * @return GridItemDecoration
     */
    public GridItemDecoration setSpace(int space) {
        this.mSpace = space;
        return this;
    }

    /**
     * 设置orientation
     * @param orientation orientation
     * @return GridItemDecoration
     */
    public GridItemDecoration setOritentation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        this.orientation = orientation;
        return this;
    }

    /**
     * 设置首行间距
     * @param space 间距
     * @return GridItemDecoration
     */
    public GridItemDecoration setFirstSpace(int space) {
        this.firstSpace = space;
        return this;
    }

}
