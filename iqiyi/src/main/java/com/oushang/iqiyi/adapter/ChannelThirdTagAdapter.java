package com.oushang.iqiyi.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.lib_base.base.rv.BaseAdapter;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_service.entries.ChannelTag;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zeelang
 * @Description: 频道详情第三级标签适配器
 * @Time: 2021/12/7 0007  9:31
 * @Since: 1.0
 */
public class ChannelThirdTagAdapter extends BaseAdapter<ChannelTag, BaseViewHolder> {
    private ChannelTag mParentChannelTag;

    public ChannelThirdTagAdapter(Context context, ChannelTag parent) {
        super(context, parent.getChildren());
        this.mParentChannelTag = parent;
    }

    @Override
    public int setItemViewLayout() {
        return R.layout.item_channel_third_tag;
    }

    @Override
    public void onBindData(@NonNull BaseViewHolder holder, ChannelTag data, int position) {
        if (data == null) {
            return;
        }
        if(data.getAlias() == null || data.getAlias().isEmpty()|| data.getAlias().equals("null")) {
            data.setAlias(data.getName());
        }

        TextView textView = holder.getView(R.id.channel_child_childtag_name);
        textView.setText(data.getAlias());

        if (data.isSelected()) {
            textView.setTextColor(mContext.getColor(R.color.color_channel_child_tag_selected));
            textView.setSelected(true);
        } else {
            textView.setTextColor(mContext.getColor(R.color.color_channel_third_tag_text));
            textView.setSelected(false);
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(!v.isSelected()) {
//                    select(position);
//                    Bundle eventParams = new Bundle();
//                    eventParams.putParcelable(EventConstant.EVENT_PARAMS_THIRD_CHANNEL_TAG, data);
//                    eventParams.putParcelable(EventConstant.EVENT_PARAMS_CHILD_CHANNEL_TAG, mParentChannelTag);
//                    EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_SELECT_THIRD_CHANNEL_TAG, eventParams));
//                }
                Map<String, String> statValue = new HashMap<>();
                statValue.put("label", mParentChannelTag.getName());
                statValue.put("content", data.getName());
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5930, statValue);

                select(position);
                Bundle eventParams = new Bundle();
                eventParams.putParcelable(EventConstant.EVENT_PARAMS_THIRD_CHANNEL_TAG, data);
                eventParams.putParcelable(EventConstant.EVENT_PARAMS_CHILD_CHANNEL_TAG, mParentChannelTag);
                EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_SELECT_THIRD_CHANNEL_TAG, eventParams));
            }
        });
    }

    public void select(int position) {
        int size = mDatas.size();
        if (position >= 0 && position < size) {
            for(int i = 0; i < size; i++) {
                mDatas.get(i).setSelected(i == position);
            }
            notifyDataSetChanged();
        }
    }
}
