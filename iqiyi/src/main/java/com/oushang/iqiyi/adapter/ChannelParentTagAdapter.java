package com.oushang.iqiyi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.entries.ChannelParentTag;
import com.oushang.iqiyi.ui.TagView;
import com.oushang.lib_base.base.rv.BaseAdapter;
import com.oushang.lib_base.base.rv.BaseViewHolder;
import com.oushang.lib_service.entries.ChannelTag;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * @Author: zeelang
 * @Description: 分类一级标签适配器
 * @Time: 2021/12/18 0018  11:46
 * @Since: 1.0
 */
public class ChannelParentTagAdapter extends BaseAdapter<ChannelParentTag, BaseViewHolder> {
    private static final String TAG = ChannelParentTagAdapter.class.getSimpleName();

    //是否启用编辑
    private boolean isEditable = false;

    private OnItemEditClickListen mOnItemEditClickListen;

    private OnItemClickListen mOnItemClickListen;

    public ChannelParentTagAdapter(Context context, List<ChannelParentTag> datas) {
        super(context, datas);
    }

    @Override
    public int setItemViewLayout() {
        return R.layout.item_channel_tag;
    }

    @Override
    public void onBindData(@NonNull BaseViewHolder holder, ChannelParentTag data, int position) {
        if (data == null || data.getChannelTag() == null) {
            return;
        }
        holder.setIsRecyclable(false); //禁用缓存，使用缓存导致状态混乱

        ChannelTag channelTag = data.getChannelTag();

        TagView tagView = holder.getView(R.id.channel_tag_name);
        tagView.setText(channelTag.getName());

        int editState = data.getEditState(); //编辑状态

        switch (editState) {
            case ChannelParentTag.DELETE_STATE: //数据是删除状态
                tagView.setEditState(TagView.EditState.DELETABLE);//设置tagView删除状态，并回调编辑状态监听
                break;
            case ChannelParentTag.ADD_STATE: //数据是添加状态
                tagView.setEditState(TagView.EditState.ADDABLE);//设置添加状态，并回调编辑状态监听
                break;
            case ChannelParentTag.COMPLETE_STATE: //数据是完成状态
                tagView.setEditState(TagView.EditState.COMPLETE); //设置完成状态，并回调编辑状态监听
                break;
            case ChannelParentTag.NONE_STATE: //数据是无状态
                tagView.setEditState(TagView.EditState.NONE);//设置无状态,并回调编辑状态监听
                break;
        }

        //tagView启用编辑监听
        tagView.setOnEditableChangeListener(new TagView.OnEditableChangeListener() {
            @Override
            public void onChange(boolean isEditable) {
                Log.d(TAG, "isEditable:" + isEditable);
                if (isEditable) { //如果启用编辑
                    if (data.isCommonChannel()) { //如果是常用频道
                        tagView.setEditState(TagView.EditState.DELETABLE);//设置删除状态，并回调编辑状态监听
                    } else { //如果是所有频道
                        int state = data.getEditState();
                        if (state == ChannelParentTag.COMPLETE_STATE) { //如果已添加到常用频道
                            tagView.setEditState(TagView.EditState.COMPLETE); //设置完成状态，并回调编辑状态监听
                        } else { //如果没有添加到常用频道
                            tagView.setEditState(TagView.EditState.ADDABLE);//设置添加状态，并回调编辑状态监听
                        }
                    }
                } else { //不启用编辑
                    tagView.setEditState(TagView.EditState.NONE);//设置无状态
                }
            }
        });

        //tagView 编辑状态监听
        tagView.setOnStateChangeListener(new TagView.OnStateChangeListener() {
            @Override
            public void onChange(TagView.EditState state) {
                Log.d(TAG, "onChange:" + state);
                if (!tagView.isEditEnable()) {
                    tagView.setTagImageVisible(false);
                    return;
                }
                switch (state) { //根据编辑状态，更新tagView图标
                    case ADDABLE: //添加状态
                        tagView.setTagImageVisible(true);
//                        tagView.setTagImageResource(R.drawable.channel_tag_add);
                        tagView.setTagTouchBackRound(R.drawable.ic_tag_add);
                        break;
                    case DELETABLE: //删除状态
                        tagView.setTagImageVisible(true);
//                        tagView.setTagImageResource(R.drawable.channel_tag_delete);
                        tagView.setTagTouchBackRound(R.drawable.ic_tag_delete);
                        break;
                    case COMPLETE: //已添加状态
                        tagView.setTagImageVisible(true);
//                        tagView.setTagImageResource(R.drawable.channel_tag_completed);
                        tagView.setTagTouchBackRound(R.drawable.ic_tag_complete);
                        break;
                    case NONE: //无状态
                        tagView.setTagImageVisible(false);
                        break;
                }
            }
        });

        //tagView点击操作
        tagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListen != null && ((TagView) v).getState() != TagView.EditState.DELETABLE) {
                    mOnItemClickListen.onClick(v, position);
                }
            }
        });

        //tagView编辑操作
        tagView.setOnEditClickListener(new TagView.OnEditClickListener() {
            @Override
            public void onClick(View v, TagView.EditState state) {
                Log.d(TAG, "onEditClick:" + state);
//                if (mOnItemEditClickListen != null) {
//                    mOnItemEditClickListen.onClick(v, state, position);
//                }
            }
        });

        tagView.setOnTagEditClickListener(new TagView.OnTagEditClickListener() {
            @Override
            public void onTagEditClick(TagView view, TagView.EditState state) {
                Log.d(TAG, "onTagEditClick:" + state);
                if (mOnItemEditClickListen != null) {
                    mOnItemEditClickListen.onClick(view, state, position);
                }
            }
        });

        //根据每个tag是否启用编辑,决定tagView是否启用编辑并显示图标
        tagView.setEditEnable(data.isEditEnable());//tagView 启用状态并回调启用编辑监听


    }

    /**
     * 是否启用
     *
     * @param editEnable
     */
    public void setEditEnable(boolean editEnable) {
        Log.d(TAG, "isEditable:" + isEditable + ",editEnable:" + editEnable);
        List<ChannelParentTag> removeList = new ArrayList<>();
        for (ChannelParentTag tag : mDatas) {
            if (editEnable != tag.isEditEnable()) {
                tag.setEditEnable(editEnable); //变更每个tag 是否启用编辑
            }
            if (tag.isDelete()) {
                removeList.add(tag);
            }
        }
        for (ChannelParentTag tag : removeList) { //清除删除的tag
            mDatas.remove(tag);
        }
        notifyDataSetChanged();
    }

    /**
     * 更新标签状态
     *
     * @param tag       一级频道标签
     * @param editState 状态
     */
    public void updateState(ChannelParentTag tag, int editState) {
        if (tag != null && mDatas != null && !mDatas.isEmpty()) {
            int size = mDatas.size();
            for (int i = 0; i < size; i++) {
                ChannelParentTag channelTag = mDatas.get(i);
                if (channelTag.equals(tag) && !channelTag.isCommonChannel()) {
                    channelTag.setEditState(editState);
                }
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 批量更新频道标签状态
     *
     * @param channelParentTagList 频道标签列表
     * @param editState            编辑状态
     */
    public void updateState(List<ChannelParentTag> channelParentTagList, int editState) {
        if (channelParentTagList != null && !channelParentTagList.isEmpty()) {
            channelParentTagList.forEach(channelParentTag -> {
                mDatas.forEach(channelParentTag1 -> {
                    if (channelParentTag1.equals(channelParentTag)) {
                        channelParentTag1.setEditState(editState);
                    }
                });
            });
            notifyDataSetChanged();
        }

    }

    /**
     * 添加标签
     *
     * @param tag 标签
     * @return 返回被挤掉的数据
     */
    public List<ChannelParentTag> addTag(ChannelParentTag tag) {
        List<ChannelParentTag> outChannelParentTagList = new ArrayList<>();
        if (tag != null && tag.isCommonChannel() && mDatas != null) {
            if(!mDatas.isEmpty()) { //如果数据不为空
                //过滤有效的数据
                List<ChannelParentTag> channelParentTagList = mDatas.stream().filter(channelParentTag -> !channelParentTag.isDelete()).collect(Collectors.toList());
                int size = channelParentTagList.size();
                if (size <= 5) {
                    for (ChannelParentTag channelParentTag : mDatas) {
                        if (channelParentTag.equals(tag)) {
                            channelParentTagList.remove(channelParentTag);
                            channelParentTagList.add(0, tag);
                        } else {
                            channelParentTagList.add(0, tag);
                            if (channelParentTagList.size() > 5) {
                                ChannelParentTag removeTag = channelParentTagList.get(channelParentTagList.size() - 1);
                                Log.d(TAG, "removeTag:" + removeTag);
                                outChannelParentTagList.add(removeTag); // 这个挤下来的，后面要恢复可添加状态
                                channelParentTagList.remove(removeTag);
                            }
                        }
                        break;
                    }
                    mDatas = null;
                    mDatas = channelParentTagList;
                    Log.d(TAG, "data:" + mDatas);
                    notifyDataSetChanged();
                }
            } else {
                Log.d(TAG, "data is empty!");
                mDatas.add(0,tag);
                notifyDataSetChanged();
            }
        }
        return outChannelParentTagList;
    }

    /**
     * 删除
     * @param tag
     * @param softDelete 是否软删除（只是置为删除状态）
     */
    public void removeTag(ChannelParentTag tag, boolean softDelete) {
        if (tag != null && mDatas != null && !mDatas.isEmpty()) {
            if(softDelete) {
                for (ChannelParentTag channelParentTag : mDatas) {
                    if (tag.equals(channelParentTag)) {
                        tag.setDelete(true); //设置为删除
                    }
                }
            } else {
                int index = mDatas.indexOf(tag);
                if(mDatas.removeIf(channelParentTag -> channelParentTag.equals(tag))) {
                    notifyItemRemoved(index);
                }
            }
        }
    }

    public List<ChannelParentTag> getDatas() {
        return mDatas;
    }

    /**
     * 设置标签编辑监听
     *
     * @param clickListen 标签编辑监听
     */
    public void setOnItemEditClickListen(OnItemEditClickListen clickListen) {
        this.mOnItemEditClickListen = clickListen;
    }

    /**
     * 设置标签点击监听
     *
     * @param clickListen 标签点击监听
     */
    public void setOnItemClickListen(OnItemClickListen clickListen) {
        this.mOnItemClickListen = clickListen;
    }

    /**
     * 标签点击监听
     */
    public interface OnItemClickListen {
        void onClick(View view, int position);
    }

    /**
     * 标签编辑监听
     */
    public interface OnItemEditClickListen {
        void onClick(TagView view, TagView.EditState state, int position);
    }
}
