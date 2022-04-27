package com.oushang.iqiyi.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.entries.SelectionEntry;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.ui.LinePulseView;
import com.oushang.iqiyi.utils.ThemeManager;
import com.oushang.lib_base.base.rv.BaseAdapter;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.Collections;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: 更多选集适配器(数字型）
 * @Time: 2021/8/27 14:20
 * @Since: 1.0
 */
public class MoreSelectionsDigitalAdapter extends BaseAdapter<SelectionEntry, BaseViewHolder> {
    private static final String TAG = MoreSelectionsDigitalAdapter.class.getSimpleName();
    private int mCurrentSort = Constant.SORT_ORDER; //当前排序, 默认顺序排序
    private OnSelectedListener mSelectedListener;
    private OnSortChangedListener mOnSortChangedListener;
    private ThemeManager.ThemeMode mThemeMode = ThemeManager.sThemeMode;//黑白模式切换

    public MoreSelectionsDigitalAdapter(Context context, List<SelectionEntry> datas) {
        this(context, datas, null);
    }

    public MoreSelectionsDigitalAdapter(Context context, List<SelectionEntry> datas, OnSortChangedListener listener) {
        super(context, datas);
        mOnSortChangedListener = listener;
    }

    @Override
    public int setItemViewLayout() {
        return R.layout.item_video_info_more_selections;
    }

    @Override
    public void onBindData(@NonNull BaseViewHolder holder, SelectionEntry data, int position) {
        TextView view = holder.getView(R.id.video_info_selection);
        VideoInfo videoInfo = data.getVideoInfo();
        if (videoInfo != null) {
            view.setText(String.valueOf(videoInfo.getOrder()));
        }
        holder.setIsRecyclable(false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5917, String.valueOf(data.getOrder()));
                setSelected(position);
                Bundle eventParams = new Bundle();
                eventParams.putParcelable(EventConstant.EVENT_PARAMS_PLAY_SELECT_SELECTION, data);
                EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_PLAY_SELECT_SELECTION, eventParams));
            }
        });
        LinePulseView pulseView = holder.getView(R.id.play_line_pulse);
        if (data.isSelected()) {
            view.setText("");
            view.setSelected(true);
            pulseView.setVisibility(View.VISIBLE);
            pulseView.start();
        } else {
            view.setSelected(false);
            pulseView.setVisibility(View.GONE);
            pulseView.stop();
        }
        Log.d(TAG, "themeMode:" + mThemeMode);
        mThemeMode = ThemeManager.getThemeMode();
        if(mThemeMode == ThemeManager.ThemeMode.NIGHT) {
            view.setBackground(mContext.getDrawable(R.drawable.video_info_order_skin_selection_shape));
            view.setTextColor(mContext.getColor(R.color.color_skin_synopsis_text));
        } else {
            view.setBackground(mContext.getDrawable(R.drawable.video_info_order_skin_selection_shape_notnight));
            view.setTextColor(mContext.getColor(R.color.color_skin_synopsis_text_notnight));
        }
    }

    public void setSelectOrder(int order) {
        Log.d(TAG, "order:" + order + ",data:" + mDatas);
        if(mDatas != null && !mDatas.isEmpty()) {
            int size = mDatas.size();
            for(int i = 0; i < size; i++) {
                SelectionEntry entry = mDatas.get(i);
                int o = entry.getOrder();
                entry.setSelected(order == o);
            }
            notifyDataSetChanged();
        }
    }

    public void setSelected(int position) {
        Log.d(TAG, "position:" + position + ",size:" + mDatas.size());
        if (position >= mDatas.size()) {
            return;
        }
        SelectionEntry selectedEntry = mDatas.get(position);
        selectedEntry.setSelected(true);
        if (mSelectedListener != null) {
            mSelectedListener.onSelect();
        }
        for (SelectionEntry entry: mDatas) {
            if (entry != selectedEntry && entry.isSelected()) {
                entry.setSelected(false);
            }
        }
        notifyDataSetChanged();
    }

    public void setSelected(VideoInfo videoInfo) {
        if (videoInfo != null && mDatas != null && !mDatas.isEmpty()) {
            int size = mDatas.size();
            for(int i = 0; i < size; i++) {
                SelectionEntry entry = mDatas.get(i);
                VideoInfo info = entry.getVideoInfo();
                if(videoInfo.equals(info)) { //如果视频相同
                    entry.setSelected(true); //设置为选中状态
                } else if (videoInfo.isAlbum()) { //如果视频是专辑视频
                    if (videoInfo.getDefaultEpi() != null) {
                        long qipuId = videoInfo.getDefaultEpi().getQipuId();
                        entry.setSelected(info.getQipuId() == qipuId); //如果是相同的视频id,设置为选中状态,否则不选中
                    } else {
                        entry.setSelected(info.getAlbumId() == videoInfo.getAlbumId());
                    }
                } else {
                    entry.setSelected(false);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void reverse() {
        Log.d(TAG, "reverse");
        Collections.reverse(mDatas);
        notifyDataSetChanged();
    }

    public void changeSort(int sort) {
        Log.d(TAG, this + " changeSort:" + sort + ",currentSort:" + mCurrentSort);
        if(mCurrentSort != sort) {
            mCurrentSort = sort;
            reverse();
        }
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        this.mSelectedListener = listener;
    }

    public interface OnSelectedListener {
        void onSelect();
    }

    public interface OnSortChangedListener {
        void onChanged(int sort);
    }

    public void updateSkin(ThemeManager.ThemeMode themeMode) {
        this.mThemeMode = themeMode;
        notifyDataSetChanged();
    }
}
