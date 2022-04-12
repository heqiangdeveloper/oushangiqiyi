package com.oushang.iqiyi.adapter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.entries.SelectionEntry;
import com.oushang.iqiyi.ui.GridItemDecoration;
import com.oushang.lib_base.base.rv.BaseAdapter;
import com.oushang.lib_base.base.rv.BaseViewHolder;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 更多选集数字型，viewpager适配器
 * @Time: 2021/8/27 14:53
 * @Since: 1.0
 */
public class MoreSelectionsViewPagerAdapter extends BaseAdapter<List<SelectionEntry>, BaseViewHolder> {
    private static final String TAG = MoreSelectionsViewPagerAdapter.class.getSimpleName();

    private int lastSelectPosition = -1;

    public MoreSelectionsViewPagerAdapter(Context context, List<List<SelectionEntry>> datas) {
        super(context, datas);
    }

    @Override
    public int setItemViewLayout() {
        return R.layout.item_episode_selections_viewpager;
    }

    @Override
    public void onBindData(@NonNull BaseViewHolder holder, List<SelectionEntry> data, int position) {
        if (data == null) {
            return;
        }
        RecyclerView recyclerView = holder.getView(R.id.episode_selection_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 5));
        if (recyclerView.getItemDecorationCount() == 0) {
            recyclerView.addItemDecoration(new GridItemDecoration(29, GridItemDecoration.HORIZONTAL));
            recyclerView.addItemDecoration(new GridItemDecoration(30, GridItemDecoration.VERTICAL));
        }
        MoreSelectionsDigitalAdapter mDigitalAdapter = new MoreSelectionsDigitalAdapter(mContext, data);
        mDigitalAdapter.setOnSelectedListener(new MoreSelectionsDigitalAdapter.OnSelectedListener() {
            @Override
            public void onSelect() {
                Log.d(TAG, "selected position:" + position);
                if (lastSelectPosition == -1) {
                    lastSelectPosition = position;
                }
                setSelected(lastSelectPosition, position);
                lastSelectPosition = position;
            }
        });
        recyclerView.setAdapter(mDigitalAdapter);
    }

    public void setSelected(int lastSelectPosition, int currentSelectPosition) {
        Log.d(TAG, "last selected position:" + lastSelectPosition + "current selected position:" + currentSelectPosition);
        if (lastSelectPosition != currentSelectPosition) {
            List<SelectionEntry> laseSelectionEntryList = mDatas.get(lastSelectPosition);
            for(SelectionEntry entry: laseSelectionEntryList) {
                if (entry.isSelected()) {
                    entry.setSelected(false);
                    notifyItemChanged(lastSelectPosition);
                }
            }
        }
    }


}
