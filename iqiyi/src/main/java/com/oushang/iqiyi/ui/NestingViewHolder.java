package com.oushang.iqiyi.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oushang.iqiyi.R;
import com.oushang.lib_base.base.rv.BaseViewHolder;

/**
 * @Author: zeelang
 * @Description: 嵌套布局
 * @Time: 2021/7/8 20:43
 * @Since: 1.0
 */
public class NestingViewHolder extends BaseViewHolder {
    private RecyclerView recyclerView;

    public NestingViewHolder(@NonNull View itemView) {
        super(itemView);
        init();
    }

    private void init() {
        recyclerView = getView(R.id.nesting_rv);
    }

    /**
     * 获取NestingViewHolder
     * @param context context
     * @param parent viewGroup
     * @param layoutId 布局id
     * @return NestingViewHolder
     */
    public static NestingViewHolder getNestingViewHoler(Context context, ViewGroup parent,int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent,false);
        return new NestingViewHolder(itemView);
    }

    /**
     * 设置adapter
     * @param adapter adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * 获取Recyclerview
     * @return RecyclerView
     */
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

}
