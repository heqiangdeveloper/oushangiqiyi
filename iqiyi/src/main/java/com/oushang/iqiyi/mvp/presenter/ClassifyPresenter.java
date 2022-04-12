package com.oushang.iqiyi.mvp.presenter;

import android.util.Log;

import com.oushang.iqiyi.base.BaseServicePresenter;
import com.oushang.iqiyi.entries.ChannelParentTag;
import com.oushang.iqiyi.entries.ChannelTag;
import com.oushang.iqiyi.mvp.model.ClassifyModel;
import com.oushang.iqiyi.mvp.view.IClassifyView;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.utils.HandlerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: 分类逻辑
 * @Time: 2021/7/12 10:06
 * @Since: 1.0
 */
public class ClassifyPresenter extends BaseServicePresenter<IClassifyView, ClassifyModel> {
    private static final String TAG = ClassifyPresenter.class.getSimpleName();

    @Override
    protected ClassifyModel createModel() {
        return new ClassifyModel();
    }

    //加载常用频道
    public void loadNormalChannelTag() {
        List<ChannelTag> channelTagList = model.getNormalChannelTagList();
        if (getView() != null) {
            getView().onLoadCommonChannelTagList(channelTagList);
        }
    }

    //加载所有频道
    public void loadAllChannelTag() {
        if (isAttach()) {
            execute(model.getAllChannelTags(),
                    channelTags -> {
                        List<ChannelTag> channelTagList = new ArrayList<>();
                        for(com.oushang.lib_service.entries.ChannelTag tag: channelTags) {
                            String id = tag.getId();
                            if (id.equals("0") || id.endsWith("12")) {
                                continue;
                            }
                            ChannelTag channelTag = new ChannelTag();
                            channelTag.setChannelId(Integer.parseInt(id));
                            channelTag.setChannelName(tag.getName());
                            channelTag.setCommonChannel(false);
                            channelTag.setEditEnable(false);
                            channelTagList.add(channelTag);
                        }
                        getView().onLoadAllChannelTagList(channelTagList);
                    },
                    throwable -> {
                        Log.d(TAG, "error:" + Log.getStackTraceString(throwable));
                        return true;
                    },
                    aBoolean -> false);
        }
    }

    public void loadCommonChannelParentTag(boolean editEnable) {
        Log.d(TAG, "loadCommonChannelParentTag");
        if(isAttach()) {
            List<ChannelParentTag> channelParentTagList = model.getCommonChannelParentTagList();
            if(channelParentTagList != null && !channelParentTagList.isEmpty()) {
                for(ChannelParentTag tag: channelParentTagList) {
                    tag.setEditEnable(editEnable);
                }
                getView().onLoadCommonChannelParentTag(channelParentTagList);
            } else {
                getView().onLoadEmptyCommonChannelParentTag();
            }
        }
    }


    public void loadAllChannelParentTag() {
        if(isAttach()) {
            execute(model.getAllChannelTags(),
                    channelTags -> {
                        List<ChannelParentTag> channelParentTagList = new ArrayList<>();
                        for(com.oushang.lib_service.entries.ChannelTag tag: channelTags) {
                            String id = tag.getId();
                            if (id.equals("0") || id.endsWith("12")) { //剔除推荐、VIP专区 频道标签
                                continue;
                            }
                            ChannelParentTag channelParentTag = new ChannelParentTag(tag);
                            channelParentTagList.add(channelParentTag);
                        }
                        getView().onLoadAllChannelParentTag(channelParentTagList);
                    },
                    throwable -> {
                        Log.d(TAG, "error:" + Log.getStackTraceString(throwable));
                        return true;
                    },
                    aBoolean -> false);
        }
    }
}
