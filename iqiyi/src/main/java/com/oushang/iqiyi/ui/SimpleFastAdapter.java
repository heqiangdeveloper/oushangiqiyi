package com.oushang.iqiyi.ui;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.oushang.lib_base.base.rv.BaseAdapter;
import com.oushang.lib_base.base.rv.BaseViewHolder;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 简便快捷适配器
 * @Time: 2021/7/21 15:09
 * @Since: 1.0
 */
public class SimpleFastAdapter<T> extends BaseAdapter<T, BaseViewHolder> {

    private int layoutId;

    public SimpleFastAdapter(Context context, int itemLayoutId, List<T> datas) {
        super(context, datas);
        this.layoutId = itemLayoutId;
    }

    @Override
    public int setItemViewLayout() {
        return layoutId;
    }

    @Override
    public void onBindData(@NonNull BaseViewHolder holder, T data, int position) {
        convert(holder, data, position);
    }

    public void convert(BaseViewHolder holder, T data, int position){

    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<T> datas) {
        this.mDatas = datas;
        notifyDataSetChanged();
    }

}
