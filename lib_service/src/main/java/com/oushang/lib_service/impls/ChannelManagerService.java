package com.oushang.lib_service.impls;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.entries.ChannelInfo;
import com.oushang.lib_service.entries.ChannelTag;
import com.oushang.lib_service.interfaces.ChannelManager;
import com.oushang.lib_service.iqiyiweb.IqiyiApi;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @Author: zeelang
 * @Description: 频道管理
 * @Time: 2021/6/28 19:48
 * @Since: 1.0
 */
@Route(path = Constant.PATH_SERVICE_CHANNEL_MANAGER)
public class ChannelManagerService implements ChannelManager {
    private static final String TAG = ChannelManagerService.class.getSimpleName();

    @Override
    public void init(Context context) {

    }

    /**
     * 获取所有频道标签
     *
     * @return 频道标签列表
     */
    @Override
    public Observable<List<ChannelTag>> getAllChannelTags() {
        return IqiyiApi.navlist().map(new Function<String, List<ChannelTag>>() {
            @Override
            public List<ChannelTag> apply(String s) throws Exception {
                List<ChannelTag> channelTagList = new ArrayList<>();
                JSONArray jsonArray = JSON.parseArray(s);
                int size = jsonArray.size();
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String name = jsonObject.getString("name");
                    ChannelTag channelTag = new ChannelTag(id, name);
                    channelTagList.add(channelTag);
                }
                return channelTagList;
            }
        });
    }


    @Override
    public Observable<ChannelInfo> getChannelInfos(String channelName, String mode, String threeCategoryId, int pageNum, int pageSize, int ispurchase, int ondemand, boolean isRequireTags) {
        return IqiyiApi.getChannelInfo(channelName, mode, threeCategoryId, pageNum, pageSize, ispurchase, ondemand, isRequireTags ? "1" : "0")
                .map(s -> {
                    Gson gson = new Gson();
                    return gson.fromJson(s, ChannelInfo.class);
                });
    }


}
