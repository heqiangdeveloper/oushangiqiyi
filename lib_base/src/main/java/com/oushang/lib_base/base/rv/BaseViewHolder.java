package com.oushang.lib_base.base.rv;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author: zeelang
 * @Description: 通用的RecyclerView ViewHolder
 * @Time: 2021/7/6 17:50
 * @Since: 1.0
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {
    View itemView;
    final SparseArray<View> views;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        views = new SparseArray<>();
    }

    /**
     * 获取ViewHodler
     * @param context context
     * @param parent  viewGroup
     * @param layoutId 布局Id
     * @param <T>  viewHolder
     * @return viewHolder
     */
    public static <T extends BaseViewHolder> T getViewHolder(Context context, ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent,false);
        return (T) new BaseViewHolder(itemView);
    }

    /**
     * 获取itemView
     * @return
     */
    public View getItemView() {
        return itemView;
    }

    /**
     * 获取view
     * @param viewId viewId
     * @param <T> view的类型
     * @return view
     */
    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

}
