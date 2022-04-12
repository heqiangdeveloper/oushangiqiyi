package com.oushang.iqiyi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jakewharton.rxbinding2.view.RxView;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.entries.SelectChannelChildTag;
import com.oushang.lib_base.base.rv.BaseAdapter;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_service.entries.ChannelTag;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @Author: zeelang
 * @Description: 频道子标签适配器
 * @Time: 2021/10/21 0021  17:10
 * @Since: 1.0
 */
public class ChannelChildTagAdapter extends BaseAdapter<ChannelTag, BaseViewHolder> {

    private ItemSelectListener mItemSelectListener;

    public ChannelChildTagAdapter(Context context, List<ChannelTag> datas) {
        super(context, datas);
    }

    @Override
    public int setItemViewLayout() {
        return R.layout.item_channel_child_tag;
    }

    @Override
    public void onBindData(@NonNull BaseViewHolder holder, ChannelTag data, int position) {
        if (data == null) {
            return;
        }
        if (data.getName().equals("专区")) { //修改"专区"改为"免费/付费"
            if(data.getAlias() == null || data.getAlias().isEmpty() || data.getAlias().equals("null")) {
                data.setAlias("免费/付费");
            }
            List<ChannelTag> children = data.getChildren();
            children = children.stream()
                    .filter(channelTag -> channelTag.getId().equals("VIP") || channelTag.getId().equals("FREE")) //剔除不是vip和免费的
                    .peek(channelTag -> {
                        if(channelTag.getAlias() == null || channelTag.getAlias().isEmpty() || channelTag.getAlias().equals("null")) {
                            if (channelTag.getId().equals("VIP")) { //vip修改为 "付费"
                                channelTag.setAlias("付费");
                            } else if (channelTag.getId().equals("FREE")) {
                                channelTag.setAlias("免费");
                            } else {
                                channelTag.setAlias(channelTag.getName());
                            }
                        }
                    })
                    .collect(Collectors.toList());
            data.setChildren(children);
        } else {
            if(data.getAlias() == null || data.getAlias().isEmpty() || data.getAlias().equals("null")) {
                data.setAlias(data.getName());
            }
        }
        List<ChannelTag> children = data.getChildren();
        if (children != null && !children.isEmpty() &&
                (children.get(0).getAlias() == null ||!children.get(0).getAlias().equals("全部"))) { //添加 "全部" 子标签
            ChannelTag channelTag = new ChannelTag("all_" + data.getId(), data.getName().equals("专区")?"免费/付费" : data.getName());
            channelTag.setAlias("全部");
            children.add(0, channelTag);
        }

        boolean isSelected = data.isSelected();
        TextView textView = holder.getView(R.id.channel_child_item_tag_tv);
        textView.setText(data.getAlias());

        if (isSelected) {
            textView.setTextColor(mContext.getColor(R.color.color_channel_chile_tag_text_selected));
            textView.setSelected(true);
        } else {
            textView.setTextColor(mContext.getColor(R.color.color_channel_child_tag_text));
            textView.setSelected(false);
        }

        RxView.clicks(textView)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    Log.d("ChannelChildTagAdapter", "onSelected");
                    if (mItemSelectListener != null) {
                        mItemSelectListener.onSelected(data, position);
                    }
                });
    }

    public void setSelect(int position) {
        int size = mDatas.size();
        for(int i = 0; i < size; i++) {
            ChannelTag channelTag = mDatas.get(i);
            channelTag.setSelected(i == position);
        }
        notifyDataSetChanged();
    }

    public void setMultiSelect(int position) {
        int size = mDatas.size();
        if (position >= 0 && position < size) {
            mDatas.get(position).setSelected(true);
            notifyItemChanged(position);
        }
    }

    /**
     * 更换选择的名称
     * @param channelTag 子标签
     */
    public void changeSelectName(ChannelTag channelTag) {
        if(channelTag != null && mDatas != null && !mDatas.isEmpty()) {
            int size = mDatas.size();
            for(int i = 0; i < size; i++) {
                if(channelTag.equals(mDatas.get(i))) {
                    mDatas.get(i).setName(channelTag.getName());
                    notifyItemChanged(i);
                }
            }
        }
    }

    /**
     * 更改别名
     * @param channelTag 子标签
     * @param isSelected 是否选择状态
     */
    public void changeAliasName(ChannelTag channelTag, boolean isSelected) {
        if(channelTag != null && mDatas != null && !mDatas.isEmpty()) {
            int size = mDatas.size();
            for(int i = 0; i < size; i++) {
                if(channelTag.equals(mDatas.get(i))) {
                    mDatas.get(i).setAlias(channelTag.getAlias());
                    mDatas.get(i).setSelected(isSelected);
                    notifyItemChanged(i);
                }
            }
        }
    }

    public void setItemSelectListener(ItemSelectListener itemSelectListener) {
        this.mItemSelectListener = itemSelectListener;
    }

    public interface ItemSelectListener {
        void onSelected(ChannelTag data, int position);
    }
}
