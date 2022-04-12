package com.oushang.iqiyi.ui;

import android.graphics.Rect;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author: DELL
 * @Description: recyclerview分隔线
 * @Time: 2021/7/21 15:33
 * @Since:
 */
public class LineItemDecoration extends RecyclerView.ItemDecoration{
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    private int space;
    private int orientation;

    public LineItemDecoration(int space, int orientation) {
        this.space = space;
        this.orientation = orientation;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (LinearLayoutManager.HORIZONTAL == orientation) {
            outRect.right = space;
        } else {
            outRect.bottom = space;
        }
    }
}
