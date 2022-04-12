package com.oushang.iqiyi.ui;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oushang.iqiyi.R;
import com.oushang.lib_base.base.rv.BaseAdapter;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 水平滑动加载组件
 * @Time: 2021/7/8 16:28
 * @Since: 1.0
 */
public abstract class HorizontalScrollAdapter<T extends GridMultiAdapter> extends BaseAdapter<T, NestingViewHolder> {
    private static final String TAG = HorizontalScrollAdapter.class.getSimpleName();

    private RecyclerView.ItemDecoration mHorizontalItemDecoration;
    private RecyclerView.ItemDecoration mVerticalItemDecoration;

    public HorizontalScrollAdapter(Context context, List<T> datas) {
        super(context, datas);
    }

    @Override
    public int setItemViewLayout() {
        return R.layout.nesting_layout;
    }

    @NonNull
    @Override
    public NestingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return NestingViewHolder.getNestingViewHoler(mContext, parent, setItemViewLayout());
    }

    @Override
    public void onBindData(@NonNull NestingViewHolder holder, T data, int position) {
        //获取viewholder里面的RecyclerView
        RecyclerView recyclerView = holder.getRecyclerView();
        //设置水平布局
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        //设置item间距
        if (recyclerView.getItemDecorationCount() == 0) { //防止重复叠加item间距
           if(mHorizontalItemDecoration != null) {
                recyclerView.addItemDecoration(mHorizontalItemDecoration);
            }
            if (mVerticalItemDecoration != null) {
                recyclerView.addItemDecoration(mVerticalItemDecoration);
            }
        }
        holder.setAdapter(mDatas.get(position));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
    }

    /**
     * 设置水平item间距
     * @param decoration ItemDecoration
     */
    public void setHorizontalItemDecoration(RecyclerView.ItemDecoration decoration) {
        this.mHorizontalItemDecoration = decoration;
    }

    /**
     * 设置垂直item间距
     * @param decoration ItemDecoration
     */
    public void setVerticalItemDecoration(RecyclerView.ItemDecoration decoration) {
        this.mVerticalItemDecoration = decoration;
    }

}







