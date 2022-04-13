package com.oushang.lib_base.base.rv;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 通用的RecyclerView适配器，只适合单布局
 * @Time: 2021/7/6 18:08
 * @Since: 1.0
 */
public abstract class BaseAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    //context
    protected Context mContext;

    //数据
    protected volatile List<T> mDatas;

    public BaseAdapter(Context context, List<T> datas) {
        this.mContext = context;
        this.mDatas = datas;
    }

    /**
     * 设置布局
     * @return 布局id
     */
    public abstract int setItemViewLayout();


    /**
     * 布局绑定数据
     * @param holder  itemViewHolder
     * @param data    数据
     * @param position itemView的位置
     */
    public abstract void onBindData(@NonNull VH holder, T data, int position);

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return VH.getViewHolder(mContext, parent, setItemViewLayout());
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        onBindData(holder, mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    /**
     * 更新数据
     * @param datas 数据list
     */
    public void updateData(List<T> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
    }

//    /**
//     * 添加数据
//     * @param data 数据
//     */
//    public void addData(T data) {
//        mDatas.add(data);
//        notifyDataSetChanged();
//    }
//
//    /**
//     * 添加数据集
//     * @param datas 数据集
//     */
//    public void addData(List<T> datas) {
//        mDatas.addAll(datas);
//        notifyDataSetChanged();
//    }



}
