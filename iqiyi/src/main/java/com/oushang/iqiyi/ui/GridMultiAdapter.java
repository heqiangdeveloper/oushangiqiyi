package com.oushang.iqiyi.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oushang.lib_base.base.rv.BaseMultiAdapter;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 多布局组件适配器
 * @Time: 2021/7/8 14:05
 * @Since: 1.0
 */
public abstract class GridMultiAdapter<T extends ISpanItem> extends BaseMultiAdapter<T> {
//    protected List<T> datas;

    public GridMultiAdapter(Context context, List<T> datas) {
        super(context, datas);
//        this.datas = datas;
    }

    /**
     * 设置总行数
     * @return int 行数
     */
    public abstract int setSpanCount();

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, setSpanCount());
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mDatas.get(position).getSpanSize();
            }
        });
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }
}
