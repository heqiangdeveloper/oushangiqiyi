package com.oushang.iqiyi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.entries.ChannelTag;
import com.oushang.iqiyi.entries.MutiType;
import com.oushang.iqiyi.ui.GridMultiAdapter;
import com.oushang.iqiyi.ui.TagView;
import com.oushang.lib_base.base.rv.BaseViewHolder;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 频道标签适配器
 * @Time: 2021/7/16 15:34
 * @Since: 1.0
 */
public class ChannelTagAdapter extends GridMultiAdapter<ChannelTag> {
    private static final String TAG = ChannelTagAdapter.class.getSimpleName();

    //多少列item
    private static final int SPAN_COUNT = 5;

    //item点击监听
    private OnItemClickListen itemClickListen;

    //item编辑监听
    private OnItemEditClickListen itemEditClickListen;

    public ChannelTagAdapter(Context context, List<ChannelTag> datas) {
        super(context, datas);
        //添加布局与类型对应
        addViewType(MutiType.TEXT, R.layout.item_channel_tag);
    }

    @Override
    public int setSpanCount() {
        return SPAN_COUNT;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindData(@NonNull BaseViewHolder holder, ChannelTag data, int position) {
        TagView view = holder.getView(R.id.channel_tag_name);
        view.setText(data.getChannelName());

        holder.setIsRecyclable(false); //禁用缓存，使用缓存导致状态混乱

        //如果是常用频道
//        if (data.isCommonChannel()) {
//            //设置常用频道背景
//            view.setImageDrawable(mContext.getDrawable(R.drawable.common_channel_tag_shape));
//        }

        //启用编辑监听
        view.setOnEditableChangeListener(new TagView.OnEditableChangeListener() {
            @Override
            public void onChange(boolean isEditable) {
                if (isEditable) { //如果启用编辑
                    if (data.isCommonChannel()) { //如果是常用频道
                        view.setEditState(TagView.EditState.DELETABLE);//设置删除状态
                    } else { //如果是所有频道
                        int state = data.getEditState();
                        if (state == ChannelTag.COMPLETE_STATE) { //如果已添加到常用频道
                            view.setEditState(TagView.EditState.COMPLETE);
                        } else { //如果没有添加到常用频道
                            view.setEditState(TagView.EditState.ADDABLE);//设置添加状态
                        }
                    }
                } else { //不启用编辑
                    view.setEditState(TagView.EditState.NONE);//设置无状态
                }
            }
        });

        //编辑状态监听
        view.setOnStateChangeListener(new TagView.OnStateChangeListener() {
            @Override
            public void onChange(TagView.EditState state) {
                if (!view.isEditEnable()) {
                    view.setTagImageVisible(false);
                    return;
                }
                switch (state) {
                    case ADDABLE: //添加状态
                        data.setEditState(ChannelTag.ADD_STATE);
//                        view.setTagImageVisible(true);
//                        view.setTagImageResource(R.drawable.channel_tag_add);
                        break;
                    case DELETABLE: //删除状态
                        data.setEditState(ChannelTag.DELETE_STATE);
//                        view.setTagImageVisible(true);
//                        view.setTagImageResource(R.drawable.channel_tag_delete);
                        break;
                    case COMPLETE: //已添加状态
                        data.setEditState(ChannelTag.COMPLETE_STATE);
//                        view.setTagImageVisible(true);
//                        view.setTagImageResource(R.drawable.channel_tag_completed);
                        break;
                    case NONE: //无状态
                        data.setEditState(ChannelTag.NONE_STATE);
//                        view.setTagImageVisible(false);
                        break;
                }
            }
        });

        view.setOnEditClickListener(new TagView.OnEditClickListener() {
            @Override
            public void onClick(View v, TagView.EditState state) {
                if (itemEditClickListen != null) {
                    itemEditClickListen.onClick(v, state, position);
                }
            }
        });

        //点击
        view.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if (itemClickListen != null && ((TagView) v).getState() != TagView.EditState.DELETABLE) {
                    itemClickListen.onClick(v, position);
                    notifyDataSetChanged();
                }
            }
        });

        //设置启用编辑
        view.setEditEnable(data.isEditEnable());

        int editState = data.getEditState();
        switch (editState) {
            case ChannelTag.NONE_STATE:
                view.setTagImageVisible(false);
                break;
            case ChannelTag.ADD_STATE:
                view.setTagImageVisible(true);
                view.setTagImageResource(R.drawable.channel_tag_add);
                break;
            case ChannelTag.DELETE_STATE:
                view.setTagImageVisible(true);
                view.setTagImageResource(R.drawable.channel_tag_delete);
                break;
            case ChannelTag.COMPLETE_STATE:
                view.setTagImageVisible(true);
                view.setTagImageResource(R.drawable.channel_tag_completed);
                break;
        }

    }

    //设置是否启用编辑
    @SuppressLint("NotifyDataSetChanged")
    public void setEditEnable(boolean enable) {
        for (ChannelTag tag : mDatas) {
            if (enable != tag.isEditEnable()) {
                tag.setEditEnable(enable);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 更新标签状态
     *
     * @param tag       频道标签
     * @param editState 状态
     */
    public void updateState(ChannelTag tag, int editState) {
        if (tag != null && mDatas != null && !mDatas.isEmpty()) {
            int size = mDatas.size();
            for (int i = 0; i < size; i++) {
                ChannelTag channelTag = mDatas.get(i);
                if (channelTag.equals(tag)) {
                    channelTag.setEditState(editState);
                }
            }
            notifyDataSetChanged();
        }
    }

    public List<ChannelTag> getData() {
        return mDatas;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ChannelTag> channelTagList) {
        this.mDatas = channelTagList;
        notifyDataSetChanged();
    }

    public void setOnItemEditClickListen(OnItemEditClickListen clickListen) {
        this.itemEditClickListen = clickListen;
    }

    public void setOnItemClickListen(OnItemClickListen clickListen) {
        this.itemClickListen = clickListen;
    }

    public interface OnItemClickListen {
        void onClick(View view, int position);
    }

    public interface OnItemEditClickListen {
        void onClick(View view, TagView.EditState state, int position);
    }
}
