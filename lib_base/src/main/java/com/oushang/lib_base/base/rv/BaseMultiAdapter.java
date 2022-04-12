package com.oushang.lib_base.base.rv;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 多布局RecyclerView适配器
 * @Time: 2021/7/6 18:24
 * @Since: 1.0
 */
public abstract class BaseMultiAdapter<T extends IMultiItem> extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = BaseMultiAdapter.class.getSimpleName();
    protected Context mContext;
    protected List<T> mDatas;
    protected SparseIntArray mLayouts;

    public BaseMultiAdapter(Context context, List<T> datas) {
        this.mContext = context;
        this.mDatas = datas;
    }

    /**
     * 绑定数据
     * @param holder itemView
     * @param data   数据
     * @param position itemView的位置，
     */
    public abstract void onBindData(@NonNull BaseViewHolder holder, T data, int position);

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayouts == null || mLayouts.size() == 0) {
            throw new IllegalArgumentException("viewType must be add! you can call addViewType(int,int)");
        }
        int layoutId = mLayouts.get(viewType);
        return BaseViewHolder.getViewHolder(mContext,parent,layoutId);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        onBindData(holder, mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas != null?mDatas.size():0;
    }

    @Override
    public int getItemViewType(int position) {
        return mDatas.get(position).getViewType();
    }

    /**
     * 添加多布局类型和布局id
     * @param type  类型
     * @param resId 布局id
     */
    protected void addViewType(int type, int resId) {
        if (mLayouts == null) {
            mLayouts = new SparseIntArray();
        }
        mLayouts.put(type,resId);
    }
}
